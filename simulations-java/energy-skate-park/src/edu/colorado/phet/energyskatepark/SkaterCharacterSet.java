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
//        addSkaterCharacter( new SkaterCharacter( "images/ferrari-side2.gif", "Ferarri", 2000.0, 1.0 ) );
//        addSkaterCharacter( new SkaterCharacter( "images/Lunar-Rover.gif", "Lunar Rover", 210.0, 1.2 ) );
//        addSkaterCharacter( new SkaterCharacter( "images/Mars-Lander.gif", "Mars Lander", 180.0, 1.3 ) );
//        addSkaterCharacter( new SkaterCharacter( "images/motorcycle.gif", "Motorcycle", 227.0, 1.0 ) );
        addSkaterCharacter( new SkaterCharacter( "images/bulldog.gif", "Bulldog", 20.0, 1.125) );
        addSkaterCharacter( new SkaterCharacter( "images/bugboard.gif", "Bug", 0.2, 0.75) );
        addSkaterCharacter( new SkaterCharacter( "images/ball.png", "Ball", 20.0, 0.6, 2.0 ) );
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return (SkaterCharacter[])list.toArray( new SkaterCharacter[0] );
    }

    private void addSkaterCharacter( SkaterCharacter skaterCharacter ) {
        list.add( skaterCharacter );
    }
}
