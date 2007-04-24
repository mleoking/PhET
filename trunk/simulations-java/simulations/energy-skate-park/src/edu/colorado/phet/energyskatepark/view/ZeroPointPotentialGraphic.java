/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.common.LucidaSansFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2005
 * Time: 9:29:24 PM
 *
 */

public class ZeroPointPotentialGraphic extends PhetPNode {
    private EnergySkateParkSimulationPanel canvas;

    public ZeroPointPotentialGraphic( final EnergySkateParkSimulationPanel canvas ) {
        this.canvas = canvas;

        PhetPPath background = new PhetPPath( new Line2D.Double( 0, 0, 5000, 0 ), new BasicStroke( 30, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3 ), new Color( 0, 0, 0, 0 ) );
        addChild( background );

        PhetPPath path = new PhetPPath( new Line2D.Double( 0, 0, 5000, 0 ), new BasicStroke( 3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3, new float[]{16, 8}, 0 ), Color.blue );
        addChild( path );

        ShadowPText text = new ShadowPText( EnergySkateParkStrings.getString( "pe.0.at.this.dotted.line" ) );
        text.setFont( new LucidaSansFont( 16, true ) );
        text.setTextPaint( Color.black );
        text.setShadowColor( new Color( 128, 128, 255 ) );
        addChild( text );
        text.setOffset( 2, 2 );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                double dy = event.getCanvasDelta().getHeight();
                PDimension dim = new PDimension( 0, dy );
                canvas.getPhetRootNode().screenToWorld( dim );
                canvas.getEnergySkateParkModel().translateZeroPointPotentialY( dim.getHeight() );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    public void setZeroPointPotential( double y ) {
        Point2D.Double pt = new Point2D.Double( 0, y );
        canvas.getPhetRootNode().worldToScreen( pt );
        double viewY = pt.getY();
        setOffset( 0, viewY );
    }
}
