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

import edu.colorado.phet.simlauncher.resources.SimResource;

import java.net.URL;
import java.io.File;

/**
 * CatalogResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogResource extends SimResource {
    public CatalogResource( URL url, File localRoot ) {
        super( url, localRoot );
    }
}
