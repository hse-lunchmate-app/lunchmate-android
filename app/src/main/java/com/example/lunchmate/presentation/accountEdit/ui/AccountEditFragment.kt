package com.example.lunchmate.presentation.accountEdit.ui

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetAddPhotoBinding
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.domain.model.UserPatch
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.schedule.ui.ReservedSlotBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException


class AccountEditFragment: Fragment(R.layout.fragment_account_edit) {
    private lateinit var binding: FragmentAccountEditBinding
    private val REQUEST_CODE_GALLERY = 1
    private val REQUEST_CODE_CAMERA = 2
    private var galleryPermissionLauncher: ActivityResultLauncher<String>? = null
    private var cameraPermissionLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE_GALLERY)
            } else {
                Toast.makeText(requireContext(), "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } else {
                Toast.makeText(requireContext(), "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }
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
            val dialog = AddPhotoBottomSheet(::openGalleryForImage, ::openCameraForImage)
            dialog.show((activity as MainActivity).supportFragmentManager, "")
        }

        binding.saveButton.setOnClickListener {
            if (emptyFieldsCheck()) {
                activity.viewModel.patchUser(
                    "id1", UserPatch(
                        binding.edittextName.text.toString(),
                        binding.edittextTg.text.toString(),
                        binding.edittextTaste.text.toString(),
                        binding.edittextInfo.text.toString(),
                        activity.offices[binding.spinnerOffice.selectedItemPosition].id
                    )
                ).observe(viewLifecycleOwner) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { user ->
                                    activity.currentUser = user
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                            Status.LOADING -> {

                            }
                        }
                    }
                }
                setCurrentFragment(accountFragment)
            }
        }
    }

    private fun setUpObserver() {
        val activity = activity as MainActivity
        activity.viewModel.getUser("id1").observe(viewLifecycleOwner) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryPermissionLauncher?.launch(READ_MEDIA_IMAGES)
        } else {
            galleryPermissionLauncher?.launch(READ_EXTERNAL_STORAGE)
        }
    }

    private fun openCameraForImage() {
        cameraPermissionLauncher?.launch(CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    val imageUri: Uri = data.data!!
                    //val imageStream: InputStream? = (activity as MainActivity).contentResolver?.openInputStream(imageUri)
                    //val selectedImage = BitmapFactory.decodeStream(imageStream)
                    //binding.photo.setImageBitmap(selectedImage)
                    Picasso.with(context).load(imageUri).into(binding.photo)
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

    private fun emptyFieldsCheck(): Boolean {
        var flag = true
        if (binding.edittextName.text.toString().trim().isEmpty()){
            binding.edittextName.setBackgroundResource(R.drawable.rounded_et_error)
            binding.errorMsgName.visibility = View.VISIBLE
            binding.errorMsgName.text = "Поле имени должно быть заполнено"
            binding.labelName.setTextColor(resources.getColor(R.color.red_700))
            flag = false
        }
        else{
            binding.edittextName.setBackgroundResource(R.drawable.rounded_et)
            binding.errorMsgName.visibility = View.GONE
            binding.labelName.setTextColor(resources.getColor(R.color.grey_500))
        }
        if (binding.edittextTg.text.toString().trim().isEmpty()){
            binding.edittextTg.setBackgroundResource(R.drawable.rounded_et_error)
            binding.errorMsgTg.visibility = View.VISIBLE
            binding.errorMsgTg.text = "Поле телеграма должно быть заполнено"
            binding.labelTg.setTextColor(resources.getColor(R.color.red_700))
            flag = false
        }
        else if (!binding.edittextTg.text.toString().trim().matches(Regex("""[A-Za-z0-9_]"""))){
            binding.edittextTg.setBackgroundResource(R.drawable.rounded_et_error)
            binding.errorMsgTg.visibility = View.VISIBLE
            binding.errorMsgTg.text = "Телеграм должен иметь допустимое значение"
            binding.labelTg.setTextColor(resources.getColor(R.color.red_700))
            flag = false
        }
        else{
            binding.edittextTg.setBackgroundResource(R.drawable.rounded_et)
            binding.errorMsgTg.visibility = View.GONE
            binding.labelTg.setTextColor(resources.getColor(R.color.grey_500))
        }
        return flag
    }

}