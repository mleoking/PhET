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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * EnergyManipulator
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyManipulator extends ImageNode {

    public EnergyManipulator() {
        super( QTConstants.IMAGE_DRAG_HANDLE_LR );
    }
}
