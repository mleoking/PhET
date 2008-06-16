/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;


public class StoveNode extends PNode {

    // Offset in Y direction for stove, tweak as needed.
    private static final double BURNER_Y_OFFSET = 20; 

    // Heat value, ranges from -1 to +1.
    private double heat;
    private PImage fireImage;
    private PImage iceImage;
    private PImage stoveImage;
    private StoveControlPanel stoveControlPanel;

    public StoveNode() {

        fireImage = StatesOfMatterResources.getImageNode("flames.gif");
        fireImage.setOffset( 0, BURNER_Y_OFFSET );
        addChild(fireImage);

        iceImage = StatesOfMatterResources.getImageNode("ice.gif");
        iceImage.setOffset( 0, BURNER_Y_OFFSET );
        addChild(iceImage);

        stoveImage = StatesOfMatterResources.getImageNode("stove.png");
        stoveImage.setOffset( 0, BURNER_Y_OFFSET );
        addChild(stoveImage);

        stoveControlPanel = new StoveControlPanel();
        stoveControlPanel.addListener(new StoveControlPanel.Listener() {
            public void valueChanged(double value) {
                heat = value;
                update();
            }
        });
        PSwing stoveControlPanelNode = new PSwing(stoveControlPanel);
        addChild(stoveControlPanelNode);
        stoveControlPanelNode.setOffset(stoveImage.getFullBoundsReference().getWidth() + 15, 0);

        update();
    }

    private void update() {

        if (heat > 0) {
            fireImage.setOffset(stoveImage.getFullBoundsReference().width / 2 - fireImage.getFullBoundsReference().width / 2,
                    -heat * stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET);
            iceImage.setOffset(stoveImage.getFullBoundsReference().width / 2 - iceImage.getFullBoundsReference().width / 2, 0);
        } else if (heat <= 0) {
            iceImage.setOffset(stoveImage.getFullBoundsReference().width / 2 - iceImage.getFullBoundsReference().width / 2,
                    heat * stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET);
            fireImage.setOffset(stoveImage.getFullBoundsReference().width / 2 - fireImage.getFullBoundsReference().width / 2, 0);
        }
        iceImage.setVisible(heat<0);
        fireImage.setVisible(heat>0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Stove Node Test");
                StoveNode stoveNode = new StoveNode();
                stoveNode.setOffset(100, 200);
                testFrame.addNode(stoveNode);
                testFrame.setVisible(true);
            }
        });
    }
}
