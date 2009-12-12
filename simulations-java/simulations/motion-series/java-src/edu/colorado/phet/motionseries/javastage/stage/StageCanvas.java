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

public class StageCanvas extends PSwingCanvas implements StageContainer {
    private double stageWidth;
    private double stageHeight;
    private Rectangle2D modelBounds;
    private Stage stage;
    private PText utilityStageNode = new PText("Utility node"); //to facilitate transforms

    //use screen coordinates instead of stage coordinates to keep stroke a fixed width
    private PhetPPath stageContainerDebugRegion;
    private PhetPPath stageBoundsDebugRegion;
    private ModelViewTransform2D transform;

    public StageCanvas(double stageWidth, double stageHeight, Rectangle2D modelBounds) {
        this.stageWidth = stageWidth;
        this.stageHeight = stageHeight;
        this.modelBounds = modelBounds;

        stage = new Stage(stageWidth, stageHeight);

        stageContainerDebugRegion = new PhetPPath(getContainerBounds(), new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{20, 8}, 0f), Color.blue);
        stageBoundsDebugRegion = new PhetPPath(getStageInScreenCoordinates(), new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{17, 5}, 0f), Color.red);
        transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight));

        utilityStageNode.setVisible(false);
        utilityStageNode.setPickable(false);
        addStageNode(utilityStageNode);

        addListener(new Listener() {
            public void changed() {
                updateRegions();
            }
        });
    }

    /**
     * Creates a StageCanvas with scale sx = sy
     */
    public StageCanvas(double stageWidth, Rectangle2D modelBounds) {
        this(stageWidth, modelBounds.getHeight() / modelBounds.getWidth() * stageWidth, modelBounds);
    }

    public Rectangle2D getContainerBounds() {
        return new Rectangle2D.Double(0, 0, getWidth(), getHeight());
    }

    public void addListener(final Listener listener) {//todo: need to be able to remove listeners
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                listener.changed();
            }
        });
    }

    public void addChild(PNode node) {
        getLayer().addChild(node);
    }

    public boolean containsChild(PNode node) {//todo: make sure they weren't asking about a stage or model node
        return getLayer().getChildrenReference().contains(node);
    }

    public void addScreenNode(PNode screenNode) {
        addChild(screenNode);
    }

    //get the rectangle that entails the stage, but in screen coordinates

    public Rectangle2D getStageInScreenCoordinates() {
        return stageToScreen(new Rectangle2D.Double(0, 0, stage.getWidth(), stage.getHeight()));
    }

    public Point2D modelToScreen(double x, double y) {
        return utilityStageNode.localToGlobal(transform.modelToView(x, y));
    }

    Dimension2D canvasToStageDelta(double dx, double dy) {
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

    public void removeScreenNode(PNode node) {
        removeChild(node);
    }

    private void removeChild(PNode node) {
        getLayer().removeChild(node);
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
        return containsChild(new StageNode(stage, this, node));//todo: requires equality tests to work
    }

    public boolean containsScreenNode(PNode node) {
        return containsChild(node);
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