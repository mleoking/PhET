/**
 * Class: CommandQueue
 * Package: edu.colorado.phet.common.model.command
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common_cck.model;

import java.util.Vector;

public class CommandQueue implements Command {
    private Vector al = new Vector();

    public int size() {
        return al.size();
    }

    public void doIt() {
        while( !al.isEmpty() ) {
            commandAt( 0 ).doIt();
            al.remove( 0 );
        }
    }

    private Command commandAt( int i ) {
        return (Command)al.get( i );
    }

    public void addCommand( Command c ) {
        al.add( c );
    }

}
