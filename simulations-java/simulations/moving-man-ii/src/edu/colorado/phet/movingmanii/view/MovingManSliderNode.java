package edu.colorado.phet.movingmanii.view;

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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public abstract class MovingManSliderNode extends PNode {
    protected PhetPPath trackPPath;
    protected PNode sliderThumb;
    protected double value = 0.0;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private boolean highlighted = false;
    private Color highlightColor;
    private Function.LinearFunction transform;
    private Range modelRange;
    private Range viewRange;

    public MovingManSliderNode(Range modelRange, double value, Range viewRange, Color color) {
        this(modelRange, value, viewRange, new PImage(ColorArrows.createArrow(color)), color);
    }

    public MovingManSliderNode(final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor) {
        this.modelRange = modelRange;
        this.viewRange = viewRange;
        this.value = _value;
        transform = new Function.LinearFunction(modelRange.getMin(), modelRange.getMax(), viewRange.getMax(), viewRange.getMin());//top should be positive, so min and max are switched
        modelRange.addObserver(new SimpleObserver() {
            public void update() {
                transform.setInput(modelRange.getMin(), modelRange.getMax());
            }
        });
        viewRange.addObserver(new SimpleObserver() {
            public void update() {
                updateTransform(viewRange);
            }
        });
        updateTransform(viewRange);
        this.sliderThumb = sliderThumb;
        this.highlightColor = highlightColor;
        this.sliderThumb.addInputEventListener(new CursorHandler());
        trackPPath = new PhetPPath(Color.white, new BasicStroke(1), Color.black);
        addChild(trackPPath);
        addChild(sliderThumb);
        sliderThumb.addInputEventListener(new PBasicInputEventHandler() {
            Point2D initDragPoint = null;
            double origValue;

            public void mousePressed(PInputEvent event) {
                initDragPoint = event.getPositionRelativeTo(sliderThumb.getParent());
                if (value < modelRange.getMin()) {
                    origValue = modelRange.getMin();
                } else if (value > modelRange.getMax()) {
                    origValue = modelRange.getMax();
                } else {
                    origValue = value;
                }
                notifySliderThumbGrabbed();
            }

            public void mouseReleased(PInputEvent event) {
                initDragPoint = null;
            }

            public void mouseDragged(PInputEvent event) {
                if (initDragPoint == null) {
                    mousePressed(event);
                }
                double nodeDY = getDragViewDelta(event, initDragPoint);
                double modelDY = viewToModelDelta(nodeDY);
                double value = clamp(origValue + modelDY);
                notifySliderDragged(value);
            }

        });

        //todo: catch layout changes
        updateLayout();
        updateTrackPPath();
    }

    protected abstract void updateTransform(Range viewRange);

    protected abstract double getDragViewDelta(PInputEvent event, Point2D initDragPoint);

    private double viewToModelDelta(double dy) {
        double x0 = transform.createInverse().evaluate(0);
        double x1 = transform.createInverse().evaluate(dy);
        return x1 - x0;
    }

    public void setViewRange(double min, double max) {
        viewRange.setMin(min);
        viewRange.setMax(max);
        updateLayout();
    }

    protected void updateLayout() {
        updateTrackPath();
        updateThumbLocation();
    }

    protected abstract void updateTrackPath();

    private void notifySliderDragged(double value) {
        for (Listener listener : listeners) {
            listener.sliderDragged(value);
        }
    }

    private void notifySliderThumbGrabbed() {
        for (Listener listener : listeners) {
            listener.sliderThumbGrabbed();
        }
    }

    public double clamp(double v) {
        return MathUtil.clamp(modelRange.getMin(), v, modelRange.getMax());
    }

    /**
     * Sets the value of the slider for this chart.
     *
     * @param value the value to set for this controller.
     */
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            updateThumbLocation();
            notifyValueChanged();
        }
    }

    protected void updateThumbLocation() {
        setThumbLocation(modelToView(clamp(value)));
    }

    protected abstract void setThumbLocation(double location);

    private double modelToView(double v) {
        return transform.evaluate(v);
    }

    /**
     * Gets the value of the control slider.
     *
     * @return the value of the control slider.
     */
    public double getValue() {
        return value;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        updateTrackPPath();
    }

    private void updateTrackPPath() {
        trackPPath.setStroke(new BasicStroke(highlighted ? 2 : 1));
        trackPPath.setStrokePaint(highlighted ? highlightColor : Color.black);
    }

    public void setTrackPath(Shape shape) {
        trackPPath.setPathTo(shape);
    }

    /**
     * Clients can listen for value change events, whether generated by the slider or another call.
     */
    public static interface Listener {
        void valueChanged();

        void sliderThumbGrabbed();

        void sliderDragged(double value);
    }

    public static class Adapter implements Listener {

        public void valueChanged() {
        }

        public void sliderThumbGrabbed() {
        }

        public void sliderDragged(double value) {
        }
    }

    /**
     * Adds a listener for value change events.
     *
     * @param listener the value change listener.
     */
    public void addListener(MovingManSliderNode.Listener listener) {
        listeners.add(listener);
    }

    private void notifyValueChanged() {
        for (Listener listener : listeners) {
            listener.valueChanged();
        }
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

    public static class Vertical extends MovingManSliderNode {
        public Vertical(Range modelRange, double value, Range viewRange, Color color) {
            super(modelRange, value, viewRange, color);
        }

        public Vertical(final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor) {
            super(modelRange, _value, viewRange, sliderThumb, highlightColor);
        }

        @Override
        protected void updateTransform(Range viewRange) {
            super.transform.setOutput(viewRange.getMax(), viewRange.getMin());
        }

        @Override
        protected double getDragViewDelta(PInputEvent event, Point2D initDragPoint) {
            double y = event.getPositionRelativeTo(sliderThumb.getParent()).getY();
            double delta = y - initDragPoint.getY();
            return delta;
        }

        @Override
        protected void updateTrackPath() {
            setTrackPath(new Rectangle2D.Double(0, getViewMin(), 5, getViewMax()));
        }

        @Override
        protected void setThumbLocation(double location) {
            sliderThumb.setOffset(trackPPath.getFullBounds().getCenterX() - sliderThumb.getFullBounds().getWidth() / 2.0,
                    location - sliderThumb.getFullBounds().getHeight() / 2.0);
        }
    }

    public static class Horizontal extends MovingManSliderNode {
        public Horizontal(Range modelRange, double value, Range viewRange, Color color) {
            this(modelRange, value, viewRange, new PImage(BufferedImageUtils.getRotatedImage(ColorArrows.createArrow(color), Math.PI / 2)), color);
        }

        public Horizontal(final Range modelRange, final double _value, final Range viewRange, final PNode sliderThumb, Color highlightColor) {
            super(modelRange, _value, viewRange, sliderThumb, highlightColor);
        }

        @Override
        protected void updateTransform(Range viewRange) {
            super.transform.setOutput(viewRange.getMin(), viewRange.getMax());
        }

        @Override
        protected void updateTrackPath() {
            setTrackPath(new Rectangle2D.Double(getViewMin(), 0, getViewMax(), 5));
        }

        @Override
        protected void setThumbLocation(double location) {
            sliderThumb.setOffset(location - sliderThumb.getFullBounds().getWidth() / 2.0,
                    trackPPath.getFullBounds().getCenterY() - sliderThumb.getFullBounds().getHeight() / 2.0);
        }

        @Override
        protected double getDragViewDelta(PInputEvent event, Point2D initDragPoint) {
            double x = event.getPositionRelativeTo(sliderThumb.getParent()).getX();
            double delta = x - initDragPoint.getX();
            return delta;
        }
    }
}