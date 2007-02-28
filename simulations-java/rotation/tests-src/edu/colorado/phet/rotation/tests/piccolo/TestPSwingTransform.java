package edu.colorado.phet.rotation.tests.piccolo;

/**
 *
 * @author Maarten Billemont
 * I noticed that there is a PComboBox to replace the JComboBox inside a  
Piccolo scene. This resolves most of my problems.

Two problems remain. The first I have resolved by making a small  
modification to the PComboBox code.
My PSwing is a JPanel. The PComboBox resides within that panel. On line  
153 of PComboBox, there is the following line of code:

return new Rectangle( (int)r.getX(), (int)r.getMaxY(),  
(int)sup.getWidth(), (int)sup.getHeight() );

This assumes the PSwing node in the piccolo scene IS the PComboBox.  
However, in my case, my PSwing node is but a container for my PComboBox.
As a result, the Combo Box's popup appeared somewhere below the PSwing  
node, not directly below my PComboBox. Modifying the code to look like  
this resolves that issue:

return new Rectangle( (int)r.getX(), (int)(r.getY()  
+ comboBox.getHeight()), (int)sup.getWidth(), (int)sup.getHeight() );

The second issue I have is related to an earlier issue I still have not  
been able to resolve. The fact that zooming and panning the camera causes  
problems with children of the camera node. Since this PSwing node of mine  
is a direct child of the camera and not of the canvas (because it needs to  
appear as a 'sticky' element on my scene), the view transform of the  
camera is interfering in a very strange way with my swing components. Any  
non-swing nodes that are children of the camera node act as expected. They  
are not affected by the camera's view transform and stay nicely static  
while the rest of the scene (children of the canvas) can be panned and  
zoomed. This PSwing node with a JPanel, however, is having issues. The  
drawing of all components inside the PSwing works perfectly. However,  
interacting with those components, by for example clicking on them, does  
not work well. The area where a component responds to clicks is being  
affected by the camera's view transform, while the actual component is  
being drawn without this transform. This leads to a situation where you  
have to guess where on the scene I should click to activate or interact  
with my component. Also, the PComboBox's popup box's painting is also  
affected by the camera's view transform. Where all other component's paint  
was unaffected by the view transform, the PComboBox's is, as is it's  
'interaction overlay'. As a result I can click on the overlay where it is  
being drawn, it just isn't being drawn and clicked on the right place  
relative to it's actual Combo Box parent.

I hope this makes some sense. I have so far been unable to isolate the  
cause of this. Hopefully someone with more experience as to how Swing  
works and how Piccolo's view transform is put in design has some time and  
better luck with this.

 */

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;

public class TestPSwingTransform extends PFrame {

    private PSwingCanvas pSwingCanvas;

    public TestPSwingTransform() {

        super( "Bug Test", false, new PSwingCanvas() );
        pSwingCanvas = (PSwingCanvas)getCanvas();
    }


    public void initialize() {

        PNode node, node2;
        PComboBox box;

        /* Make a piccolo test node */
        getCanvas().getLayer().addChild( node = new PNode() );
        node.setOffset( 100, 100 );
        node.setPaint( Color.GRAY );

        getCanvas().getCamera().addChild( node2 = new PText( "Drag Me" ) );
        node2.setOffset( node.getOffset() );
        node.setBounds( node2.getBounds() );
        node2.setPaint( Color.GREEN );

        /* Make a swing test button */
        getCanvas().getCamera().addChild( node = new PSwing( pSwingCanvas,
                                                             new JButton( "Click me" ) ) );
        node.setOffset( 100, 200 );

        getCanvas().getLayer().addChild( node2 = new PNode() );
        node2.setOffset( node.getOffset() );
        node2.setBounds( node.getBounds() );
        node2.setPaint( Color.GRAY );
        node2.moveToBack();

        /* Make a swing test combobox */
        getCanvas().getCamera().addChild( node = new PSwing( pSwingCanvas,
                                                             box = new PComboBox( new String[]{"Click me"} ) ) );
        node.setOffset( 100, 300 );
        box.setEnvironment( (PSwing)node, pSwingCanvas );

        getCanvas().getLayer().addChild( node2 = new PNode() );
        node2.setOffset( node.getOffset() );
        node2.setBounds( node.getBounds() );
        node2.setPaint( Color.GRAY );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new TestPSwingTransform();
            }
        } );
    }
}

