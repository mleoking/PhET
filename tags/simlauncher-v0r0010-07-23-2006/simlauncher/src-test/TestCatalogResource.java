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
import edu.colorado.phet.simlauncher.resources.CatalogResource;
import edu.colorado.phet.simlauncher.resources.SimResourceException;
import edu.colorado.phet.simlauncher.util.DebugStringFile;

/**
 * TestCatalogResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestCatalogResource {

    public static void main( String[] args ) throws SimResourceException {
        CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                               Configuration.instance().getLocalRoot() );
        catalogResource.download();
        DebugStringFile dst = new DebugStringFile( catalogResource.getLocalFile().getAbsolutePath() );
        System.out.println( "catalogResource.getLocalFile().getAbsolutePath() = " + catalogResource.getLocalFile().getAbsolutePath() );
        System.out.println( dst.getContents() );
    }
}
