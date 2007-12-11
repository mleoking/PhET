/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import edu.colorado.phet.build.translate.TranslationDiscrepancy;

public class FileUtils {
    private static String DEFAULT_ENCODING = "utf-8";

    public static String loadFileAsString(File file) throws IOException {
        return loadFileAsString(file, DEFAULT_ENCODING);
    }

    public static String loadFileAsString(File file, String encoding) throws IOException {
        InputStream inStream = new FileInputStream(file);

        ByteArrayOutputStream outStream;

        try {
            outStream = new ByteArrayOutputStream();

            int c;
            while ((c = inStream.read()) >= 0) {
                outStream.write(c);
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }

        return new String(outStream.toByteArray(), encoding);
    }


    public static void filter(File inputFile, File outputFile, HashMap map) throws IOException {
        filter(inputFile, outputFile, map, DEFAULT_ENCODING);
    }

    public static void writeString(File outputFile, String text, String encoding) throws IOException {
        writeBytes(outputFile, text.getBytes(encoding));
    }

    public static void writeString(File outputFile, String text) throws IOException {
        writeString(outputFile, text, DEFAULT_ENCODING);
    }

    public static void writeBytes(File outputFile, byte[] bytes) throws IOException {
        outputFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        try {
            outputStream.write(bytes);
        }
        finally {
            outputStream.close();
        }
    }

    private static String replaceAll(String body, String find, String replacement) {
        boolean changed;

        do {
            changed = false;

            int indexOfFindText = body.indexOf(find);

            if (indexOfFindText != -1) {
                changed = true;

                String before = body.substring(0, indexOfFindText);
                String after = body.substring(indexOfFindText + find.length());

                body = before + replacement + after;
            }

        }
        while (changed);

        return body;
    }

    public static String filter(HashMap map, String file) {
        Set set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            String key = (String)iterator.next();
            String value = (String)map.get(key);

            //echo( "key = " + key + ", value=" + value );

            file = replaceAll(file, "@" + key + "@", value);
        }
        return file;
    }

    public static void filter(File source, File destFile, HashMap filterMap, String encoding) throws IOException {
        String text = loadFileAsString(source, encoding);
        String result = filter(filterMap, text);
        writeString(destFile, result, encoding);
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();

            for (int i = 0; i < children.length; i++) {
                delete(children[i]);
            }
        }

        file.delete();
    }

    public static void copyTo(File source, File dest) throws IOException {
        copyAndClose(new FileInputStream(source), new FileOutputStream(dest),true);
    }

    public static void copy(InputStream source, OutputStream dest, boolean buffered) throws IOException {
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

    public static void copyAndClose(InputStream source, OutputStream dest,boolean buffered) throws IOException {
        copy(source, dest,buffered);
        source.close();
        dest.close();
    }

    public static void unzip(File zipFileName, File targetDir) throws IOException {
        unzip(zipFileName, targetDir, new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        });
    }

    public static void unzip(File zipFileName, File targetDir, FileFilter filter) throws IOException {
        ZipFile zipFile = new ZipFile(zipFileName);

        Enumeration enumeration = zipFile.entries();

        while (enumeration.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)enumeration.nextElement();

            String name = entry.getName();

            if (filter.accept(new File(targetDir, name))) {
                if (entry.isDirectory()) {
                    new File(targetDir, name).mkdirs();
                }
                else {
                    File targetFile = new File(targetDir, name);

                    targetFile.getParentFile().mkdirs();

                    InputStream source = zipFile.getInputStream(entry);
                    FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

                    copyAndClose(source, fileOutputStream,false);

                    source.close();
                    fileOutputStream.close();
                }
            }
        }
        zipFile.close();
    }

    public static void testUnzip() throws IOException {
        final Pattern excludePattern = Pattern.compile( TranslationDiscrepancy.quote("quantum-tunneling") + "[\\\\/]localization[\\\\/]" + TranslationDiscrepancy.quote("quantum-tunneling") + ".*\\.properties");

        unzip(new File("/Users/jdegoes/Desktop/quantum-tunneling.jar"), new File("/Users/jdegoes/Desktop/temp-dir"), new FileFilter() {
            public boolean accept(File file) {
                if (excludePattern.matcher(file.getAbsolutePath()).find()) {
                    return false;
                }

                return true;
            }
        });
    }

    public static void zipSingleFile(CRC32 crc, File rootDir, File file, ZipOutputStream zipOutputStream) throws IOException {
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            for (int i = 0; i < f.length; i++) {
                zipSingleFile(crc, rootDir, f[i], zipOutputStream);
            }
        }
        else {
            String path = file.getAbsolutePath().substring(rootDir.getAbsolutePath().length());
            path = path.replace('\\', '/');
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            ZipEntry zipEntry = new ZipEntry(path);
//            zipEntry.setMethod(ZipEntry.STORED);
//            zipEntry.setCompressedSize(file.length());
//            zipEntry.setSize(file.length());
//            zipEntry.setCrc(crc.getValue());


            zipOutputStream.putNextEntry(zipEntry);

            FileInputStream inputStream = new FileInputStream(file);
            System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath() + ", path=" + path);
            copy(inputStream, zipOutputStream,false);


            zipOutputStream.closeEntry();
            inputStream.close();
        }
    }

    public static void zip(CRC32 crc, File dir, File dest) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
        zipSingleFile(crc, dir, dir, zipOutputStream);
        zipOutputStream.close();
    }

    public static void main(String[] args) throws IOException {
        zip(new CRC32(), new File("/Users/jdegoes/Desktop/temp-dir"), new File("/Users/jdegoes/Desktop/temp-dir-rezipped.zip"));
    }


}
