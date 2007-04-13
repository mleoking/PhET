/**
 * Class: ExerciseView
 * Class: edu.colorado.games4education.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:01:58 PM
 */
package edu.colorado.games4education.exercise;

import javax.swing.*;

public class MessageView {
    private Message message;

    public MessageView( Message message ) {
        this.message = message;
    }

    public void doIt() {
        JOptionPane.showMessageDialog( null,
                                       message.getText(),
                                       "Don't Panic!",
                                       JOptionPane.INFORMATION_MESSAGE );
    }
}
