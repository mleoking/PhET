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
    private ExerciseModel exercise;
    private JFrame owner;
    boolean done = false;

    public ExerciseView( ExerciseModel exercise, JFrame owner ) {
        this.exercise = exercise;
        this.owner = owner;
    }

    public boolean doIt() {
        HashMap rbToAnswerMap = new HashMap();
        JDialog exerciseDlg = new JDialog( owner,
                                           false );
        Container exercisePane = exerciseDlg.getContentPane();
        exercisePane.setLayout( new BorderLayout() );

        // Add the text of the question
        JPanel questionPane = new JPanel();
        JEditorPane questionTextPane = new JEditorPane( "text/html", exercise.getQuestion() );
        questionPane.add( questionTextPane );
        exercisePane.add( questionPane, BorderLayout.NORTH );

        // Add the choices
        ButtonGroup choicesBG = new ButtonGroup();
        Answer[] choices = exercise.getChoices();
        JPanel answerPane = new JPanel( new GridBagLayout() );
//        exerciseDlg.getContentPane().add( answerPane );
        exercisePane.add( answerPane, BorderLayout.CENTER );
        try {
            for( int i = 0; i < choices.length; i++ ) {
                JRadioButton answerRB = new JRadioButton( choices[i].getId() + ") " + choices[i].getText() );
                rbToAnswerMap.put( answerRB, choices[i] );
                choicesBG.add( answerRB );
                GraphicsUtil.addGridBagComponent( answerPane, answerRB,
                                                  0, i, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

        // Add the button to submit the answer
        JButton submitBtn = new JButton( new AbstractAction( "Submit" ) {
            public void actionPerformed( ActionEvent e ) {
                done = true;
            }
        } );
        JPanel buttonPane = new JPanel();
        buttonPane.add( submitBtn );
        exercisePane.add( buttonPane, BorderLayout.SOUTH );


        exerciseDlg.pack();
        GraphicsUtil.centerDialogOnScreen( exerciseDlg );
        exerciseDlg.setVisible( true );
        try {
            while( done == false ) {
                Thread.sleep( 500 );
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }

        Answer selectedAnswer = (Answer)rbToAnswerMap.get( GraphicsUtil.getSelection( choicesBG ) );
        exerciseDlg.setVisible( false );
        return selectedAnswer == exercise.getCorrectAnswer();


//        Object choice = JOptionPane.showInputDialog( null,
//                                                     exercise.getQuestion(),
//                                                     "So tell me...",
//                                                     JOptionPane.QUESTION_MESSAGE,
//                                                     null,
//                                                     exercise.getChoices(),
//                                                     "a" );
//        return ( choice == exercise.getCorrectAnswer() );
    }

}
