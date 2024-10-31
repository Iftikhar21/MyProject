package com.example.ourbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GuestDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_GUEST, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_GUEST = "guestapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_GUEST = "allguest"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_NICKNAME = "nickname"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_BIRTH = "birth"
        private const val COLUMN_NUMBER = "number"
        private const val COLUMN_PHOTO = "photo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_GUEST ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_NICKNAME TEXT, $COLUMN_EMAIL TEXT, " +
                "$COLUMN_ADDRESS TEXT, $COLUMN_BIRTH DATE, $COLUMN_NUMBER TEXT, $COLUMN_PHOTO BLOB)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_GUEST"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertGuest (guest: Guest) {
        val values = ContentValues().apply {
            put(COLUMN_NAME, guest.name)
            put(COLUMN_NICKNAME, guest.nickname)
            put(COLUMN_EMAIL, guest.email)
            put(COLUMN_ADDRESS, guest.address)
            put(COLUMN_BIRTH, guest.birth)
            put(COLUMN_NUMBER, guest.number)
            put(COLUMN_PHOTO, guest.photo)
        }
        val db = writableDatabase
        db.insert(TABLE_GUEST, null, values)
        db.close()
    }

    fun getAllGuest(): List<Guest> {
        val guestList = mutableListOf<Guest>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_GUEST"
        val cursor = db.rawQuery(query,null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
            val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
            val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))

            val guest = Guest(id, name, nickname, email, address, birth, number, photo)
            guestList.add(guest)
        }
        cursor.close()
        db.close()
        return guestList
    }

    fun updateGuest(guest: Guest) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, guest.name)
            put(COLUMN_NICKNAME, guest.nickname)
            put(COLUMN_EMAIL, guest.email)
            put(COLUMN_ADDRESS, guest.address)
            put(COLUMN_BIRTH, guest.birth)
            put(COLUMN_NUMBER, guest.number)
            put(COLUMN_PHOTO, guest.photo)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(guest.id.toString())
        db.update(TABLE_GUEST, values, whereClause, whereArgs)
        db.close()
    }

    fun getGuestById(guestId: Int): Guest {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_GUEST WHERE $COLUMN_ID = $guestId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
        val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
        val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))
        val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))

        cursor.close()
        db.close()
        return Guest(id, name, nickname, email, address, birth, number.toString(), photo)
    }

    fun deleteGuest(guestId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(guestId.toString())
        db.delete(TABLE_GUEST, whereClause, whereArgs)
        db.close()
    }
}