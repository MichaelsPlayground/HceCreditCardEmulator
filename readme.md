# HCE Credit Card Emulator

Actual state: **unfinished project**

This app emulates a Credit Card using  the **Host Card Emulation** technology in Android.

The app will accept **APDU** commands from a contactless card-reader via **NFC** and send 
responds back to the card reader up to a transaction.

You can import your real card data for this purpose to that the app is working as clone of your card.

Curated list of AID: https://en.wikipedia.org/wiki/EMV

```plaintext

```




AndroidManifest.xml:
```plaintext
...
    <!-- Card emulation was introduced in API 19. -->
    <!-- Min/target SDK versions (<uses-sdk>) managed by build.gradle -->
    <uses-feature android:name="android.hardware.nfc.hce" android:required="true" />
    <uses-permission android:name="android.permission.NFC" />
...
        </activity>
        <!-- BEGIN_INCLUDE(CardEmulationManifest) -->
        <!-- Service for handling communication with NFC terminal. -->
        <service android:name=".cardemulation.CardService"
            android:exported="true"
            android:permission="android.permission.BIND_NFC_SERVICE">
            <!-- Intent filter indicating that we support card emulation. -->
            <intent-filter>
                <action android:name="android.nfc.cardemulation.action.HOST_APDU_SERVICE"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
            <!-- Required XML configuration file, listing the AIDs that we are emulating cards
                 for. This defines what protocols our card emulation service supports. -->
            <meta-data android:name="android.nfc.cardemulation.host_apdu_service"
                android:resource="@xml/aid_list"/>
        </service>
        <!-- END_INCLUDE(CardEmulationManifest) -->
    </application>
</manifest>
```

/res/xml/aid_list:
```plaintext
<?xml version="1.0" encoding="utf-8"?>
<!--
Copyright (C) 2013 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<!-- This file defines which AIDs this application should emulate cards for.

     Vendor-specific AIDs should always start with an "F", according to the ISO 7816 spec. We
     recommended vendor-specific AIDs be at least 6 characters long, to provide sufficient
     uniqueness. Note, however, that longer AIDs may impose a burden on non-Android NFC terminals.
     AIDs may not exceed 32 characters (16 bytes).

     Additionally, AIDs must always contain an even number of characters, in hexadecimal format.

     In order to avoid prompting the user to select which service they want to use when the device
     is scanned, this app must be selected as the default handler for an AID group by the user, or
     the terminal must select *all* AIDs defined in the category simultaneously ("exact match").
-->
<!-- BEGIN_INCLUDE(CardEmulationXML) -->
<host-apdu-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/service_name"
    android:apduServiceBanner="@drawable/hce_260_96"
    android:requireDeviceUnlock="false">
    <!--
    If category="payment" is used for any aid-groups, you must also add an android:apduServiceBanner
    attribute above, like so:

    android:apduServiceBanner="@drawable/settings_banner"

     apduServiceBanner should be 260x96 dp. In pixels, that works out to...
       - drawable-xxhdpi: 780x288 px
       - drawable-xhdpi:  520x192 px
       - drawable-hdpi:   390x144 px
       - drawable-mdpi:   260x96  px

    The apduServiceBanner is displayed in the "Tap & Pay" menu in the system Settings app, and
    is only displayed for apps which implement the "payment" AID category.

    Since this sample is implementing a non-standard card type (a loyalty card, specifically), we
    do not need to define a banner.

    Important: category="payment" should only be used for industry-standard payment cards. If you are
        implementing a closed-loop payment system (e.g. stored value cards for a specific merchant
        or transit system), use category="other". This is because only one "payment" card may be
        active at a time, whereas all "other" cards are active simultaneously (subject to AID
        dispatch).
    -->

    <aid-group android:description="@string/card_title" android:category="payment">
        <!-- select PPSE 2PAY.SYS.DDF01 -->        
        <aid-filter android:name="325041592E5359532E4444463031"/>
        <!-- VisaCard -->
        <aid-filter android:name="A0000000031010"/>
        <aid-filter android:name="A0000000032010"/>
        <aid-filter android:name="A0000000032020"/>
        <aid-filter android:name="A0000000038010"/>
        <!-- MasterCard -->
        <aid-filter android:name="A0000000041010"/>
        <aid-filter android:name="A0000000049999"/>
        <aid-filter android:name="A0000000043060"/>
        <aid-filter android:name="A0000000046000"/>
        <aid-filter android:name="A0000000048002"/>
        <aid-filter android:name="A0000000050001"/>
        <!-- American Express -->
        <aid-filter android:name="A00000002501"/>
        <aid-filter android:name="A00000079001"/>
    </aid-group>
<!-- END_INCLUDE(CardEmulationXML) -->
</host-apdu-service>
```

build.gradle (app):
```plaintext
    // needed for Json handling
    implementation 'com.google.code.gson:gson:2.9.1'
```



