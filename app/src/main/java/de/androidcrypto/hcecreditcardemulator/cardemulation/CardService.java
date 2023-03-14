package de.androidcrypto.hcecreditcardemulator.cardemulation;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;

/**
 * This is a sample APDU Service which demonstrates how to interface with the card emulation support
 * added in Android 4.4, KitKat.
 *
 * <p>This sample replies to any requests sent with the string "Hello World". In real-world
 * situations, you would need to modify this code to implement your desired communication
 * protocol.
 *
 * <p>This sample will be invoked for any terminals selecting AIDs of 0xF11111111, 0xF22222222, or
 * 0xF33333333. See src/main/res/xml/aid_list.xml for more details.
 *
 * <p class="note">Note: This is a low-level interface. Unlike the NdefMessage many developers
 * are familiar with for implementing Android Beam in apps, card emulation only provides a
 * byte-array based communication channel. It is left to developers to implement higher level
 * protocol support as needed.
 */
public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    private static final String PPSE_AID = "2PAY.SYS.DDF01";
    //private static final String PPSE_AID = "325041592e5359532e4444463031"; // PPSE_AID = "2PAY.SYS.DDF01"
    private static final String VISA_AID = "A0000000031010";
    private static final String VISA_SELECT_PPSE_COMMAND = "00a404000e325041592e5359532e444446303100";
    private static final String VISA_SELECT_PPSE_RESPONSE = "6f2b840e325041592e5359532e4444463031a519bf0c1661144f07a00000000310109f0a080001050100000000";
    private static final String VISA_SELECT_AID_COMMAND = "00a4040007a000000003101000";
    private static final String VISA_SELECT_AID_RESPONSE = "6f5d8407a0000000031010a5525010564953412044454249542020202020208701029f38189f66049f02069f03069f1a0295055f2a029a039c019f37045f2d02656ebf0c1a9f5a0531082608269f0a080001050100000000bf6304df200180";
    private static final String VISA_GET_PROCESSING_OPTIONS_COMMAND = "80a80000238321a0000000000000000001000000000008400000000000084007020300801733700000";
    private static final String VISA_GET_PROCESSING_OPTIONS_RESPONSE = "77478202200057134921828094896752d25022013650000000000f5f3401009f100706040a03a020009f26089f98ecaea782d0739f2701809f3602033c9f6c0216009f6e0420700000";

    // AID for our loyalty card service.
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";
    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");

    private static final byte[] SELECT_APDU_PPSE_COMMAND = HexStringToByteArray(VISA_SELECT_PPSE_COMMAND);
    private static final byte[] SELECT_APDU_PPSE_RESPONSE = HexStringToByteArray(VISA_SELECT_PPSE_RESPONSE);
    private static final byte[] SELECT_APDU_VISA_COMMAND = HexStringToByteArray(VISA_SELECT_AID_COMMAND);
    private static final byte[] SELECT_APDU_VISA_RESPONSE = HexStringToByteArray(VISA_SELECT_AID_RESPONSE);
    private static final byte[] GET_PROCESSING_OPTIONS_COMMAND_VISA = HexStringToByteArray(VISA_GET_PROCESSING_OPTIONS_COMMAND);
    private static final byte[] GET_PROCESSING_OPTIONS_RESPONSE_VISA = HexStringToByteArray(VISA_GET_PROCESSING_OPTIONS_RESPONSE);
    private static final int CHECK_FIRST_BYTES_GET_PROCESSING_OPTIONS_COMMAND_VISA = 6; // compare the first xx bytes to apduCommand and check the complete length
    private static final int PAN_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA = 16; // the PAN begins at pos xx of the hex string and is 16 chars long
    private static final int EXP_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA = 33; // the Expire Date begins at pos xx of the hex string and is 3 bytes long
    // note on expire date: d25022 means d = separator, 2502 = YYMM, 2.. next field, so best use String copy
    private static String PAN_NEW = "4871778899001122";
    private static String EXP_NEW = "2605";


    //private static final byte[] SELECT_APDU_VISA = BuildSelectApdu(VISA_AID);
    private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);

    private int status = 0;
    // 0 = no connection attempt
    // 1 = select PPSE done
    // 2 = select AID (here VISA) done
    // 3 = getProcessingOptions done

    // https://stackoverflow.com/questions/26208056/initial-handshake-between-nfc-controller-and-pos-reader


    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    @Override
    public void onDeactivated(int reason) { }

    /**
     * This method will be called when a command APDU has been received from a remote device. A
     * response APDU can be provided directly by returning a byte-array in this method. In general
     * response APDUs must be sent as quickly as possible, given the fact that the user is likely
     * holding his device over an NFC reader when this method is called.
     *
     * <p class="note">If there are multiple services that have registered for the same AIDs in
     * their meta-data entry, you will only get called if the user has explicitly selected your
     * service, either as a default or just for the next tap.
     *
     * <p class="note">This method is running on the main thread of your application. If you
     * cannot return a response APDU immediately, return null and use the {@link
     * #sendResponseApdu(byte[])} method later.
     *
     * @param commandApdu The APDU that received from the remote device
     * @param extras A bundle containing extra data. May be null.
     * @return a byte-array containing the response APDU, or null if no response APDU can be sent
     * at this point.
     */
    // BEGIN_INCLUDE(processCommandApdu)
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        log("************************************");
        log("Received APDU: " + ByteArrayToHexString(commandApdu));
        //Log.i(TAG, "************************************");
        //Log.i(TAG, "Received APDU: " + ByteArrayToHexString(commandApdu));
        //Log.i(TAG, "SELECT_VISA  : " + ByteArrayToHexString(SELECT_APDU_VISA));


        int statusNext = 0;
        byte[] response;
        byte[] completeResponse;
        if (Arrays.equals(commandApdu, SELECT_APDU_PPSE_COMMAND)) {
            Log.i(TAG, "SELECT_APDU_PPSE_COMMAND qualifies for statusNext = 1");
            statusNext = 1;
        }
        if (Arrays.equals(commandApdu, SELECT_APDU_VISA_COMMAND)) {
            Log.i(TAG, "SELECT_APDU_VISA_COMMAND qualifies for statusNext = 2");
            statusNext = 2;
        }

        System.out.println("## commandApdu: " + ByteArrayToHexString(Arrays.copyOf(commandApdu, CHECK_FIRST_BYTES_GET_PROCESSING_OPTIONS_COMMAND_VISA)));
        System.out.println("## GPOVISAApdu: " + ByteArrayToHexString(Arrays.copyOf(GET_PROCESSING_OPTIONS_COMMAND_VISA, CHECK_FIRST_BYTES_GET_PROCESSING_OPTIONS_COMMAND_VISA)));

        if (Arrays.equals(Arrays.copyOf(commandApdu, CHECK_FIRST_BYTES_GET_PROCESSING_OPTIONS_COMMAND_VISA), Arrays.copyOf(GET_PROCESSING_OPTIONS_COMMAND_VISA, CHECK_FIRST_BYTES_GET_PROCESSING_OPTIONS_COMMAND_VISA))) {
            // at this point the first xx bytes are equals
            // now check for the complete length
            if (commandApdu.length == GET_PROCESSING_OPTIONS_COMMAND_VISA.length) {
                Log.i(TAG, "GET_PROCESSING_OPTIONS_COMMAND_VISA qualifies for statusNext = 3");
                statusNext = 3;
            }
        }

        if ((status == 0) && (statusNext == 1)) {
            Log.i(TAG, "01 received selectPPSE: " + ByteArrayToHexString(commandApdu));
            // send the select PPSE response
            completeResponse = ConcatArrays(SELECT_APDU_PPSE_RESPONSE, SELECT_OK_SW);
            Log.i(TAG, "01 send selectPPSE response: " + ByteArrayToHexString(completeResponse));
            status = 1;
            return completeResponse;
        } else if ((status == 1) && (statusNext == 2)) {
            Log.i(TAG, "02 received selectAid: " + ByteArrayToHexString(commandApdu));
            // send the select AID response
            completeResponse = ConcatArrays(SELECT_APDU_VISA_RESPONSE, SELECT_OK_SW);
            Log.i(TAG, "02 send selectAid response: " + ByteArrayToHexString(completeResponse));
            status = 2;
            return completeResponse;
        } else if ((status == 2) && (statusNext == 3)) {
            Log.i(TAG, "03 received getProcessingOptions: " + ByteArrayToHexString(commandApdu));
            // send the getProcessingOptions response

            // here we are modifying the GPO response for a new CC number
            String newPanGpoResponse = VISA_GET_PROCESSING_OPTIONS_RESPONSE.replace(VISA_GET_PROCESSING_OPTIONS_RESPONSE.substring(PAN_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA, PAN_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA + 16), PAN_NEW);
            String newExpGpoResponse = newPanGpoResponse.replace(newPanGpoResponse.substring(EXP_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA, EXP_POSITION_IN_GET_PROCESSING_OPTIONS_RESPONSE_VISA + 4), EXP_NEW);
            completeResponse = ConcatArrays(HexStringToByteArray(newExpGpoResponse), SELECT_OK_SW);
/*
77478202200057134487177889902605225022013650000000000F5F3401009F100706040A03A020009F26089F98ECAEA782D0739F2701809F3602033C9F6C0216009F6E0420700000
77478202200057134921828094896752d25022013650000000000f5f3401009f100706040a03a020009f26089f98ecaea782d0739f2701809f3602033c9f6c0216009f6e0420700000
 */
            //completeResponse = ConcatArrays(GET_PROCESSING_OPTIONS_RESPONSE_VISA, SELECT_OK_SW);
            Log.i(TAG, "03 send getProcessingOptions response: " + ByteArrayToHexString(completeResponse));
            status = 3;
            return completeResponse;
        }


        // If the APDU matches the SELECT AID command for this service,
        // send the loyalty card account number, followed by a SELECT_OK status trailer (0x9000).
        /*
        if (Arrays.equals(SELECT_APDU, commandApdu)) {
            String account = AccountStorage.GetAccount(this);
            byte[] accountBytes = account.getBytes();
            Log.i(TAG, "Sending account number: " + account);
            return ConcatArrays(accountBytes, SELECT_OK_SW);
        } else {
            return UNKNOWN_CMD_SW;
        }

     */
        return UNKNOWN_CMD_SW;
    }
    // END_INCLUDE(processCommandApdu)

    private static byte[] selectApdu(byte[] aid) {
        byte[] commandApdu = new byte[6 + aid.length];
        commandApdu[0] = (byte) 0x00;  // CLA
        commandApdu[1] = (byte) 0xA4;  // INS
        commandApdu[2] = (byte) 0x04;  // P1
        commandApdu[3] = (byte) 0x00;  // P2
        commandApdu[4] = (byte) (aid.length & 0x0FF);       // Lc
        System.arraycopy(aid, 0, commandApdu, 5, aid.length);
        commandApdu[commandApdu.length - 1] = (byte) 0x00;  // Le
        return commandApdu;
    }


    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
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
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
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
