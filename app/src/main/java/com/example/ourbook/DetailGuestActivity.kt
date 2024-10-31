package com.example.ourbook

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ourbook.databinding.ActivityDetailGuestBinding

class DetailGuestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailGuestBinding
    private lateinit var db: GuestDatabaseHelper
    private var guestId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = GuestDatabaseHelper(this)

        // Get the guest ID from the intent
        guestId = intent.getIntExtra("guest_id", -1)

        // Check if we received a valid ID
        if (guestId == -1) {
            finish() // Close the activity if no valid ID was passed
            return
        }

        // Get guest data and populate the views
        try {
            val guest = db.getGuestById(guestId)

            // Check if guest photo is not null before trying to decode it
            guest.photo?.let { photoBytes ->
                val bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                binding.viewPhoto.setImageBitmap(bitmap)
            }

            // Set the text views with null safety
            binding.viewName.text = guest.name
            binding.viewNickname.text = guest.nickname
            binding.viewEmail.text = guest.email
            binding.viewAddress.text = guest.address
            binding.viewBirth.text = guest.birth
            binding.viewNumber.text = guest.number

        } catch (e: Exception) {
            // Handle any errors that might occur when fetching or displaying the data
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}