package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class ImagePotentialGraphic extends RectangularPotentialGraphic {
    private PImage image;
    private int origWidth;

    public ImagePotentialGraphic( QWIPanel component, final RectangularPotential potential ) {
        super( component, potential );
        image = PImageFactory.create( "images/atom3.gif" );
        addChild( image );
        origWidth = image.getImage().getWidth( null );
        disableCloseGraphic();
        disablePotentialDisplayGraphic();
        disableResizeCorner();
        disableBodyGraphic();
        update();
    }

    protected void update() {
        super.update();
        if( super.getPotential() != null && image != null ) {
            Rectangle modelRect = getPotential().getBounds();
            Rectangle viewRect = super.getViewRectangle( modelRect );
            if( viewRect.width == 0 ) {
                return;
            }
            image.setScale( ( (double)viewRect.width ) / origWidth );
            image.setOffset( viewRect.x, viewRect.y );
//            image.setOffset( 0, 0 );
        }
    }
}
