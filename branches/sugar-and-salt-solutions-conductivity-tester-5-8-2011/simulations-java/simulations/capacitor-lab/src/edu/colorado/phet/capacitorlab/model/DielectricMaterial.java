// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Color;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class and subclasses for dielectric materials.
 * All subclasses for "real" materials are immutable.
 * The subclass for a "custom" material has a mutable dielectric constant.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DielectricMaterial {

    private final String name;
    private final Color color;

    private final Property<Double> dielectricConstantProperty;

    public DielectricMaterial( String name, double dielectricConstant, Color color ) {
        this.name = name;
        this.dielectricConstantProperty = new Property<Double>( dielectricConstant );
        this.color = color;
    }

    public void reset() {
        dielectricConstantProperty.reset();
    }

    public String getName() {
        return name;
    }

    public double getDielectricConstant() {
        return dielectricConstantProperty.getValue();
    }

    protected void setDielectricConstant( double dielectricConstant ) {
        dielectricConstantProperty.setValue( dielectricConstant );
    }

    public void addDielectricConstantObserver( SimpleObserver o ) {
        dielectricConstantProperty.addObserver( o );
    }

    public void removeDielectricConstantObserver( SimpleObserver o ) {
        dielectricConstantProperty.removeObserver( o );
    }

    public Color getColor() {
        return color;
    }

    public boolean isOpaque() {
        return color.getAlpha() == 255;
    }

    public static class Teflon extends DielectricMaterial {
        public Teflon() {
            super( CLStrings.TEFLON, CLConstants.EPSILON_TEFLON, CLPaints.TEFLON );
        }
    }

    public static class Glass extends DielectricMaterial {
        public Glass() {
            super( CLStrings.GLASS, CLConstants.EPSILON_GLASS, CLPaints.GLASS );
        }
    }

    public static class Paper extends DielectricMaterial {
        public Paper() {
            super( CLStrings.PAPER, CLConstants.EPSILON_PAPER, CLPaints.PAPER );
        }
    }

    /**
     * A custom dielectric material with mutable dielectric constant.
     */
    public static class CustomDielectricMaterial extends DielectricMaterial {

        public CustomDielectricMaterial( double dielectricConstant ) {
            super( CLStrings.CUSTOM, dielectricConstant, CLPaints.CUSTOM_DIELECTRIC );
        }

        /**
         * Dielectric constant is mutable for custom materials, so make this method public.
         * @param dielectricConstant
         */
        public void setDielectricConstant( double dielectricConstant ) {
            super.setDielectricConstant( dielectricConstant );
        }
    }
}
