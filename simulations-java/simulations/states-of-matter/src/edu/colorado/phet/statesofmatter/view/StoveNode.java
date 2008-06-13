/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;


public class StoveNode extends PNode {
    
    public StoveNode (){
        
        PImage fireImage = StatesOfMatterResources.getImageNode( "flames.gif" );
        addChild(fireImage);

        PImage iceImage = StatesOfMatterResources.getImageNode( "ice.gif" );
        addChild(iceImage);

        PImage stoveImage = StatesOfMatterResources.getImageNode( "stove.png" );
        addChild(stoveImage);

        fireImage.setOffset(stoveImage.getFullBoundsReference().width/2 - fireImage.getFullBoundsReference().width/2, 0);
        iceImage.setOffset(stoveImage.getFullBoundsReference().width/2 - iceImage.getFullBoundsReference().width/2, 0);

        StoveControlPanel stoveControlPanel = new StoveControlPanel();
        PSwing stoveControlPanelNode = new PSwing(stoveControlPanel);
        addChild(stoveControlPanelNode);
        stoveControlPanelNode.setOffset(stoveImage.getFullBoundsReference().getWidth() + 10, 0);
    }
    
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable(){
            public void run(){
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Stove Node Test");
                testFrame.addNode( new StoveNode() );
                testFrame.setVisible( true );
            }
        });
    }
}
