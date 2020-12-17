import { SmsResponse } from './index';

declare module '@kyivstarteam/react-native-sms-user-consent' {
  export interface SmsResponse {
    receivedOtpMessage: string;
  }
  class SmsUserConsent {
    listenOTP(): Promise<SmsResponse>;
    removeOTPListener(): Promise<void>;
  }

  export default SmsUserConsent;
}
