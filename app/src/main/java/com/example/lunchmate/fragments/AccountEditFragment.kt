package com.example.lunchmatelocal

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetAddPhotoBinding
import com.example.lunchmate.databinding.BottomSheetFreeSlotBinding
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.model.User
import com.example.lunchmate.model.UserPatch
import com.example.lunchmate.utils.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*


class AccountEditFragment: Fragment(R.layout.fragment_account_edit) {
    private lateinit var binding: FragmentAccountEditBinding
    private val REQUEST_CODE_GALLERY = 1
    private val REQUEST_CODE_CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountEditBinding.bind(view)
        val accountFragment = AccountFragment()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.GONE

        val activity = activity as MainActivity
        setUpObserver()

        binding.backButton.setOnClickListener {
            setCurrentFragment(accountFragment)
        }

        binding.changePhotoButton.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
            val bottomBinding = BottomSheetAddPhotoBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_add_photo, null))

            bottomBinding.gallery.setOnClickListener {
                openGalleryForImage()
                dialog.dismiss()
            }

            bottomBinding.camera.setOnClickListener {
                openCameraForImage()
                dialog.dismiss()
            }

            dialog.setContentView(bottomBinding.root)
            dialog.show()
        }

        binding.saveButton.setOnClickListener {
            activity.viewModel.patchUser("1", UserPatch(
                binding.edittextName.text.toString(),
                binding.edittextTg.text.toString(),
                binding.edittextTaste.text.toString(),
                binding.edittextInfo.text.toString(),
                activity.offices[binding.spinnerOffice.selectedItemPosition].id
            )).observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { user ->
                                activity.currentUser = user
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
            setCurrentFragment(accountFragment)
        }
    }

    private fun setUpObserver() {
        val activity = activity as MainActivity
        activity.viewModel.getUser("1").observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { user ->
                            activity.currentUser = user
                            setUpCurrentUser(activity.currentUser) }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun setUpCurrentUser(currentUser: User) {
        //binding.photo.setImageResource(currentUser.getPhoto())
        binding.edittextName.setText(currentUser.name)
        binding.edittextLogin.setText("@"+currentUser.login)
        binding.edittextTg.setText(currentUser.messenger)
        binding.edittextTaste.setText(currentUser.tastes)
        binding.edittextInfo.setText(currentUser.aboutMe)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, (activity as MainActivity).officeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOffice.adapter = adapter
        binding.spinnerOffice.setSelection((activity as MainActivity).offices.indexOf(currentUser.office))
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private fun openCameraForImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    val imageUri: Uri = data.data!!
                    val imageStream: InputStream? = (activity as MainActivity).contentResolver?.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding.photo.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Что-то пошло не так...", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Вы не выбрали фото", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    val photo = data.extras!!["data"] as Bitmap
                    binding.photo.setImageBitmap(photo)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Что-то пошло не так...", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Вы не сделали фото", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFragment, fragment)
            commit()
        }

}