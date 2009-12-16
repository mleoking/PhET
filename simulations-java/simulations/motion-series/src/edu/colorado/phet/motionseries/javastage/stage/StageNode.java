/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * This represents a node in the stage coordinate frame.
 *
 * @author Sam Reid
 * @see PlayArea
 * @see PlayArea#addStageNode(edu.umd.cs.piccolo.PNode)
 */
public class StageNode extends PNode {
    /**
     * The stage in which this StageNode is positioned
     */
    private Stage stage;

    /**
     * The StageContainer in which the Stage is contained, allows us to know how the
     */
    private StageContainer stageContainer;

    /**
     * The node depicted by this StageNode.
     */
    private PNode node;

    public StageNode(Stage stage, StageContainer stageContainer, PNode node) {
        if (stage == null) throw new IllegalArgumentException("Stage was null");
        if (stageContainer == null) throw new IllegalArgumentException("Stage container was null");
        if (node == null) throw new IllegalArgumentException("node was null");

        this.stage = stage;
        this.stageContainer = stageContainer;
        this.node = node;

        addChild(node);
        stageContainer.addContainerBoundsChangeListener(new StageContainer.Listener() {
            public void stageContainerBoundsChanged() {
                updateLayout();
            }
        });
        stage.addObserver(new SimpleObserver() {
            public void update() {
                updateLayout();
            }
        });
        updateLayout();
    }

    /**
     * Checks for equality based on the reference equality of the node, stage and stageContainer.
     * This is used in PlayArea's containment checking methods.
     *
     * @param o the object to check for equality
     * @return true if the this node and the specified object are considered equal
     * @see PlayArea#containsStageNode(edu.umd.cs.piccolo.PNode)
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StageNode stageNode = (StageNode) o;

        if (node != stageNode.node) return false;
        if (stage != stageNode.stage) return false;
        if (stageContainer != stageNode.stageContainer) return false;

        return true;
    }

    /*
     * Updates the layout when the Stage or StageContainer bounds change by centering the Stage in the StageContainer.
     */
    protected void updateLayout() {
        double canvasX = stageContainer.getContainerBounds().getX();
        double canvasY = stageContainer.getContainerBounds().getY();
        double canvasWidth = stageContainer.getContainerBounds().getWidth();
        double canvasHeight = stageContainer.getContainerBounds().getHeight();
        if (canvasWidth > 0 && canvasHeight > 0) {
            double widthScale = canvasWidth / stage.getWidth();
            double heightScale = canvasHeight / stage.getHeight();
            double scale = Math.min(widthScale, heightScale);
            double patchedScale = (scale > 0) ? scale : 1.0;
            setScale(patchedScale);

            double scaledWidth = patchedScale * stage.getWidth();
            double scaledHeight = patchedScale * stage.getHeight();
            setOffset(canvasWidth / 2 - scaledWidth / 2 + canvasX, canvasHeight / 2 - scaledHeight / 2 + canvasY);
        }
    }
}
