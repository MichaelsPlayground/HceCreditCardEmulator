package de.androidcrypto.hcecreditcardemulator;

public class VisaCard2Data {

    /**
     * This class is holding the data of a sample VisaCard.
     * The card provides no files as no AFL data is present
     * The card number is anonymized so you should not use it :-)
     */

    // DKB Bank VisaCard
    public static String visacard2DataJsonString = "{\n" +
            "  \"aid\": [\n" +
            "    {\n" +
            "      \"afl\": \"10020400\",\n" +
            "      \"aid\": \"a0000000031010\",\n" +
            "      \"aidName\": \"VISA credit/debit\",\n" +
            "      \"applicationTransactionCounter\": \"0060\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"260930\",\n" +
            "      \"files\": [\n" +
            "        {\n" +
            "          \"addressAfl\": \"1002\",\n" +
            "          \"content\": \"7081fb9081f88893cf85a81325ab8da6a4196eb5787291db7205f61b172b26deb867da427f1d0e438e86400aea81a0f2826b250da618108389bdabe2a75c0168a28bb97645158b57ca8faa1d38d7a56e0a4171ec0d5e048d048dd98106bcadb3b5cac80485ff9c0fc970b4ea95d557fb9dd065bf75eb06f51df5a2c20479058ede6c8a376d9bfbf0c05b9e2b5aac1ec5982e2a9d861573e892da87b68357306e88cb054ab0090e01670a73d23fa239f4ae1283110fca40d46edc6c8021d15b3c147251b3c5e754f0fa9d82b7934ed34a12ef3d0a66c0c2a26a32e9722b10653516b356440aa8eece8d1d023829394adc2f9309ff60fc5baf51c0b24690be\",\n" +
            "          \"dataLength\": 254,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 2,\n" +
            "          \"sfi\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"1003\",\n" +
            "          \"content\": \"70079f3201038f0109\",\n" +
            "          \"dataLength\": 9,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 3,\n" +
            "          \"sfi\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"1004\",\n" +
            "          \"content\": \"7081eb9f4681b07e3b33a489fb75a23643407d2ebf48a808957165aa538d681213d71495b577086e63a24e847ed29d2ceba4bb3b1784361221287607ace4b8bfce09dd8364d4709293ed52b528623472fb6157094b12367534d7cf5c20b810058c817fb87c130111ee53c3855fd2b2a95449d03795541ea7c6ef942b0b069bfa7caa5d0ec6db0e428f18d03adcf7f92fb7e5516403adc629f3ffbd6900a1f308fbe5d28cba795c6c62d7573333abed15ad00a4da4ba8a99f4701035a0811223344556677885f24032609305f280202765f3401009f0702c0809f4a01829f6e04207000009f690701000000000000\",\n" +
            "          \"dataLength\": 238,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 4,\n" +
            "          \"sfi\": 2\n" +
            "        }\n" +
            "      ],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a80000238321a0000000000000001000000000000000097800000000000978230301003839303100\",\n" +
            "      \"getProcessingOptionsResponse\": \"7781c68202000094041002040057131122334455667788d26092012166408100000f9f100706010a03a020009f2608f8b7b89dc566574b9f2701809f360200619f6c0238009f4b81800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "      \"lastOnlineATCRegister\": \"0041\",\n" +
            "      \"leftPinTryCounter\": \"02\",\n" +
            "      \"logFormat\": \"9f27019f36029f02069f03069f1a025f2a0295059a039c01\",\n" +
            "      \"numberOfFiles\": 3,\n" +
            "      \"panFound\": \"1122334455667788\",\n" +
            "      \"selectAidCommand\": \"00a4040007a000000003101000\",\n" +
            "      \"selectAidResponse\": \"6f578407a0000000031010a54c50085649534120444b428701015f2d046465656e9f38189f66049f02069f03069f1a0295055f2a029a039c019f3704bf0c1a9f0a0800010502000000009f5a053109780276bf6304df200180\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"cardName\": \"DKB Visa anon\",\n" +
            "  \"cardType\": \"VISA credit/debit\",\n" +
            "  \"numberOfAid\": 1,\n" +
            "  \"selectPpseCommand\": \"00a404000e325041592e5359532e444446303100\",\n" +
            "  \"selectPpseResponse\": \"6f38840e325041592e5359532e4444463031a526bf0c2361214f07a000000003101050085649534120444b428701019f0a080001050200000000\"\n" +
            "}\n";

}
