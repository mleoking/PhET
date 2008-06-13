package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
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
 * Apr 24, 2008 at 6:48:47 PM
 */
public class SliderNode extends PNode {
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 30;
    private static final int DEFAULT_THUMB_WIDTH = 10;
    private static final int DEFAULT_THUMB_HEIGHT = 30;

    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;

    private double min;
    private double max;
    private double value;
    private ThumbNode thumbNode;
    private double dragmin;
    private double dragmax;
    private RestrictedRangeNode lowerRestrictedRange;
    private RestrictedRangeNode upperRestrictedRange;

    private ArrayList listeners = new ArrayList();

    public SliderNode( double min, double max, double value ) {
        this.min = min;
        this.max = max;
        this.dragmin = this.min;
        this.dragmax = this.max;
        this.value = value;
        thumbNode = new ThumbNode();
        lowerRestrictedRange = new RestrictedRangeNode( 0, dragmin );
        upperRestrictedRange = new RestrictedRangeNode( dragmax, this.max );
        addChild( new TrackNode() );
        addChild( lowerRestrictedRange );
        addChild( upperRestrictedRange );
        addChild( thumbNode );
        updateThumbLocation();
    }

    public void setDragRange( double dragmin, double dragmax ) {
        this.dragmin = dragmin;
        this.dragmax = dragmax;
        updateRestrictedRanges();
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
        updateThumbLocation();
        //todo: check that the value appears in the range
    }

    private class RestrictedRangeNode extends PNode {
        private PhetPPath path;
        private double min;
        private double max;

        public RestrictedRangeNode( double min, double max ) {
            this.min = min;
            this.max = max;
            path = new PhetPPath( Color.red, new BasicStroke( 1 ), Color.black );
            addChild( path );
            updatePath();
        }

        private void updatePath() {
            path.setPathTo( createTrackShape( min, max ) );
            path.setVisible( min != max );
        }

        public void setRange( double min, double max ) {
            this.min = min;
            this.max = max;
            updatePath();
        }
    }

    private Shape createTrackShape( double min, double max ) {
        return new BasicStroke( 2 ).createStrokedShape( new Line2D.Double( modelToView( min ), height / 2, modelToView( max ), height / 2 ) );
    }

    private void updateRestrictedRanges() {
        lowerRestrictedRange.setRange( min, dragmin );
        upperRestrictedRange.setRange( dragmax, max );
    }

    private void updateThumbLocation() {
        thumbNode.setOffset( modelToView( value ), 0 );
    }

    private double modelToView( double value ) {
        return new Function.LinearFunction( min, max, 0, width ).evaluate( value );
    }

    private class TrackNode extends PNode {
        private TrackNode() {
            PPath path = new PhetPPath( createTrackShape( min, max ), Color.lightGray, new BasicStroke( 1 ), Color.black );
            addChild( path );
        }
    }

    private class ThumbNode extends PNode {
        private int thumbWidth = DEFAULT_THUMB_WIDTH;
        private int thumbHeight = DEFAULT_THUMB_HEIGHT;
        private Point2D dragStartPT;

        private ThumbNode() {
            PPath thumb = new PhetPPath( new RoundRectangle2D.Double( 0, 0, thumbWidth, thumbHeight, 6, 6 ), new Color( 237, 200, 120 ), new BasicStroke(), Color.black );
            thumb.setOffset( -thumb.getFullBounds().getWidth() / 2, height / 2 - thumb.getFullBounds().getHeight() / 2 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    dragStartPT = event.getPositionRelativeTo( ThumbNode.this );
                }

                public void mouseDragged( PInputEvent event ) {
                    Point2D dragEndPT = event.getPositionRelativeTo( ThumbNode.this );
                    PDimension d = new PDimension( dragEndPT.getX() - dragStartPT.getX(), dragEndPT.getY() - dragEndPT.getY() );
                    ThumbNode.this.localToGlobal( d );
                    setValue( value + d.getWidth() );
                }
            } );
            addChild( thumb );
        }
    }

    public void setValue( double v ) {
        double origValue = getValue();
        v = MathUtil.clamp( min, v, max );
        double newValue = MathUtil.clamp( dragmin, v, dragmax );
        if ( newValue != origValue ) {
            this.value = newValue;
            notifyValueChanged();
            updateThumbLocation();
        }
    }

    private void notifyValueChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (ChangeListener) listeners.get( i ) ).stateChanged( new ChangeEvent( this ) );
        }
    }

    public static class SwingSlider extends PhetPCanvas {
        public SwingSlider() {
            setPreferredSize( new Dimension( DEFAULT_WIDTH, DEFAULT_HEIGHT ) );
            addScreenChild( new SliderNode( 0, 100, 50 ) );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas();
        SliderNode sliderNode = new SliderNode( 0, 100, 50 );
        sliderNode.setOffset( 100, 100 );
        contentPane.addScreenChild( sliderNode );
        frame.setContentPane( contentPane );
        frame.setVisible( true );
        sliderNode.setDragRange( 25, 75 );

        JFrame frame2 = new JFrame();
        frame2.setContentPane( new SwingSlider() );
        frame2.pack();
        frame2.setVisible( true );
    }

}
