/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import edu.umd.cs.piccolo.nodes.PImage;


/**
 * ImageNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ImageNode extends PImage {

    public ImageNode() {
        super();
    }
    
    public ImageNode( String resourceName ) {
        super();
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource( resourceName );
        Image image = Toolkit.getDefaultToolkit().getImage( url );
        setImage( image );
    }
}
