package com.elhady.memories.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.elhady.memories.R
import com.elhady.memories.adapter.MemoriesAdapter
import com.elhady.memories.databinding.FragmentMemoriesBinding
import com.elhady.memories.MainActivity
import com.elhady.memories.utils.SwipeToDelete
import com.elhady.memories.utils.hideKeyboard
import com.elhady.memories.viewmodel.MemoriesViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit


class MemoriesFragment : Fragment(R.layout.fragment_memories) {


    private val memoriesViewModel: MemoriesViewModel by activityViewModels()
    private lateinit var memoriesAdapter: MemoriesAdapter
    private lateinit var binding: FragmentMemoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMemoriesBinding.bind(view)
        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view)

        requireView().hideKeyboard()

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.statusBarColor = Color.WHITE
        }


        val count = parentFragmentManager.backStackEntryCount
        Log.d("backStackCount", count.toString())
        memoriesViewModel.saveImagePath(null)


        //Receives confirmation from the memoriesContentFragment
        setFragmentResultListener("key") { _, bundle ->
            when (val result = bundle.getString("bundleKey")) {
                "Memories Saved", "Empty Memories Discarded" -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        Snackbar.make(view, result, Snackbar.LENGTH_SHORT).apply {
                            animationMode = Snackbar.ANIMATION_MODE_FADE
                            setAnchorView(R.id.addMemoriesFab)
                        }.show()
                        binding.recyclerviewMemories.isVisible = false
                        delay(300)
                        recyclerViewDisplay()
                        binding.recyclerviewMemories.isVisible = true
                    }
                }
            }
        }

        //sets up RecyclerView
        recyclerViewDisplay()
        swipeToDelete(binding.recyclerviewMemories)

        //implements search function
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                binding.noData.isVisible = false
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    binding.clearText.visibility = View.VISIBLE
                    val text = s.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        memoriesViewModel.searchMemories(query).observe(viewLifecycleOwner) {
                            memoriesAdapter.submitList(it)
                        }
                    } else {
                        observerDataChanges()
                    }
                } else {
                    observerDataChanges()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    binding.clearText.visibility = View.GONE
                }
            }

        })

        binding.search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        binding.clearText.setOnClickListener {
            clearTxtFunction()
            it.isVisible = false
            binding.noData.isVisible = false
        }



        binding.addMemoriesFab.setOnClickListener {
            binding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(MemoriesFragmentDirections.actionMemoriesFragmentToMemoriesContentFragment())
        }
        binding.innerFab.setOnClickListener {
            navController.navigate(MemoriesFragmentDirections.actionMemoriesFragmentToMemoriesContentFragment())
        }



        binding.recyclerviewMemories.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    binding.chatFabText.isVisible = false

                }
                scrollX == scrollY -> {
                    binding.chatFabText.isVisible = true

                }
                else -> {
                    binding.chatFabText.isVisible = true
                }
            }
        }
    } //onViewCreated closed

    private fun recyclerViewDisplay() {
        @SuppressLint("SwitchIntDef")
        when (resources.configuration.orientation) {
            ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.recyclerviewMemories.apply {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            memoriesAdapter = MemoriesAdapter()
            memoriesAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = memoriesAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()
    }

    private fun observerDataChanges() {
        memoriesViewModel.getAllMemories().observe(viewLifecycleOwner) { list ->
            binding.noData.isVisible = list.isEmpty()
            memoriesAdapter.submitList(list)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {

        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = memoriesAdapter.currentList[position]
                var actionBtnTapped = false
                memoriesViewModel.deleteMemories(note)
                binding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if (binding.search.text.toString().isEmpty()) {
                    observerDataChanges()
                }
                val snackBar = Snackbar.make(
                    requireView(), "Note Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (!actionBtnTapped) {
                            (note?.imagePath?.isNotEmpty()) -> {
                                val toDelete = File(note.imagePath)
                                if (toDelete.exists()) {
                                    toDelete.delete()
                                }
                            }
                        }
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO") {
                            memoriesViewModel.saveMemories(note)
                            binding.noData.isVisible = false
                            actionBtnTapped = true

                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.addMemoriesFab)
                }
                snackBar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellow
                    )
                )
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun clearTxtFunction() {
        binding.search.apply {
            text.clear()
            hideKeyboard()
            clearFocus()
            observerDataChanges()
        }
    }


}