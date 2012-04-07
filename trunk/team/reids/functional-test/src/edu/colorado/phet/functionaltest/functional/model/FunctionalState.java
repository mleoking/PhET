// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractions.util.FJUtils;

/**
 * @author Sam Reid
 */
public @Data class FunctionalState {
    public final List<Circle> circles;

    public FunctionalState sort() {
        return new FunctionalState( circles.sort( FJUtils.ord( new F<Circle, Double>() {
            @Override public Double f( final Circle circle ) {
                return circle.dragging ? 1.0 : 0.0;
            }
        } ) ) );
    }

    public FunctionalState stepInTime( final double simulationTimeChange ) {
        return new FunctionalState( circles.map( new F<Circle, Circle>() {
            @Override public Circle f( final Circle circle ) {
                return circle.dragging ? circle : circle.translate( new Dimension2DDouble( 1, 1 ) );
            }
        } ) );
    }
}