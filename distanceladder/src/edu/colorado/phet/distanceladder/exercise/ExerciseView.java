/**
 * Class: ExerciseView
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:01:58 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class ExerciseView {
    private JFrame owner;
    private ExerciseModel exercise;
    private ExerciseDialog exerciseDlg;

    public ExerciseView( JFrame owner, ExerciseModel exercise ) {
        this.owner = owner;
        this.exercise = exercise;
    }

    /**
     * Displays the exercise in a non-modal dialog. Returns true or false
     * depending on whether the user selects the correct answer.
     *
     * @return
     */
    public boolean doIt() {
        if( exerciseDlg == null ) {
            exerciseDlg = new ExerciseDialog( owner, exercise );
            exerciseDlg.setLocationRelativeTo( owner );
        }
        exerciseDlg.present();
        while( exerciseDlg.getSelectedAnswer() == null ) {
            try {
                Thread.sleep( 500 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        exerciseDlg.setVisible( false );
        return exerciseDlg.getSelectedAnswer() == exercise.getCorrectAnswer();
    }

    private class ExerciseDialog extends JDialog {
        private ButtonGroup choiceBG = new ButtonGroup();
        private HashMap rbToAnswerMap = new HashMap();
        private Answer selectedAnswer;

        public ExerciseDialog( JFrame owner, ExerciseModel exerciseModel ) {
            super( owner, false );

            // Don't let the user close the dialog with the icon in the upper right corner
            this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );

            // Add the question
            Container container = this.getContentPane();
            JEditorPane questionPane = new JEditorPane( "text/html", exerciseModel.getQuestion() );
            container.add( questionPane, BorderLayout.NORTH );

            // Add the choices
            JPanel choicePane = new JPanel( new GridBagLayout() );
            container.add( choicePane, BorderLayout.CENTER );
            for( int i = 0; i < exerciseModel.getChoices().length; i++ ) {
                Answer answer = exerciseModel.getChoices()[i];
                JRadioButton choiceRB = new JRadioButton( answer.getText() );
                choiceBG.add( choiceRB );
                rbToAnswerMap.put( choiceRB, answer );
                try {
                    GraphicsUtil.addGridBagComponent( choicePane, choiceRB,
                                                      0, i, 1, 1,
                                                      GridBagConstraints.NONE,
                                                      GridBagConstraints.WEST );
                }
                catch( AWTException e ) {
                    e.printStackTrace();
                }
            }

            // Add the submit button
            JButton submitBtn = new JButton( new AbstractAction( "Submit" ) {
                public void actionPerformed( ActionEvent e ) {
                    JRadioButton selectedRB = GraphicsUtil.getSelection( choiceBG );
                    selectedAnswer = (Answer)rbToAnswerMap.get( selectedRB );
                }
            } );
            JPanel buttonPane = new JPanel();
            buttonPane.add( submitBtn );
            container.add( buttonPane, BorderLayout.SOUTH );

            this.pack();
        }

        public Answer getSelectedAnswer() {
            return selectedAnswer;
        }

        public void present() {
            selectedAnswer = null;
            exerciseDlg.setVisible( true );
        }
    }
}
