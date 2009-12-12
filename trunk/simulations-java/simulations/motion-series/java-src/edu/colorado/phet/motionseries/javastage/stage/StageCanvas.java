/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * The StageCanvas is a PSwingCanvas that provides direct support for the three coordinate frames typically used in PhET simulations:
 * The model coordinate frame, which is used to depict the physical system that is depicted.  Typically this will have physical units such as meters or nanometers.
 * The stage coordinate frame, which automatically scales up and down with the size of the container, and is guaranteed to be 100% visible on the screen.
 * The screen coordinate frame, which is the same as pixel coordinates for absolute/global positioning of nodes.
 * <p/>
 * To use this canvas, create a node that is specified in coordinates in the frame in which it will be added.
 * For example: In a simulation which shows a meter stick in model coordinates (assuming model coordinates are meters),
 * the MeterStickNode would have a length of 1.0 and be added to the model coordinate frame.
 * <p/>
 * This class provides convenience methods for transforming between the various coordinate frames, and provides the capability of obtaining bounds of one coordinate frame in another coordinate frame.
 * For example, client code may wish to know "what are the bounds of the stage in screen coordinates?"  This, for example, is provided by StageCanvas#getStageInScreenCoordinates
 * <p/>
 * ToDo:
 * //todo: compute stage bounds dynamically, based on contents of the stage?
 *
 * @author Sam Reid
 */
public class StageCanvas extends PSwingCanvas implements StageContainer {
    /**
     * Stage model object represents the
     */
    private Stage stage;
    /**
     * This node is used internally to make coordinate transforms.  It is a child of the stage node, and is invisible and unpickable.
     */
    private PText utilityStageNode = new PText("Utility node");

    /**
     * This is a screen node used for debugging purposes to depict the bounds of the canvas.
     */
    private PhetPPath stageContainerDebugRegion;
    /**
     * This is a screen node used for debuggin purposes to depict the bounds of the stage.  It is shown in screen coordinates instead of stage coordinates to keep stroke a fixed width.
     */
    private PhetPPath stageBoundsDebugRegion;
    /**
     * A rectangular transform that projects model bounds to stage bounds.
     */
    private ModelViewTransform2D transform;

