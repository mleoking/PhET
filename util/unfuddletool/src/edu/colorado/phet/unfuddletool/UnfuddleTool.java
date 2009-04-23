package edu.colorado.phet.unfuddletool;

import edu.colorado.phet.unfuddletool.gui.UnfuddleToolGUI;

public class UnfuddleTool {

    public static void main( String[] args ) {
        Authentication.auth = args[0];

        new UnfuddleToolGUI();
    }

}
