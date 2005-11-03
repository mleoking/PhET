/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.piccolo.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * StoveGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StoveGraphic extends RegisterablePNode {
    private PImage flames;
    private PImage ice;

    public StoveGraphic() {
        init();
    }

    private void init() {
        // Set up the stove, flames, and ice
        Point2D stoveLocation = new Point2D.Double( SolubleSaltsConfig.VESSEL_ULC.getX() + SolubleSaltsConfig.VESSEL_SIZE.getWidth() / 2,
                                                    SolubleSaltsConfig.VESSEL_ULC.getY() + SolubleSaltsConfig.VESSEL_SIZE.getHeight() + 50 );
        PImage stove = PImageFactory.create( SolubleSaltsConfig.STOVE_IMAGE_FILE );
        stove.setOffset( stoveLocation );

        flames = PImageFactory.create( SolubleSaltsConfig.FLAMES_IMAGE_FILE );
        flames.setOffset( stoveLocation );

        ice = PImageFactory.create( SolubleSaltsConfig.ICE_IMAGE_FILE );
        ice.setOffset( stoveLocation );

        // Add a rectangle that will mask the ice and flames when they are behind the stove
        Rectangle2D r = new Rectangle2D.Double( 0, 0, stove.getWidth(), stove.getHeight() );
        PPath mask = new PPath( r );
        mask.setPaint( Color.white );
        mask.setStrokePaint( Color.white );
        mask.setOffset( stoveLocation );

        this.addChild( flames );
        this.addChild( ice );
        this.addChild( mask );
        this.addChild( stove );

        setRegistrationPoint( stove.getWidth()/2, 0.0 );
    }

//    public void
}