// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This node draws a box around a small rectangle in the mini beaker, with lines to the ParticleWindowNode to indicate that is the magnified region.
 *
 * @author Sam Reid
 */
public class ZoomIndicatorNode extends PNode {
    public ZoomIndicatorNode( final ObservableProperty<Color> lineColor, MiniBeakerNode miniBeakerNode, ParticleWindowNode particleWindowNode ) {

        //Get the bounds of the relevant regions
        Rectangle2D miniBeakerBounds = miniBeakerNode.getFullBounds();
        Rectangle2D particleWindowBounds = particleWindowNode.getFullBounds();
        double size = 5;
        Rectangle2D zoomRect = new Rectangle2D.Double( miniBeakerBounds.getCenterX() - size / 2, ( miniBeakerBounds.getCenterY() + miniBeakerBounds.getMaxY() ) / 2 - size / 2, size, size );

        //Show a small rectangle in the beaker of the zoomed-in region
        addChild( new PhetPPath( zoomRect, new BasicStroke( 1 ), ParticleWindowNode.FRAME_COLOR ) );

        //Draw lines from the zoomed in box to the particle box
        Stroke zoomLineStroke = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 20, 10 }, 0 );
        addChild( new PhetPPath( new Line2D.Double( zoomRect.getCenterX(), zoomRect.getY(), particleWindowBounds.getX(), particleWindowBounds.getY() ), zoomLineStroke, lineColor.get() ) {{
            lineColor.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setStrokePaint( color );
                }
            } );
        }} );
        addChild( new PhetPPath( new Line2D.Double( zoomRect.getCenterX(), zoomRect.getMaxY(), particleWindowBounds.getX(), particleWindowBounds.getMaxY() ), zoomLineStroke, lineColor.get() ) {{
            lineColor.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setStrokePaint( color );
                }
            } );
        }} );

//        addChild( new PhetPPath( new Line2D.Double( zoomRect.getMaxX(), zoomRect.getY(), particleWindowBounds.getMaxX(), particleWindowBounds.getY() ), zoomLineStroke, zoomLineColor ) );
//        addChild( new PhetPPath( new Line2D.Double( zoomRect.getMaxX(), zoomRect.getMaxY(), particleWindowBounds.getMaxX(), particleWindowBounds.getMaxY() ), zoomLineStroke, zoomLineColor ) );
    }
}