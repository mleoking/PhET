// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

/**
 * @author Sam Reid
 */
public class MediumState {
    public final String name;
    public final double index;
    public final boolean mystery;
    public final boolean custom;

    public MediumState( String name, double index ) {
        this( name, index, false );
    }

    public MediumState( String name, double index, boolean mystery ) {
        this( name, index, mystery, false );
    }

    public MediumState( String name, double index, boolean mystery, boolean custom ) {
        this.name = name;
        this.index = index;
        this.mystery = mystery;
        this.custom = custom;
    }

    @Override
    public String toString() {
        return name;
    }
}