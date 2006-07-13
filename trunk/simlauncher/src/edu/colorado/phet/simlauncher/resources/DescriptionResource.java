/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * DescriptionResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DescriptionResource extends SimResource {

    public DescriptionResource( URL url, File localRoot ) {
        super( url, localRoot );
    }

    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader( new FileReader( getLocalFile() ) );
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
