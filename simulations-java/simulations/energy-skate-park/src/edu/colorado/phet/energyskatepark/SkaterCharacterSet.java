package edu.colorado.phet.energyskatepark;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 10:25:07 PM
 */
public class SkaterCharacterSet {
    private ArrayList list = new ArrayList();

    public SkaterCharacterSet() {
        addSkaterCharacter( new SkaterCharacter( "images/skater3.png", "PhET Skater", 75.0, 1.8 ) );
        addSkaterCharacter( new SkaterCharacter( "images/starskater2.gif", "Star Skater", 60.0, 1.5 ) );
        addSkaterCharacter( new SkaterCharacter( "images/bulldog.gif", "Bulldog", 20.0, 1.125 ) );
        addSkaterCharacter( new SkaterCharacter( "images/bluebug.gif", "Bug", 0.2, 0.75 ) );
        addSkaterCharacter( new SkaterCharacter( "images/red-ball.png", "Ball", 5.0, 0.3, 2.0 ) );
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return (SkaterCharacter[])list.toArray( new SkaterCharacter[0] );
    }

    private void addSkaterCharacter( SkaterCharacter skaterCharacter ) {
        list.add( skaterCharacter );
    }
}
