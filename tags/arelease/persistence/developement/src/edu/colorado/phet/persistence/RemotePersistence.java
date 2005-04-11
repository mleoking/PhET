/**
 * Class: RemotePersistence
 * Package: edu.colorado.phet.persistence
 * Author: Another Guy
 * Date: Feb 5, 2004
 */
package edu.colorado.phet.persistence;

import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;

public class RemotePersistence implements PersistenceStrategy {
    private String urlString;
    private String fileName;

    public RemotePersistence( String url, String fileName ) {
        this.urlString = url;
        this.fileName = fileName;
    }

    public void store( String text ) {
        String scriptData = fileName.concat( "|" ).concat( text );
        try {
            // Construct data
            String data = URLEncoder.encode( "fcData", "UTF-8" ) + "="
                    + URLEncoder.encode( scriptData, "UTF-8" );

            // Send the data
            URL url = new URL( urlString );
            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );
            OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
            wr.write( data );
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            String line;
            while( ( line = rd.readLine() ) != null ) {
                // Discard. We seem to need to read these lines for things to work
                System.out.println( "from server: " + line );
            }
            wr.close();
            rd.close();

            wr.close();
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
