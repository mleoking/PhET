/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PHSliderNode extends PNode {
    
    private static final int SIGNIFICANT_DECIMALS = 2;
    private static final IntegerRange RANGE = new IntegerRange( -1, 15, 7 );
    private static final int FIRST_TICK = 0;
    private static final int TICK_SPACING = 2;
    private static final int MODEL_TO_VIEW_SCALE = (int) Math.pow( 10, SIGNIFICANT_DECIMALS );
    
    private static final Color ACID_COLOR = PHScaleConstants.H3O_COLOR;
    private static final Color BASE_COLOR = PHScaleConstants.OH_COLOR;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Font RANGE_FONT = new PhetFont( 16 );
    private static final Font TICK_FONT = new PhetFont( 16 );
    
    public PHSliderNode( PDimension trackSize ) {
        this( trackSize.getWidth(), trackSize.getHeight() );
    }

    public PHSliderNode( double trackWidth, double trackHeight ) {
        super();
        
        TrackNode trackNode = new TrackNode( trackWidth, trackHeight );
        
        int min = RANGE.getMin() * MODEL_TO_VIEW_SCALE;
        int max = RANGE.getMax() * MODEL_TO_VIEW_SCALE;
        int defaultValue = RANGE.getDefault() * MODEL_TO_VIEW_SCALE;
        JSlider slider = new JSlider( JSlider.VERTICAL, min, max, defaultValue );
        slider.setMajorTickSpacing( 1 * MODEL_TO_VIEW_SCALE );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setPaintTrack( false );
        slider.setPreferredSize( new Dimension( slider.getPreferredSize().width, (int)trackHeight ) );
        Hashtable sliderLabelTable = new Hashtable();
        for ( int i = 0; i <= 14; i += 2 ) {
            sliderLabelTable.put( new Integer( i * MODEL_TO_VIEW_SCALE ), new JLabel( String.valueOf( i ) ) );
        }
        slider.setLabelTable( sliderLabelTable );
        PSwing sliderWrapper = new PSwing( slider );
        
        PText acidLabelNode = new PText( PHScaleStrings.LABEL_ACID );
        acidLabelNode.setFont( RANGE_FONT );
        acidLabelNode.rotate( -Math.PI / 2 );
        
        PText baseLabelNode = new PText( PHScaleStrings.LABEL_BASE );
        baseLabelNode.setFont( RANGE_FONT );
        baseLabelNode.rotate( -Math.PI / 2 );

        addChild( sliderWrapper );
        addChild( trackNode );
        addChild( acidLabelNode );
        addChild( baseLabelNode );
        
        sliderWrapper.setOffset( 0, 0 );
        trackNode.setOffset( sliderWrapper.getOffset() );
        acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + 4 ), trackNode.getFullBoundsReference().getHeight() - 5 ); // bottom left of the track
        baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + 4 ), baseLabelNode.getFullBoundsReference().getHeight() + 5 ); // top left of the track
    }
    
    /*
     * The slider track, filled with a gradient that indicates the transition from acid to base.
     */
    private static class TrackNode extends PPath {
        public TrackNode( double width, double height ) {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            Paint trackPaint = new GradientPaint( 0f, 0f, BASE_COLOR, 0f, (float)height, ACID_COLOR );
            setPaint( trackPaint );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_COLOR );
        }
    }
}
