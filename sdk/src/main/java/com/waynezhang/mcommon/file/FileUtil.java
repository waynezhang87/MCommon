package com.waynezhang.mcommon.file;

import com.waynezhang.mcommon.util.L;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {
    public static final String TAG = FileUtil.class.getSimpleName();

    public static boolean createDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 删除文件夹，包括目录下所有文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
            }
            file.delete();
        } else {
            L.d(TAG, "delete file not exists : " + file.getAbsolutePath());
        }
    }

    public static int copyFile(String srcPath, String destPath) {
        return copyFile(srcPath, destPath, true);
    }

    public static int copyFile(String srcPath, String destPath, boolean rewrite) {
        return copyFile(new File(srcPath), new File(destPath), rewrite);
    }

    public static int copyFile(File fromFile, File toFile, boolean rewrite) {
        if (!fromFile.exists()) {
            return 1;
        }

        if (!fromFile.isFile()) {
            return 2;
        }

        if (!fromFile.canRead()) {
            return 3;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        // 当文件不存时，canWrite一直返回的都是false
        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;

            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            return 5;
        }

        return 0;
    }
}
