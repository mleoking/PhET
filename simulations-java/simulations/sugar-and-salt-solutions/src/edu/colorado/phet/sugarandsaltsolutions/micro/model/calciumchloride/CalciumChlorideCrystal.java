// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.CalciumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CalciumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;

/**
 * This crystal for Calcium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystal extends Crystal<SphericalParticle> {
    public CalciumChlorideCrystal( ImmutableVector2D position, CalciumChlorideLattice lattice ) {
        super( position, new CalciumIonParticle().radius + new ChlorideIonParticle().radius, new Function2<Component, Double, SphericalParticle>() {
                   public SphericalParticle apply( Component component, Double angle ) {
                       if ( component instanceof CalciumIon ) {
                           return new CalciumIonParticle();
                       }
                       else {
                           return new ChlorideIonParticle();
                       }
                   }
               }, lattice );
    }
}