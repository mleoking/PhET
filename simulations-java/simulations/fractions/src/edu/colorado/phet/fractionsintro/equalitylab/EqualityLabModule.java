// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab;

import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

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