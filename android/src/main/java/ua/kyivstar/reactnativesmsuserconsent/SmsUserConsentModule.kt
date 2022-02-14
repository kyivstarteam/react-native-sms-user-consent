package ua.kyivstar.reactnativesmsuserconsent

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import com.facebook.react.bridge.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


class SmsUserConsentModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private val reactContext: ReactApplicationContext? = reactContext
  private var promise: Promise? = null
  private var receiver: SmsRetrieveBroadcastReceiver? = null
  private val E_OTP_ERROR = "E_OTP_ERROR"
  private val RECEIVED_OTP_PROPERTY = "receivedOtpMessage"
  val SMS_CONSENT_REQUEST = 1244

  override fun initialize() {
    super.initialize()
    reactContext?.addActivityEventListener(mActivityEventListener)
  }

  override fun getName(): String {
    return "SmsUserConsent"
  }

  @ReactMethod
  fun listenOTP(promise: Promise) {
    if (this.promise != null) {
      promise.reject(E_OTP_ERROR, Error("Reject previous request"))
    }
    this.promise = promise
    if (reactContext?.currentActivity != null) {
      val task: Task<Void> = SmsRetriever.getClient(reactContext.currentActivity!!).startSmsUserConsent(null)

      task.addOnSuccessListener { // successfully started an SMS Retriever for one SMS message
        registerReceiver()
      }
      task.addOnFailureListener { e -> promise.reject(E_OTP_ERROR, e) }
    }
  }

  @ReactMethod
  fun removeOTPListener() {
    unregisterReceiver()
    promise = null
  }

  private fun registerReceiver() {
    if (reactContext?.currentActivity != null) {
      receiver = SmsRetrieveBroadcastReceiver(reactContext.currentActivity)
      val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
      reactContext.currentActivity?.registerReceiver(receiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)
    }
  }

  private fun unregisterReceiver() {
    if (receiver != null) {
      reactContext?.currentActivity?.unregisterReceiver(receiver)
      receiver = null
    }
  }

  private val mActivityEventListener: ActivityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, intent: Intent?) {
      when (requestCode) {
        SMS_CONSENT_REQUEST -> {
          unregisterReceiver()
          if (resultCode == RESULT_OK  && intent != null) {
            // Get SMS message content
            val message = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            val map = Arguments.createMap()
            map.putString(RECEIVED_OTP_PROPERTY, message)
            promise?.resolve(map)
          } else {
            promise?.reject(E_OTP_ERROR, Error("Result code: $resultCode"))
          }
          promise = null
        }
      }
    }
  }

}
