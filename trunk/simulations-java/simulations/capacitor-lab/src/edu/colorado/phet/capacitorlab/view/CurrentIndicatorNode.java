/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Arrow and electron that indicates the direction of current flow.
 * Transparency is modulated proportional to dV/dt (change in voltage over change in time).
 * When voltage goes to zero, this node fades out over a period of time.
 * <p>
 * Origin is at the tip of the arrow.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CurrentIndicatorNode extends PhetPNode {
    
    // arrow properties
    private static final double ARROW_LENGTH = 175;
    private static final double ARROW_HEAD_WIDTH = 60;
    private static final double ARROW_HEAD_HEIGHT = 50;
    private static final double ARROW_TAIL_WIDTH = 0.4 * ARROW_HEAD_WIDTH;
    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 ); // origin at the tip
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( ARROW_LENGTH, 0 );
    private static final Color ARROW_COLOR = new Color( 83, 200, 236 );
    
    // electron properties
    private static final double ELECTRON_DIAMETER = 0.8 * ARROW_TAIL_WIDTH;
    private static final Paint ELECTRON_FILL_COLOR = new RoundGradientPaint( 0, 0, Color.WHITE, new Point2D.Double( ELECTRON_DIAMETER / 4, ELECTRON_DIAMETER / 4 ), ARROW_COLOR );
    private static final Stroke ELECTRON_STROKE = new BasicStroke( 1f );
    private static final Color ELECTRON_STROKE_COLOR = Color.BLACK;
    private static final Font ELECTRON_MINUS_FONT = new PhetFont( Font.BOLD, 24 );
    
    private final Battery battery;
    private final DoubleRange voltageRange;

    public CurrentIndicatorNode( Battery battery, DoubleRange voltageRange ) {
        
        this.battery = battery;
        this.voltageRange = new DoubleRange( voltageRange );
        
        ArrowNode arrowNode = new ArrowNode( ARROW_TAIL_LOCATION, ARROW_TIP_LOCATION, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        arrowNode.setPaint( ARROW_COLOR );
        addChild( arrowNode );
        
        SphericalNode electronNode = new SphericalNode( ELECTRON_DIAMETER, ELECTRON_FILL_COLOR, false /* convertToImage */ );
        electronNode.setStroke( ELECTRON_STROKE );
        electronNode.setStrokePaint( ELECTRON_STROKE_COLOR );
        addChild( electronNode );
        
        PText minusNode = new PText( "-" );
        minusNode.setFont( ELECTRON_MINUS_FONT );
        addChild( minusNode );
        
        // layout
        double x = 0;
        double y = 0;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - ( 0.6 * ( arrowNode.getFullBoundsReference().getWidth() - ARROW_HEAD_HEIGHT ) );
        y = arrowNode.getFullBoundsReference().getCenterY();
        electronNode.setOffset( x, y );
        x = electronNode.getFullBoundsReference().getCenterX() - ( minusNode.getFullBoundsReference().getWidth() / 2 );
        y = electronNode.getFullBoundsReference().getCenterY() - ( minusNode.getFullBoundsReference().getHeight() / 2 ) - 1; 
        minusNode.setOffset( x, y );
    }
}
