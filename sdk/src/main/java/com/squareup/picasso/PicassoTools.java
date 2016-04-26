package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.ByteString;

/**
 * Created by don on 1/5/15.
 */
public class PicassoTools {
    public static void clearCache (Context context) {
        Picasso.with(context).cache.clear();
        deleteDirectoryTree(context.getCacheDir());
    }

    private static void deleteDirectoryTree(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectoryTree(child);
            }
        }

        fileOrDirectory.delete();
    }

    private static String createKey(Request request) {
        return Utils.createKey(request);
    }

    public static String getKey(String path) {
        Request req = new Request.Builder(Uri.parse(path)).build();
        String key = createKey(req);
        return key;
    }

    public static String getOkHttpKey(String url) {
        return md5Hex(url);
    }

    /** Returns a 32 character string containing an MD5 hash of {@code s}. */
    public static String md5Hex(String s) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5bytes = messageDigest.digest(s.getBytes("UTF-8"));
            return ByteString.of(md5bytes).hex();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
