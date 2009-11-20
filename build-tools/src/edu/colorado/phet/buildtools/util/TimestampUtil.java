package edu.colorado.phet.buildtools.util;

import java.io.*;
import java.security.CodeSigner;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TimestampUtil {

    //returns timestamp date or null if not present
    //throws an exception if different parts have different timestamps
    public static Timestamp getTimestamp(File fileJAR) throws IOException {
        ArrayList<Timestamp> results = new ArrayList<Timestamp>();
        JarFile jarFile = new JarFile(fileJAR);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            readStream(jarFile, jarEntry);//must be read so that getCodeSigners doesn't return null
            CodeSigner[] a = jarEntry.getCodeSigners();
            for (int i = 0; a != null && i < a.length; i++) {
                CodeSigner codeSigner = a[i];
                Timestamp t = codeSigner.getTimestamp();
//                System.out.println("entry = " + jarEntry.getName() +
////                        ", signer = " + codeSigner +
//                        ", timestamp = " + t.getTimestamp());
                results.add(t);
            }
        }
        jarFile.close();
        if (results.size() == 0) {
            return null;
        } else {
            Timestamp t = results.get(0);
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).equals(t)) {
                    throw new RuntimeException("Different components had different timestamps");
                }
            }
            return t;
        }
    }

    private static void readStream(JarFile jarFile, JarEntry jarEntry) throws IOException {
        InputStream stream = jarFile.getInputStream(jarEntry);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line = bufferedReader.readLine();
        while (line != null) line = bufferedReader.readLine();
    }

    public static void main(String[] args) throws IOException {
        if (args.length==0) printUsage();
        Timestamp timestamp = getTimestamp(new File(args[0]));
        System.out.println("timestamp = " + timestamp.getTimestamp());
    }

    private static void printUsage() {
        System.out.println("Usage: args[0] = filename");
    }
}
