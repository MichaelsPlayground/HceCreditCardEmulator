package de.androidcrypto.hcecreditcardemulator;

public class GiroCard2Data {

    /**
     * This class is holding the data of a sample Card.
     * The card provides no files as no AFL data is present
     * The card number is anonymized so you should not use it :-)
     */

    // card name:
    public static String girocard2DataJsonString = "{\n" +
            "  \"aid\": [\n" +
            "    {\n" +
            "      \"afl\": \"180101002001010020040400080505010807070108030301\",\n" +
            "      \"aid\": \"a00000005945430100\",\n" +
            "      \"aidName\": \"Zentraler Kreditausschuss (ZKA) Girocard Electronic Cash\",\n" +
            "      \"applicationTransactionCounter\": \"0252\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"2112\",\n" +
            "      \"files\": [\n" +
            "        {\n" +
            "          \"addressAfl\": \"1801\",\n" +
            "          \"content\": \"7081e28f01059081b078cdb2c84b435325ec4478fd6f0f9f0dd61210a78c791adcb22c85fb0095db3a540658569a1c0d35a48d1fd9c2dba83ed941fcb3f2cfe56c943bfa0f8d25f0896284006cbdc10821cf0f0f6ec033332f8eb52c1acad9c52221a27dd23aba70c27c547aece994c7dc5c4d5f1b28529a803340cc249caf6bcb3614d071de141f89a1f4a545c5598395864474514e42c7f1edbeedef27b9a50eeb81ed5762a0af36505ee084703dfd168ec6f02245077d8b9f3201039224b0568adf146b092492be46e5d57d920b026be8e734264cf34710483a0af52d46790f01ab0000\",\n" +
            "          \"dataLength\": 229,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2001\",\n" +
            "          \"content\": \"70339f47030100019f480a757271487e0b220c81cb0000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "          \"dataLength\": 53,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2004\",\n" +
            "          \"content\": \"7081b49f4681b087df5942ee89317aea2e53d477ab272794375e9025b0447b304f52e07f54494bea054076a0fd22faf4ee85cfd06ae61c44e0bf1c0156b1c0f287312e1c9460c0b93fac7bdd88a6cf286daeeab5d81310ff49b9d80f4b905261429b44a2c0e3b876ee8825fbb6ff3aef14a645983e886a61a7acde252698868b74033bbecee902050196579b2df75bfe070a14a45ce710c5e782da9ecd20d21db77352461b031ad83d9137615b8a63aca55900619a7a9c\",\n" +
            "          \"dataLength\": 183,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 4,\n" +
            "          \"sfi\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0805\",\n" +
            "          \"content\": \"70385f24032112315a0a6726428902046846007f5f3401025f280202809f0702ffc09f0d05fc40a480009f0e0500101800009f0f05fc40a49800\",\n" +
            "          \"dataLength\": 58,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 5,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0807\",\n" +
            "          \"content\": \"701c8e0c00000000000000001f0302039f080200029f6c02ffff9f4a0182\",\n" +
            "          \"dataLength\": 30,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 7,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0803\",\n" +
            "          \"content\": \"703d8c1b9f02069f03069f1a0295055f2a029a039c019f37049f35019f34038d0991108a0295059f370457136726428902046846007d21122010254828156f\",\n" +
            "          \"dataLength\": 63,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 3,\n" +
            "          \"sfi\": 1\n" +
            "        }\n" +
            "      ],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80002100000000100000000000000009780000000000097823030100383930312200000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"77379f2701809f360202539f26089bf6b4cf3f91c9a29f10200fa6d0a43140200000000010000000000f030000000100001d22012418011202\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"7781949f4b81902508c43a783caaacacf48a9f2b162ee972521a8fd2b0e47d11825c584187a21a407687e65f9ca7ada1397cdab5e2bbe0912ebae992667d25a09874e5bdae395ce50693ba8398f0cefa67c31e0b9589dbd199022a7c316de999e428db5417789766396b3b7fc1ff69c8ebe1284c9f76fc9458bf95abe2a25f5c58eb1ed9dba8887f3af6f5f26eef3c4cebc62934ea284d\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a800000a8308000000001000000000\",\n" +
            "      \"getProcessingOptionsResponse\": \"771e820219809418180101002001010020040400080505010807070108030301\",\n" +
            "      \"lastOnlineATCRegister\": \"\",\n" +
            "      \"leftPinTryCounter\": \"03\",\n" +
            "      \"logFormat\": \"9f02065f2a029a039f52059f36029f2701ca019505\",\n" +
            "      \"numberOfFiles\": 6,\n" +
            "      \"panFound\": \"6726428902046846007\",\n" +
            "      \"selectAidCommand\": \"00a4040009a0000000594543010000\",\n" +
            "      \"selectAidResponse\": \"6f478409a00000005945430100a53a50086769726f636172648701019f38069f02069f1d025f2d046465656ebf0c1a9f4d02190a9f6e07028000003030009f0a080001050100000000\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"afl\": \"180101002001010020040400080505010807070108030301\",\n" +
            "      \"aid\": \"a0000003591010028001\",\n" +
            "      \"aidName\": \"Euro Alliance of Payment Schemes s.c.r.l. – EAPS Girocard EAPS\",\n" +
            "      \"applicationTransactionCounter\": \"0253\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"2112\",\n" +
            "      \"files\": [\n" +
            "        {\n" +
            "          \"addressAfl\": \"1801\",\n" +
            "          \"content\": \"7081e28f01059081b078cdb2c84b435325ec4478fd6f0f9f0dd61210a78c791adcb22c85fb0095db3a540658569a1c0d35a48d1fd9c2dba83ed941fcb3f2cfe56c943bfa0f8d25f0896284006cbdc10821cf0f0f6ec033332f8eb52c1acad9c52221a27dd23aba70c27c547aece994c7dc5c4d5f1b28529a803340cc249caf6bcb3614d071de141f89a1f4a545c5598395864474514e42c7f1edbeedef27b9a50eeb81ed5762a0af36505ee084703dfd168ec6f02245077d8b9f3201039224b0568adf146b092492be46e5d57d920b026be8e734264cf34710483a0af52d46790f01ab0000\",\n" +
            "          \"dataLength\": 229,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2001\",\n" +
            "          \"content\": \"70339f47030100019f480a757271487e0b220c81cb0000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "          \"dataLength\": 53,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 1,\n" +
            "          \"sfi\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"2004\",\n" +
            "          \"content\": \"7081b49f4681b087df5942ee89317aea2e53d477ab272794375e9025b0447b304f52e07f54494bea054076a0fd22faf4ee85cfd06ae61c44e0bf1c0156b1c0f287312e1c9460c0b93fac7bdd88a6cf286daeeab5d81310ff49b9d80f4b905261429b44a2c0e3b876ee8825fbb6ff3aef14a645983e886a61a7acde252698868b74033bbecee902050196579b2df75bfe070a14a45ce710c5e782da9ecd20d21db77352461b031ad83d9137615b8a63aca55900619a7a9c\",\n" +
            "          \"dataLength\": 183,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 4,\n" +
            "          \"sfi\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0805\",\n" +
            "          \"content\": \"70385f24032112315a0a6726428902046846007f5f3401025f280202809f0702ffc09f0d05fc40a480009f0e0500101800009f0f05fc40a49800\",\n" +
            "          \"dataLength\": 58,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 5,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0807\",\n" +
            "          \"content\": \"701c8e0c00000000000000001f0302039f080200029f6c02ffff9f4a0182\",\n" +
            "          \"dataLength\": 30,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 7,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0803\",\n" +
            "          \"content\": \"703d8c1b9f02069f03069f1a0295055f2a029a039c019f37049f35019f34038d0991108a0295059f370457136726428902046846007d21122010254828156f\",\n" +
            "          \"dataLength\": 63,\n" +
            "          \"offlineAuth\": 1,\n" +
            "          \"record\": 3,\n" +
            "          \"sfi\": 1\n" +
            "        }\n" +
            "      ],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80002100000000100000000000000009780000000000097823030100383930312200000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"77379f2701809f360202549f26085348f83ff49970b69f10200fa6d0a43140200000000010000000000f030000000100001e22012418011202\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"7781949f4b81900b449f17f16575717643df192e7b0f666417dac8e0ca6ee0c5b3596ec9d414830840c06e036a987af58bbe989f6b7f01e34a467ad71cb3efb949b3d7fcf2ae9555047b5eb477d8c5468709da89519fb1de78a953545d95caf956f9169a40f3edcd0406235c26fb8a2609295a6b738fa67d075500580fa762cce0b5d812915665ac85473d2afaf7195dacfd6ce5de077d\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a800000a8308000000001000000000\",\n" +
            "      \"getProcessingOptionsResponse\": \"771e820219809418180101002001010020040400080505010807070108030301\",\n" +
            "      \"lastOnlineATCRegister\": \"\",\n" +
            "      \"leftPinTryCounter\": \"03\",\n" +
            "      \"logFormat\": \"9f02065f2a029a039f52059f36029f2701ca019505\",\n" +
            "      \"numberOfFiles\": 6,\n" +
            "      \"panFound\": \"6726428902046846007\",\n" +
            "      \"selectAidCommand\": \"00a404000aa000000359101002800100\",\n" +
            "      \"selectAidResponse\": \"6f48840aa0000003591010028001a53a50086769726f636172648701019f38069f02069f1d025f2d046465656ebf0c1a9f4d02190a9f6e07028000003030009f0a080001050100000000\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"afl\": \"08020500\",\n" +
            "      \"aid\": \"d27600002547410100\",\n" +
            "      \"aidName\": \"ZKA Girocard ATM\",\n" +
            "      \"applicationTransactionCounter\": \"0254\",\n" +
            "      \"checkFirstBytesGetProcessingOptions\": 6,\n" +
            "      \"expirationDateFound\": \"211231\",\n" +
            "      \"files\": [\n" +
            "        {\n" +
            "          \"addressAfl\": \"0802\",\n" +
            "          \"content\": \"70059f08020002\",\n" +
            "          \"dataLength\": 7,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 2,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0803\",\n" +
            "          \"content\": \"703d8c1b9f02069f03069f1a0295055f2a029a039c019f37049f35019f34038d0991108a0295059f370457136726428902046846007d21122010254828156f\",\n" +
            "          \"dataLength\": 63,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 3,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0804\",\n" +
            "          \"content\": \"700c8e0a00000000000000000203\",\n" +
            "          \"dataLength\": 14,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 4,\n" +
            "          \"sfi\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"addressAfl\": \"0805\",\n" +
            "          \"content\": \"70385f24032112315a0a6726428902046846007f5f3401025f280202809f0702ffc09f0d05fc40a480009f0e0500101800009f0f05fc40a49800\",\n" +
            "          \"dataLength\": 58,\n" +
            "          \"offlineAuth\": 0,\n" +
            "          \"record\": 5,\n" +
            "          \"sfi\": 1\n" +
            "        }\n" +
            "      ],\n" +
            "      \"getApplicationCryptogramCommand\": \"80ae80002100000000100000000000000009780000000000097823030100383930312200000000\",\n" +
            "      \"getApplicationCryptogramResponse\": \"77379f2701809f360202559f2608d20877b90a9a26d69f10200fa6d0a431c0200000000010000000000f090000000100001e22012418011202\",\n" +
            "      \"getInternalAuthenticationCommand\": \"0088000004e153f3e800\",\n" +
            "      \"getInternalAuthenticationResponse\": \"7781949f4b81902ba1d4078069b62c68c72c031447573313c58595cb8cc91e5fdb8f71ce2abac4d66efd5602e6c7f8941974e2a0fb493554807535f50e3b08f9e6feafde1ecbd5fe5aedc8d7711f57772040599a8bae4fb641a0ec9c3e00588a279d708da59379dc9b8029515342f28f9816f99e64d037ba7eabbbedcfe929eb9ef0ac08638436d9ecc2fe239b2064465aa2f7b8f13a6f\",\n" +
            "      \"getProcessingOptionsCommand\": \"80a800000683040000220000\",\n" +
            "      \"getProcessingOptionsResponse\": \"770a82021800940408020500\",\n" +
            "      \"lastOnlineATCRegister\": \"\",\n" +
            "      \"leftPinTryCounter\": \"03\",\n" +
            "      \"logFormat\": \"9f02065f2a029a039f52059f36029f2701ca019505\",\n" +
            "      \"numberOfFiles\": 4,\n" +
            "      \"panFound\": \"6726428902046846007f\",\n" +
            "      \"selectAidCommand\": \"00a4040009d2760000254741010000\",\n" +
            "      \"selectAidResponse\": \"6f4a8409d27600002547410100a53d50086769726f636172648701019f38099f33029f35019f40015f2d046465656ebf0c1a9f4d02190a9f6e07028000003030009f0a080001050100000000\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"cardName\": \"voba gc non\",\n" +
            "  \"cardType\": \"Zentraler Kreditausschuss (ZKA) Girocard Electronic Cash\",\n" +
            "  \"numberOfAid\": 3,\n" +
            "  \"selectPpseCommand\": \"00a404000e325041592e5359532e444446303100\",\n" +
            "  \"selectPpseResponse\": \"6f67840e325041592e5359532e4444463031a555bf0c5261194f09a000000059454301008701019f0a080001050100000000611a4f0aa00000035910100280018701019f0a08000105010000000061194f09d276000025474101008701019f0a080001050100000000\"\n" +
            "}\n";

}
