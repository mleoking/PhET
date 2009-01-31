package edu.colorado.phet.build.util;

import java.io.*;

/**
 * Created by: Sam
 * Aug 5, 2008 at 10:47:00 AM
 */
public class LicenseInfo {
    private File file;

    public LicenseInfo( File licenseInfoTXTFile ) {
        this.file = licenseInfoTXTFile;
    }

    public String toString() {
        String out = "";
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                out += line;
                line = bufferedReader.readLine();
                if ( line != null ) {
                    out += "\n<br>";//br is for use in HTML reporting utility
                }
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return out;
    }
}
