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
        while( !exerciseDlg.isSubmitted() ) {
            try {
                Thread.sleep( 500 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        exerciseDlg.setVisible( false );
        return exercise.evaluate( exerciseDlg.getSelectedAnswer() );
//        return exerciseDlg.getSelectedAnswer() == exercise.getCorrectAnswer();
    }

    private class ExerciseDialog extends JDialog {
        private ButtonGroup choiceBG = new ButtonGroup();
        private HashMap rbToAnswerMap = new HashMap();
        private Answer selectedAnswer;
        private boolean submitted = false;

        public ExerciseDialog( JFrame owner, ExerciseModel exerciseModel ) {
            super( owner, false );

            // Don't let the user close the dialog with the icon in the upper right corner
            this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );

            // Add the question
            Container container = this.getContentPane();
            container.setLayout( new BorderLayout() );
            JEditorPane questionPane = new JEditorPane( "text/html", exerciseModel.getQuestion() );
            questionPane.setEditable( false );
            JScrollPane jScrollPane = new JScrollPane( questionPane );
            jScrollPane.setPreferredSize( new Dimension( 300, 200 ) );
            container.add( jScrollPane, BorderLayout.NORTH );

            // Add the choices
            JPanel choicePane = new JPanel( new GridBagLayout() );
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
            container.add( choicePane, BorderLayout.CENTER );

            // Add the submit button
            JButton submitBtn = new JButton( new AbstractAction( "Submit" ) {
                public void actionPerformed( ActionEvent e ) {
                    JRadioButton selectedRB = GraphicsUtil.getSelection( choiceBG );
                    selectedAnswer = (Answer)rbToAnswerMap.get( selectedRB );
                    submitted = true;
                }
            } );
            JPanel buttonPane = new JPanel();
            buttonPane.add( submitBtn );
            container.add( buttonPane, BorderLayout.SOUTH );

//            container.setLayout( new GridBagLayout() );
//            int rowIdx = 0;
//            try {
//                GraphicsUtil.addGridBagComponent( container, jScrollPane,
//                                                  0, rowIdx++, 1, 1,
//                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( container, choicePane,
//                                                  0, rowIdx++, 1, 1,
//                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( container, buttonPane,
//                                                  0, rowIdx++, 1, 1,
//                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
//            }
//            catch( AWTException e ) {
//                e.printStackTrace();
//            }
            this.pack();
        }

        public Answer getSelectedAnswer() {
            return selectedAnswer;
        }

        public void present() {
            submitted = false;
            selectedAnswer = null;
            exerciseDlg.setVisible( true );
        }

        boolean isSubmitted() {
            return submitted;
        }
    }
}
