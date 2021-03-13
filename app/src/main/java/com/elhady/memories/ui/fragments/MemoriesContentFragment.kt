package com.elhady.memories.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.elhady.memories.R
import com.elhady.memories.databinding.BottomSheetDialogBinding
import com.elhady.memories.databinding.FragmentMemoriesContentBinding
import com.elhady.memories.model.Memories
import com.elhady.memories.MainActivity
import com.elhady.memories.utils.*
import com.elhady.memories.viewmodel.MemoriesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.android.synthetic.main.bottom_sheet_dialog.*
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MemoriesContentFragment : Fragment(R.layout.fragment_memories_content) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentMemoriesContentBinding
    private lateinit var result: String
    private lateinit var photoFile: File
    private var memories: Memories? = null
    private var color = -1
    private val memoriesViewModel: MemoriesViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getDateInstance().format(Date())
    private val REQUEST_IMAGE_CAPTURE = 100
    private val SELECT_IMAGE_FROM_STORAGE = 101
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: MemoriesContentFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val animation = MaterialContainerTransform().apply {
//            drawingViewId = R.id.fragment
//            scrimColor = Color.TRANSPARENT
//            duration = 300L
//            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
//        }
//        sharedElementEnterTransition = animation
//        sharedElementReturnTransition = animation
//        addSharedElementListener()
    }

//    @SuppressLint("InflateParams", "QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentMemoriesContentBinding.bind(view)

        /* Sets the unique transition name for the layout that is
         being inflated using SharedElementEnterTransition class */
        ViewCompat.setTransitionName(
            contentBinding.memoriesContentFragmentParent,
            "recyclerView_${args.memories?.id}"
        )

        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity
        registerForContextMenu(contentBinding.memoriesImage)

        contentBinding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            saveNoteAndGoBack()
        }

        try {
            contentBinding.etMemoriesContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    contentBinding.bottomBarMarkdown.visibility = View.VISIBLE
                    contentBinding.etMemoriesContent.setStylesBar(contentBinding.styleBar)
                } else contentBinding.bottomBarMarkdown.visibility = View.GONE
            }
        } catch (e: Throwable) {
            Log.d("TAG", "e.stackTraceToString()")
        }

        contentBinding.memoriesOptionsMenu.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView: View = layoutInflater.inflate(
                R.layout.bottom_sheet_dialog,
                null
            )

            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }
            val bottomSheetBinding = BottomSheetDialogBinding.bind(bottomSheetView)

            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener { value ->
                        color = value
                        contentBinding.apply {
                            memoriesContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentMemoriesContent.setBackgroundColor(color)
                            bottomBarMarkdown.setBackgroundColor(color)
                            activity.window.statusBarColor = color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            bottomSheetBinding.takePicture.setOnClickListener {
                val permission = ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {

                    val permissionArray = arrayOf(Manifest.permission.CAMERA)
                    ActivityCompat.requestPermissions(
                        activity,
                        permissionArray,
                        REQUEST_IMAGE_CAPTURE
                    )
                    ActivityCompat.OnRequestPermissionsResultCallback { requestCode,
                                                                        permissions,
                                                                        grantResults ->
                        when (requestCode) {
                            REQUEST_IMAGE_CAPTURE -> {
                                if (permissions[0] == Manifest.permission.CAMERA &&
                                    grantResults.isNotEmpty()
                                ) {
                                    Log.d("tag", "this function is called")
                                    takePictureIntent()
                                }
                            }
                        }
                    }
                }
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    takePictureIntent()
                    bottomSheetDialog.dismiss()
                }
            }
//            @Suppress("DEPRECATION")
            bottomSheetBinding.selectImage.setOnClickListener {
                Intent(Intent.ACTION_GET_CONTENT).also { chooseIntent ->
                    chooseIntent.type = "image/*"
                    chooseIntent.resolveActivity(activity.packageManager!!.also {
                        startActivityForResult(chooseIntent, SELECT_IMAGE_FROM_STORAGE)
                    })
                }
                bottomSheetDialog.dismiss()
            }
        }

        //opens with existing memo item
        setUpMemories()

        activity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveNoteAndGoBack()
                }
            })
    }

