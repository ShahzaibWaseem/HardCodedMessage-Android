package com.shahzaib.message

import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shahzaib.message.databinding.MessageActivityBinding

class MessageActivity: AppCompatActivity(){
    private val smsManager = SmsManager.getDefault() as SmsManager
    private lateinit var binding: MessageActivityBinding
    private val SEND_SMS_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val phoneNumber: String = "+923075354771"
        binding.sendingNumberTextView.text = phoneNumber

        binding.sendMessageButton.setOnClickListener{
            val text: String = "Welcome to C.H.I."
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED)
                smsManager.sendTextMessage(phoneNumber, null, text, null, null)
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),
                    SEND_SMS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }
}