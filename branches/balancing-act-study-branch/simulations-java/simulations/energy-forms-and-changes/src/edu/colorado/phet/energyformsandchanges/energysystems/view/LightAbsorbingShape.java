// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A shape that absorbs light as the light passes through it.
 *
 * @author John Blanco
 */
public class LightAbsorbingShape {
    public final Shape shape;
    public final Property<Double> lightAbsorptionCoefficient;

    public LightAbsorbingShape( Shape shape, double initialLightAbsorptionCoefficient ) {
        this.shape = shape;
        lightAbsorptionCoefficient = new Property<Double>( initialLightAbsorptionCoefficient );
    }
}
