package edu.colorado.phet.common.phetcommon.util;

import java.io.*;
import java.net.URL;

public class NetworkUtils {

    //TODO consolidate with many copies in other phet subprojects such as build process, util/updater
    public static void download( String urlAddress, File file ) throws FileNotFoundException {
        file.getParentFile().mkdirs();
        try {
            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
            InputStream inputStream = new URL( urlAddress ).openConnection().getInputStream();
            byte[] data = new byte[2048];
            int read = 0;
            while ( ( read = inputStream.read( data ) ) != -1 ) {
                outputStream.write( data, 0, read );
            }
            inputStream.close();
            outputStream.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
