package com.example.ourbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class GuestAdapter(private var guest: List<Guest>, context: Context) :
    RecyclerView.Adapter<GuestAdapter.GuestViewHolder>(){

    private val db: GuestDatabaseHelper = GuestDatabaseHelper(context)

    class GuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameText)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTxt)
        val emailTextView: TextView = itemView.findViewById(R.id.emailTxt)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTxt)
        val birthTextView: TextView = itemView.findViewById(R.id.tglLahirTxt)
        val numberTextView: TextView = itemView.findViewById(R.id.numberTxt)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val detailButton: LinearLayout = itemView.findViewById(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return GuestViewHolder(view)
    }

    override fun getItemCount(): Int = guest.size

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        val guest = guest[position]

        val img = guest.photo
        val bitmap = BitmapFactory.decodeByteArray(img,0, img.size)
        holder.nameTextView.text = guest.name
        holder.imageView.setImageBitmap(bitmap)
        holder.nicknameTextView.text = guest.nickname
        holder.emailTextView.text = guest.email
        holder.addressTextView.text = guest.address
        holder.birthTextView.text = guest.birth
        holder.numberTextView.text = guest.number

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateGuestActivity::class.java).apply {
                putExtra("guest_id", guest.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            val delete = AlertDialog.Builder(holder.itemView.context)
            delete.setTitle("Delete Guest")
            delete.setMessage("Are You Sure to Delete? (Data Can't be Change)")
            delete.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            delete.setPositiveButton("Yes") { dialog, _ ->
                val db = GuestDatabaseHelper(holder.itemView.context)
                db.deleteGuest(guest.id)
                refreshData(db.getAllGuest())
                Toast.makeText(holder.itemView.context, "Guest Deleted", Toast.LENGTH_SHORT).show()
            }
            delete.show()
        }

        holder.detailButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailGuestActivity::class.java).apply {
                putExtra("guest_id", guest.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }
    fun refreshData(newGuest: List<Guest>) {
        guest = newGuest
        notifyDataSetChanged()
    }

}