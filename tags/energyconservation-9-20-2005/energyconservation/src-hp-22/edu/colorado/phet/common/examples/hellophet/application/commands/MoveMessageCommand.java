/**
 * Class: MoveMessageCommand
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common.examples.hellophet.application.commands;

import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.model.command.Command;

public class MoveMessageCommand implements Command {
    Message m;
    double y;

    public MoveMessageCommand( Message m, double y ) {
        this.m = m;
        this.y = y;
    }

    public void doIt() {
        m.setY( y );
    }
}
