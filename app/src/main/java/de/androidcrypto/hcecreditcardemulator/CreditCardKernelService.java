package de.androidcrypto.hcecreditcardemulator;

import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;
import de.androidcrypto.hcecreditcardemulator.models.Aid;
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

    private final byte[] SELECT_OK_SW = hexToBytes("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private final byte[] UNKNOWN_CMD_SW = hexToBytes("0000");

    // this is vor the next step - load an individual file
    private final String CARDS_FOLDER = "cards";
    private final String FILENAME = "lloyds visa.json";
    private Aids aids; // will contain all data from the card

    enum Status {
        NO_SELECT,
        PPSE_SELECTED,
        AID_SELECTED,
        GPO_DONE
    }
    private int foundAid = -1; // no AID selection
    Status cardStatus = Status.NO_SELECT;
    // Aids model
    private String CARD_NAME;
    private String CARD_TYPE;
    private static byte[] SELECT_PPSE_COMMAND;
    private static byte[] SELECT_PPSE_RESPONSE;
    private int NUMBER_OF_AID;
    private Aid[] AID_MODEL;
    private byte[][] SELECT_AID_COMMAND;
    private byte[][] SELECT_AID_RESPONSE;
    private byte[][] GET_PROCESSING_OPTIONS_COMMAND;
    private byte[][] GET_PROCESSING_OPTIONS_RESPONSE;

    public CreditCardKernelService() {
        // init for the service
        LoadEmulatorData led = new LoadEmulatorData(null);
        aids = led.getAidsFromInternalStorage(FILENAME);
        //aids = getAidsFromInternalStorage(FILENAME);
        initCardData();
    }

    @Override
    public byte[] processCommandApdu(byte[] receivedBytes, Bundle bundle) {
        // is called when a new commandApdu comes in
        log("command received: " + bytesToHex(receivedBytes));
        System.out.println("received: " + bytesToHex(receivedBytes));
        log("card status: " + cardStatus);

        byte[] completeResponse;
        // analyze command
        if (Arrays.equals(receivedBytes, SELECT_PPSE_COMMAND)) {
            cardStatus = Status.PPSE_SELECTED;
            foundAid = -1; // invalidate old foundings
            log("received SELECT_PPSE_COMMAND qualifies for cardStatus " + cardStatus);
            completeResponse = ConcatArrays(SELECT_PPSE_RESPONSE, SELECT_OK_SW);
            log("send SELECT_PPSE_RESPONSE: " + bytesToHex(completeResponse));
            return completeResponse;
        }

        int foundAidTemp = findDataInDataArray(SELECT_AID_COMMAND, receivedBytes);
        log("found an AID in the command foundAid: " + foundAidTemp);
        if (foundAidTemp > -1) {
            cardStatus = Status.AID_SELECTED;
            foundAid = foundAidTemp;
            log("received SELECT_AID_COMMAND qualifies for cardStatus " + cardStatus);
            completeResponse = ConcatArrays(SELECT_AID_RESPONSE[foundAid], SELECT_OK_SW);
            log("send SELECT_AID_RESPONSE: " + bytesToHex(completeResponse));
            return completeResponse;
        }

        // next commands are allowed only if status is not AID_SELECTED or GPO_DONE
        if (cardStatus == Status.AID_SELECTED | cardStatus == Status.GPO_DONE) {
            // next step is usually Get Processing Options
            // foundAid should be not -1
            if (Arrays.equals(receivedBytes, GET_PROCESSING_OPTIONS_COMMAND[foundAid])) {
                cardStatus = Status.GPO_DONE;
                log("received GET_PROCESSING_OPTIONS_COMMAND qualifies for cardStatus " + cardStatus);
                completeResponse = ConcatArrays(GET_PROCESSING_OPTIONS_RESPONSE[foundAid], SELECT_OK_SW);
                log("send GET_PROCESSING_OPTIONS_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }

            // next step is to read files if there is an afl in response
            // todo deny file reading when no files are present



        }





        // todo reset cardStatus ?
        //cardStatus = Status.NO_SELECT;
        //foundAid = -1; // reset
        log("cardStatus resetted to " + cardStatus);
        log("return UNKNOWN_CMD_SW");
        System.out.println("return UNKNOWN_CMD_SW");
        //return UNKNOWN_CMD_SW;
        return null;
    }


    @Override
    public void onDeactivated(int i) {
        // is called when the connection between this device and a NFC card reader ends
        log("received deactivated message with code: " + i);
    }




    private void initCardData() {
        // data from Aids model
        System.out.println(aids.dumpAids());
        CARD_NAME = aids.getCardName();
        CARD_TYPE = aids.getCardType();
        SELECT_PPSE_COMMAND = hexToBytes(aids.getSelectPpseCommand());
        SELECT_PPSE_RESPONSE = hexToBytes(aids.getSelectPpseResponse());
        NUMBER_OF_AID = aids.getNumberOfAid();
        System.out.println("*** NUMBER_OF_AID: " + NUMBER_OF_AID + " ***");
        AID_MODEL = aids.getAid();
        // data from Aid model
        SELECT_AID_COMMAND = new byte[NUMBER_OF_AID][];
        SELECT_AID_RESPONSE = new byte[NUMBER_OF_AID][];
        GET_PROCESSING_OPTIONS_COMMAND = new byte[NUMBER_OF_AID][];
        GET_PROCESSING_OPTIONS_RESPONSE = new byte[NUMBER_OF_AID][];

        for (int i = 0; i < NUMBER_OF_AID; i++){
            SELECT_AID_COMMAND[i] = hexToBytes(AID_MODEL[i].getSelectAidCommand());
            SELECT_AID_RESPONSE[i] = hexToBytes(AID_MODEL[i].getSelectAidResponse());
            GET_PROCESSING_OPTIONS_COMMAND[i] = hexToBytes(AID_MODEL[i].getGetProcessingOptionsCommand());
            GET_PROCESSING_OPTIONS_RESPONSE[i] = hexToBytes(AID_MODEL[i].getGetProcessingOptionsResponse());
        }
    }

    private int findDataInDataArray(@NonNull byte[][] dataArray, @NonNull byte[] data) {
        for (int i = 0; i < dataArray.length; i++) {
            if (Arrays.equals(data, dataArray[i])) return i;
        }
        return -1;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(@NonNull byte[] first, @NonNull byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * section for conversions
     */

    /**
     * converts a byte array to a hex encoded string
     * @param bytes
     * @return hex encoded string
     */
    public static String bytesToHex(@NonNull byte[] bytes) {
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
        String timestampMillis = getTimestampMillis();
        Log.i(TAG, timestampMillis + " " + message);
        System.out.println(timestampMillis + " " + message);
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
