// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * StoveGraphic
 *
 * @author Ron LeMaster
 */
public class StoveGraphic extends RegisterablePNode {
    private PImage flames;
    private PImage ice;

    public StoveGraphic() {
        init();
    }

    private void init() {
        // Set up the stove, flames, and ice
        PImage stove = PImageFactory.create( SolubleSaltsConfig.STOVE_IMAGE_FILE );
        flames = PImageFactory.create( SolubleSaltsConfig.FLAMES_IMAGE_FILE );
        ice = PImageFactory.create( SolubleSaltsConfig.ICE_IMAGE_FILE );

        // Add a rectangle that will mask the ice and flames when they are behind the stove
        Rectangle2D r = new Rectangle2D.Double( 0, 0, stove.getWidth(), stove.getHeight() );
        PPath mask = new PPath( r );
        mask.setPaint( Color.white );
        mask.setStrokePaint( Color.white );

        this.addChild( flames );
        this.addChild( ice );
        this.addChild( mask );
        this.addChild( stove );

        setRegistrationPoint( stove.getWidth() / 2, 0.0 );
    }

}