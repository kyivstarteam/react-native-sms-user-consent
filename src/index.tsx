import { NativeModules, Platform } from 'react-native';

export interface SmsResponse {
  receivedOtpMessage: string;
}

type SmsUserConsentType = {
  listenOTP(): Promise<SmsResponse>;
  removeOTPListener(): Promise<void>;
};

const { SmsUserConsent: SmsUserConsentModule } = NativeModules;

class SmsUserConsent {
  static listenOTP() {
    if (Platform.OS !== 'android') {
      return 'false';
    }
    return SmsUserConsentModule.listenOTP();
  }
  static removeOTPListener() {
    if (Platform.OS !== 'android') {
      return 'false';
    }
    return SmsUserConsentModule.removeOTPListener();
  }
}

export default SmsUserConsent as SmsUserConsentType;
