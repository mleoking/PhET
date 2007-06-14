/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/LaunchTest.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.resources.JnlpResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class LaunchTest {
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

        JFrame frame = new JFrame( );
        frame.getContentPane().add( new JTextArea(jr.getLocalContents() ));
        frame.pack();
        frame.setVisible( true );
    }
}
