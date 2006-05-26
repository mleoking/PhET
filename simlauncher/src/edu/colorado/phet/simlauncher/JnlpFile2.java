/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * JnlpFile
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JnlpFile2 {
    String path;

    public JnlpFile2( String path ) {
        this.path = path;
    }



    /**
     * For debugging
     *
     * @return
     */
    public String getContents() {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader( new FileReader( path ) );
            String str;
            while( ( str = in.readLine() ) != null ) {
                sb.append( str );
            }
            in.close();
        }
        catch( IOException e ) {
        }
        return sb.toString();
    }
}
