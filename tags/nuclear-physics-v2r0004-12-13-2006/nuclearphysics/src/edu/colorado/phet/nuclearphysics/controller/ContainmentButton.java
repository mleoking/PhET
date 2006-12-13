/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;

/**
 * FireButton
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ContainmentButton extends PhetGraphicsButton {
    private static BufferedImage buttonUpImg;
    private static BufferedImage buttonDownImg;
    static {
        try {
            ContainmentButton.buttonDownImg = ImageLoader.loadBufferedImage( "images/containment-button-down.png");
            ContainmentButton.buttonUpImg = ImageLoader.loadBufferedImage( "images/containment-button-up.png");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    public ContainmentButton( Component component ) {
        super( component, ContainmentButton.buttonUpImg, ContainmentButton.buttonDownImg  );
    }
}
