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
 * The stage coordinate frame, which automatically scales up and down with the size of the container.
 * The screen coordinate frame, which is the same as pixel coordinates for absolute/global positioning of nodes.
 * <p/>
 * This class provides convenience methods for transforming between the various coordinate frames, and provides the capability of obtaining bounds of one coordinate frame in another coordinate frame.
 * For example, client code may wish to know "what are the bounds of the stage in screen coordinates?"  This, for example, is provided by StageCanvas#getStageInScreenCoordinates
 * @author Sam Reid
 */
public class StageCanvas extends PSwingCanvas implements StageContainer {
    /**
     * Stage model object represents the
     */
    private Stage stage;
    /**
     * This node is used to make coordinate transforms.  It is a child of the stage node, and is invisible and unpickable.
     */
    private PText utilityStageNode = new PText("Utility node");

    //use screen coordinates instead of stage coordinates to keep stroke a fixed width
    private PhetPPath stageContainerDebugRegion;
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

        stageContainerDebugRegion = new PhetPPath(getContainerBounds(), new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{20, 8}, 0f), Color.blue);
        stageBoundsDebugRegion = new PhetPPath(getStageInScreenCoordinates(), new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{17, 5}, 0f), Color.red);
        transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight));

        utilityStageNode.setVisible(false);
        utilityStageNode.setPickable(false);
        addStageNode(utilityStageNode);

        addContainerBoundsChangeListener(new Listener() {
            public void stageContainerBoundsChanged() {
                updateRegions();
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

        //if removeComponentListener uses reference equality, we could implement SCCA.equals and use this body:
        //removeComponentListener(new StageCanvasComponentAdapter(listener));
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

    public boolean containsScreenNode(PNode node) {
        return getLayer().getChildrenReference().contains(node);
    }

    /**
     * Returns the rectangle that entails the stage, but in screen coordinates.
     *
     * @return the rectangle that entails the stage in screen coordinates.
     */
    public Rectangle2D getStageInScreenCoordinates() {
        return stageToScreen(new Rectangle2D.Double(0, 0, stage.getWidth(), stage.getHeight()));
    }

    public Point2D modelToScreen(double x, double y) {
        return utilityStageNode.localToGlobal(transform.modelToView(x, y));
    }

    public Dimension2D canvasToStageDelta(double dx, double dy) {
        return utilityStageNode.globalToLocal(new PDimension(dx, dy));
    }

    public Rectangle2D stageToScreen(Rectangle2D shape) {
        return utilityStageNode.localToGlobal(shape);
    }

    public void setStageBounds(double width, double height) {
        stage.setSize(width, height);
        stageBoundsDebugRegion.setPathTo(getStageInScreenCoordinates());
        transform.setViewBounds(new Rectangle2D.Double(0, 0, width, height));
    }

    public void toggleScreenNode(PNode node) {
        if (!containsScreenNode(node))
            addScreenNode(node);
        else
            removeScreenNode(node);
    }

    public void addStageNode(PNode node) {
        addScreenNode(new StageNode(stage, this, node));
    }

    public void removeStageNode(PNode node) {
        removeScreenNode(new StageNode(stage, this, node));
    }

    public void addModelNode(PNode node) {
        addStageNode(new ModelNode(transform, node));
    }

    public void removeModelNode(PNode node) {
        removeStageNode(new ModelNode(transform, node));
    }

    public void panModelViewport(double dx, double dy) {
        transform.panModelViewport(dx, dy);
    }

    public boolean containsStageNode(PNode node) {
        return containsScreenNode(new StageNode(stage, this, node));//todo: requires equality tests to work
    }

    public void updateRegions() {
        stageBoundsDebugRegion.setPathTo(getStageInScreenCoordinates());
        stageContainerDebugRegion.setPathTo(getContainerBounds());
    }

    public void toggleDebugs() {
        toggleScreenNode(stageContainerDebugRegion);
        toggleScreenNode(stageBoundsDebugRegion);
    }
}