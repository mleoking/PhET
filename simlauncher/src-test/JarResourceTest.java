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
import edu.colorado.phet.simlauncher.model.resources.JarResource;
import edu.colorado.phet.simlauncher.model.resources.JnlpResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JarResourceTest {
    public static void main( String[] args ) throws IOException, SimResourceException {
        URL url = null;
        try {
            url = new URL( "http://www.colorado.edu/physics/phet/simulations/cck/cck.jnlp" );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        File localRoot = Configuration.instance().getLocalRoot();
        JnlpResource jr  = new JnlpResource( url, localRoot );

        JarResource[] jarResources = jr.getJarResources();
        for( int i = 0; i < jarResources.length; i++ ) {
            JarResource jarResource = jarResources[i];
            // See if we can reference the resource
            jarResource.download();
            System.out.println( "jarResource.getLocalFile().getAbsolutePath() = " + jarResource.getLocalFile().getAbsolutePath() );
            System.out.println( "jarResource.getLocalFile().exists() = " + jarResource.getLocalFile().exists() );
        }
    }
}
