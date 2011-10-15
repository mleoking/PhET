// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.matchinggame.model.FractionRepresentation;
import edu.colorado.phet.fractions.intro.matchinggame.model.MatchingGameModel;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    public MatchingGameCanvas( MatchingGameModel model ) {
        for ( FractionRepresentation representation : model.fractionRepresentations ) {
            addChild( representation.toNode() );
        }
    }
}