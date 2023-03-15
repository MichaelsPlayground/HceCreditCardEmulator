package de.androidcrypto.hcecreditcardemulator;

public class VisaCard1Data {

    /**
     * This class is holding the data of a sample VisaCard.
     * The card provides no files as no AFL data is present
     * The card number is anonymized so you should not use it :-)
     */

    // Lloyds Bank VisaCard
    public static String visacard1DataJsonString = "{\n" +
            "  \"aid\": [\n" +
            "    {\n" +
            "      \"afl\": \"\",\n" +
            "      \"aid\": \"a0000000031010\",\n" +
            "      \"aidName\": \"VISA credit/debit\",\n" +
            "      \"applicationTransactionCounter\": \"0377\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"2502\",\n" +
            "      \"files\": [],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a80000238321a0000000000000001000000000000000097800000000000978230301003839303100\",\n" +
            "      \"getProcessingOptionsResponse\": \"77478202200057131122334455667788d25022013650000000000f5f3401009f100706040a03a020009f2608aeb8ea19e9685bf49f2701809f360203789f6c0216009f6e0420700000\",\n" +
            "      \"lastOnlineATCRegister\": \"0295\",\n" +
            "      \"leftPinTryCounter\": \"03\",\n" +
            "      \"logFormat\": \"\",\n" +
            "      \"numberOfFiles\": 0,\n" +
            "      \"panFound\": \"1122334455667788\",\n" +
            "      \"selectAidCommand\": \"00a4040007a000000003101000\",\n" +
            "      \"selectAidResponse\": \"6f5d8407a0000000031010a5525010564953412044454249542020202020208701029f38189f66049f02069f03069f1a0295055f2a029a039c019f37045f2d02656ebf0c1a9f5a0531082608269f0a080001050100000000bf6304df200180\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"cardName\": \"Lloyds Visa anon\",\n" +
            "  \"cardType\": \"VISA credit/debit\",\n" +
            "  \"numberOfAid\": 1,\n" +
            "  \"selectPpseCommand\": \"00a404000e325041592e5359532e444446303100\",\n" +
            "  \"selectPpseResponse\": \"6f2b840e325041592e5359532e4444463031a519bf0c1661144f07a00000000310109f0a080001050100000000\"\n" +
            "}\n";

}
