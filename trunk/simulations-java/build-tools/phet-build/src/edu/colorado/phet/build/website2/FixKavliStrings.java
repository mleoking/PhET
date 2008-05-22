package edu.colorado.phet.build.website2;

import edu.colorado.phet.build.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 22, 2008
 * Time: 12:31:00 PM
 */
public class FixKavliStrings {
    public static void main(String[] args) throws IOException {
        File f = new File(args[0]);
        new FixKavliStrings().processFile(f);
    }

    private void processFile(File f) throws IOException {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                processFile(file);
            }
        } else if (f.getName().endsWith("jnlp")) {
//        File file = new File("C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\circuit-construction-kit\\deploy\\circuit-construction-kit-ac_ja.jnlp");
            String s = FileUtils.loadFileAsString(f, "UTF-16");
            int index = s.toLowerCase().indexOf("UTF-16");
            boolean utf16 = index >= 0;
            System.out.println("File=" + f + "utf16 = " + utf16);
            if (utf16) {
//        System.out.println("before=\n" + s);
                s = s.replaceAll("<icon.*>", "");
//        System.out.println("after=\n" + s);
                FileUtils.writeString(f, s, "UTF-16");
                System.out.println("Wrote over " + f.getAbsolutePath());
            } else {
                System.out.println("Not UTF-16");
            }
        } else {
            System.out.println("Skipping: " + f);
        }
    }
}
