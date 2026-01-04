package com.Projet.forum;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtils {

    public static Uri createTempImage(Context context, byte[] data) throws Exception {
        File file = File.createTempFile("upload_", ".jpg", context.getCacheDir());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        return Uri.fromFile(file);
    }
}
