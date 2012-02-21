// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Concentration units, specific to solution
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationUnitsNode extends PText {
    public ConcentrationUnitsNode( final Property<BeersLawSolution> solutionProperty, PhetFont font ) {
        setFont( font );
        solutionProperty.addObserver( new SimpleObserver() {
            public void update() {
                setText( solutionProperty.get().getViewUnits() );
            }
        } );
    }
}
