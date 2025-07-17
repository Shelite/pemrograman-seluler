package com.example.pandora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Intent
import android.net.Uri
import android.app.Activity
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

class DatabaseHelper(context:Context): SQLiteOpenHelper(
    context,DATABASE_NAME,null,DATABASE_VERSION
) {
companion object{
    private val DATABASE_NAME = "Pandora"
    private val DATABASE_VERSION = 4
    private val TABLE_ACCOUNT = "account"
    private val COLUMN_EMAIL = "email"
    private val COLUMN_NAME = "name"
    private val COLUMN_LEVEL = "level"
    private val COLUMN_PASSWORD = "password"

    private val TABLE_IMAGE = "uploaded_images"
    private val COLUMN_IMAGE_ID = "id"
    private val COLUMN_IMAGE_PATH = "image_path"
    private val COLUMN_IMAGE_NAME = "image_name"

    }
    private val CREATE_ACCOUNT_TABLE = (
        "CREATE TABLE " + TABLE_ACCOUNT + " (" +
        COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
        COLUMN_NAME + " TEXT, " +
        COLUMN_LEVEL + " TEXT, " +
        COLUMN_PASSWORD + " TEXT)"
    )
    private val DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS $TABLE_ACCOUNT"

    private val CREATE_IMAGE_TABLE = (
        "CREATE TABLE " + TABLE_IMAGE + " (" +
        COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_IMAGE_PATH + " TEXT, " +
        COLUMN_IMAGE_NAME + " TEXT)"
    )

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DROP_ACCOUNT_TABLE)
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_IMAGE")
        onCreate(p0)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_ACCOUNT_TABLE)
        db?.execSQL(CREATE_IMAGE_TABLE)
    }

    fun insertAccount(email: String, name: String, password: String, level: String = "user"): Boolean {
        val db = this.writableDatabase
        val contentValues = android.content.ContentValues()
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_LEVEL, level)
        contentValues.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_ACCOUNT, null, contentValues)
        db.close()
        return result != -1L
    }

    fun checkLogin(email:String, password:String):Boolean{
        val colums = arrayOf(COLUMN_NAME)
        val db = this.readableDatabase

        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"

        val selectionArgs = arrayOf(email,password)
        val cursor = db.query(TABLE_ACCOUNT,
            colums,
            selection,
            selectionArgs,
            null,
            null,
            null)
        val cursorCount = cursor.count
        cursor.close()
        db.close()
        if(cursorCount > 0)
            return true
        else
            return false
    }
    fun isEmailExist(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_ACCOUNT, arrayOf(COLUMN_EMAIL), "$COLUMN_EMAIL = ?", arrayOf(email), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isPasswordCorrect(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNT,
            arrayOf(COLUMN_EMAIL),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )
        val correct = cursor.count > 0
        cursor.close()
        db.close()
        return correct
    }

    fun insertImage(imagePath: String, imageName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = android.content.ContentValues()
        contentValues.put(COLUMN_IMAGE_PATH, imagePath)
        contentValues.put(COLUMN_IMAGE_NAME, imageName)
        val result = db.insert(TABLE_IMAGE, null, contentValues)
        db.close()
        return result != -1L
    }

    fun getLastImagePath(): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_IMAGE,
            arrayOf(COLUMN_IMAGE_PATH),
            null, null, null, null,
            "$COLUMN_IMAGE_ID DESC",
            "1"
        )
        var path: String? = null
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
        }
        cursor.close()
        db.close()
        return path
    }

    fun getAllImages(): List<Pair<Int, Pair<String, String>>> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_IMAGE,
            arrayOf(COLUMN_IMAGE_ID, COLUMN_IMAGE_PATH, COLUMN_IMAGE_NAME),
            null, null, null, null,
            "$COLUMN_IMAGE_ID DESC"
        )
        val images = mutableListOf<Pair<Int, Pair<String, String>>>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_ID))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_NAME))
                images.add(Pair(id, Pair(path, name)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return images
    }

    fun updateImageName(id: Int, newName: String): Boolean {
        val db = this.writableDatabase
        val contentValues = android.content.ContentValues()
        contentValues.put(COLUMN_IMAGE_NAME, newName)
        val result = db.update(TABLE_IMAGE, contentValues, "$COLUMN_IMAGE_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun deleteImage(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_IMAGE, "$COLUMN_IMAGE_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}


