declare module '@kyivstarteam/react-native-sms-user-consent' {
  export interface SmsResponse {
    receivedOtpMessage: string;
  }
  class SmsUserConsent {
    static listenOTP(): Promise<SmsResponse>;
    static removeOTPListener(): Promise<void>;
  }

  export default SmsUserConsent;
}
