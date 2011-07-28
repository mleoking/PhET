// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.IfElse;

/**
 * Color to show for the specified particle
 *
 * @author Sam Reid
 */
public class IonColor extends IfElse<Color> {
    public IonColor( MicroModel microModel, SphericalParticle particle ) {
        super( microModel.showChargeColor, particle.chargeColor, particle.color );
    }
}
