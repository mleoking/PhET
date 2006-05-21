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

import java.net.URL;
import java.io.File;

/**
 * JarResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JarResource extends SimResource {
    public JarResource( URL url, File localRoot ) {
        super( url, localRoot );
    }
    
}
