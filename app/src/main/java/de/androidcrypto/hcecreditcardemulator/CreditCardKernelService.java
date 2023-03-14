package de.androidcrypto.hcecreditcardemulator;

import android.content.Context;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;
import de.androidcrypto.hcecreditcardemulator.models.Aids;

/**
 * This is a system service that acts as an NFC service for Host Card Emulation (HCE)
 * It emulates the behaviour of a credit card and accept the same commands and responds
 * with the same data as an original credit card.
 *
 * You need to set this app as primary payment app in your Android's device settings:
 * connections -> NFC -> Tap and pay -> tab "payments" select "HCE"
 * Very often the GooglePay service is selected and you have to change it to HCE
 * Doing this will cancel your ability to use GooglePay on this device. To change the
 * behavior back to GooglePay simply go to settings and select GooglePay.
 *
 */



public class CreditCardKernelService extends HostApduService {

    private static final String TAG = "CCKernel";
    private static final String KERNEL_VERSION = "1.0";
    // value are setup here as static, next version allows to read them from storage
    private static final String SELECT_PPSE_COMMAND = "00a404000e325041592e5359532e444446303100";
    private static final String SELECT_PPSE_RESPONSE = "6f2b840e325041592e5359532e4444463031a519bf0c1661144f07a00000000310109f0a080001050100000000";
    private static final String SELECT_AID_COMMAND = "00a4040007a000000003101000";
    private static final String SELECT_AID_RESPONSE = "6f5d8407a0000000031010a5525010564953412044454249542020202020208701029f38189f66049f02069f03069f1a0295055f2a029a039c019f37045f2d02656ebf0c1a9f5a0531082608269f0a080001050100000000bf6304df200180";

    private final byte[] SELECT_OK_SW = hexToBytes("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private final byte[] UNKNOWN_CMD_SW = hexToBytes("0000");

    // this is vor the next step - load an individual file
    private final String FILENAME = "lloyds visa.json";
    private Aids aids; // will contain all data from the card

    public CreditCardKernelService() {
        // init for the service
        Context context = getBaseContext();
        if (context == null) {
            System.out.println("CONTEXT IS NULL");
        } else {
            System.out.println("CONTEXT IS NOT NULL");
        }
        LoadEmulatorData led = new LoadEmulatorData(context);
        aids = led.getAidsFromInternalStorage(FILENAME);
    }

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        // is called when a new commandApdu comes in
        log("received: " + bytesToHex(bytes));
        System.out.println("received: " + bytesToHex(bytes));


        log("return UNKNOWN_CMD_SW");
        System.out.println("return UNKNOWN_CMD_SW");
        return UNKNOWN_CMD_SW;
    }


    @Override
    public void onDeactivated(int i) {
        // is called when the connection between this device and a NFC card reader ends
        log("received deactivated message with code: " + i);
    }


    /**
     * section for conversions
     */

    /**
     * converts a byte array to a hex encoded string
     * @param bytes
     * @return hex encoded string
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    /**
     * converts a hex encoded string to a byte array
     * @param str
     * @return
     */
    public static byte[] hexToBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }

    /**
     * section for loggin
     */

    private void log(String message) {
        Log.i(TAG, getTimestampMillis() + " " + message);
    }

    private String getTimestampMillis() {
        // O = SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ZonedDateTime
                    .now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("uuuu.MM.dd HH:mm:ss.SSS"));
        } else {
            return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
        }
    }
}
