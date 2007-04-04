/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractPhetResourceLoader implements PhetResourceLoader {
    public byte[] getResource( String resource ) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        InputStream stream = getResourceAsStream( resource );

        try {
            byte[] buffer = new byte[1000];
            int bytesRead;

            while( ( bytesRead = stream.read( buffer ) ) >= 0 ) {
                out.write( buffer, 0, bytesRead );
            }

            out.flush();
        }
        catch( Exception e ) {
            try {
                stream.close();
            }
            catch( IOException e1 ) {
                e1.printStackTrace();
            }
        }

        return out.toByteArray();
    }
}
