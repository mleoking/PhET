/**
 * Class: SetYCommand
 * Package: edu.colorado.phet.common.examples.hellophet.viewX
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.examples.hellophet.application.commands;

import edu.colorado.phet.common.examples.hellophet.model.Message;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.command.Command;

public class SetYCommand implements Command {
    private Message m;
    private int y;

    public SetYCommand( Message m, int y ) {
        this.m = m;
        this.y = y;
    }

    public void doIt() {
        m.setY( y );
    }

    public void doItLater( BaseModel model ) {
        model.execute( this );
    }
}
