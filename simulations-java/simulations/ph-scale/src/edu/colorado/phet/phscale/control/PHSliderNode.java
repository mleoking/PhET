/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PHSliderNode extends PNode {

    private static final Color ACID_COLOR = PHScaleConstants.H3O_COLOR;
    private static final Color BASE_COLOR = PHScaleConstants.OH_COLOR;
    private static final Stroke TRACK_STROKE = new BasicStroke( 1f );
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Font RANGE_FONT = new PhetFont( 16 );
    private static final Font TICK_FONT = new PhetFont( 16 );
    
    public PHSliderNode( double trackWidth, double trackHeight ) {
        super();
        
        JSlider slider = new JSlider( JSlider.VERTICAL, -100, 1500, 500 );
        slider.setMajorTickSpacing( 100 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setPaintTrack( false );
        slider.setPreferredSize( new Dimension( slider.getPreferredSize().width, 500 ) );
        Hashtable sliderLabelTable = new Hashtable();
        sliderLabelTable.put( new Integer( 0 ), new JLabel( "0" ) );
        sliderLabelTable.put( new Integer( 200 ), new JLabel( "2" ) );
        sliderLabelTable.put( new Integer( 400 ), new JLabel( "4" ) );
        sliderLabelTable.put( new Integer( 600 ), new JLabel( "6" ) );
        sliderLabelTable.put( new Integer( 800 ), new JLabel( "8" ) );
        sliderLabelTable.put( new Integer( 1000 ), new JLabel( "10" ) );
        sliderLabelTable.put( new Integer( 1200 ), new JLabel( "12" ) );
        sliderLabelTable.put( new Integer( 1400 ), new JLabel( "14" ) );
        slider.setLabelTable( sliderLabelTable );
        PSwing sliderWrapper = new PSwing( slider );
        
        double w = 10;
        double h = slider.getPreferredSize().getHeight();
        PPath sliderTrackNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        Paint trackPaint = new GradientPaint( 0f, 0f, BASE_COLOR, 0f, (float)h, ACID_COLOR );
        sliderTrackNode.setPaint( trackPaint );
        sliderTrackNode.setStroke( TRACK_STROKE );
        sliderTrackNode.setStrokePaint( TRACK_COLOR );
        
        PText acidLabelNode = new PText( PHScaleStrings.LABEL_ACID );
        acidLabelNode.setFont( RANGE_FONT );
        acidLabelNode.rotate( -Math.PI / 2 );
        sliderTrackNode.addChild( acidLabelNode );
        acidLabelNode.setOffset( -( acidLabelNode.getFullBoundsReference().getWidth() + 4 ), sliderTrackNode.getFullBoundsReference().getHeight() - acidLabelNode.getFullBoundsReference().getHeight() ); // bottom left of the track
        
        PText baseLabelNode = new PText( PHScaleStrings.LABEL_BASE );
        baseLabelNode.setFont( RANGE_FONT );
        baseLabelNode.rotate( -Math.PI / 2 );
        sliderTrackNode.addChild( baseLabelNode );
        baseLabelNode.setOffset( -( baseLabelNode.getFullBoundsReference().getWidth() + 4 ), baseLabelNode.getFullBoundsReference().getHeight() ); // top left of the track

        sliderWrapper.setOffset( 0, 0 );
        sliderTrackNode.setOffset( sliderWrapper.getOffset() );
        
        addChild( sliderWrapper );
        addChild( sliderTrackNode );
    }
}
