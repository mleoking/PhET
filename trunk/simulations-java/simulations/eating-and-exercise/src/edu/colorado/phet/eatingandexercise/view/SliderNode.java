package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Jul 1, 2008 at 9:43:43 AM
 */
public class SliderNode extends PNode {
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private double min;
    private double max;
    private double value;
    private ThumbNode thumbNode;
    private TrackNode trackNode;
    private PhetPPath backgroundForMouseEventHandling;

    private ArrayList listeners = new ArrayList();

    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 30;
    private static final int DEFAULT_THUMB_WIDTH = 10;
    private static final int DEFAULT_THUMB_HEIGHT = 30;

    public SliderNode( double min, double max, double value ) {
        this.min = min;
        this.max = max;
        this.value = value;

        trackNode = new TrackNode();
        thumbNode = new ThumbNode();
        backgroundForMouseEventHandling = new PhetPPath( new Color( 0, 0, 0, 0 ) );
        backgroundForMouseEventHandling.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {

                double viewPoint = event.getPositionRelativeTo( SliderNode.this ).getX();
                double modelvalue = viewToModel( viewPoint );

                double range = getRange();
                double dx = range / 20;
                double sign = modelvalue > getValue() ? 1 : -1;
                setValue( getValue() + sign * dx );
            }
        } );
        addInputEventListener( new CursorHandler() );

        addChild( trackNode );
        addChild( backgroundForMouseEventHandling );
        addChild( thumbNode );

        updateThumb();
        updateLayout();
//        System.out.println( "trackNode.getFullBounds() = " + trackNode.getFullBounds() );
    }

    private void updateLayout() {
        backgroundForMouseEventHandling.setPathTo( trackNode.getFullBounds().createUnion( thumbNode.getFullBounds() ) );
    }

    public double getValue() {
        return value;
    }

    public void addChangeListener( ChangeListener changeListener ) {
        listeners.add( changeListener );
    }

    public void setRange( double min, double max ) {
        this.min = min;
        this.max = max;
        updateThumb();
        //todo: check that the value appears in the range
    }

    protected Shape createTrackShape( double min, double max ) {
        return new BasicStroke( 2 ).createStrokedShape( new Line2D.Double( modelToView( min ), height / 2, modelToView( max ), height / 2 ) );
    }

    protected void updateThumb() {
        updateThumbLocation();
        Shape shape = new RoundRectangle2D.Double( 0, 0, thumbNode.getThumbWidth(), thumbNode.getThumbHeight(), 6, 6 );
        if ( value < min ) {
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( thumbNode.getThumbWidth(), 0 );
            path.lineTo( 0, thumbNode.getThumbHeight() / 2 );
            path.lineTo( thumbNode.getThumbWidth(), thumbNode.getThumbHeight() );
            path.lineTo( thumbNode.getThumbWidth(), 0 );
            shape = path.getGeneralPath();
        }
        else if ( value > max ) {
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( thumbNode.getThumbWidth(), thumbNode.getThumbHeight() / 2 );
            path.lineTo( 0, thumbNode.getThumbHeight() );
            path.lineTo( 0, 0 );
            shape = path.getGeneralPath();
        }
        thumbNode.setThumbState( new ThumbState( value < min || value > max ? Color.red : new Color( 237, 200, 120 ),
                                                 shape ) );
    }

    private void updateThumbLocation() {
        thumbNode.setOffset( modelToView( getClampedValue() ), 0 );
        repaint();//SRR 8-11-2008: adding this repaint() call resolves this problem, --When I switch from female to male, the body fat slider now has two brown rounded rectangles. As soon as I clicked on it, it corrected itself. I couldn’t’ get it to do it again.
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
            updateThumb();
        }
    }

    private void notifyValueChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (ChangeListener) listeners.get( i ) ).stateChanged( new ChangeEvent( this ) );
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

    private double getRange() {
        return max - min;
    }

    protected class ThumbNode extends PNode {
        private int thumbWidth = DEFAULT_THUMB_WIDTH;
        private int thumbHeight = DEFAULT_THUMB_HEIGHT;
        private Point2D dragStartPT;
        private PPath thumb;

        private ThumbNode() {
            thumb = new PhetPPath( new RoundRectangle2D.Double( 0, 0, thumbWidth, thumbHeight, 6, 6 ), new Color( 237, 200, 120 ), new BasicStroke(), Color.black );
            thumb.setOffset( -thumb.getFullBounds().getWidth() / 2, height / 2 - thumb.getFullBounds().getHeight() / 2 );
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
                    dragStartPT = event.getPositionRelativeTo( RestrictedSliderNode.ThumbNode.this );
                }

                public void mouseDragged( PInputEvent event ) {
                    Point2D dragEndPT = event.getPositionRelativeTo( RestrictedSliderNode.ThumbNode.this );
                    PDimension d = new PDimension( dragEndPT.getX() - dragStartPT.getX(), dragEndPT.getY() - dragEndPT.getY() );
                    RestrictedSliderNode.ThumbNode.this.localToGlobal( d );
                    double proposedValue = value + viewToModelRelative( d.getWidth() );
                    setValue( clamp( proposedValue ) );
                }
            } );
            addChild( thumb );
        }

        private void handleMouse( PInputEvent event, Cursor predefinedCursor ) {
            Object o = event.getComponent();
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

        public void setThumbState( RestrictedSliderNode.ThumbState thumbState ) {
            setThumbPaint( thumbState.getPaint() );
            thumb.setPathTo( thumbState.getShape() );
        }

        public void setThumbPaint( Paint paint ) {
            thumb.setPaint( paint );
        }

        public double getThumbWidth() {
            return thumbWidth;
        }

        public double getThumbHeight() {
            return thumbHeight;
        }
    }

    public static class ThumbState {
        private Paint paint;
        private Shape shape;

        public ThumbState( Paint paint, Shape shape ) {
            this.paint = paint;
            this.shape = shape;
        }

        public Paint getPaint() {
            return paint;
        }

        public Shape getShape() {
            return shape;
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas();
        final SliderNode sliderNode = new SliderNode( 50, 100, 50 );
        sliderNode.setOffset( 100, 100 );
        contentPane.addScreenChild( sliderNode );
        frame.setContentPane( contentPane );
        frame.setVisible( true );
//
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sliderNode.setRange( 0, 1 );
            }
        } );
        timer.setRepeats( false );
        timer.start();
    }
}