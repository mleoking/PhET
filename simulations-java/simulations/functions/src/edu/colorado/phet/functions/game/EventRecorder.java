// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.data.Option;

import java.util.ArrayList;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class EventRecorder {
    private ArrayList<Vector2D> mousePressed = new ArrayList<Vector2D>();

    public void mousePressed( final Vector2D position ) {
        mousePressed.add( position );
    }

    //Creates a UserInput and clears the buffer
    public UserInput process() {
        final UserInput userInput = toUserInput();
        mousePressed.clear();
        return userInput;
    }

    private UserInput toUserInput() {
        if ( mousePressed.size() > 0 ) {
            return new UserInput( Option.some( mousePressed.get( mousePressed.size() - 1 ) ) );
        }
        else {
            return new UserInput( Option.<Vector2D>none() );
        }
    }
}