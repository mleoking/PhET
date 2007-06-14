/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/util/DebugStringFile.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/06/09 00:31:40 $
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
 * @version $Revision: 1.2 $
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
