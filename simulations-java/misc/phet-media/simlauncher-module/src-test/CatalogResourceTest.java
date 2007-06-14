/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/CatalogResourceTest.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.resources.CatalogResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.io.IOException;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class CatalogResourceTest {
    public static void main( String[] args ) throws IOException, SimResourceException {
        CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                               Configuration.instance().getLocalRoot() );
        catalogResource.download();
    }
}
