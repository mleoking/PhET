package edu.colorado.phet.fitness.control;

import javax.swing.*;

import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 17, 2008 at 10:22:29 PM
 */
public class DietNode extends PNode {
    private PText dietTextReadout;
    private Human human;

    public DietNode( FitnessModel model ) {
        this.human = model.getHuman();

        dietTextReadout = new PText( "Balanced Diet" );
        PSwing pSwing = new PSwing( new JButton( "Edit Diet" ) );
        addChild( dietTextReadout );
        addChild( pSwing );
        pSwing.setOffset( 0, dietTextReadout.getFullBounds().getMaxY() );
        human.addListener( new Human.Adapter() {
            public void dietChanged() {
                dietTextReadout.setText( human.getDiet().getName() );
            }
        } );
    }
}
