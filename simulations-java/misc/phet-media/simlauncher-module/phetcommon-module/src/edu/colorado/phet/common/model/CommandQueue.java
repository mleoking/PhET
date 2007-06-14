/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/model/CommandQueue.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.9 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.model;

import java.util.Vector;

/**
 * A synchronized list of Commands to be executed in the BaseModel's update.
 *
 * @author ?
 * @version $Revision: 1.9 $
 */
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
