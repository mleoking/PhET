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
public class JnlpResourceTest {
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
        jr.download();

        System.out.println( "jr.getLocalContents() = " + jr.getLocalContents() );

        Object[] o = jr.getJarResources();
        for( int i = 0; i < o.length; i++ ) {
            Object o1 = o[i];
            System.out.println( "o1 = " + o1 );
        }
    }
}
