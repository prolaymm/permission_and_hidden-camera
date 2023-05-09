package com.prolaymm.readcontact

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.app.appsearch.SetSchemaRequest.READ_CONTACTS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.util.Size
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

import com.cottacush.android.hiddencam.*
import java.io.File

class MainActivity : AppCompatActivity(), OnImageCapturedListener {

    val TAG = "OneShotFragment"
    lateinit var  contactButton: Button

    private lateinit var hiddenCam: HiddenCam
    private lateinit var baseStorageFolder: File
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactButton = findViewById(R.id.contacy_button)

        contactButton.setOnClickListener{


            val permission = android.Manifest.permission.READ_CONTACTS
            singlePermissionLauncher.launch(permission)
      //   var names =    getNamePhoneDetails()
           // Log.d("contact",names.toString())
        }

        baseStorageFolder = File(this.getExternalFilesDir(null), "HiddenCam").apply {
            if (exists()) deleteRecursively()
            mkdir()
        }
        hiddenCam = HiddenCam(
            this, baseStorageFolder, this,
            targetResolution = Size(1080, 1920)
        )

        contactButton.setOnClickListener {
            hiddenCam.captureImage()
        }
    }

    override fun onStart() {
        super.onStart()
        hiddenCam.start()
    }

    @SuppressLint("Range")
    fun getNamePhoneDetails(): MutableList<Contact> {
        val names :  MutableList<Contact> = mutableListOf()
        val cr = contentResolver
        val cur = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null)
        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                names.add(Contact(id , name , number))
            }
        }
        return names
    }

    private  val singlePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted ->
        Log.d("permissiomn", "is grandted $isGranted")
        if(isGranted) {

               var names =    getNamePhoneDetails()
             Log.d("contact",names.toString())
        } else {

            Toast.makeText(this,"Permssion denied",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onImageCaptureError(e: Throwable?) {
        e?.run {
            val message = "Image captured failed:${e.message}"
            showToast(message)
            log(message)
            printStackTrace()
        }
    }

    override fun onImageCaptured(image: File) {
        val message = "Image captured, saved to:${image.absolutePath}"
        Log.d("capture","$message")

        Toast.makeText(this,"message is $message ",Toast.LENGTH_LONG ).show()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    private fun log(message: String) {
        Log.d(TAG, message)
    }

}
data class Contact(
    val id : String? ,
    val name : String?,
    val number : String?)