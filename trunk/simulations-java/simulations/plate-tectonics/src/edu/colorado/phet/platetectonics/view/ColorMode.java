// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.view.materials.CombinedMaterial;
import edu.colorado.phet.platetectonics.view.materials.DensityMaterial;
import edu.colorado.phet.platetectonics.view.materials.EarthMaterial;
import edu.colorado.phet.platetectonics.view.materials.TemperatureMaterial;

/**
 * Enum covering the different ways we can false-color the cross-section of earth
 */
public enum ColorMode {
    DENSITY( new DensityMaterial(), Strings.LESS_DENSE, Strings.MORE_DENSE ),
    TEMPERATURE( new TemperatureMaterial(), Strings.COOL, Strings.WARM ),
    COMBINED( new CombinedMaterial(), "Testing", "Testing" );

    private final EarthMaterial material;
    private final String minString;
    private final String maxString;

    private ColorMode( EarthMaterial material, String minString, String maxString ) {
        this.material = material;
        this.minString = minString;
        this.maxString = maxString;
    }

    public EarthMaterial getMaterial() {
        return material;
    }

    public String getMinString() {
        return minString;
    }

    public String getMaxString() {
        return maxString;
    }
}
