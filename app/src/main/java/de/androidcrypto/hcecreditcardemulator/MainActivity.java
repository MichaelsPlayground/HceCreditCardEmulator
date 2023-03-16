package de.androidcrypto.hcecreditcardemulator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.androidcrypto.hcecreditcardemulator.common.logger.Log;
import de.androidcrypto.hcecreditcardemulator.common.logger.LogFragment;
import de.androidcrypto.hcecreditcardemulator.common.logger.LogWrapper;
import de.androidcrypto.hcecreditcardemulator.common.logger.MessageOnlyLogFilter;
import de.androidcrypto.hcecreditcardemulator.models.Aids;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    Button selectImportFile, saveImportFileToInternalStorage, listImportedFilesInInternalStorage, selectImportedFilesInInternalStorage;
    com.google.android.material.textfield.TextInputLayout givenNameLayout;
    com.google.android.material.textfield.TextInputEditText givenName, information;
    TextView tvLog;

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    private Context contextSave;
    private byte[] contentLoadedByte;
    private String jsonContentLoaded;
    private String importFileName;
    private final String CARDS_FOLDER = "cards"; // the imported files are stored here
    private final String JSON_FILE_EXTENSION = ".json";
    private final String CREDIT_CARD_KERNEL_SERVICE_DATA_FILE = "card.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        contextSave = getApplicationContext();

        selectImportFile = findViewById(R.id.btnSelectImportFile);
        saveImportFileToInternalStorage = findViewById(R.id.btnSaveImportFileToInternalStorage);
        listImportedFilesInInternalStorage = findViewById(R.id.btnListImportedFilesInInternalStorage);
        selectImportedFilesInInternalStorage = findViewById(R.id.btnSelectImportedFileInInternalStorage);
        givenNameLayout = findViewById(R.id.etGivenNameLayout);
        givenName = findViewById(R.id.etGivenName);
        information = findViewById(R.id.etInformation);
        tvLog = findViewById(R.id.tvLog);

        //givenName.setFilters(new InputFilter[] { filter1});


        initializeLogging();

        // todo workflow
        // 1 button import emulation data file
        // -> filePicker to choose file from external dir
        // -> import the file to internal storage subfolder cards
        // 2 button list imported cards
        // -> list all files in the subfolder cards
        // 3 button choose file from internal storage / subfolder cards as card to load / emulate
        // -> list files in internal storage subfolder cards with fileChooser
        // -> copy the file to files root folder


        Log.d(TAG, getTimestampMillis() + " System started");

        selectImportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                information.setText("");
                jsonContentLoaded = "";
                contentLoadedByte = null;
                openFileFromExternalSharedStorage();
            }
        });

        saveImportFileToInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentLoadedByte == null) {
                    writeToUiToast("you need to import a file before saving it");
                    return;
                }
                String givenNameString = givenName.getText().toString();
                if (TextUtils.isEmpty(givenNameString)) {
                    writeToUiToast("you need to give a name to the file before saving it");
                    return;
                }
                boolean nameIsValid = isValid(givenNameString);
                if (!nameIsValid) {
                    givenNameLayout.setError("characters a-z and 0-9 allowed here only");
                } else {
                    givenNameLayout.setError(null);
                    writeToUiToast("files save in internal storage with fileName " + givenNameString);
                    final String storageName = givenNameString + JSON_FILE_EXTENSION;
                    boolean writeResult = writeTextToInternalStorage(storageName, CARDS_FOLDER, jsonContentLoaded);
                    if (writeResult) {
                        information.setText("The import file was stored in internal storage with this name: " + storageName);
                    } else {
                        information.setText("ERROR: the import file was NOT stored in internal storage with this name: " + storageName + "\nPlease reimport the file.");
                        saveImportFileToInternalStorage.setEnabled(false);
                    }
                }
            }
        });

        listImportedFilesInInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> filesInInternalStorage = listFilesInInternalStorage(CARDS_FOLDER);
                int nrOfFiles = filesInInternalStorage.size();
                StringBuilder sb = new StringBuilder();
                sb.append("found ").append(nrOfFiles).append(" files:");
                for (int i = 0; i < nrOfFiles; i++) {
                    sb.append("\n").append(filesInInternalStorage.get(i).replace(JSON_FILE_EXTENSION, ""));
                }
                information.setText(sb.toString());
            }
        });

        selectImportedFilesInInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> filesInInternalStorage = listFilesInInternalStorage(CARDS_FOLDER, JSON_FILE_EXTENSION);
                int nrOfFiles = filesInInternalStorage.size();

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                //builderSingle.setIcon(R.drawable.hce_96_96);
                builderSingle.setTitle("Select One Name:-");

                /* fixed data
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Hardik");
                arrayAdapter.add("Archit");
                arrayAdapter.add("Jignesh");
                arrayAdapter.add("Umang");
                arrayAdapter.add("Gatti");*/
                // dynamic data
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice, filesInInternalStorage);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        // todo use this file and copy it to the main files folder with a fixed name so that the
                        // todo CreditCardKernelServices can pick it up for general usage
                        /*
                        // this is a mini dialog with just an OK button
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                        */
                    }
                });
                builderSingle.show();
            }
        });
    }

    /**
     * checks that the imported file contend is a valid Aids model file
     */
    private void checkImportedContent(){
        Aids aids;
        Gson gson = new Gson();
        // disable next step until a reading is done
        saveImportFileToInternalStorage.setEnabled(false);
        if (contentLoadedByte == null) {
            information.setText("there is no content to import, sorry");
            writeToUiToast("there is no content to import, sorry");
            return;
        }
        jsonContentLoaded = new String(contentLoadedByte, StandardCharsets.UTF_8);
        try {
            aids = gson.fromJson(jsonContentLoaded, Aids.class);
        } catch (IllegalStateException | JsonSyntaxException e) {
            information.setText("Error: cannot read the file - is it really an Export emulation data file ?");
            android.util.Log.e(TAG, "Error: cannot read the file - is it really an Export emulation data file ?");
            writeToUiToast("Error: cannot read the file - is it really an Export emulation data file ?");
            return;
        }
        saveImportFileToInternalStorage.setEnabled(true);
        // aids is valid now
        int nrOfAid = aids.getNumberOfAid();
        StringBuilder sb = new StringBuilder();
        sb.append("found ").append(nrOfAid).append(" applications on the card:");
        for (int i = 0; i < nrOfAid; i++) {
            sb.append("\n").append(aids.getAid()[i].getAidName());
        }
        givenName.setText(getSafeFilenameWithoutExtension(aids.getCardName()));
        information.setText(sb.toString());
    }


    /*
    sector UI tools
     */

    // This expression allows only characters from a-z, A-Z, numbers from 0-9 and a '_' (underscore) character.
    public static boolean isValid(String str)
    {
        boolean isValid = false;
        //String expression = "^[a-z_A-Z0-9 ]*$"; // This expression allows only characters from a-z, A-Z, numbers from 0-9 and a ‘ ‘(space) or '_' (underscore) character.
        String expression = "^[a-z_A-Z0-9]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }

    /**
     * This importFilter accepts character, digits, underscores, and hyphens.
     * usage: edittext.setFilters(new InputFilter[] { filter });
     */
/*
    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            for (int i = start;i < end;i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) &&
                        !Character.toString(source.charAt(i)).equals("_") &&
                        !Character.toString(source.charAt(i)).equals("-"))
                {
                    return "";
                }
            }
            return null;
        }
    };

    InputFilter filter1 = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int idstart, int dend) {
            if (source.equals("")) {
                return source;
            }
            if (source.toString().matches("[a-zA-Z ]+")) {
                return source;
            }
            return "";
        }
    };

    InputFilter filter2 = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int idstart, int dend) {
            return null;
        }
    };
*/

    private void writeToUiToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /** Create a chain of targets that will receive log data */
    //@Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.

        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        //msgFilter.setNext((LogNode) tvLog);

        Log.i(TAG, "Ready");

        // change textSize in LogFragment line 84
        // mLogView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    }

    private static String getTimestampMillis() {
        // O = SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ZonedDateTime
                    .now(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("uuuu.MM.dd HH:mm:ss.SSS"));
        } else {
            return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
        }
    }

    /**
     * section internal storage handling
     */

    /**
     * concatenates the filename with a subfolder
     * @param filename
     * @param subfolder
     * @return a String subfolder | File.separator | filename
     */
    public String concatenateFilenameWithSubfolder(@NonNull String filename, String subfolder) {
        if (TextUtils.isEmpty(subfolder)) {
            return filename;
        } else {
            return subfolder + File.separator + filename;
        }
    }

    /**
     * splits a complete filename in the filename [0] and extension [1]
     * @param filename with extension
     * @return a String array with filename [0] and extension [1]
     */
    private String[] splitFilename(@NonNull String filename) {
        return filename.split(".");
    }

    /**
     * counts the number of file extensions (testing on '.' in the filename)
     * @param filename
     * @return number of extensions
     */
    //public int countChar(String str, char c)
    private int getNumberOfExtensions(@NonNull String filename)
    {
        char c = '.';
        int count = 0;
        for(int i=0; i < filename.length(); i++)
        {    if(filename.charAt(i) == c)
            count++;
        }
        return count;
    }
    private int getNumberOfExtensionsOld(@NonNull String filename) {
        String[] parts = filename.split(".");
        System.out.println("parts: " + parts.length);
        return parts.length - 1;
    }

    /**
     * converts a filename to a Android safe filename
     * @param filename WITHOUT extension
     * @return new filename
     */
    private String getSafeFilename(@NonNull String filename) {
        final int MAX_LENGTH = 127;
        filename = filename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        int end = Math.min(filename.length(),MAX_LENGTH);
        return filename.substring(0,end);
    }

    /**
     * converts a filename to a Android safe filename
     * @param filename WITHOUT extension
     * @return new filename
     */
    private String getSafeFilenameWithoutExtension(@NonNull String filename) {
        final int MAX_LENGTH = 127;
        filename = filename.replaceAll("[^a-zA-Z0-9]", "_");
        int end = Math.min(filename.length(),MAX_LENGTH);
        return filename.substring(0,end);
    }

    /**
     * read a file from internal storage and return the content as UTF-8 encoded string
     * @param filename
     * @param subfolder
     * @return the content as String
     */
    public String readStringFileFromInternalStorage(@NonNull String filename, String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), filename);
        } else {
            File subfolderFile = new File(getFilesDir(), subfolder);
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }
            file = new File(subfolderFile, filename);
        }
        String completeFilename = concatenateFilenameWithSubfolder(filename, subfolder);
        if (!fileExistsInInternalStorage(completeFilename)) {
            return "";
        }
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * writes a string to the filename in internal storage, If a subfolder is provided the file is created in the subfolder
     * if the file is existing it will be overwritten
     * @param filename
     * @param subfolder
     * @param data
     * @return true if writing is successful and false if not
     */
    private boolean writeTextToInternalStorage(@NonNull String filename, String subfolder, @NonNull String data){
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), filename);
        } else {
            File subfolderFile = new File(getFilesDir(), subfolder);
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }
            file = new File(subfolderFile, filename);
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.append(data);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * read a file from internal storage and return the content as byte array
     * @param filename
     * @param subfolder
     * @return the content as String
     */
    public byte[] readBinaryDataFromInternalStorage(@NonNull String filename, String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), filename);
        } else {
            File subfolderFile = new File(getFilesDir(), subfolder);
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }
            file = new File(subfolderFile, filename);
        }
        String completeFilename = concatenateFilenameWithSubfolder(filename, subfolder);
        if (!fileExistsInInternalStorage(completeFilename)) {
            return null;
        }
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * writes a byte array to the filename in internal storage, If a subfolder is provided the file is created in the subfolder
     * if the file is existing it will be overwritten
     * @param filename
     * @param subfolder
     * @param data
     * @return true if writing is successful and false if not
     */
    private boolean writeBinaryDataToInternalStorage(@NonNull String filename, String subfolder, @NonNull byte[] data){
        final int BUFFER_SIZE = 8096;
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), filename);
        } else {
            File subfolderFile = new File(getFilesDir(), subfolder);
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }
            file = new File(subfolderFile, filename);
        }
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                FileOutputStream out = new FileOutputStream(file))
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int nread;
            while ((nread = in.read(buffer)) > 0) {
                out.write(buffer, 0, nread);
            }
            out.flush();
        } catch (IOException e) {
            android.util.Log.e(TAG, "ERROR on encryption: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * checks if a file in internal storage is existing
     * @param completeFilename with all subfolders
     * @return true if file exists and false if not
     */
    private boolean fileExistsInInternalStorage(String completeFilename) {
        File file = new File(getFilesDir(), completeFilename);
        return file.exists();
    }

    /**
     * deletes a file in internal storage
     * @param completeFilename with all subfolders
     * @return true if deletion was successful
     */
    private boolean fileDeleteInInternalStorage(String completeFilename) {
        File file = new File(getFilesDir(), completeFilename);
        return file.delete();
    }

    /**
     * list all files in the (sub-) folder of internal storage
     * @param subfolder
     * @return ArrayList<String> with filenames
     */
    public ArrayList<String> listFilesInInternalStorage(String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), "");
        } else {
            file = new File(getFilesDir(), subfolder);
            /*
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }

             */

        }
        File[] files = file.listFiles();
        if (files == null) return null;
        ArrayList<String> fileNames = new ArrayList<>();
        for (File value : files) {
            if (value.isFile()) {
                fileNames.add(value.getName());
            }
        }
        return fileNames;
    }

    /**
     * list all files in the (sub-) folder of internal storage without the file extension
     * @param subfolder
     * @param fileExtension
     * @return ArrayList<String> with filenames
     */
    public ArrayList<String> listFilesInInternalStorage(String subfolder, String fileExtension) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), "");
        } else {
            file = new File(getFilesDir(), subfolder);
            /*
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }

             */

        }
        File[] files = file.listFiles();
        if (files == null) return null;
        ArrayList<String> fileNames = new ArrayList<>();
        for (File value : files) {
            if (value.isFile()) {
                fileNames.add(value.getName().replace(fileExtension, ""));
            }
        }
        return fileNames;
    }

    /**
     * list all folder in the (sub-) folder of internal storage
     * @param subfolder
     * @return ArrayList<String> with folder names
     */
    public ArrayList<String> listFolderInInternalStorage(String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(getFilesDir(), "");
        } else {
            file = new File(getFilesDir(), subfolder);
        }
        File[] files = file.listFiles();
        if (files == null) return null;
        ArrayList<String> folderNames = new ArrayList<>();
        for (File value : files) {
            if (!value.isFile()) {
                folderNames.add(value.getName());
            }
        }
        return folderNames;
    }

    /**
     * section open a file from external storage
     */

    private void openFileFromExternalSharedStorage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        boolean pickerInitialUri = false;
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        fileOpenActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> fileOpenActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent resultData = result.getData();
                        // The result data contains a URI for the document or directory that
                        // the user selected.
                        Uri uri = null;
                        if (resultData != null) {
                            uri = resultData.getData();
                            // Perform operations on the document using its URI.
                            try {
                                //contentLoadedByte = readBytesFromUri(uri);
                                readBytesFromUri(uri);
                                //showFileContent();
                            } catch (IOException e) {
                                contentLoadedByte = null;
                                e.printStackTrace();
                                writeToUiToast("ERROR: " + e.toString());
                                return;
                            }
                        }
                    }
                }
            });

    private byte[] readBytesFromUri(Uri uri) throws IOException {
        if (contextSave != null) {
            ContentResolver contentResolver = contextSave.getContentResolver();
            importFileName = queryName(contentResolver, uri);
            // warning: contextSave needs to get filled

            Thread DoReadFile = new Thread() {
                public void run() {
                    try (InputStream inputStream = contentResolver.openInputStream(uri);
                         // this dynamically extends to take the bytes you read
                         ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();) {
                        // this is storage overwritten on each iteration with bytes
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        // we need to know how may bytes were read to write them to the byteBuffer
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteBuffer.write(buffer, 0, len);
                        }
                        // and then we can return your byte array.
                        //return byteBuffer.toByteArray();
                        contentLoadedByte = byteBuffer.toByteArray();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkImportedContent();
                                //saveFileToInternalStorage();
                                //Toast.makeText(DeleteGoogleDriveFile.this, "selected file deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            };
            DoReadFile.start();

            /*
            try (InputStream inputStream = contentResolver.openInputStream(uri);
                 // this dynamically extends to take the bytes you read
                 ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();) {
                // this is storage overwritten on each iteration with bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                // and then we can return your byte array.
                return byteBuffer.toByteArray();
            }
            */
        }
        return null;
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

}