/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 9:04:48 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class CrossSectionGraphic extends PhetPNode {
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private PPath path;
    public static final BasicStroke STROKE = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{10, 5}, 0 );

    public CrossSectionGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.path = new PPath();

        path.setStroke( STROKE );
        path.setStrokePaint( Color.black );
        addChild( path );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        Rectangle2D rect = latticeScreenCoordinates.getScreenRect();
        path.setPathTo( new Line2D.Double( rect.getMinX(), rect.getCenterY(), rect.getMaxX(), rect.getCenterY() ) );
    }

    public void setColor( Color color ) {
        path.setStrokePaint( color );
    }
}
