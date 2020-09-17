package com.shahzaib.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val messages: Array<SmsMessage?> = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            Toast.makeText(context, message!!.displayOriginatingAddress + ": " + message.messageBody, Toast.LENGTH_LONG).show()
        }
    }
}
