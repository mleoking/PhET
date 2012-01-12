// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.equalitylab;

import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class EqualityLabModule extends AbstractFractionsModule {
    public EqualityLabModule() {
        super( "Equality Lab" );
        final EqualityLabModel model = new EqualityLabModel();
        setSimulationPanel( new EqualityLabCanvas( model ) );
    }
}