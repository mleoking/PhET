// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.view.beaker;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * VesselNode is the visual representation of the vessel that contains the solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class VesselNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String[] MAJOR_TICK_LABELS = { "\u00bd", "1" }; // 1/2L, 1L
    
    private static final Color TICK_COLOR = Color.BLACK;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final double MAJOR_TICK_LENGTH = 20;
    private static final double MINOR_TICK_LENGTH = 12;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Font TICK_LABEL_FONT = new PhetFont( 20 );
    private static final double TICK_LABEL_X_SPACING = 8;
    
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color OUTLINE_COLOR = Color.BLACK;
    
    private static final double SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK = 10;
    private static final PDimension BEAKER_LIP_SIZE = new PDimension( 20, 20 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GeneralPath beakerPath;
    private final PPath beakerNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public VesselNode( PDimension size, final double maxVolume ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // outline
        final float width = (float) size.getWidth();
        final float height = (float) size.getHeight();
        
        beakerPath = new GeneralPath();
        beakerPath.reset();
        beakerPath.moveTo( (float) -BEAKER_LIP_SIZE.getWidth(), (float)-( BEAKER_LIP_SIZE.getHeight() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );
        beakerPath.lineTo( 0f, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( 0f, height );
        beakerPath.lineTo( width, height );
        beakerPath.lineTo( width, (float) -SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK );
        beakerPath.lineTo( (float) ( width + BEAKER_LIP_SIZE.getWidth() ), (float)-( BEAKER_LIP_SIZE.getHeight() + SPACE_BETWEEN_TOP_OF_BEAKER_AND_TOP_TICK ) );
        
        beakerNode = new PPath( beakerPath );
        beakerNode.setPaint( null );
        beakerNode.setStroke( OUTLINE_STROKE );
        beakerNode.setStrokePaint( OUTLINE_COLOR );
        addChild( beakerNode );
        
        // tick marks
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double rightX = size.getWidth(); // don't use bounds or position will be off because of stroke width
        final double bottomY = size.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = size.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick
                Shape tickPath = new Line2D.Double( rightX - MAJOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MAJOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
                
                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length ) {
                    String label = MAJOR_TICK_LABELS[ labelIndex ] + AABSStrings.UNITS_LITERS;
                    PText textNode = new PText( label );
                    textNode.setFont( TICK_LABEL_FONT );
                    textNode.setTextPaint( TICK_COLOR );
                    ticksNode.addChild( textNode );
                    double xOffset = tickNode.getFullBounds().getMinX() - textNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
                    double yOffset = tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                    textNode.setOffset( xOffset, yOffset );
                }
            }
            else {
                // minor tick
                Shape tickPath = new Line2D.Double( rightX - MINOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MINOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
            }
        }
    }
    
    public PDimension getLipSize() {
        return BEAKER_LIP_SIZE;
    }
}
