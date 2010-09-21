package edu.colorado.phet.unfuddletool;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.gui.UnfuddleToolGUI;
import edu.colorado.phet.unfuddletool.handlers.MilestoneHandler;
import edu.colorado.phet.unfuddletool.handlers.PersonHandler;
import edu.colorado.phet.unfuddletool.util.Activity;
import edu.colorado.phet.unfuddletool.util.UpdateThread;

public class UnfuddleTool {

    public static void main( String[] args ) {
        if ( args.length > 0 ) {
            Authentication.auth = args[0];
        }
        else {
            String user = JOptionPane.showInputDialog( "Unfuddle username:" );
            String pass = JOptionPane.showInputDialog( "Unfuddle password:" );
            Authentication.auth = user + ":" + pass;
        }

        UnfuddleToolGUI gui = new UnfuddleToolGUI();

        // load a few things first (not required, but happens on first ticket load anyways)
        PersonHandler.getPersonHandler();
        MilestoneHandler.getMilestoneHandler();

        Activity.requestRecentActivity( 20 );

        Thread updateThread = new UpdateThread();
        updateThread.start();

    }

}
