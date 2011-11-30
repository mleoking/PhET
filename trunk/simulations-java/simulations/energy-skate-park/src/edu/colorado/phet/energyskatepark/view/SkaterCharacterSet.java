// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.util.ArrayList;

import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 10:25:07 PM
 */
public class SkaterCharacterSet {
    private final ArrayList list = new ArrayList();

    public SkaterCharacterSet() {
        addSkaterCharacter( getDefaultCharacter() );
        addSkaterCharacter( new SkaterCharacter( "starskater2.gif", EnergySkateParkResources.getString( "skater.star-skater" ), 60.0, 1.5 ) );
        addSkaterCharacter( new SkaterCharacter( "bulldog.png", EnergySkateParkResources.getString( "skater.bulldog" ), 20.0, 1.125 ) );
        addSkaterCharacter( new SkaterCharacter( "bugboard.gif", EnergySkateParkResources.getString( "skater.bug" ), 0.2, 0.6 ) );
        addSkaterCharacter( new SkaterCharacter( "red-ball.png", EnergySkateParkResources.getString( "skater.ball" ), 5.0, 0.3, 2.0 ) );
    }

    public static SkaterCharacter getDefaultCharacter() {
        return new SkaterCharacter( "skater3.png", EnergySkateParkResources.getString( "skater.phet-skater" ), 75.0, 1.8 );
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return (SkaterCharacter[]) list.toArray( new SkaterCharacter[0] );
    }

    private void addSkaterCharacter( SkaterCharacter skaterCharacter ) {
        list.add( skaterCharacter );
    }
}