//    private fun addSharedElementListener() {
//        (sharedElementEnterTransition as Transition).addListener(
//            object : TransitionListenerAdapter() {
//                override fun onTransitionStart(transition: Transition) {
//                    super.onTransitionStart(transition)
//                    if (args.memories?.imagePath != null) {
//                        contentBinding.memoriesImage.isVisible = true
//                        val uri = Uri.fromFile(File(args.memories?.imagePath!!))
//                        job.launch {
//                            requireContext().asyncImageLoader(
//                                uri,
//                                contentBinding.memoriesImage,
//                                this
//                            )
//                        }
//                    } else contentBinding.memoriesImage.isVisible = false
//                }
//            }
//        )
//    }

    /**
     * This Method handles the save and update operation.
     *
     * Checks if the memo arg is null
     * It will save the memo with a unique id.
     *
     * If memo arg has data it will update
     * memo to save any changes. */
    private fun saveNoteAndGoBack() {

        if (contentBinding.etTitle.text.toString().isEmpty() &&
            contentBinding.etMemoriesContent.text.toString().isEmpty()
        ) {
            result = "Empty Memo Discarded"
            setFragmentResult("key", bundleOf("bundleKey" to result))
            navController.navigate(
                MemoriesContentFragmentDirections
                    .actionMemoriesContentFragmentToMemoriesFragment()
            )

        } else {
            memories = args.memories
            when (memories) {
                null -> {
                    memoriesViewModel.saveMemories(
                        Memories(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etMemoriesContent.getMD(),
                            currentDate,
                            color,
                            memoriesViewModel.setImagePath()
                        )
                    )
                    result = "Memories Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    navController.navigate(
                        MemoriesContentFragmentDirections
                            .actionMemoriesContentFragmentToMemoriesFragment()
                    )

                }
                else -> {
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if (memories != null) {
            memoriesViewModel.updateMemories(
                Memories(
                    memories!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etMemoriesContent.getMD(),
                    currentDate,
                    color,
                    memoriesViewModel.setImagePath()
                )
            )
        }
    }

//    @SuppressLint("QueryPermissionsNeeded")
//    @Suppress("DEPRECATION")
    private fun takePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { captureIntent ->
            photoFile = getPhotoFile(requireActivity())
            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                getString(R.string.fileAuthority),
                photoFile
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            captureIntent.resolveActivity(activity?.packageManager!!.also {
                startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE)
            })
        }
    }

    private fun menuIconWithText(r: Drawable, title: String): CharSequence {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("   $title")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun setUpMemories() {
        val memo = args.memories
        val title = contentBinding.etTitle
        val content = contentBinding.etMemoriesContent
        val lastEdited = contentBinding.lastEdited
        val savedImage = memoriesViewModel.setImagePath()

        if (memo == null) {
            lastEdited.text =
                getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
            setImage(memoriesViewModel.setImagePath())
        }

        if (memo != null) {
            title.setText(memo.title)
            content.renderMD(memo.content)
            lastEdited.text = getString(R.string.edited_on, memo.date)
            color = memo.color
            if (savedImage != null) setImage(savedImage)
            else memoriesViewModel.saveImagePath(memo.imagePath)
            contentBinding.apply {
                job.launch {
                    delay(10)
                    memoriesContentFragmentParent.setBackgroundColor(color)
                    memoriesImage.isVisible = true
                }
                toolbarFragmentMemoriesContent.setBackgroundColor(color)
                bottomBarMarkdown.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor = memo.color
        }
    }

    /**
     * This method gets a filePath as a string and converts it into URI
     * then passes that URI and the target imageView to and extension function
     * loadImage that will the image to its given target*/
    private fun setImage(filePath: String?) {
        if (filePath != null) {
            val uri = Uri.fromFile(File(filePath))
            contentBinding.memoriesImage.isVisible = true
            try {
                job.launch {
                    requireContext().asyncImageLoader(uri, contentBinding.memoriesImage, this)
                }
            } catch (e: Exception) {
                context?.shortToast(e.message)
                contentBinding.memoriesImage.isVisible = false
            }
        } else contentBinding.memoriesImage.isVisible = false
    }

//    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            memoriesViewModel.saveImagePath(photoFile.absolutePath)
            setImage(photoFile.absolutePath)
        }
        if (requestCode == SELECT_IMAGE_FROM_STORAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            Log.d("Tag", uri.toString())
            if (uri != null) {
                val selectedImagePath = getImageUrlWithAuthority(
                    requireContext(),
                    uri,
                    requireActivity()
                )
                memoriesViewModel.saveImagePath(selectedImagePath)
                setImage(selectedImagePath)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(
            0,
            1,
            1,
            menuIconWithText(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_delete
                )!!, getString(R.string.delete)
            )
        )
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                if (memories?.imagePath != null) {
                    val toDelete = File(memories?.imagePath!!)
                    if (toDelete.exists()) {
                        toDelete.delete()
                    }
                }
                if (memoriesViewModel.setImagePath() != null) {
                    val toDelete = File(memoriesViewModel.setImagePath()!!)
                    if (toDelete.exists()) {
                        toDelete.delete()
                    }
                    memoriesViewModel.saveImagePath(null)
                }

                contentBinding.memoriesImage.isVisible = false
                updateNote()
                context?.shortToast("Deleted")
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (job.isActive) {
            job.cancel()
        }
    }


}