// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures.tests;

import fj.F;
import fj.data.List;

/**
 * @author Sam Reid
 */
public class TestFJ {
    static abstract class Animal {
        public abstract String getSound();

        public static F<Animal, String> _getSound = new F<Animal, String>() {
            @Override public String f( final Animal animal ) {
                return animal.getSound();
            }
        };
    }

    static class Lion extends Animal {
        @Override public String getSound() {
            return "Roar!";
        }
    }

    public static void main( String[] args ) {
        List<Lion> animals = List.list( new Lion(), new Lion() );
//        List<String> sounds = animals.map( Animal._getSound );
    }
}