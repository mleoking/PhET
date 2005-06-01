/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: May 30, 2005
 * Time: 10:20:33 PM
 * Copyright (c) May 30, 2005 by Sam Reid
 */

public abstract class BarrierGraphic extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private SurfaceGraphic surfaceGraphic;
    public PhetImageGraphic imageGraphic;

    public BarrierGraphic( Component component, RampPanel rampPanel, SurfaceGraphic surfaceGraphic ) {
        super( component );
        this.rampPanel = rampPanel;
        this.surfaceGraphic = surfaceGraphic;
        imageGraphic = new PhetImageGraphic( component, "images/barrier2.jpg" );
        addGraphic( imageGraphic );
        surfaceGraphic.getSurface().addObserver( new SimpleObserver() {
            public void update() {
                BarrierGraphic.this.update();
            }
        } );
        update();
    }

    private AffineTransform createTransform( double position, double scaleX, double fracSize ) {
        return surfaceGraphic.createTransform( position, new Dimension( (int)( imageGraphic.getImage().getWidth() * scaleX ), (int)( imageGraphic.getImage().getHeight() * fracSize ) ) );
    }

    private void update() {
        setAutorepaint( false );
        AffineTransform transform = createTransform( getBarrierPosition(), 1, 1 );
        transform.concatenate( AffineTransform.getTranslateInstance( 0, getYOffset() ) );
        transform.concatenate( AffineTransform.getTranslateInstance( getOffsetX(), 0 ) );
        imageGraphic.setTransform( transform );
        repaint();
    }

    protected abstract int getOffsetX();

    protected abstract double getBarrierPosition();

    private double getYOffset() {
        return 5;
    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public SurfaceGraphic getRampGraphic() {
        return surfaceGraphic;
    }
}
