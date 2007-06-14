/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/TestCatalogResource.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.resources.CatalogResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;
import edu.colorado.phet.simlauncher.util.DebugStringFile;

/**
 * TestCatalogResource
 *
 * @author Ron LeMaster
 * @version $Revision: 1.4 $
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
