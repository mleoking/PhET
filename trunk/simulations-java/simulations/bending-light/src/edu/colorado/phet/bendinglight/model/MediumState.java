// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

/**
 * @author Sam Reid
 */
public class MediumState {
    public final String name;
    public final double index;
    public final boolean mystery;

    public MediumState( String name, double index ) {
        this( name, index, false );
    }

    public MediumState( String name, double index, boolean mystery ) {
        this.name = name;
        this.index = index;
        this.mystery = mystery;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isCustom() {
        return false;
    }
}