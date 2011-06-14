package edu.colorado.phet.unfuddle.process;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: Sam
* May 14, 2008 at 7:31:17 PM
*/
public class BasicProcess implements MyProcess {
    public String invoke( String cmd ) throws IOException {
        Process p = Runtime.getRuntime().exec( cmd );
        StringBuffer s = new StringBuffer();
        InputStream in = p.getInputStream();
        int c;
        while ( ( c = in.read() ) != -1 ) {//blocks until data is available
            s.append( (char) c );
            System.out.print( c );
        }
        in.close();
        return s.toString();
    }
}
