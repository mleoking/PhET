/**
 * Class: ExerciseView
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:01:58 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import javax.swing.*;

public class MessageView {
    private Message message;

    public MessageView( Message message ) {
        this.message = message;
    }

    public void doIt() {
        JOptionPane.showConfirmDialog( null,
                                       message.getText(),
                                       "Don't Panic!",
                                       JOptionPane.OK_CANCEL_OPTION );
//        JOptionPane.showMessageDialog( null,
//                                       message.getText(),
//                                       "Don't Panic!",
//                                       JOptionPane.INFORMATION_MESSAGE );
    }
}
