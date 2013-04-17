package edu.colorado.phet.functions;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Sam Reid
 */
public class GenerateStringsLevels {
    public static void main( String[] args ) {
        new GenerateStringsLevels().start();
    }

    private void start() {
        F<String, String> reverse = new F<String, String>() {
            @Override public String f( final String s ) {
                return new StringBuffer( s ).reverse().toString();
            }
        };
        F<String, String> first = new F<String, String>() {
            @Override public String f( final String s ) {
                if ( s.length() > 0 ) {
                    return s.charAt( 0 ) + "";
                }
                else {
                    return "";
                }
            }
        };
        F<String, String> toUpperCase = new F<String, String>() {
            @Override public String f( final String s ) {
                return s.toUpperCase();
            }
        };
        F<String, String> copy = new F<String, String>() {
            @Override public String f( final String s ) {
                return s + s;
            }
        };
        F<String, String> onlyVowels = new F<String, String>() {
            @Override public String f( final String s ) {
                final List<Character> letters = List.fromString( s ).filter( new F<Character, Boolean>() {
                    @Override public Boolean f( final Character character ) {
                        return List.list( 'a', 'e', 'i', 'o', 'u' ).elementIndex( Equal.charEqual, character ).isSome();
                    }
                } );
                String x = "";
                for ( Character letter : letters ) {
                    x += letter;
                }
                return x;
            }
        };

        F<String, String> onlyConsonants = new F<String, String>() {
            @Override public String f( final String s ) {
                final List<Character> letters = List.fromString( s ).filter( new F<Character, Boolean>() {
                    @Override public Boolean f( final Character character ) {
                        return List.list( 'a', 'e', 'i', 'o', 'u' ).elementIndex( Equal.charEqual, character ).isNone();
                    }
                } );
                String x = "";
                for ( Character letter : letters ) {
                    x += letter;
                }
                return x;
            }
        };

        List<F<String, String>> functions = List.list( reverse, first, toUpperCase, copy, onlyVowels, onlyConsonants );

        List<String> inputs = List.list( "hello", "PhET", "one", "racecar", "dessert" );

        Random random = new Random();
        for ( int i = 0; i < 100; i++ ) {
            //choose 2-4 of the functions and apply them to one input
            ArrayList<F<String, String>> f = new ArrayList<F<String, String>>();
            int numToChoose = random.nextInt( 3 ) + 2;
            for ( int k = 0; k < numToChoose; k++ ) {
                f.add( functions.index( random.nextInt( functions.length() ) ) );
            }

            String string = inputs.index( random.nextInt( inputs.length() ) );
            String originalString = string;
            for ( F<String, String> myFunction : f ) {
                string = myFunction.f( string );
            }
            System.out.println( originalString + " => " + string );
        }
    }
}