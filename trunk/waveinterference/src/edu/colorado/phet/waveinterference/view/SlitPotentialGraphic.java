/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:11:44 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitPotentialGraphic extends PNode {
    private SlitPotential slitPotential;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    //todo remove assumption that all bars are distinct.

    public SlitPotentialGraphic( SlitPotential slitPotential, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.slitPotential = slitPotential;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                update();
            }
        } );
        update();
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
    }

    private void update() {
        removeAllChildren();
        Rectangle[]r = slitPotential.getBarrierRectangles();
        for( int i = 0; i < r.length; i++ ) {
            Rectangle rectangle = r[i];
            if( !rectangle.isEmpty() ) {
                Rectangle2D screenRect = latticeScreenCoordinates.toScreenRect( rectangle );

                PPath path = new PPath( screenRect );
                path.setPaint( new Color( 241, 216, 148 ) );
                path.setStroke( new BasicStroke( 2 ) );
                path.setStrokePaint( Color.black );
                addChild( path );
            }
        }
    }
}
