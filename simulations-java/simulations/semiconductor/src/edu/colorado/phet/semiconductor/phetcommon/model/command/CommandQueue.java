/**
 * Class: CommandQueue
 * Package: edu.colorado.phet.common.model.command
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.model.command;

import java.util.Vector;

public class CommandQueue implements Command {
    Vector al = new Vector();

    public void doIt() {
        while( !al.isEmpty() ) {
            commandAt( 0 ).doIt();
            al.remove( 0 );
        }
    }

    private Command commandAt( int i ) {
        return (Command)al.get( i );
    }

}
