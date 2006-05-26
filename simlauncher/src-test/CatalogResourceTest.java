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
import edu.colorado.phet.simlauncher.JnlpResource;
import edu.colorado.phet.simlauncher.JarResource;
import edu.colorado.phet.simlauncher.CatalogResource;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CatalogResourceTest {
    public static void main( String[] args ) throws IOException {
        CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                               Configuration.instance().getLocalRoot() );
        catalogResource.download();
    }
}
