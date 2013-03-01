// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public abstract class MotionSliderNode extends PNode {
    private PhetPPath trackPPath;
    protected PhetPPath tickMarkAtZero;
    private PNode sliderThumb;
    private double value = 0.0;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private boolean highlighted = false;
    private Color highlightColor;
    private Function.LinearFunction transform;
    private Range modelRange;
    private Range viewRange;

    public final int TRACK_WIDTH = 5;
    public final int TICK_HEIGHT = TRACK_WIDTH + 2;

    public MotionSliderNode( Range modelRange, double value, Range viewRange, Color color ) {
        this( modelRange, value, viewRange, new PImage( ColorArrows.createArrow( color ) ), color );
    }

    /**
     * This sim uses bar images for slider thumbs in the intro panel, and arrows in the chart panel.
     *
     * @param color
     * @return
     * @throws IOException
     */
    private static BufferedImage getBarImage( Color color ) throws IOException {
        return ColorArrows.filter( MotionResources.loadBufferedImage( "bar-template.png" ), color );
    }

    public MotionSliderNode( final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor ) {
        this.modelRange = modelRange;
        this.viewRange = viewRange;
        this.value = _value;
        transform = new Function.LinearFunction( modelRange.getMin(), modelRange.getMax(), viewRange.getMax(), viewRange.getMin() );//top should be positive, so min and max are switched
        modelRange.addObserver( new SimpleObserver() {
            public void update() {
                transform.setInput( modelRange.getMin(), modelRange.getMax() );
                updateLayout();
            }
        } );
        viewRange.addObserver( new SimpleObserver() {
            public void update() {
                updateTransform( viewRange );
            }
        } );
        updateTransform( viewRange );
        this.sliderThumb = sliderThumb;
        this.highlightColor = highlightColor;
        this.sliderThumb.addInputEventListener( new CursorHandler() );
        trackPPath = new PhetPPath( Color.white, new BasicStroke( 1 ), Color.black );
        addChild( trackPPath );

        tickMarkAtZero = createTickMark();
        addChild( tickMarkAtZero );

        addChild( sliderThumb );
        sliderThumb.addInputEventListener( new PBasicInputEventHandler() {
            Point2D initDragPoint = null;
            double origValue;

            public void mousePressed( PInputEvent event ) {
                initDragPoint = event.getPositionRelativeTo( sliderThumb.getParent() );
                if ( value < modelRange.getMin() ) {
                    origValue = modelRange.getMin();
                }
                else if ( value > modelRange.getMax() ) {
                    origValue = modelRange.getMax();
                }
                else {
                    origValue = value;
                }
                notifySliderThumbGrabbed();
            }

            public void mouseReleased( PInputEvent event ) {
                initDragPoint = null;
                notifySliderThumbReleased();
            }

            public void mouseDragged( PInputEvent event ) {
                if ( initDragPoint == null ) {
                    mousePressed( event );
                }
                double nodeDY = getDragViewDelta( event, initDragPoint );
                double modelDY = viewToModelDelta( nodeDY );
                double value = clamp( origValue + modelDY );
                notifySliderDragged( value );
            }

        } );

        //todo: catch layout changes
        updateLayout();
        updateTrackPPathStrokeAndPaint();
    }

    private void notifySliderThumbReleased() {
        for ( Listener listener : listeners ) {
            listener.sliderThumbReleased();
        }
    }

    protected abstract PhetPPath createTickMark();

    protected abstract void updateTransform( Range viewRange );

    protected abstract double getDragViewDelta( PInputEvent event, Point2D initDragPoint );

    private double viewToModelDelta( double dy ) {
        double x0 = transform.createInverse().evaluate( 0 );
        double x1 = transform.createInverse().evaluate( dy );
        return x1 - x0;
    }

    public void setViewRange( double min, double max ) {
        viewRange.setMin( min );
        viewRange.setMax( max );
        updateLayout();
    }

    protected void updateLayout() {
        updateTrackPath();
        updateTickMarkAtZero();
        updateThumb();
    }

    protected abstract void updateTickMarkAtZero();

    protected abstract void updateTrackPath();

    private void notifySliderDragged( double value ) {
        for ( Listener listener : listeners ) {
            listener.sliderDragged( value );
        }
    }

    private void notifySliderThumbGrabbed() {
        for ( Listener listener : listeners ) {
            listener.sliderThumbGrabbed();
        }
    }

    public double clamp( double v ) {
        return MathUtil.clamp( modelRange.getMin(), v, modelRange.getMax() );
    }

    /**
     * Sets the value of the slider for this chart.
     *
     * @param value the value to set for this controller.
     */
    public void setValue( double value ) {
        if ( this.value != value ) {
            this.value = value;
            updateThumb();
            notifyValueChanged();
        }
    }

    protected void updateThumb() {
        updateThumbLocation();
        updateThumbToIndicateOutOfRange();
    }

    protected void updateThumbLocation() {
        setThumbLocation( modelToView( clamp( value ) ) );
    }

    /**
     * The transparency of the thumb is used to indicate when a value is out of range.  Needs to be tested with team members and during interviews.
     */
    protected void updateThumbToIndicateOutOfRange() {
        getSliderThumb().setRotation( 0.0 );
        double distanceOutOfRange = getDistanceOutOfRange();
        if ( distanceOutOfRange > 0 ) {
            Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 20, 1.0, 0.2 );
            double v = MathUtil.clamp( 0.2, linearFunction.evaluate( distanceOutOfRange ), 1.0 );
            getSliderThumb().setTransparency( (float) v );
        }
        else {
            getSliderThumb().setTransparency( 1 );
        }
    }

    private double getDistanceOutOfRange() {
        if ( clamp( getValue() ) < getValue() ) {//exceeded max
            return getValue() - getMax();

        }
        else if ( clamp( getValue() ) > getValue() ) {//exceeded min
            return getMin() - getValue();
        }
        else { return 0.0; }
    }

    protected abstract void setThumbLocation( double location );

    private double modelToView( double v ) {
        return transform.evaluate( v );
    }

    /**
     * Gets the value of the control slider.
     *
     * @return the value of the control slider.
     */
    public double getValue() {
        return value;
    }

    public void setHighlighted( boolean highlighted ) {
        this.highlighted = highlighted;
        updateTrackPPathStrokeAndPaint();
    }

    private void updateTrackPPathStrokeAndPaint() {
        trackPPath.setStroke( new BasicStroke( highlighted ? 2 : 1 ) );
        trackPPath.setStrokePaint( highlighted ? highlightColor : Color.black );
    }

    protected void setTrackPath( Shape shape ) {
        trackPPath.setPathTo( shape );
    }

    public PNode getSliderThumb() {
        return sliderThumb;
    }

    /**
     * Clients can listen for value change events, whether generated by the slider or another call.
     */
    public static interface Listener {
        void valueChanged();

        void sliderThumbGrabbed();

        void sliderThumbReleased();

        void sliderDragged( Double value );
    }

    public static class Adapter implements Listener {

        public void valueChanged() {
        }

        public void sliderThumbGrabbed() {
        }

        public void sliderThumbReleased() {
        }

        public void sliderDragged( Double value ) {//Uses Double for compatibility with Scala, double was problematic for unknown reasons
        }
    }

    /**
     * Adds a listener for value change events.
     *
     * @param listener the value change listener.
     */
    public void addListener( MotionSliderNode.Listener listener ) {
        listeners.add( listener );
    }

    private void notifyValueChanged() {
        for ( Listener listener : listeners ) {
            listener.valueChanged();
        }
    }

    public boolean contains( double value ) {
        return value >= getMin() && value <= getMax();
    }

    public double getMin() {
        return modelRange.getMin();
    }

    public double getMax() {
        return modelRange.getMax();
    }

    public double getViewMin() {
        return viewRange.getMin();
    }

    public double getViewMax() {
        return viewRange.getMax();
    }

    public static class Vertical extends MotionSliderNode {
        public Vertical( Range modelRange, double value, Range viewRange, Color color ) throws IOException {
            super( modelRange, value, viewRange, color );
        }

        public Vertical( final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor ) {
            super( modelRange, _value, viewRange, sliderThumb, highlightColor );
        }

        @Override
        protected PhetPPath createTickMark() {
            return new PhetPPath( new Line2D.Double( 0, 0, TICK_HEIGHT, 0 ), Color.black, new BasicStroke( 2 ), Color.black );
        }

        @Override
        protected void updateTransform( Range viewRange ) {
            super.transform.setOutput( viewRange.getMax(), viewRange.getMin() );
        }

        @Override
        protected double getDragViewDelta( PInputEvent event, Point2D initDragPoint ) {
            double y = event.getPositionRelativeTo( super.getSliderThumb().getParent() ).getY();
            double delta = y - initDragPoint.getY();
            return delta;
        }

        @Override
        protected void updateTickMarkAtZero() {
            //should not have tick mark
            tickMarkAtZero.setVisible( false );
        }

        @Override
        protected void updateTrackPath() {
            setTrackPath( new Rectangle2D.Double( 0, getViewMin(), TRACK_WIDTH, getViewMax() ) );
        }

        @Override
        protected void setThumbLocation( double location ) {
            getSliderThumb().setRotation( 0.0 );
            getSliderThumb().setOffset( super.getTrackNode().getFullBounds().getCenterX() - getSliderThumb().getFullBounds().getWidth() / 2.0,
                                        location - getSliderThumb().getFullBounds().getHeight() / 2.0 );
        }
    }

    protected PNode getTrackNode() {
        return trackPPath;
    }

    public static class Horizontal extends MotionSliderNode {

        public Horizontal( Range modelRange, double value, Range viewRange, Color color ) throws IOException {
            this( modelRange, value, viewRange, new PImage( BufferedImageUtils.flipY( BufferedImageUtils.getRotatedImage( getBarImage( color ), -Math.PI / 2 ) ) ), color );
        }

        public Horizontal( final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor ) {
            super( modelRange, _value, viewRange, sliderThumb, highlightColor );
        }

        @Override
        protected PhetPPath createTickMark() {
            final Line2D.Double createTickMarkPath = createTickMarkPath( 0 );
            return new PhetPPath( createTickMarkPath, Color.black, new BasicStroke( 2 ), Color.black );
        }

        private Line2D.Double createTickMarkPath( double thumbHeight ) {
            final Line2D.Double createTickMarkPath = new Line2D.Double( 0, 0, 0, thumbHeight / 2 + TRACK_WIDTH / 2 );
            return createTickMarkPath;
        }

        @Override
        protected void updateTransform( Range viewRange ) {
            super.transform.setOutput( viewRange.getMin(), viewRange.getMax() );
        }

        @Override
        protected void updateTrackPath() {
            setTrackPath( new Rectangle2D.Double( getViewMin(), 0, getViewMax(), TRACK_WIDTH ) );
        }

        @Override
        protected void setThumbLocation( double location ) {
            getSliderThumb().setRotation( 0.0 );
            getSliderThumb().setOffset( location - getSliderThumb().getFullBounds().getWidth() / 2.0,
                                        getTrackNode().getFullBounds().getCenterY() - getSliderThumb().getFullBounds().getHeight() / 2.0 );
        }

        @Override
        protected double getDragViewDelta( PInputEvent event, Point2D initDragPoint ) {
            double x = event.getPositionRelativeTo( getSliderThumb().getParent() ).getX();
            double delta = x - initDragPoint.getX();
            return delta;
        }

        @Override
        protected void updateTickMarkAtZero() {
            tickMarkAtZero.setPathTo( createTickMarkPath( getSliderThumb().getFullBounds().getHeight() ) );
            tickMarkAtZero.setOffset( ( getViewMin() + getViewMax() ) / 2 - tickMarkAtZero.getFullBounds().getWidth() / 2, 0.0 );
            tickMarkAtZero.setVisible( contains( 0 ) );
        }
    }

    public void setModelRange( double min, double max ) {
        modelRange.setMin( min );
        modelRange.setMax( max );
    }
}