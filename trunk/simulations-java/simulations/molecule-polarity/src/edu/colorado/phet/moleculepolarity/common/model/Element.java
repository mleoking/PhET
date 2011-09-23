// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.awt.Color;

/**
 * Elements, as they are known by Jmol.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Element {

    public final int elementNumber; // element number in the periodic table
    public final Color color; // color used by Jmol

    public Element( int elementNumber, Color color ) {
        this.elementNumber = elementNumber;
        this.color = color;
    }
}
