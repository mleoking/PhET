package edu.colorado.phet.fitness.control;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 17, 2008 at 10:23:50 PM
 */
public class ExerciseNode extends PNode {
    public ExerciseNode() {
        JButton addExercise = new JButton( "Add Exercise" );
        PSwing addExerciseButtonNode = new PSwing( addExercise );
        addChild( addExerciseButtonNode );
    }
}
