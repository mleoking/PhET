// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final Property<MatchingGameState> state = new Property<MatchingGameState>( initialState() );
}
