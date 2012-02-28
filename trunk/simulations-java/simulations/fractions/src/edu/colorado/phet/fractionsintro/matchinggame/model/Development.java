// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

/**
 * Flags for development
 *
 * @author Sam Reid
 */
@Data public class Development {

    //Show a shape outline on the text for the equals sign
    public final boolean outline;

    public Development outline( boolean outline ) {
        return new Development( outline );
    }
}
