package de.androidcrypto.hcecreditcardemulator;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.androidcrypto.hcecreditcardemulator.models.Aids;

/**
 * This class is responsible to load the individual card data from internal storage
 * It returns the Aids model
 */

public class LoadEmulatorData {
    private static final String TAG = "LoadEmulatorData";

    //private final String CARDS_FOLDER = "cards";
    private final String DATA_FOLDER = "data"; // the files for CreditCardKernelService are stored here
    private final String CREDIT_CARD_KERNEL_SERVICE_DATA_FILE = "card.json";
    private final Context context;

    public LoadEmulatorData(@NonNull Context context) {
        this.context = context;
    }

    /*
    public ArrayList<String> getFileList() {
        return listFilesInInternalStorage(CARDS_FOLDER);
    }

    public String getFileContent(@NonNull String fileName) {
        return readStringFileFromInternalStorage(fileName, CARDS_FOLDER);
    }

     */

    public Aids getAidsFromInternalStorage() {
        Aids aids;
        Gson gson = new Gson();
        String jsonLoaded = readStringFileFromInternalStorage(CREDIT_CARD_KERNEL_SERVICE_DATA_FILE, DATA_FOLDER);
        if (TextUtils.isEmpty(jsonLoaded)) {
            Log.e(TAG, "Error: File not found");
            return null;
        }
        try {
            aids = gson.fromJson(jsonLoaded, Aids.class);
        } catch (IllegalStateException | JsonSyntaxException e) {
            Log.e(TAG, "Error: cannot read the file - is it really an Export emulation data file ?");
            return null;
        }
        Log.d(TAG, "Loaded file for emulation: " + CREDIT_CARD_KERNEL_SERVICE_DATA_FILE);
        return aids;
    }

    /*
    public Aids getAidsFromInternalStorage(@NonNull String fileName) {
        Aids aids;
        Gson gson = new Gson();

        //String jsonLoaded = VisaCard1Data.visacard1DataJsonString;
        //String jsonLoaded = MasterCard1Data.mastercardcard1DataJsonString;
        String jsonLoaded = GiroCard2Data.girocard2DataJsonString;
        try {
            aids = gson.fromJson(jsonLoaded, Aids.class);
        } catch (IllegalStateException | JsonSyntaxException e) {
            Log.e(TAG, "Error: cannot read the file - is it really an Export emulation data file ?");
            return null;
        }
        Log.d(TAG, "Loaded file for emulation: " + fileName);
        return aids;
    }
*/

    /**
     * read a file from internal storage and return the content as UTF-8 encoded string
     * @param filename
     * @param subfolder
     * @return the content as String
     */
    private String readStringFileFromInternalStorage(@NonNull String filename, String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(context.getFilesDir(), filename);
            //file = context.getExternalFilesDir(filename);
        } else {
            File subfolderFile = new File(context.getFilesDir(), subfolder);
            //File subfolderFile = context.getExternalFilesDir(subfolder);
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
     * checks if a file in internal storage is existing
     * @param completeFilename with all subfolders
     * @return true if file exists and false if not
     */
    private boolean fileExistsInInternalStorage(String completeFilename) {
        File file = new File(context.getFilesDir(), completeFilename);
        //File file = context.getExternalFilesDir(completeFilename);
        return file.exists();
    }

    /**
     * concatenates the filename with a subfolder
     * @param filename
     * @param subfolder
     * @return a String subfolder | File.separator | filename
     */
    private String concatenateFilenameWithSubfolder(@NonNull String filename, String subfolder) {
        if (TextUtils.isEmpty(subfolder)) {
            return filename;
        } else {
            return subfolder + File.separator + filename;
        }
    }

    /**
     * list all files in the (sub-) folder of internal storage
     * @param subfolder
     * @return ArrayList<String> with filenames
     */
    public ArrayList<String> listFilesInInternalStorage(String subfolder) {
        File file;
        if (TextUtils.isEmpty(subfolder)) {
            file = new File(context.getFilesDir(), "");
        } else {
            file = new File(context.getFilesDir(), subfolder);
            /*
            if (!subfolderFile.exists()) {
                subfolderFile.mkdirs();
            }

             */

        }
        System.out.println("file: " + file.getAbsolutePath());
        File[] files = file.listFiles();
        if (files == null) {
            System.out.println("files is NULL");
            return null;
        } else {
            System.out.println("files is NOT NULL");
        }
        ArrayList<String> fileNames = new ArrayList<>();
        for (File value : files) {
            if (value.isFile()) {
                fileNames.add(value.getName());
            }
        }
        return fileNames;
    }

}
