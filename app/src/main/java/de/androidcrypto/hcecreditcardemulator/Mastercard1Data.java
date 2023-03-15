package de.androidcrypto.hcecreditcardemulator;

public class Mastercard1Data {

    /**
     * This class is holding the data of a sample MasterCard.
     * The card provides no files as no AFL data is present
     * The card number is anonymized so you should not use it :-)
     */

    // AAB MasterCard
    public static String mastercardcard1DataJsonString = "{\n" +
            "  \"aid\": [\n" +
            "    {\n" +
            "      \"afl\": \"080101001001010120010200\",\n" +
            "      \"aid\": \"a0000000041010\",\n" +
            "      \"aidName\": \"MasterCard\",\n" +
            "      \"applicationTransactionCounter\": \"\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"2403\",\n" +
            "      \"files\": [\n" +
            "        {\n" +
            "          \"addressAfl\": \"0801\",\n" +
            "          \"content\": \"70759f6c0200019f6206000000000f009f63060000000000fe563442353337353035303030303136303131305e202f5e323430333232313237393433323930303030303030303030303030303030309f6401029f65020f009f660200fe9f6b135375050000160110d24032210000000000000f9f670102\",\n" +
            "          \"dataLength\": 119,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"1001\",\n" +
            "          \"content\": \"7081a69f420209785f25032203015f24032403315a0853750500001601105f3401009f0702ffc09f080200028c279f02069f03069f1a0295055f2a029a039c019f37049f35019f45029f4c089f34039f21039f7c148d0c910a8a0295059f37049f4c088e0e000000000000000042031e031f039f0d05b4508400009f0e0500000000009f0f05b4708480005f280202809f4a018257135375050000160110d24032212794329000000f\",\n" +
            "          \"dataLength\": 169,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2001\",\n" +
            "          \"content\": \"7081b89f4701039f4681b03cada902afb40289fbdfea01950c498191442c1b48234dcaff66bca63cbf821a3121fa808e4275a4e894b154c1874bddb00f16276e92c73c04468253b373f1e6a9a89e2705b4670682d0adff05617a21d7684031a1cdb438e66cd98d591dc376398c8aab4f137a2226122990d9b2b4c72ded6495d637338fefa893ae7fb4eb845f8ec2e260d2385a780f9fda64b3639a9547adad806f78c9bc9f17f9d4c5b26474b9ba03892a754ffdf24df04c702f86\",\n" +
            "          \"dataLength\": 187,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2002\",\n" +
            "          \"content\": \"7081e08f01059f3201039224abfd2ebc115c3796e382be7e9863b92c266ccabc8bd014923024c80563234e8a11710a019081b004cc60769cabe557a9f2d83c7c73f8b177dbf69288e332f151fba10027301bb9a18203ba421bda9c2cc8186b975885523bf6707f287a5e88f0f6cd79a076319c1404fcdd1f4fa011f7219e1bf74e07b25e781d6af017a9404df9fd805b05b76874663ea88515018b2cb6140dc001a998016d28c4af8e49dfcc7d9cee314e72ae0d993b52cae91a5b5c76b0b33e7ac14a7294b59213ca0c50463cfb8b040bb8ac953631b80fa85a698b00228b5ff44223\",\n" +
            "          \"dataLength\": 227,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 2,\n" +
            "          \"sfi\": 4\n" +
            "        }\n" +
            "      ],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"7781849f4b8180b5925f7029ce794393ec440b2768c7d400372f93354c6aabf9faf944c94923ce42be88101201fa51cc5c6d02abcab3ee734c6d68b595c348a7689cbc78a365622fcee845f2f93185ec6967ba9a379d3c0384eb22538ec0d91896bf14d22541aade9e835c92afa245cc6cde111c5fda1c57c062651f30851da0e8bf024b4b6fd0\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a8000002830000\",\n" +
            "      \"getProcessingOptionsResponse\": \"771282021980940c080101001001010120010200\",\n" +
            "      \"lastOnlineATCRegister\": \"\",\n" +
            "      \"leftPinTryCounter\": \"03\",\n" +
            "      \"logFormat\": \"0000000000000000000000000000000000000000000000000000\",\n" +
            "      \"numberOfFiles\": 4,\n" +
            "      \"panFound\": \"5375050000160110\",\n" +
            "      \"selectAidCommand\": \"00a4040007a000000004101000\",\n" +
            "      \"selectAidResponse\": \"6f528407a0000000041010a54750104465626974204d6173746572436172649f12104465626974204d6173746572436172648701019f1101015f2d046465656ebf0c119f0a04000101019f6e0702800000303000\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"cardName\": \"aab mc\",\n" +
            "  \"cardType\": \"MasterCard\",\n" +
            "  \"numberOfAid\": 1,\n" +
            "  \"selectPpseCommand\": \"00a404000e325041592e5359532e444446303100\",\n" +
            "  \"selectPpseResponse\": \"6f3c840e325041592e5359532e4444463031a52abf0c2761254f07a000000004101050104465626974204d6173746572436172648701019f0a0400010101\"\n" +
            "}\n";

}
