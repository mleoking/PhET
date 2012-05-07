// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameCanvas;

import static edu.colorado.phet.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Module for the matching game tab.
 *
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule( boolean dev ) {
        this( dev, new MatchingGameModel() );
    }

    public MatchingGameModule( boolean dev, MatchingGameModel model ) {
        super( Components.matchingGameTab, "Matching Game", model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new MatchingGameModule( true ) ); }
}