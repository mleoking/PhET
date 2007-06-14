/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/ImageResourceTest.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;
import edu.colorado.phet.simlauncher.model.resources.ThumbnailResource;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ImageResourceTest
 *
 * @author Ron LeMaster
 * @version $Revision: 1.6 $
 */
public class ImageResourceTest {
    public static void main( String[] args ) throws IOException, SimResourceException {
        URL url = null;
        try {
            url = new URL( "http://www.colorado.edu/physics/phet/web-pages/Design/Assets/images/appletIcons/SpringsAndMasses.jpg");
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        File localRoot = Configuration.instance().getLocalRoot();
        ThumbnailResource ir = new ThumbnailResource( url, localRoot );
        ir.download();

        ImageIcon ii = new ImageIcon( url );
        JFrame frame = new JFrame( );
//        frame.getContentPane().add( new JLabel(ii ));
        frame.getContentPane().add( new JLabel(ir.getImageIcon()));
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
