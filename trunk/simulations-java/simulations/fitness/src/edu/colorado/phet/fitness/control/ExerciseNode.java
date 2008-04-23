package edu.colorado.phet.fitness.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.Exercise;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 17, 2008 at 10:23:50 PM
 */
public class ExerciseNode extends PNode {
    private PText textReadout;
    private Human human;

    public ExerciseNode( final FitnessModel model ) {
        this.human = model.getHuman();

        textReadout = new PText( "text" );
        JButton button = new JButton( "Edit Exercise" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setPaused( true );
                Exercise exercise = (Exercise) JOptionPane.showInputDialog( null, "Select Exercise", "Select Exercise", JOptionPane.QUESTION_MESSAGE, null, FitnessModel.availableExercise, FitnessModel.availableExercise[0] );
                if ( exercise != null ) {
//                    human.setExercise( exercise );
                }
                model.setPaused( false );
            }
        } );
        PSwing pSwing = new PSwing( button );
        addChild( textReadout );
        addChild( pSwing );
        pSwing.setOffset( 0, textReadout.getFullBounds().getMaxY() );
        human.addListener( new Human.Adapter() {
            public void exerciseChanged() {
                updateTextReadout();
            }
        } );
        updateTextReadout();
    }

    private void updateTextReadout() {
//        textReadout.setText( human.getExerciseObject() == null ? "User Modified" : human.getExerciseObject().getName() );
    }

}
