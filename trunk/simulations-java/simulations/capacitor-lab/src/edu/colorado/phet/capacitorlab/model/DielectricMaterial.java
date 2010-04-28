/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.text.MessageFormat;

import edu.colorado.phet.capacitorlab.CLStrings;


public abstract class DielectricMaterial {
    
    private final String name;
    private final double dielectricConstant;
    
    public DielectricMaterial( String name, double dielectricConstant ) {
        this.name = name;
        this.dielectricConstant = dielectricConstant;
    }
    
    public String getName() {
        return name;
    }

    public double getDielectricConstant() {
        return dielectricConstant;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format( CLStrings.FORMAT_DIELECTRIC_MATERIAL, name, dielectricConstant );
    }
    
    @Override
    /**
     * Two materials are equal if their names and dielectric constants are the same.
     */
    public boolean equals( Object object ) {
        boolean equals = false;
        if ( object != null && object instanceof DielectricMaterial ) {
            DielectricMaterial material = (DielectricMaterial) object;
            equals = ( getName().equals( material.getName() ) && getDielectricConstant() == material.getDielectricConstant() );
        }
        return equals;
    }
    
    public static class Teflon extends DielectricMaterial {
        public Teflon() {
            super( "teflon", 2.1 );
        }
    }
    
    public static class Polystyrene extends DielectricMaterial {
        public Polystyrene() {
            super( "polystyrene", 2.5 );
        }
    }
    
    public static class Paper extends DielectricMaterial {
        public Paper() {
            super( "paper", 3.5 );
        }
    }
    
    public static class CustomDielectricMaterial extends DielectricMaterial {
        
        public CustomDielectricMaterial() {
            super( "CUSTOM", 1 );
        }
        
        @Override
        public String toString() {
            return getName();
        }
    }
}
