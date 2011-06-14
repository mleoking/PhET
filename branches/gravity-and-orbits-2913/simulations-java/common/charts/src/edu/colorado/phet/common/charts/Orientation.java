// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.charts;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 2:42:22 PM
 */

public class Orientation {
    public static final Orientation HORIZONTAL = new Orientation( "Horizontal", true );
    public static final Orientation VERTICAL = new Orientation( "Vertical", false );
    private String name;
    public boolean horizontal;

    private Orientation( String name, boolean horizontal ) {
        this.name = name;
        this.horizontal = horizontal;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isVertical() {
        return !horizontal;
    }

    public String toString() {
        return name;
    }

    public boolean equals( Object obj ) {
        if ( obj instanceof Orientation ) {
            Orientation orientation = (Orientation) obj;
            return orientation.name.equals( name );
        }
        else {
            return false;
        }
    }

    public Orientation opposite() {
        if ( isVertical() ) {
            return HORIZONTAL;
        }
        else {
            return VERTICAL;
        }
    }
}
