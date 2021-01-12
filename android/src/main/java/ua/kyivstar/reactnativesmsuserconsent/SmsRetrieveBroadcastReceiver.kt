package ua.kyivstar.reactnativesmsuserconsent

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

class SmsRetrieveBroadcastReceiver(currentActivity: Activity?): BroadcastReceiver() {
  val SMS_CONSENT_REQUEST = 1244

  private var activity: Activity? = currentActivity

  override fun onReceive(context: Context?, intent: Intent) {
    val extras = intent.extras
    if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action && extras != null) {
      val smsRetrieverStatus: Status = extras[SmsRetriever.EXTRA_STATUS] as Status
      
      when (smsRetrieverStatus.statusCode) {
        CommonStatusCodes.SUCCESS ->                     // Get consent intent
          try {
            val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
            activity!!.startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
          } catch (e: ActivityNotFoundException) {
            // Handle the exception ...
          }
        CommonStatusCodes.TIMEOUT -> {
        }
      }
    }
  }
}
