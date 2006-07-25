/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.resources.CatalogResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.io.IOException;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogResourceTest {
    public static void main( String[] args ) throws IOException, SimResourceException {
        CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                               Configuration.instance().getLocalRoot() );
        catalogResource.download();
    }
}
