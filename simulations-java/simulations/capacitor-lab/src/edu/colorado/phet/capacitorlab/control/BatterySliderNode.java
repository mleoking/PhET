package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Slider used to control battery voltage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatterySliderNode extends PhetPNode {
    
    private static final double TRACK_LENGTH = 125;
    private static final Color TRACK_COLOR = Color.BLACK;
    private static final Stroke TRACK_STROKE = new BasicStroke( 2f );
    
    private static final double TICK_LENGTH = 10;
    private static final Color TICK_COLOR = TRACK_COLOR;
    private static final Stroke TICK_STROKE = new BasicStroke( 2f );
    
    private static final Color LABEL_COLOR = TRACK_COLOR;
    private static final DecimalFormat LABEL_FORMAT = new DecimalFormat( "0.0" );
    private static final Font LABEL_FONT = new PhetFont( 14 );
    
    private final DoubleRange voltageRange;
    private final KnobNode knobNode;
    private final TrackNode trackNode;
    
    public BatterySliderNode( DoubleRange voltageRange ) {
        
        this.voltageRange = new DoubleRange( voltageRange );
        
        trackNode = new TrackNode( voltageRange, TRACK_LENGTH, TICK_LENGTH );
        addChild( trackNode );
        
        knobNode = new KnobNode();
        addChild( knobNode );
        
        double x = 15;
        double y = 60;
        trackNode.setOffset( x, y );
        x = trackNode.getXOffset() - ( knobNode.getFullBoundsReference().getWidth() / 2 );
        y = trackNode.getYOffset() + ( TRACK_LENGTH / 2 ) - ( knobNode.getFullBoundsReference().getHeight() / 2 );
        knobNode.setOffset( x, y );
    }
    
    /**
     * Slider knob (aka thumb), highlighted while the mouse is pressed or the mouse is inside the knob.
     */
    private static class KnobNode extends PImage {
        
        private boolean mousePressed;
        private boolean mouseInside;
        
        public KnobNode() {
            super( CLImages.SLIDER_KNOB );
            addInputEventListener( new PBasicInputEventHandler() {
                
                @Override
                public void mousePressed( PInputEvent event ) {
                    mousePressed = true;
                }

                @Override
                public void mouseReleased( PInputEvent event ) {
                    mousePressed = false;
                    if ( !mouseInside ) {
                        setImage( CLImages.SLIDER_KNOB );
                    }
                }

                @Override
                public void mouseEntered( PInputEvent event ) {
                    mouseInside = true;
                    setImage( CLImages.SLIDER_KNOB_HIGHLIGHT );
                }

                @Override
                public void mouseExited( PInputEvent event ) {
                    mouseInside = false;
                    if ( !mousePressed ) {
                        setImage( CLImages.SLIDER_KNOB );
                    }
                }
            } );
        }
    }
    
    private static class TrackNode extends PComposite {
        
        public TrackNode( DoubleRange range, double trackLength, double tickLength ) {
            
            assert( range.getMax() > 0 && range.getMin() < 0 );
            
            PPath trackNode = new PPath( new Line2D.Double( 0, 0, 0, trackLength ) );
            trackNode.setStroke( TRACK_STROKE );
            trackNode.setStrokePaint( TRACK_COLOR );
            addChild( trackNode );
            
            TickNode maxTickNode = new TickNode( tickLength, range.getMax() );
            addChild( maxTickNode );
            
            TickNode zeroTickNode = new TickNode( tickLength, 0 );
            addChild( zeroTickNode );
            
            TickNode minTickNode = new TickNode( tickLength, range.getMin() );
            addChild( minTickNode );
            
            double x = 0;
            double y = 0;
            trackNode.setOffset( x, y );
            maxTickNode.setOffset( x, y );
            y = ( trackNode.getFullBoundsReference().getHeight() / 2 );
            zeroTickNode.setOffset( x, y );
            y = trackNode.getFullBoundsReference().getHeight();
            minTickNode.setOffset( x, y );
        }
    }
    
    private static class TickNode extends PComposite {
        
        public TickNode( double length, double value ) {
            
            PPath lineNode = new PPath( new Line2D.Double( 0, 0, length, 0 ) );
            lineNode.setStroke( TICK_STROKE );
            lineNode.setStrokePaint( TICK_COLOR );
            addChild( lineNode );
            
            LabelNode labelNode = new LabelNode( value );
            addChild( labelNode );
            
            double x = 0;
            double y = 0;
            lineNode.setOffset( x, y );
            x = lineNode.getFullBoundsReference().getWidth() + 10;
            y = ( lineNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight() ) / 2;
            labelNode.setOffset( x, y );
        }
    }
    
    private static class LabelNode extends PText {
        
        public LabelNode() {
            this( null );
        }
        
        public LabelNode( double value ) {
            this( MessageFormat.format( CLStrings.FORMAT_VOLTAGE, LABEL_FORMAT.format( value ) ) );
        }
        
        public LabelNode( String value ) {
            super( value );
            setFont( LABEL_FONT );
            setTextPaint( LABEL_COLOR );
        }
    }
}
