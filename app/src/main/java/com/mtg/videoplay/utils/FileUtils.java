package com.mtg.videoplay.utils;

import static com.mtg.videoplay.view.dialog.DialogChange.context;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.mtg.videoplay.BuildConfig;
import com.mtg.videoplay.OnActionCallback;
import com.mtg.videoplay.model.FileVideo;
import com.mtg.videoplay.view.fragment.VideoFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FileUtils {

    public final static String DEFAULT_TEMP = "/.Temp/";

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString;
        if (fileSize <= 0) {
            fileSizeString = "0KB";
        } else if (fileSize < (1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < (1024 * 1024 * 1024)) {
            fileSizeString = df.format((double) fileSize / (1024 * 1024)) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / (1024 * 1024 * 1024)) + "GB";
        }
        return fileSizeString;
    }

//    public static String getMimeType(Uri uri) {
//        String mimeType = null;
//        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
//            ContentResolver cr = App.getInstance().getContentResolver();
//            mimeType = cr.getType(uri);
//        } else {
//            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString());
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                    fileExtension.toLowerCase());
//        }
//        return mimeType;
//    }


    public static String getFileExtensionNoPoint(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        return getFileExtensionNoPoint(new File(path));
    }


    private static String getFileExtensionNoPoint(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        if (fileName != null && fileName.length() > 0) {
            int lastIndex = fileName.lastIndexOf('.');
            if ((lastIndex > -1) && (lastIndex < (fileName.length() - 1))) {
                return fileName.substring(lastIndex + 1);
            }
        }
        return "";
    }


    private static boolean isCompareFiles(String path1, String path2) {
        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }
        if (path1.equalsIgnoreCase(path2)) {
            return true;
        } else {
            return isCompareFiles(new File(path1), new File(path2));
        }
    }

    private static boolean isCompareFiles(File file1, File file2) {
        if (file1 == null || file2 == null) {
            return false;
        }
        if (file1.getPath().equalsIgnoreCase(file2.getPath())) {
            return true;
        }
        return false;
    }


    private static boolean isSDExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static String getSDCardFilesPath() {
        if (!isSDExist()) {
            return "";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }


    private static String getSDCardDownloadPath() {
        if (!isSDExist()) {
            return "";
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
    }


    public static String getFileNameNoExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getFileNameNoExtension(new File(filePath));
    }


    public static String getFileNameNoExtension(File file) {
        if (file == null) {
            return "";
        }
        String filename = file.getName();
        if (!TextUtils.isEmpty(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    private static boolean isFileExist(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                return file.exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private static String getFileMimeTypeFromExtension(String fileType) {
        try {
            if (TextUtils.isEmpty(fileType)) {
                return "*/*";
            }
            fileType = fileType.replace(".", "");
            if (fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("wps")) {
                fileType = "doc";
            } else if (fileType.equalsIgnoreCase("xlsx")) {
                fileType = "xls";
            } else if (fileType.equalsIgnoreCase("pptx")) {
                fileType = "ppt";
            }
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            if (mimeTypeMap.hasExtension(fileType)) {
                return mimeTypeMap.getMimeTypeFromExtension(fileType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "*/*";
    }

//    public static String getAppTempPath() {
//        return getAvailableFilesPathAndroidData(true) + DEFAULT_TEMP;
//    }


//    private static String getAvailableFilesPathAndroidData(boolean boolToCache) {
//        if (!isSDExist()) {
//            if (boolToCache) {
//                return App.getInstance().getCacheDir().getAbsolutePath() + "/";
//            }
//            return App.getInstance().getFilesDir().getAbsolutePath() + "/";
//        } else {
//            if (boolToCache) {
//                return App.getInstance().getExternalCacheDir().getAbsolutePath() + "/";
//            }
//            return App.getInstance().getExternalFilesDir("").getAbsolutePath() + "/";
//        }
//    }


    public static void deleteFileAndroid11(AppCompatActivity activity, FileVideo media, ActivityResultLauncher<IntentSenderRequest> launcher) {
        File file = new File(media.getPath());
        Uri uri = Uri.fromFile(file);
        ContentResolver contentResolver = activity.getContentResolver();
        PendingIntent pendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            ArrayList<Uri> collection = new ArrayList<>();
//            String mineType = getMimeType(uri);
            Uri contentUri = MediaStore.Video.Media.getContentUri("external");

//            boolean isVideo = mineType.contains("video");
//            if (isVideo) {
//            }
//            boolean isAudio = mineType.contains("audio");
//            if (isAudio) {
//                contentUri = MediaStore.Audio.Media.getContentUri("external");
//            }
            collection.add(ContentUris.withAppendedId(contentUri, media.getId()));
            pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);

        }
        if (pendingIntent != null) {
            IntentSender sender = pendingIntent.getIntentSender();
            IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
            launcher.launch(request);
        }
    }

    public static ActivityResultLauncher<IntentSenderRequest> requestLauncher(AppCompatActivity activity, OnActionCallback callback) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        callback.callback("key1",null);
                    }
                });
    }


    public static void rename(Context activity, FileVideo media, ActivityResultLauncher<IntentSenderRequest> launcher) {
        File file = new File(media.getPath());
        Uri uri = Uri.fromFile(file);
        ContentResolver contentResolver = activity.getContentResolver();
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            ArrayList<Uri> collection = new ArrayList<>();
            Uri contentUri = MediaStore.Video.Media.getContentUri("external");
            collection.add(ContentUris.withAppendedId(contentUri, media.getId()));
            pendingIntent = MediaStore.createWriteRequest(contentResolver, collection);
            if (pendingIntent != null) {
                IntentSender sender = pendingIntent.getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                launcher.launch(request);
            }
        }


    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static ActivityResultLauncher<IntentSenderRequest> requestLauncher(AppCompatActivity activity) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        FileVideo media = VideoFragment.RenameList.get(VideoFragment.lc_rename);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, VideoFragment.newName);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            context.getContentResolver().update(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId()), contentValues, null);
                        }
                    }
                });
    }


}
