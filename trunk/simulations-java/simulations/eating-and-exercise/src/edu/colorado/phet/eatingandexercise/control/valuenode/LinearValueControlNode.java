package edu.colorado.phet.eatingandexercise.control.valuenode;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.eatingandexercise.view.SliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Jun 24, 2008 at 4:01:47 PM
 */
public class LinearValueControlNode extends PNode {
    private PText labelNode;
    private PSwing readoutNode;
    private SliderNode sliderNode;
    private PText unitsNode;

    public LinearValueControlNode( String label, String units, double min, double max, double value ) {

        labelNode = new PText( label );
        addChild( labelNode );

        readoutNode = new PSwing( new JTextField( value + "" ) );
        addChild( readoutNode );

        unitsNode = new PText( units );
        addChild( unitsNode );

        sliderNode = new SliderNode( min, max, value );
        addChild( sliderNode );

        relayout();
    }

    private void relayout() {
        readoutNode.setOffset( labelNode.getFullBounds().getMaxX(), 0 );
        unitsNode.setOffset( readoutNode.getFullBounds().getMaxX(), 0 );
        sliderNode.setOffset( unitsNode.getFullBounds().getMaxX(), 0 );
    }

    public static void main( String[] args ) {
        PiccoloTestFrame piccoloTestFrame = new PiccoloTestFrame( LinearValueControlNode.class.getName() );
        piccoloTestFrame.addNode( new LinearValueControlNode( "label", "units", 0, 10, 2 ) );
        piccoloTestFrame.setVisible( true );
    }
}
