// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo slider, which can also indicate when a value is out of the slider range.
 *
 * @author Sam Reid
 */
public class SliderNode extends PNode {
    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 30;
    private static final int DEFAULT_KNOB_WIDTH = 10;
    private static final int DEFAULT_KNOB_HEIGHT = 30;

    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private double min;
    private double max;
    private double value;
    private KnobNode knobNode;

    private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public SliderNode( double min, double max, double value ) {
        this.min = min;
        this.max = max;
        this.value = value;

        TrackNode trackNode = new TrackNode();
        knobNode = new KnobNode();
        PhetPPath backgroundForMouseEventHandling = new PhetPPath( new Color( 0, 0, 0, 0 ) );
        backgroundForMouseEventHandling.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {

                double viewPoint = event.getPositionRelativeTo( SliderNode.this ).getX();
                double modelValue = viewToModel( viewPoint );

                double range = SliderNode.this.max - SliderNode.this.min;
                double dx = range / 20;
                double sign = modelValue > getValue() ? 1 : -1;
                setValue( getValue() + sign * dx );
            }
        } );
        addInputEventListener( new CursorHandler() );

        addChild( trackNode );
        addChild( backgroundForMouseEventHandling );
        addChild( knobNode );

        updateKnob();

        //Update the background which is used for mouse event handling.
        backgroundForMouseEventHandling.setPathTo( trackNode.getFullBounds().createUnion( knobNode.getFullBounds() ) );
    }

    public double getValue() {
        return value;
    }

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    //Set the range of the slider.  If the knob doesn't appear in the new range, it will be indicated with an arrow knob.
    public void setRange( double min, double max ) {
        this.min = min;
        this.max = max;
        updateKnob();
    }

    protected Shape createTrackShape( double min, double max ) {
        return new BasicStroke( 2 ).createStrokedShape( new Line2D.Double( modelToView( min ), height / 2, modelToView( max ), height / 2 ) );
    }

    protected void updateKnob() {
        updateKnobLocation();
        Shape shape = new RoundRectangle2D.Double( 0, 0, knobNode.getKnobWidth(), knobNode.getKnobHeight(), 6, 6 );
        if ( value < min ) {
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( knobNode.getKnobWidth(), 0 );
            path.lineTo( 0, knobNode.getKnobHeight() / 2 );
            path.lineTo( knobNode.getKnobWidth(), knobNode.getKnobHeight() );
            path.lineTo( knobNode.getKnobWidth(), 0 );
            shape = path.getGeneralPath();
        }
        else if ( value > max ) {
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( knobNode.getKnobWidth(), knobNode.getKnobHeight() / 2 );
            path.lineTo( 0, knobNode.getKnobHeight() );
            path.lineTo( 0, 0 );
            shape = path.getGeneralPath();
        }
        knobNode.setKnobState( new KnobState( value < min || value > max ? Color.red : new Color( 237, 200, 120 ), shape ) );
    }

    private void updateKnobLocation() {
        knobNode.setOffset( modelToView( getClampedValue() ), 0 );
        repaint();//SRR 8-11-2008: adding this repaint() call resolves this problem, --When I switch from female to male, the body fat slider now has two brown rounded rectangles. As soon as I clicked on it, it corrected itself. I couldn't get it to do it again.
    }

    private double getClampedValue() {
        return clamp( value );
    }

    protected double clamp( double v ) {
        return MathUtil.clamp( min, v, max );
    }

    private double modelToView( double value ) {
        return new Function.LinearFunction( min, max, 0, width ).evaluate( value );
    }

    private double viewToModel( double view ) {
        return new Function.LinearFunction( 0, width, min, max ).evaluate( view );
    }

    private double viewToModelRelative( double dv ) {
        return dv * ( max - min ) / ( width - 0 );
    }

    private void setCursorHand( Container contentPane, Cursor cursor ) {
        contentPane.setCursor( cursor );
        for ( int i = 0; i < contentPane.getComponentCount(); i++ ) {
            Component c = contentPane.getComponent( i );
            c.setCursor( cursor );
            if ( c instanceof Container ) {
                setCursorHand( (Container) c, cursor );
            }
        }
    }

    public void setValue( double value ) {
        if ( this.value != value ) {
            this.value = value;
            notifyValueChanged();//todo: external set of value shouldn't fire notification
            updateKnob();
        }
    }

    private void notifyValueChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            listeners.get( i ).stateChanged( new ChangeEvent( this ) );
        }
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    protected class TrackNode extends PNode {
        protected TrackNode() {
            PPath path = new PhetPPath( createTrackShape( min, max ), Color.lightGray, new BasicStroke( 1 ), Color.black );
            addChild( path );
        }
    }

    protected class KnobNode extends PNode {
        private int knobWidth = DEFAULT_KNOB_WIDTH;
        private int knobHeight = DEFAULT_KNOB_HEIGHT;
        private Point2D dragStartPT;
        private PPath knob;

        private KnobNode() {
            knob = new PhetPPath( new RoundRectangle2D.Double( 0, 0, knobWidth, knobHeight, 6, 6 ), new Color( 237, 200, 120 ), new BasicStroke(), Color.black );
            knob.setOffset( -knob.getFullBounds().getWidth() / 2, height / 2 - knob.getFullBounds().getHeight() / 2 );
            addInputEventListener( new CursorHandler() );//todo: fails for PNode inside PhetPCanvas wrapped inside JComponent inside PSwing inside PhetPCanvas, see workaround below
            addInputEventListener( new PBasicInputEventHandler() {
                //todo: remove this workaround for cursor handling on pswing double embedding
                public void mouseEntered( PInputEvent event ) {
                    handleMouse( event, Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }

                public void mouseExited( PInputEvent event ) {
                    handleMouse( event, Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }
            } );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    dragStartPT = event.getPositionRelativeTo( KnobNode.this );
                }

                public void mouseDragged( PInputEvent event ) {
                    Point2D dragEndPT = event.getPositionRelativeTo( KnobNode.this );
                    PDimension d = new PDimension( dragEndPT.getX() - dragStartPT.getX(), dragEndPT.getY() - dragEndPT.getY() );
                    KnobNode.this.localToGlobal( d );
                    double proposedValue = value + viewToModelRelative( d.getWidth() );
                    setValue( clamp( proposedValue ) );
                }
            } );
            addChild( knob );
        }

        private void handleMouse( PInputEvent event, Cursor predefinedCursor ) {
            PComponent o = event.getComponent();
            if ( o instanceof JComponent ) {
                JComponent jComponent = (JComponent) o;
                Window w = SwingUtilities.getWindowAncestor( jComponent );
                if ( w instanceof JFrame ) {
                    JFrame frame = (JFrame) w;
                    Container contentPane = frame.getContentPane();
                    setCursorHand( contentPane, predefinedCursor );
                }
            }
        }

        public void setKnobState( KnobState knobState ) {
            setKnobPaint( knobState.paint );
            knob.setPathTo( knobState.shape );
        }

        public void setKnobPaint( Paint paint ) {
            knob.setPaint( paint );
        }

        public double getKnobWidth() {
            return knobWidth;
        }

        public double getKnobHeight() {
            return knobHeight;
        }
    }

    public static class KnobState {
        public final Paint paint;
        public final Shape shape;

        public KnobState( Paint paint, Shape shape ) {
            this.paint = paint;
            this.shape = shape;
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new PhetPCanvas();
        final SliderNode sliderNode = new SliderNode( 50, 100, 50 );
        sliderNode.setOffset( 100, 100 );
        contentPane.addScreenChild( sliderNode );
        frame.setContentPane( contentPane );
        frame.setVisible( true );

        //After a second passes, set the range to be smaller to test the knob change
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sliderNode.setRange( 0, 1 );
            }
        } );
        timer.setRepeats( false );
        timer.start();
    }
}