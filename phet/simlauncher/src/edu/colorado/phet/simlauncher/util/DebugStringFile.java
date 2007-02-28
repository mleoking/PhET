/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * DebugStringFile
 * <p/>
 * A simple File that can return its contents as a String
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DebugStringFile {
    String path;

    public DebugStringFile( String path ) {
        this.path = path;
    }

    /**
     * For debugging
     *
     * @return the contents of the file as a String
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
