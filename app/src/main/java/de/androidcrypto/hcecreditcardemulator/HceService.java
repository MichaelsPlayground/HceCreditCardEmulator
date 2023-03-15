package de.androidcrypto.hcecreditcardemulator;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.io.File;

public class HceService extends HostApduService {

    public HceService() {
        // check for a specific file in internal storage
        System.out.println("start HceService");
        System.out.println("getBaseContext: " + getBaseContext());
        System.out.println("getApplicationContext: " + getApplicationContext());

        File file = getFilesDir();
        //File file2 = getExternalFilesDir(null);
        System.out.println("file exists: " + file.exists());
    }

    @Override
    public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
        return new byte[0];
    }

    @Override
    public void onDeactivated(int i) {

    }
}

/*

https://stackoverflow.com/questions/75736515/service-class-hostapduservice-has-no-access-to-files-npe-nullpointerexceptio

I'm trying to implement a Credit Card emulating app and setup a HostApduService-class that runs like expected.

Now I'm trying to load card data from a file (can be in internal or external storage, fixed name and folder) to be more flexible with the card data I'm going to emulate.

Although HostApduService is a service with a context **I'm not been able to access files from the service** - I'm always getting a "**java.lang.NullPointerException**":

    E/AndroidRuntime: FATAL EXCEPTION: main
        Process: de.androidcrypto.hcecreditcardemulator, PID: 26574
        java.lang.RuntimeException: Unable to instantiate service de.androidcrypto.hcecreditcardemulator.HceService: java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.File android.content.Context.getFilesDir()' on a null object reference
            at android.app.ActivityThread.handleCreateService(ActivityThread.java:3529)
            at android.app.ActivityThread.-wrap4(Unknown Source:0)
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1786)
            at android.os.Handler.dispatchMessage(Handler.java:105)
            at android.os.Looper.loop(Looper.java:164)
            at android.app.ActivityThread.main(ActivityThread.java:6944)
            at java.lang.reflect.Method.invoke(Native Method)
            at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:327)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1374)
         Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.File android.content.Context.getFilesDir()' on a null object reference
            at android.content.ContextWrapper.getFilesDir(ContextWrapper.java:237)
            at de.androidcrypto.hcecreditcardemulator.HceService.<init>(HceService.java:13)
            at java.lang.Class.newInstance(Native Method)
            at android.app.ActivityThread.handleCreateService(ActivityThread.java:3526)
            at android.app.ActivityThread.-wrap4(Unknown Source:0) 
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1786) 
            at android.os.Handler.dispatchMessage(Handler.java:105) 
            at android.os.Looper.loop(Looper.java:164) 
            at android.app.ActivityThread.main(ActivityThread.java:6944) 
            at java.lang.reflect.Method.invoke(Native Method) 
            at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:327) 
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1374) 

I checked that the applicationContext and baseContext are available within this class (not NULL).

This is the (very shortened) HceService.java class I'm using:

    package de.androidcrypto.hcecreditcardemulator;

    import android.nfc.cardemulation.HostApduService;
    import android.os.Bundle;

    import java.io.File;

    public class HceService extends HostApduService {

        public HceService() {
            // check for a specific file in internal storage
            System.out.println("start HceService");
            File file = getFilesDir();
            //File file2 = getExternalFilesDir(null);
            System.out.println("file exists: " + file.exists());
        }

        @Override
        public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
            return new byte[0];
        }

        @Override
        public void onDeactivated(int i) {

        }
    }

It will be started from AndroidManifest.xml:

    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools">

        <!-- Card emulation was introduced in API 19. -->
        <!-- Min/target SDK versions (<uses-sdk>) managed by build.gradle -->
        <uses-feature android:name="android.hardware.nfc.hce" android:required="true" />
        <uses-permission android:name="android.permission.NFC" />

        <application
            android:allowBackup="true"
            android:dataExtractionRules="@xml/data_extraction_rules"
            android:fullBackupContent="@xml/backup_rules"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:supportsRtl="true"
            android:theme="@style/Theme.HceCreditCardEmulator"
            tools:targetApi="31">
            <activity
                android:name=".MainActivity"
                android:exported="true">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />

                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>
            </activity>
            <!-- BEGIN_INCLUDE(CardEmulationManifest) -->
            <!-- Service for handling communication with NFC terminal. -->
            <service android:name=".HceService"
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

and has this aid.xml-file:

    <?xml version="1.0" encoding="utf-8"?>
        <aid-group android:description="@string/card_title" android:category="payment">
            <aid-filter android:name="F222222222"/>
            <aid-filter android:name="325041592E5359532E4444463031"/>
            <aid-filter android:name="A0000000031010"/>
        </aid-group>
    <!-- END_INCLUDE(CardEmulationXML) -->
    </host-apdu-service>

As it has the category "payments" the service can be easily switched in the device's settings:

    Settings -> connections -> NFC -> Tap and pay -> tab "payments" select "HCE"

At this time the NPE is thrown on this line 13 in HceService.java:

    File file = getFilesDir();

**So the main question is: how can I access to a file from a Service or better HostApduService class ?**

Thanks in advance.
 */