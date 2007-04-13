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

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventObject;
import java.util.EventListener;

/**
 * FireButton
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FireButton extends PhetGraphicsButton {
    private static BufferedImage buttonUpImg;
    private static BufferedImage buttonDownImg;
    static {
        try {
            buttonDownImg = ImageLoader.loadBufferedImage( "images/fire-button-down.png");
            buttonUpImg = ImageLoader.loadBufferedImage( "images/fire-button.png");
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    public FireButton( Component component ) {
        super( component, buttonUpImg, buttonDownImg  );
    }
}
