// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;

/**
 * Basic information about an element or ion to be shown, such as Na+ or Cl-, including the color, name and radius
 *
 * @author Sam Reid
 */
public class S3Element {
    //Sizes from the design doc
    public static final double CHLORINE_RADIUS = 181E-12;
    public static final double SODIUM_RADIUS = 102E-12;

    public final String name;
    public final double radius;
    public final Color color;

    public S3Element( String name, double radius, Color color ) {
        this.name = name;
        this.radius = radius;
        this.color = color;
    }

    //Use colors from edu.colorado.phet.chemistry.model.Element and size from design doc
    public static final S3Element ClIon = new S3Element( "Cl-", 181, new Color( 153, 242, 57 ) );

    //Used color from https://secure.wikimedia.org/wikipedia/en/wiki/CPK_coloring and size from design doc
    public static final S3Element NaIon = new S3Element( "Na+", 102, new Color( 171, 92, 242 ) );

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }
}
