package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
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
    private final EventListenerList listeners;
    
    private double voltage;
    
    public BatterySliderNode( DoubleRange voltageRange ) {
        assert( voltageRange.getMax() > 0 && voltageRange.getMin() < 0 );
        
        this.voltageRange = new DoubleRange( voltageRange );
        listeners = new EventListenerList();
        
        trackNode = new TrackNode( TRACK_LENGTH );
        addChild( trackNode );
        
        TickNode maxTickNode = new TickNode( TICK_LENGTH, voltageRange.getMax() );
        addChild( maxTickNode );
        
        TickNode zeroTickNode = new TickNode( TICK_LENGTH, 0 );
        addChild( zeroTickNode );
        
        TickNode minTickNode = new TickNode( TICK_LENGTH, voltageRange.getMin() );
        addChild( minTickNode );
        
        knobNode = new KnobNode();
        addChild( knobNode );
        
        // layout
        double x = 0;
        double y = 0;
        trackNode.setOffset( x, y );
        maxTickNode.setOffset( x, y );
        y = ( trackNode.getFullBoundsReference().getHeight() / 2 );
        zeroTickNode.setOffset( x, y );
        y = trackNode.getFullBoundsReference().getHeight();
        minTickNode.setOffset( x, y );
        y = 0; // don't care, this will be set by setVoltage
        knobNode.setOffset( x, y );
        
        initInteractivity();
        
        // default state
        voltage = voltageRange.getMin() - 1; // force an update
        setVoltage( voltageRange.getDefault() );
    }
    
    public void setVoltage( double voltage ) {
        if ( !voltageRange.contains( voltage ) ) {
            throw new IllegalArgumentException( "voltage is out of range: " + voltage );
        }
        if ( voltage != this.voltage ) {
            this.voltage = voltage;
            double xOffset = knobNode.getXOffset();
            double yOffset = trackNode.getFullBoundsReference().getHeight() * ( ( voltageRange.getMax() - voltage ) / voltageRange.getLength() );
            knobNode.setOffset( xOffset, yOffset );
            fireStateChanged();
        }
    }
    
    public double getVoltage() {
        return voltage;
    }
    
    /*
     * Adds interactivity to the knob.
     */
    private void initInteractivity() {
        
        // hand cursor on knob
        knobNode.addInputEventListener( new CursorHandler() );
        
        // Constrain the knob to be dragged vertically within the track
        knobNode.addInputEventListener( new PDragEventHandler() {
            
            private double globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates
            
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                // note the offset between the mouse click and the knob's origin
                Point2D pMouseLocal = event.getPositionRelativeTo( BatterySliderNode.this );
                Point2D pMouseGlobal = BatterySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = BatterySliderNode.this.localToGlobal( knobNode.getOffset() );
                globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY();
            }

            protected void drag(PInputEvent event) {
                
                // determine the knob's new offset
                Point2D pMouseLocal = event.getPositionRelativeTo( BatterySliderNode.this );
                Point2D pMouseGlobal = BatterySliderNode.this.localToGlobal( pMouseLocal );
                Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX(), pMouseGlobal.getY() - globalClickYOffset );
                Point2D pKnobLocal = BatterySliderNode.this.globalToLocal( pKnobGlobal );
                
                // convert the offset to a voltage value
                double yOffset = pKnobLocal.getY();
                double trackLength = trackNode.getFullBoundsReference().getHeight();
                double voltage = voltageRange.getMin() + voltageRange.getLength() * ( trackLength - yOffset ) / trackLength;
                if ( voltage < voltageRange.getMin() ) {
                    voltage = voltageRange.getMin();
                }
                else if ( voltage > voltageRange.getMax() ) {
                    voltage = voltageRange.getMax();
                }
                
                // set the voltage (this will move the knob)
                setVoltage( voltage );
            }
        } );
    }
    
    /*
     * Slider knob (aka thumb), highlighted while the mouse is pressed or the mouse is inside the knob.
     * Origin is in the center of the knob's bounding rectangle.
     */
    private static class KnobNode extends PNode {
        
        private boolean mousePressed;
        private boolean mouseInside;
        
        public KnobNode() {
            
            // image, origin moved to center
            final PImage imageNode = new PImage( CLImages.SLIDER_KNOB );
            addChild( imageNode );
            double x = -( imageNode.getFullBoundsReference().getWidth() / 2 );
            double y = -( imageNode.getFullBoundsReference().getHeight() / 2 );
            imageNode.setOffset( x, y );
            
            addInputEventListener( new PBasicInputEventHandler() {
                
                @Override
                public void mousePressed( PInputEvent event ) {
                    mousePressed = true;
                }

                @Override
                public void mouseReleased( PInputEvent event ) {
                    mousePressed = false;
                    if ( !mouseInside ) {
                        imageNode.setImage( CLImages.SLIDER_KNOB );
                    }
                }

                @Override
                public void mouseEntered( PInputEvent event ) {
                    mouseInside = true;
                    imageNode.setImage( CLImages.SLIDER_KNOB_HIGHLIGHT );
                }

                @Override
                public void mouseExited( PInputEvent event ) {
                    mouseInside = false;
                    if ( !mousePressed ) {
                        imageNode.setImage( CLImages.SLIDER_KNOB );
                    }
                }
            } );
        }
    }
    
    /*
     * Slider track, this is what the knob moves in.
     * Origin is a at upper left of bounding rectangle.
     */
    private static class TrackNode extends PPath {
        
        public TrackNode( double trackLength ) {
            super( new Line2D.Double( 0, 0, 0, trackLength ) );
            setStroke( TRACK_STROKE );
            setStrokePaint( TRACK_COLOR );
        }
    }
    
    /*
     * A tick mark, horizontal line + label, use to indicate a specific value on the slider track.
     * Origin is at upper left corner of horizontal line.
     */
    private static class TickNode extends PComposite {
        
        public TickNode( double length, double value ) {
            
            PPath lineNode = new PPath( new Line2D.Double( 0, 0, length, 0 ) );
            lineNode.setStroke( TICK_STROKE );
            lineNode.setStrokePaint( TICK_COLOR );
            addChild( lineNode );
            
            TickLabelNode labelNode = new TickLabelNode( value );
            addChild( labelNode );
            
            double x = 0;
            double y = 0;
            lineNode.setOffset( x, y );
            x = lineNode.getFullBoundsReference().getWidth() + 10;
            y = ( lineNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight() ) / 2;
            labelNode.setOffset( x, y );
        }
    }
    
    /*
     * A label on a tick mark.
     * Origin is at upper left of bounding rectangle.
     */
    private static class TickLabelNode extends PText {
        
        public TickLabelNode() {
            this( null );
        }
        
        public TickLabelNode( double value ) {
            this( MessageFormat.format( CLStrings.FORMAT_VOLTAGE, LABEL_FORMAT.format( value ) ) );
        }
        
        public TickLabelNode( String value ) {
            super( value );
            setFont( LABEL_FONT );
            setTextPaint( LABEL_COLOR );
        }
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
