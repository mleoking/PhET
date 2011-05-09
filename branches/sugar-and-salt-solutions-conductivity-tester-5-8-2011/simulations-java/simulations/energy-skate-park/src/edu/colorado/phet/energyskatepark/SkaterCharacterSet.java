// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 10:25:07 PM
 */
public class SkaterCharacterSet {
    private ArrayList list = new ArrayList();

    public SkaterCharacterSet() {
        addSkaterCharacter( getDefaultCharacter() );
        addSkaterCharacter( new SkaterCharacter( "energy-skate-park/images/starskater2.gif", EnergySkateParkStrings.getString("skater.star-skater"), 60.0, 1.5 ) );
        addSkaterCharacter( new SkaterCharacter( "energy-skate-park/images/bulldog.png", EnergySkateParkStrings.getString("skater.bulldog"), 20.0, 1.125 ) );
        addSkaterCharacter( new SkaterCharacter( "energy-skate-park/images/bugboard.gif", EnergySkateParkStrings.getString("skater.bug"), 0.2, 0.6 ) );
        addSkaterCharacter( new SkaterCharacter( "energy-skate-park/images/red-ball.png", EnergySkateParkStrings.getString("skater.ball"), 5.0, 0.3, 2.0 ) );
    }

    public static SkaterCharacter getDefaultCharacter() {
        return new SkaterCharacter( "energy-skate-park/images/skater3.png", EnergySkateParkStrings.getString("skater.phet-skater"), 75.0, 1.8 );
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return (SkaterCharacter[])list.toArray( new SkaterCharacter[0] );
    }

    private void addSkaterCharacter( SkaterCharacter skaterCharacter ) {
        list.add( skaterCharacter );
    }
}
