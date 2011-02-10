// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

/**
 * @author Sam Reid
 */
public class MediumState {
    public final String name;
    public final double index;

    public MediumState( String name, double index ) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isCustom() {
        return false;
    }

    public boolean isMystery() {
        return false;
    }
}
