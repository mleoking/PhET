package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * This represents a node in the stage coordinate frame.
 */
public class StageNode extends PNode {
    /**
     * The stage in which this StageNode is positioned
     */
    private Stage stage;
    /**
     * The StageContainer in which the Stage is contained
     */
    private StageContainer stageContainer;

    /**
     * The node to be displayed
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
        stageContainer.addListener(new StageContainer.Listener() {
            public void changed() {
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

    public void updateLayout() {
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
