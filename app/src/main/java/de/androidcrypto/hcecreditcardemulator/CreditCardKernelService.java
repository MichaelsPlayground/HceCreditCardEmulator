package de.androidcrypto.hcecreditcardemulator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;
import de.androidcrypto.hcecreditcardemulator.models.Aid;
import de.androidcrypto.hcecreditcardemulator.models.Aids;
import de.androidcrypto.hcecreditcardemulator.models.FilesModel;

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
    public Context context;

    private final byte[] SELECT_OK_SW = hexToBytes("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private final byte[] UNKNOWN_CMD_SW = hexToBytes("0000");
    private final byte[] NOT_SUPPORTED_CMD_SW = hexToBytes("6a81");
    private final byte[] SELECT_AID_TRAILER = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00};
    private final byte[] READ_FILE_TRAILER = new byte[]{(byte) 0x00, (byte) 0xB2};

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
    private byte[][] APPLICATION_TRANSACTION_COUNTER_COMMAND;
    private byte[][] APPLICATION_TRANSACTION_COUNTER_RESPONSE;
    private byte[][] LEFT_PIN_TRY_COUNTER_COMMAND;
    private byte[][] LEFT_PIN_TRY_COUNTER_RESPONSE;
    private byte[][] LAST_ONLINE_ATC_REGISTER_COMMAND;
    private byte[][] LAST_ONLINE_ATC_REGISTER_RESPONSE;
    private byte[][] LOG_FORMAT_COMMAND;
    private byte[][] LOG_FORMAT_RESPONSE;
    private byte[][] INTERNAL_AUTHENTICATION_COMMAND;
    private byte[][] INTERNAL_AUTHENTICATION_RESPONSE;
    private byte[][] APPLICATION_CRYPTOGRAM_COMMAND;
    private byte[][] APPLICATION_CRYPTOGRAM_RESPONSE;
    private int[] NUMBER_OF_FILES;
    private FilesModel[][] FILES;

    public CreditCardKernelService() {
        /*
        // init for the service
        LoadEmulatorData led = new LoadEmulatorData(null);
        aids = led.getAidsFromInternalStorage(FILENAME);
        //aids = getAidsFromInternalStorage(FILENAME);
        initCardData();

         */
    }

    @Override
    public void onCreate() {

        log("onCreate CreditCardKernel: " + KERNEL_VERSION);
        System.out.println("onCreate CreditCardKernel: " + KERNEL_VERSION);
        //context = getApplication();
        context = getBaseContext();
        LoadEmulatorData led = new LoadEmulatorData(context);
        ArrayList<String> fileList = led.getFileList();
        log("== fileList in internal storage subfolder cards ==");
        System.out.println("== fileList in internal storage subfolder cards ==");
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                log("entry " + i + " is " + fileList.get(i));
                System.out.println("entry " + i + " is " + fileList.get(i));
            }
        } else {
            System.out.println("fileList is NULL");
        }
        String fileContent = led.getFileContent("aab mc anon emv.json");
        System.out.println("content is\n" + fileContent);

        aids = led.getAidsFromInternalStorage(FILENAME);
        initCardData();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        log("onStart");
        return Service.START_STICKY;
    }

    /*
    @Override
    public void onCreate() {
        log("onCreate CreditCardKernel: " + KERNEL_VERSION);
        initCardData();
        super.onCreate();
    }

     */

    @Override
    public byte[] processCommandApdu(byte[] receivedBytes, Bundle bundle) {

        LoadEmulatorData led = new LoadEmulatorData(context);
        ArrayList<String> fileList = led.getFileList();
        log("== fileList in internal storage subfolder cards ==");
        System.out.println("== fileList in internal storage subfolder cards ==");
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                log("entry " + i + " is " + fileList.get(i));
                System.out.println("entry " + i + " is " + fileList.get(i));
            }
        } else {
            System.out.println("fileList is NULL");
        }
        String fContent = led.getFileContent("aab mc anon emv.json");
        System.out.println("content is\n" + fContent);


        // is called when a new commandApdu comes in
        log("card status: " + cardStatus);
        log("command received: " + bytesToHex(receivedBytes));

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

        // todo work on this as the MasterCard sample can't get read, Visa is running for Lloyds (no AFL)

        // precheck if it is a selectAid command trailer
        if (Arrays.equals(Arrays.copyOfRange(receivedBytes, 0, 4), SELECT_AID_TRAILER)) {
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
            // usually a card should not allow to read a file when no AFL is present... but I noticed they allow it anyways
            int numberOfFilesPresentInAfl = NUMBER_OF_FILES[foundAid];
            // proceed AFL command checking only if files are present
            if (numberOfFilesPresentInAfl > 0) {
                if (Arrays.equals(Arrays.copyOfRange(receivedBytes, 0, 2), READ_FILE_TRAILER)) {
                    // complete sample read command could look like 00 b2 01 0c 00
                    // the READ_FILE_TRAILER is                     00 b2
                    // the sector to read is                              01
                    // the sfi to read is                                    0c
                    // the finalization of the command is                       00
                    // check for correct length of command
                    if (receivedBytes.length == 5) {
                        // get the single bytes for rec and sfi
                        final byte rec = receivedBytes[2];
                        final byte sfi = receivedBytes[3];
                        // convert sfi to real sfi number that is shown in the json file
                        final int sfiSector = sfi >>> 3;
                        final int record = (int) rec;
                        // search for sfiSector and record in FILES
                        log("search specific file with sfi: " + sfiSector + " record: " + record);
                        byte[] fileContent = searchFileInFiles(sfiSector, record);
                        if (fileContent == null) {
                            // means that this file is not available
                            log("received READ_FILES_COMMAND: " + bytesToHex(receivedBytes));
                            log("requested file is not present on card");
                            return NOT_SUPPORTED_CMD_SW;
                        } else {
                            log("received READ_FILES_COMMAND: " + bytesToHex(receivedBytes));
                            completeResponse = ConcatArrays(fileContent, SELECT_OK_SW);
                            log("send READ_FILES_RESPONSE: " + bytesToHex(completeResponse));
                            return completeResponse;
                        }
                    } else {
                        // wrong command
                        log("the READ_FILES_COMMAND has a wrong data structure");
                       return UNKNOWN_CMD_SW;
                    }
                } // if (Arrays.equals(Arrays.copyOfRange(receivedBytes, 0, 2), READ_FILE_TRAILER)) {
            } // if (numberOfFilesPresentInAfl > 0) {





            // some single requests
            if (Arrays.equals(receivedBytes, APPLICATION_TRANSACTION_COUNTER_COMMAND[foundAid])) {
                log("received APPLICATION_TRANSACTION_COUNTER_COMMAND");
                completeResponse = ConcatArrays(APPLICATION_TRANSACTION_COUNTER_RESPONSE[foundAid], SELECT_OK_SW);
                log("send APPLICATION_TRANSACTION_COUNTER_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }
            if (Arrays.equals(receivedBytes, LEFT_PIN_TRY_COUNTER_COMMAND[foundAid])) {
                log("received LEFT_PIN_TRY_COUNTER_COMMAND");
                completeResponse = ConcatArrays(LEFT_PIN_TRY_COUNTER_RESPONSE[foundAid], SELECT_OK_SW);
                log("send LEFT_PIN_TRY_COUNTER_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }
            if (Arrays.equals(receivedBytes, LAST_ONLINE_ATC_REGISTER_COMMAND[foundAid])) {
                log("received LAST_ONLINE_ATC_REGISTER_COMMAND");
                completeResponse = ConcatArrays(LAST_ONLINE_ATC_REGISTER_RESPONSE[foundAid], SELECT_OK_SW);
                log("send LAST_ONLINE_ATC_REGISTER_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }
            if (Arrays.equals(receivedBytes, LOG_FORMAT_COMMAND[foundAid])) {
                log("received LOG_FORMAT_COMMAND");
                completeResponse = ConcatArrays(LOG_FORMAT_RESPONSE[foundAid], SELECT_OK_SW);
                log("send LOG_FORMAT_RESPONSE: " + bytesToHex(completeResponse));
                //return completeResponse;
                return UNKNOWN_CMD_SW;
            }
            if (Arrays.equals(receivedBytes, INTERNAL_AUTHENTICATION_COMMAND[foundAid])) {
                log("received INTERNAL_AUTHENTICATION");
                completeResponse = ConcatArrays(INTERNAL_AUTHENTICATION_RESPONSE[foundAid], SELECT_OK_SW);
                log("send INTERNAL_AUTHENTICATION_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }
            if (Arrays.equals(receivedBytes, APPLICATION_CRYPTOGRAM_COMMAND[foundAid])) {
                log("received APPLICATION_CRYPTOGRAM_COMMAND");
                completeResponse = ConcatArrays(APPLICATION_CRYPTOGRAM_RESPONSE[foundAid], SELECT_OK_SW);
                log("send APPLICATION_CRYPTOGRAM_RESPONSE: " + bytesToHex(completeResponse));
                return completeResponse;
            }


        }





        // todo reset cardStatus ?
        //cardStatus = Status.NO_SELECT;
        //foundAid = -1; // reset
        log("cardStatus is " + cardStatus);
        log("return UNKNOWN_CMD_SW on receivedBytes " + bytesToHex(receivedBytes));
        return UNKNOWN_CMD_SW;
        //return null;
    }


    @Override
    public void onDeactivated(int i) {
        // is called when the connection between this device and a NFC card reader ends
        log("received deactivated message with code: " + i);
        if (i == DEACTIVATION_LINK_LOSS) log("deactivated because of DEACTIVATION_LINK_LOSS");
        if (i == DEACTIVATION_DESELECTED) log("deactivated because of DEACTIVATION_DESELECTED");
        foundAid = -1;
        cardStatus = Status.NO_SELECT;
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
        APPLICATION_TRANSACTION_COUNTER_COMMAND = new byte[NUMBER_OF_AID][];
        APPLICATION_TRANSACTION_COUNTER_RESPONSE = new byte[NUMBER_OF_AID][];
        LEFT_PIN_TRY_COUNTER_COMMAND = new byte[NUMBER_OF_AID][];
        LEFT_PIN_TRY_COUNTER_RESPONSE = new byte[NUMBER_OF_AID][];
        LAST_ONLINE_ATC_REGISTER_COMMAND = new byte[NUMBER_OF_AID][];
        LAST_ONLINE_ATC_REGISTER_RESPONSE = new byte[NUMBER_OF_AID][];
        LOG_FORMAT_COMMAND = new byte[NUMBER_OF_AID][];
        LOG_FORMAT_RESPONSE = new byte[NUMBER_OF_AID][];
        INTERNAL_AUTHENTICATION_COMMAND = new byte[NUMBER_OF_AID][];
        INTERNAL_AUTHENTICATION_RESPONSE = new byte[NUMBER_OF_AID][];
        APPLICATION_CRYPTOGRAM_COMMAND = new byte[NUMBER_OF_AID][];
        APPLICATION_CRYPTOGRAM_RESPONSE = new byte[NUMBER_OF_AID][];
        NUMBER_OF_FILES = new int[NUMBER_OF_AID];


        for (int i = 0; i < NUMBER_OF_AID; i++){
            SELECT_AID_COMMAND[i] = hexToBytes(AID_MODEL[i].getSelectAidCommand());
            SELECT_AID_RESPONSE[i] = hexToBytes(AID_MODEL[i].getSelectAidResponse());
            GET_PROCESSING_OPTIONS_COMMAND[i] = hexToBytes(AID_MODEL[i].getGetProcessingOptionsCommand());
            GET_PROCESSING_OPTIONS_RESPONSE[i] = hexToBytes(AID_MODEL[i].getGetProcessingOptionsResponse());
            // todo this are static/fixed values, need to get values from json
            // todo implement an empty blank hexToBytes
            APPLICATION_TRANSACTION_COUNTER_COMMAND[i] = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, (byte) 0x36, (byte) 0x00};
            APPLICATION_TRANSACTION_COUNTER_RESPONSE[i] = hexToBytes("9f36020045");
            LEFT_PIN_TRY_COUNTER_COMMAND[i] = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, (byte) 0x17, (byte) 0x00};
            LEFT_PIN_TRY_COUNTER_RESPONSE[i] = hexToBytes("9f170103");
            LAST_ONLINE_ATC_REGISTER_COMMAND[i] = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, (byte) 0x13, (byte) 0x00};
            LAST_ONLINE_ATC_REGISTER_RESPONSE[i] = hexToBytes("9f13020044");
            LOG_FORMAT_COMMAND[i] = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, (byte) 0x4F, (byte) 0x00};
            LOG_FORMAT_RESPONSE[i] = hexToBytes("9f4f020000");
            INTERNAL_AUTHENTICATION_COMMAND[i] = hexToBytes(AID_MODEL[i].getGetInternalAuthenticationCommand());
            INTERNAL_AUTHENTICATION_RESPONSE[i] = hexToBytes(AID_MODEL[i].getGetInternalAuthenticationResponse());
            APPLICATION_CRYPTOGRAM_COMMAND[i] = hexToBytes(AID_MODEL[i].getGetApplicationCryptogramCommand());
            APPLICATION_CRYPTOGRAM_RESPONSE[i] = hexToBytes(AID_MODEL[i].getGetApplicationCryptogramResponse());
            NUMBER_OF_FILES[i] = AID_MODEL[i].getNumberOfFiles();
            // init is below
            // FILES = new FilesModel[NUMBER_OF_AID][MAX_NUMBER_OF_FILES];
        }
        // get the maximum of NUMBER_OF_FILES as the FILES array needs to get setup properly
        // needs min SDK 24
        /*
        IntSummaryStatistics stat = Arrays.stream(NUMBER_OF_FILES).summaryStatistics();
        int MAX_NUMBER_OF_FILES2 = stat.getMax();
        // needs min SDK 24
        int i = IntStream.range(0, NUMBER_OF_FILES.length).map(i -> NUMBER_OF_FILES[i]).max().getAsInt();
        */
        int MAX_NUMBER_OF_FILES = maxValueOfIntArray(NUMBER_OF_FILES);
        FILES = new FilesModel[NUMBER_OF_AID][MAX_NUMBER_OF_FILES];
        // the second run is necessary because we need to init FILES with the correct NUMBER_OF_FILES value that is read out in the loop above
        for (int i = 0; i < NUMBER_OF_AID; i++) {
            int NUMBER_OF_FILES_IN_AID = AID_MODEL[i].getNumberOfFiles();
            for (int j = 0; j < NUMBER_OF_FILES_IN_AID; j++) {
                FILES[i][j] = AID_MODEL[i].getFiles()[j];
            }
        }
    }

    public int maxValueOfIntArray(int array[]){
        // https://stackoverflow.com/a/32659836/8166854
        // not the most efficient way but we are talking about a handful of entries
        // To get the lowest value, you can use Collections.min(list)
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return Collections.max(list);
    }

    private int findDataInDataArray(@NonNull byte[][] dataArray, @NonNull byte[] data) {
        for (int i = 0; i < dataArray.length; i++) {
            if (Arrays.equals(data, dataArray[i])) return i;
        }
        return -1;
    }

    private byte[] searchFileInFiles(int sfi, int record) {
        for (int i = 0; i < NUMBER_OF_FILES[foundAid]; i++) {
            final int sfiInFile = FILES[foundAid][i].getSfi();
            final int recordInFile = FILES[foundAid][i].getRecord();
            if ((sfi == sfiInFile) && (record == recordInFile)) {
                return hexToBytes(FILES[foundAid][i].getContent());
            }
        }
        return null;
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
        //System.out.println(timestampMillis + " " + message);
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
