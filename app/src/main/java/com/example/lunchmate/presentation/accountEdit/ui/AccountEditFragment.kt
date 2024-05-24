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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.databinding.BottomSheetAddPhotoBinding
import com.example.lunchmate.databinding.FragmentAccountEditBinding
import com.example.lunchmate.domain.api.ApiHelper
import com.example.lunchmate.domain.api.LoadingState
import com.example.lunchmate.domain.api.RetrofitBuilder
import com.example.lunchmate.domain.model.User
import com.example.lunchmate.domain.model.UserPatch
import com.example.lunchmate.domain.api.Status
import com.example.lunchmate.presentation.account.ui.AccountFragment
import com.example.lunchmate.presentation.account.viewModel.AccountViewModel
import com.example.lunchmate.presentation.account.viewModel.AccountViewModelFactory
import com.example.lunchmate.presentation.accountEdit.viewModel.AccountEditViewModel
import com.example.lunchmate.presentation.accountEdit.viewModel.AccountEditViewModelFactory
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
    private lateinit var accountEditViewModel: AccountEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountEditViewModel = ViewModelProvider(
            requireActivity(), AccountEditViewModelFactory(
                ApiHelper(
                    RetrofitBuilder.apiService
                )
            )
        )[AccountEditViewModel::class.java]

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
        initialiseObservers()
        initialiseUIElements()
    }

    private fun initialiseUIElements() {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.GONE

        val activity = activity as MainActivity

        binding.backButton.setOnClickListener {
            setCurrentFragment(AccountFragment())
        }

        binding.changePhotoButton.setOnClickListener {
            val dialog = AddPhotoBottomSheet(::openGalleryForImage, ::openCameraForImage)
            dialog.show((activity as MainActivity).supportFragmentManager, "")
        }

        binding.saveButton.setOnClickListener {
            if (emptyFieldsCheck()) {
                accountEditViewModel.patchUser(
                    "id1", UserPatch(
                        binding.edittextName.text.toString(),
                        binding.edittextTg.text.toString(),
                        binding.edittextTaste.text.toString().ifEmpty { "Без предпочтений" },
                        binding.edittextInfo.text.toString().ifEmpty { "Без информации" },
                        activity.offices[binding.spinnerOffice.selectedItemPosition].id
                    )
                )
                setCurrentFragment(AccountFragment())
            }
        }
    }

    private fun initialiseObservers() {
        accountEditViewModel.getUser("id1")

        accountEditViewModel.accountData.observe(viewLifecycleOwner) {
            setUpCurrentUser(it)
        }

        accountEditViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
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
        else if (!binding.edittextTg.text.toString().trim().matches(Regex("""[a-zA-Z0-9_]+"""))){
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

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.shimmerPhoto.visibility = View.GONE
                binding.photo.visibility = View.VISIBLE
            }
            LoadingState.LOADING -> {
                binding.shimmerPhoto.visibility = View.VISIBLE
                binding.photo.visibility = View.INVISIBLE
            }
            LoadingState.ERROR -> {
                binding.shimmerPhoto.visibility = View.GONE
                binding.photo.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error Occurred!", Toast.LENGTH_LONG).show()
            }
        }
    }
}