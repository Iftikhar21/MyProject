package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: GuestDatabaseHelper
    private lateinit var guestAdapter: GuestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = GuestDatabaseHelper(this)
        guestAdapter = GuestAdapter(db.getAllGuest(), this)

        binding.guestRecycle.layoutManager = LinearLayoutManager(this)
        binding.guestRecycle.adapter = guestAdapter

        binding.addButton.setOnClickListener{
            val intent = Intent(this,AddGuestActivity::class.java)
            startActivity(intent)
        }

        binding.aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        guestAdapter.refreshData(db.getAllGuest())
    }
}