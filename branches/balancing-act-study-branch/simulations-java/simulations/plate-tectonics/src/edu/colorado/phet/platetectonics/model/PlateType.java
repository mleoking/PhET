// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;

/**
 * Different types of plates have different properties
 */
public enum PlateType {
    CONTINENTAL( true, false, 2750,
                 3500, -40000, 70000, Strings.CONTINENTAL_CRUST ),
    YOUNG_OCEANIC( false, true, 3000,
                   -4000, -10000, 45000, Strings.YOUNG_OCEANIC_CRUST ),
    OLD_OCEANIC( false, true, 3070,
                 -4000, -10000, 55000, Strings.OLD_OCEANIC_CRUST ); // old oceanic lithosphere is thicker
    private final boolean continental;
    private final boolean oceanic;
    private final float density;
    private final float crustTopY;
    private final float crustBottomY;
    private final float mantleLithosphereThickness;
    private final String specificLabel;

    PlateType( boolean isContinental, boolean isOceanic, float density,
               float crustTopY, float crustBottomY, float mantleLithosphereThickness,
               String specificLabel ) {
        continental = isContinental;
        oceanic = isOceanic;
        this.density = density;
        this.crustTopY = crustTopY;
        this.crustBottomY = crustBottomY;
        this.mantleLithosphereThickness = mantleLithosphereThickness;
        this.specificLabel = specificLabel;
    }

    public boolean isContinental() {
        return continental;
    }

    public boolean isOceanic() {
        return oceanic;
    }

    public float getDensity() {
        return density;
    }

    public float getCrustTopY() {
        return crustTopY;
    }

    public float getCrustBottomY() {
        return crustBottomY;
    }

    public float getMantleLithosphereThickness() {
        return mantleLithosphereThickness;
    }

    public float getLithosphereBottomY() {
        return getCrustBottomY() - getMantleLithosphereThickness();
    }

    public float getCrustThickness() {
        return getCrustTopY() - getCrustBottomY();
    }

    public float getLithosphereThickness() {
        return getCrustTopY() - getLithosphereBottomY();
    }

    public String getSpecificLabel() {
        return specificLabel;
    }
}
