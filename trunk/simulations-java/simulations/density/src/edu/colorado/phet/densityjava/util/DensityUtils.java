package edu.colorado.phet.densityjava.util;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 1:07:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class DensityUtils {
    public static void addDir(final File f) throws IOException {
        final String s = f.getAbsolutePath();
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }


    public static void unzip(File zipFileName, File targetDir) throws IOException {
        unzip(zipFileName, targetDir, new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        });
    }

    public static void copy(InputStream source, OutputStream dest, boolean buffered) throws IOException {
        //TODO: buffering is disabled until file truncation issue is resolved
        buffered = false;
        if (buffered) {
            source = new BufferedInputStream(source);
            dest = new BufferedOutputStream(dest);
        }

        int bytesRead;

        byte[] buffer = new byte[1024];

        while ((bytesRead = source.read(buffer)) >= 0) {
            dest.write(buffer, 0, bytesRead);
        }
    }

    public static void copyAndClose(InputStream source, OutputStream dest, boolean buffered) throws IOException {
        copy(source, dest, buffered);
        source.close();
        dest.close();
    }

    public static void unzip(File zipFileName, File targetDir, FileFilter filter) throws IOException {
        ZipFile zipFile = new ZipFile(zipFileName);

        Enumeration enumeration = zipFile.entries();

        while (enumeration.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enumeration.nextElement();

            String name = entry.getName();

            if (filter.accept(new File(targetDir, name))) {
                if (entry.isDirectory()) {
                    new File(targetDir, name).mkdirs();
                } else {
                    File targetFile = new File(targetDir, name);

                    targetFile.getParentFile().mkdirs();

                    InputStream source = zipFile.getInputStream(entry);
                    FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

                    copyAndClose(source, fileOutputStream, false);
                }
            }
        }
        zipFile.close();
    }

    public static void copyTo(File source, File dest) throws IOException {
        dest.getParentFile().mkdirs();
        copyAndClose(new FileInputStream(source), new FileOutputStream(dest), true);
    }

}
