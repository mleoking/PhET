// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameCanvas;

/**
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule() {
        super( "Matching Game", new ConstantDtClock() );
        MatchingGameModel model = new MatchingGameModel( ModelViewTransform.createIdentity() );
        setSimulationPanel( new MatchingGameCanvas( model ) );
    }
}