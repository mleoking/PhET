// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.equalitylab;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.view.EqualityLabCanvas;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Equality lab module.
 *
 * @author Sam Reid
 */
public class EqualityLabModule extends AbstractFractionsModule {
    public EqualityLabModule() {
        this( new EqualityLabModel() );
    }

    private EqualityLabModule( EqualityLabModel model ) {
        super( Components.equalityLabTab, Strings.EQUALITY_LAB, model.clock );
        setSimulationPanel( new EqualityLabCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new EqualityLabModule() ); }
}