/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Color;
import java.awt.Paint;
import java.text.MessageFormat;

import edu.colorado.phet.capacitorlab.CLStrings;


public abstract class DielectricMaterial {
    
    private final String name;
    private double dielectricConstant;
    private final Paint paint;
    
    public DielectricMaterial( String name, double dielectricConstant, Paint paint ) {
        this.name = name;
        this.dielectricConstant = dielectricConstant;
        this.paint = paint;
    }
    
    public String getName() {
        return name;
    }

    public double getDielectricConstant() {
        return dielectricConstant;
    }
    
    public Paint getPaint() {
        return paint;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format( CLStrings.FORMAT_DIELECTRIC_MATERIAL, name, dielectricConstant );
    }
    
    @Override
    /**
     * Two materials are equal if their properties are the same.
     */
    public boolean equals( Object object ) {
        boolean equals = false;
        if ( object != null && object instanceof DielectricMaterial ) {
            DielectricMaterial material = (DielectricMaterial) object;
            equals = ( getName().equals( material.getName() ) && getDielectricConstant() == material.getDielectricConstant() && getPaint().equals( material.getPaint() ) );
        }
        return equals;
    }
    
    public static class Teflon extends DielectricMaterial {
        public Teflon() {
            super( CLStrings.MATERIAL_TEFLON, 2.1, Color.RED );
        }
    }
    
    public static class Polystyrene extends DielectricMaterial {
        public Polystyrene() {
            super( CLStrings.MATERIAL_POLYSTYRENE, 2.5, Color.GREEN );
        }
    }
    
    public static class Paper extends DielectricMaterial {
        public Paper() {
            super( CLStrings.MATERIAL_PAPER, 3.5, Color.BLUE );
        }
    }
    
    public static class CustomDielectricMaterial extends DielectricMaterial {
        
        public CustomDielectricMaterial() {
            super( CLStrings.MATERIAL_CUSTOM, 2, Color.YELLOW );
        }
        
        /**
         * Dielectric constant is mutable for our custom material.
         * @param dielectricConstant
         */
        public void setDielectricConstant( double dielectricConstant ) {
            if ( dielectricConstant != super.dielectricConstant ) {
                super.dielectricConstant = dielectricConstant;
                //XXX fireStateChanged
            }
        }
        
        /**
         * Show only the custom material's name, not its mutable constant.
         */
        @Override
        public String toString() {
            return getName();
        }
    }
}
