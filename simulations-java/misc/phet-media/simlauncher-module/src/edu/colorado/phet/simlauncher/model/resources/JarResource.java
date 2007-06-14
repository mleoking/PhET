/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/model/resources/JarResource.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.model.resources;

import java.io.File;
import java.net.URL;

/**
 * JarResource
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class JarResource extends SimResource {
    public JarResource( URL url, File localRoot ) {
        super( url, localRoot );
    }
}
