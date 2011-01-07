// Copyright 2002-2011, University of Colorado
package jass.engine;

/**
 * Thrown when encountering unsupported file format.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class UnsupportedAudioFileFormatException extends Exception {
    public UnsupportedAudioFileFormatException( String s ) {
        super( s );
    }

    public UnsupportedAudioFileFormatException() {
        super();
    }
}
