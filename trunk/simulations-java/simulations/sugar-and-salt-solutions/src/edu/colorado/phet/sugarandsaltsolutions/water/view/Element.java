// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;

/**
 * Basic information about an element or ion to be shown, such as Na+ or Cl-, including the color, name and radius
 *
 * @author Sam Reid
 */
public class Element {

    public final String name;
    public final double radius;
    public final Color color;

    public Element( String name, double radius, Color color ) {
        this.name = name;
        this.radius = radius;
        this.color = color;
    }
}