    /**
     * Constructs a StageCanvas with the specified dimensions and model bounds.
     *
     * @param stageWidth  the width of the stage
     * @param stageHeight the height of the stage
     * @param modelBounds the rectangular bounds depicted in the physical model.
     */
    public StageCanvas(double stageWidth, double stageHeight, Rectangle2D modelBounds) {
        stage = new Stage(stageWidth, stageHeight);
        transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight));
        utilityStageNode.setVisible(false);
        utilityStageNode.setPickable(false);
        addStageNode(utilityStageNode);

        //Create the debug regions, both specified in screen coordinates so we have control over the stroke width:
        //The debug region to depict the stage container.
        stageContainerDebugRegion = new PhetPPath(getContainerBounds(), new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{20, 8}, 0f), Color.blue);
        //The debug region to depict the stage itself.
        stageBoundsDebugRegion = new PhetPPath(getStageInScreenCoordinates(), new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{17, 5}, 0f), Color.red);

        addContainerBoundsChangeListener(new Listener() {
            public void stageContainerBoundsChanged() {
                updateDebugRegions();
            }
        });
    }

    /**
     * Creates a StageCanvas with scale sx = sy.
     *
     * @param stageWidth  the width of the stage
     * @param modelBounds the rectangular bounds depicted by the physical model
     * @see StageCanvas#StageCanvas(double, double, java.awt.geom.Rectangle2D)
     */
    public StageCanvas(double stageWidth, Rectangle2D modelBounds) {
        this(stageWidth, modelBounds.getHeight() / modelBounds.getWidth() * stageWidth, modelBounds);
    }

    /**
     * Returns the bounds of this StageContainer as a defensive copy.
     *
     * @return the bounds of this StageContainer.
     */
    public Rectangle2D getContainerBounds() {
        return new Rectangle2D.Double(0, 0, getWidth(), getHeight());
    }

    /**
     * Adds a listener for changes in the size of this StageContainerBounds.
     *
     * @param listener the callback implementation
     */
    public void addContainerBoundsChangeListener(Listener listener) {
        StageCanvasComponentAdapter canvasComponentAdapter = new StageCanvasComponentAdapter(listener);
        addComponentListener(canvasComponentAdapter);
        listeners.add(canvasComponentAdapter);
    }

    /**
     * This list keeps track of the registered StageNode#Listener implementors.
     */
    private ArrayList<StageCanvasComponentAdapter> listeners = new ArrayList<StageCanvasComponentAdapter>();

    /**
     * Removes any references to the specified listener
     *
     * @param listener the callback implementation to be removed.
     */
    public void removeContainerBoundsChangeListener(Listener listener) {
        for (StageCanvasComponentAdapter stageCanvasComponentAdapter : listeners) {
            if (listener == stageCanvasComponentAdapter.listener) {
                removeComponentListener(stageCanvasComponentAdapter);
                listeners.remove(stageCanvasComponentAdapter);
            }
        }
    }

    /**
     * This adapter class facilitates removal of StageNode#Listener implementors.
     */
    private static class StageCanvasComponentAdapter extends ComponentAdapter {
        private Listener listener;

        private StageCanvasComponentAdapter(Listener listener) {
            this.listener = listener;
        }

        public void componentResized(ComponentEvent e) {
            listener.stageContainerBoundsChanged();
        }
    }

    /**
     * Adds the specified node to the screen coordinate frame of this StageCanvas.
     *
     * @param screenNode the node to add to the screen coordinate frame.
     */
    public void addScreenNode(PNode screenNode) {
        getLayer().addChild(screenNode);
    }

    /**
     * Removes the specified node from the screen coordinate frame of this StageCanvas.
     *
     * @param node the node to be removed
     */
    public void removeScreenNode(PNode node) {
        getLayer().removeChild(node);
    }

    /**
     * Returns true if this StageCanvas contains the specified screen node, false otherwise.
     *
     * @param node the node for which to check visibility
     * @return true if this StageCanvas contains the specified screen node, false otherwise.
     */
    public boolean containsScreenNode(PNode node) {
        return getLayer().getChildrenReference().contains(node);
    }

    /**
     * Returns the rectangle that entails the stage, but in screen coordinates.
     *
     * @return the rectangle that entails the stage, but in screen coordinates.
     */
    public Rectangle2D getStageInScreenCoordinates() {
        return stageToScreen(new Rectangle2D.Double(0, 0, stage.getWidth(), stage.getHeight()));
    }

    /**
     * Transforms the specified point from model coordinates to screen coordinates.
     *
     * @param x the model x-coordinate to transform
     * @param y the model y-coordinate to transform
     * @return the new Point2D in screen coordinates
     */
    public Point2D modelToScreen(double x, double y) {
        return utilityStageNode.localToGlobal(transform.modelToView(x, y));
    }

    /**
     * Transforms the specified dimension (i.e. a delta) from a model coordinates to stage coordinates.
     *
     * @param dx the model delta along the x-axis to transform
     * @param dy the model delta along the y-axis to transform
     * @return the dimension in stage coordinates.
     */
    public Dimension2D canvasToStageDelta(double dx, double dy) {
        return utilityStageNode.globalToLocal(new PDimension(dx, dy));
    }

    /**
     * Transforms a Rectangle2D from stage coordinates to screen coordinates.
     *
     * @param shape a Rectangle2D in stage coordinates
     * @return the Rectangle2D
     */
    public Rectangle2D stageToScreen(Rectangle2D shape) {
        //Uses a defensive copy to prevent changing the supplied argument, as piccolo normally does.
        return utilityStageNode.localToGlobal(new Rectangle2D.Double(0, 0, shape.getWidth(), shape.getHeight()));
    }

    /**
     * Sets the bounds of the Stage
     *
     * @param width  the new Stage width
     * @param height the new Stage height
     */
    public void setStageBounds(double width, double height) {
        stage.setSize(width, height);
        stageBoundsDebugRegion.setPathTo(getStageInScreenCoordinates());
        transform.setViewBounds(new Rectangle2D.Double(0, 0, width, height));
    }

    /**
     * Adds the specified node to the stage coordinate frame.
     *
     * @param node the node to be added to the stage coordinate frame.
     */
    public void addStageNode(PNode node) {
        addScreenNode(new StageNode(stage, this, node));
    }

    /**
     * Removes the specified node from the stage coordinate frame.  Does nothing if the node was not already added.
     *
     * @param node the node to be removed from the stage coordinate frame.
     */
    public void removeStageNode(PNode node) {
        removeScreenNode(new StageNode(stage, this, node));
    }

    /**
     * Returns true if the node is present in the screen coordinate frame.
     *
     * @param node the node for which to check containment
     * @return true if the node is present in the screen coordinate frame.
     */
    public boolean containsStageNode(PNode node) {
        return containsScreenNode(new StageNode(stage, this, node));
    }

    /**
     * Adds the specified node to the model coordinate frame.
     *
     * @param node the node to be added to the model coordinate frame.
     */
    public void addModelNode(PNode node) {
        addStageNode(new ModelNode(transform, node));
    }

    /**
     * Removes the specified node from the model coordinate frame.  Does nothing if the node was not already added.
     *
     * @param node the node to be removed from the model coordinate frame.
     */
    public void removeModelNode(PNode node) {
        removeStageNode(new ModelNode(transform, node));
    }

    /**
     * Returns true if the specified node is contained in the stage coordinate frame.
     *
     * @param node the node for which to check containment.
     * @return true if the specified node is contained in the stage coordinate frame.
     */
    public boolean containsModelNode(PNode node) {
        return containsStageNode(new ModelNode(transform, node));
    }

    /**
     * Translates the model view transform by the specified model offset.
     *
     * @param dx the model x-coordinate by which to translate the model viewport
     * @param dy the model y-coordinate by which to translate the model viewport
     */
    public void panModelViewport(double dx, double dy) {
        transform.panModelViewport(dx, dy);
    }

    //////////////////////////////////////////////////////////
    // Methods for Debugging
    //////////////////////////////////////////////////////////

    public void updateDebugRegions() {
        stageBoundsDebugRegion.setPathTo(getStageInScreenCoordinates());
        stageContainerDebugRegion.setPathTo(getContainerBounds());
    }

    /**
     * Toggles the visibility of the debug regions.
     */
    public void toggleDebugRegionVisibility() {
        toggleScreenNode(stageContainerDebugRegion);
        toggleScreenNode(stageBoundsDebugRegion);
    }

    /**
     * Adds the node to the screen coordinate frame if it wasn't already contained, and vice-versa.
     *
     * @param node the node for which to toggle containment.
     */
    public void toggleScreenNode(PNode node) {
        if (!containsScreenNode(node))
            addScreenNode(node);
        else
            removeScreenNode(node);
    }
}