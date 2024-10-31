package com.example.ourbook

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbook.databinding.ActivityUpdateGuestBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateGuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateGuestBinding
    private lateinit var db: GuestDatabaseHelper
    private var guestId: Int = -1

    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.viewUpdatePhoto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = GuestDatabaseHelper(this)

        binding.addUpdatePhoto.setOnClickListener {
            var avatar = 0
            if (avatar == 0) {
                if (!checkCameraPermission()) {
                    requstCameraPersmission()
                } else {
                    pickFromGallery()
                }
            } else if (avatar == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }

        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month,
                                                              dayofmonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayofmonth)
            UpdateLable(calendar, binding.updateBirthTxt)
        }

        binding.updateBirthTxt.setOnClickListener {
            DatePickerDialog(this,datePicker, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        guestId = intent.getIntExtra("guest_id", -1)
        if (guestId == -1) {
            finish()
            return
        }

        val guest = db.getGuestById(guestId)

        val img = guest.photo
        val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

        binding.updateNameTxt.setText(guest.name)
        binding.updateNicknameTxt.setText(guest.nickname)
        binding.updateEmailTxt.setText(guest.email)
        binding.updateAddressTxt.setText(guest.address)
        binding.updateBirthTxt.setText(guest.birth)
        binding.updateNumberTxt.setText(guest.number)
        binding.viewUpdatePhoto.setImageBitmap(bitmap)

        binding.updateSaveBtn.setOnClickListener {

            val save = AlertDialog.Builder(this)
            save.setTitle("Save Guest")
            save.setMessage("Are You Sure to Save the Update? (Data Can be Change)")
            save.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            save.setPositiveButton("Yes") { dialog, _ ->
                val newName = binding.updateNameTxt.text.toString()
                val newNickname = binding.updateNicknameTxt.text.toString()
                val newEmail = binding.updateEmailTxt.text.toString()
                val newAddress = binding.updateAddressTxt.text.toString()
                val newBirth = binding.updateBirthTxt.text.toString()
                val newNumber = binding.updateNumberTxt.text.toString()

                val updateGuest = Guest(
                    guestId,
                    newName,
                    newNickname,
                    newEmail,
                    newAddress,
                    newBirth,
                    newNumber,
                    ImageViewToByte(binding.viewUpdatePhoto)
                )
                db.updateGuest(updateGuest)
                Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }
            save.show()

            }
    }

    private fun UpdateLable(calendar: Calendar, birthTxt: EditText) {
        val format = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(format, Locale.UK)
        binding.updateBirthTxt.setText(sdf.format(calendar.time))
    }

    private fun requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun pickFromGallery() {
        cropImageLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }

    private fun requstCameraPersmission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        return result && result2
    }

    fun ImageViewToByte(img: ImageView): ByteArray {
        val bitmap: Bitmap = (img.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val bytes: ByteArray = stream.toByteArray()
        return bytes }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Enable Camera and Storage Permissions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    val storegaAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storegaAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}