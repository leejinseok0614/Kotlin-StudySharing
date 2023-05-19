package org.studysharing.studysharing

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import org.studysharing.studysharing.databinding.ActivityAddPhotoBinding
import org.studysharing.studysharing.model.PhotoContentDTO
import org.studysharing.studysharing.util.BaseActivity
import org.studysharing.studysharing.viewmodel.PhotoContentViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : BaseActivity() {

    private val TAG = "AddPhotoActivity"

    private lateinit var binding: ActivityAddPhotoBinding
    private val vm: PhotoContentViewModel by viewModels()

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

        // Open the Album or Add photo
        binding.addPhotoBtn.setOnClickListener {
            when (binding.addPhotoBtn.text) {
                getString(R.string.pick_image) -> {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                    photoPickerResult.launch(photoPickerIntent)
                }

                getString(R.string.upload_image) -> {
                    binding.progressBar.visibility = View.VISIBLE
                    uploadPhoto()
                }
            }
        }

        observeResult()
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadPhoto() {
        // Make file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "IMAGE_${timeStamp}_.png"

        // Upload file
        photoUri?.let { uri ->
            val contentDTO = PhotoContentDTO().apply {
                this.uid = MyApplication.auth?.currentUser?.uid
                this.userId = MyApplication.auth?.currentUser?.email
                this.explain = binding.addPhotoEt.text.toString()
                this.timestamp = System.currentTimeMillis()
            }
            vm.insert(fileName, uri, contentDTO)
        }
    }


    private fun observeResult() {
        vm.resultLiveData.observe(this) {
            println("xxx observeResult($it)")
            if (it) {
                binding.progressBar.visibility = View.GONE
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private val photoPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if (ar.resultCode == RESULT_OK) {
            photoUri = ar.data?.data
            photoUri?.let { uri ->
                binding.addPhotoBtn.text = getString(R.string.upload_image)
                Glide.with(this).load(uri).into(binding.addPhotoImg)
            }
        } else {
            Log.e(TAG, "xxx photoPickerResult error(${ar.resultCode})")
            Snackbar.make(binding.addPhotoBtn, getString(R.string.upload_fail), 1500).show()
        }
    }
}