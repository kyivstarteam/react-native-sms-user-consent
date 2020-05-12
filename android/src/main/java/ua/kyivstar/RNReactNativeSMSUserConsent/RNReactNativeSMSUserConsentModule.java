package ua.kyivstar.RNReactNativeSMSUserConsent;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;


public class RNReactNativeSMSUserConsentModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private Promise promise;
    private SmsRetrieveBroadcastReceiver receiver;
    private static final String E_OTP_ERROR = "E_OTP_ERROR";
    private static final String RECEIVED_OTP_PROPERTY = "receivedOtpMessage";
    public static final int SMS_CONSENT_REQUEST = 1244;

    public RNReactNativeSMSUserConsentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "RNReactNativeSMSUserConsent";
    }

   @ReactMethod
   public void listenOTP(final Promise promise) {
       unregisterReceiver();

       if (this.promise != null) {
           promise.reject(E_OTP_ERROR, new Error("Reject previous request"));
       }

       this.promise = promise;
       Task<Void> task = SmsRetriever.getClient(reactContext.getCurrentActivity()).startSmsUserConsent(null);
       task.addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               // successfully started an SMS Retriever for one SMS message
               registerReceiver();
           }
       });
       task.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               promise.reject(E_OTP_ERROR, e);
           }
       });
   }

    @ReactMethod
    public void removeOTPListener() {
        unregisterReceiver();
    }

    private void registerReceiver() {
        receiver = new SmsRetrieveBroadcastReceiver(reactContext.getCurrentActivity());
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        reactContext.getCurrentActivity().registerReceiver(receiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (receiver != null) {
            reactContext.getCurrentActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            switch (requestCode) {
                case SMS_CONSENT_REQUEST:
                    unregisterReceiver();
                    if (resultCode == RESULT_OK) {
                        // Get SMS message content
                        String message = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        WritableMap map = Arguments.createMap();
                        map.putString(RECEIVED_OTP_PROPERTY, message);
                        promise.resolve(map);
                    } else {
                        promise.reject(E_OTP_ERROR, new Error("Result code: " + resultCode));
                    }
                    promise = null;
                    break;
            }
        }
    };
}
