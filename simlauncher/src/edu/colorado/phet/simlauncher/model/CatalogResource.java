/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;

import edu.colorado.phet.simlauncher.model.resources.SimResource;

import java.io.File;
import java.net.URL;

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
