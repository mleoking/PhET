package edu.colorado.phet.build.reports;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.FileUtils;

/**
 * Created by: Sam
 * May 13, 2008 at 10:28:01 PM
 */
public class GenerateHTML {
    public static void main( String[] args ) throws IOException {
        new GenerateHTML().start();
    }

    private void start() throws IOException {
        String html = "<html>";
        File dir = new File( "C:\\Users\\Sam\\Desktop\\sim-out-test-1" );
        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isFile() ) {
                html += "<p>\n" +
                        "<img src=\"" + file.getName() + "\">\n" +
                        "</p>";
            }
        }
        html += "</html>";
        FileUtils.writeString( new File( dir, "index.html" ), html );
    }
}
