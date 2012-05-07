// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab;

import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas;

import static edu.colorado.phet.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Equality lab module.
 *
 * @author Sam Reid
 */
public class EqualityLabModule extends AbstractFractionsModule {
    public EqualityLabModule() {
        this( new EqualityLabModel() );
    }

    public EqualityLabModule( EqualityLabModel model ) {
        super( Components.equalityLabTab, "Equality Lab", model.clock );
        setSimulationPanel( new EqualityLabCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new EqualityLabModule() ); }
}