package edu.colorado.phet.fitness.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.fitness.model.Diet;
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

    public DietNode( final FitnessModel model ) {
        this.human = model.getHuman();

        dietTextReadout = new PText( "Balanced Diet" );
        JButton button = new JButton( "Edit Diet" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setPaused( true );
                Diet diet = (Diet) JOptionPane.showInputDialog( null, "Select a Diet", "Select a Diet", JOptionPane.QUESTION_MESSAGE, null, FitnessModel.availableDiets, FitnessModel.availableDiets[0] );
                if ( diet != null ) {
                    human.setDiet( diet );
                }
                model.setPaused( false );
            }
        } );
        PSwing pSwing = new PSwing( button );
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
