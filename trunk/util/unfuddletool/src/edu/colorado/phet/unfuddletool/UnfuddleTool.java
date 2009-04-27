package edu.colorado.phet.unfuddletool;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.gui.UnfuddleToolGUI;

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

        TicketHandler ticketHandler = TicketHandler.getTicketHandler();
        ticketHandler.setModel( gui.ticketList.model );

        PersonHandler.getPersonHandler();

        Activity.requestRecentActivity( 8 );

        Thread updateThread = new UpdateThread();
        updateThread.start();

    }

}
