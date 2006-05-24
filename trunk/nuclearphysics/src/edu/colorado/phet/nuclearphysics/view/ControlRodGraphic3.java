/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.ControlRod;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * ControlRodGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRodGraphic3 extends PhetImageGraphic implements ControlRod.ChangeListener {

    static BufferedImage image;

    static {
        try {
            image = ImageLoader.loadBufferedImage( Config.CONTROL_ROD_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ControlRod controlRod;

    public ControlRodGraphic3( Component component, ControlRod controlRod ) {
        super( component, image );
        this.controlRod = controlRod;
        controlRod.addChangeListener( this );

        AffineTransform atx = AffineTransform.getScaleInstance( controlRod.getBounds().getWidth() / image.getWidth(),
                                                                controlRod.getBounds().getHeight() / image.getHeight() );
//        AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        setTransform( atx );
        update();
    }

    public void update() {
        Shape rod = controlRod.getShape();
//
//        shape.setRect( rod.getBounds().getLocation().getX(),
//                       rod.getBounds().getLocation().getY(),
//                       rod.getBounds().getWidth(),
//                       rod.getBounds().getHeight() );
        setBoundsDirty();
        repaint();
    }

    public void translate( double dx, double dy ) {
        controlRod.translate( dx, dy );
    }

    //----------------------------------------------------------------
    // Implementation of ControlRod.ChangeListener
    //----------------------------------------------------------------
    public void changed( ControlRod.ChangeEvent event ) {
        update();
    }
}

