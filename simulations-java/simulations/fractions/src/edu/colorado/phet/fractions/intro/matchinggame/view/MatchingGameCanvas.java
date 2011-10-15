// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractions.intro.matchinggame.model.Representation;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    public MatchingGameCanvas( MatchingGameModel model ) {
        ModelViewTransform transform = ModelViewTransform.createIdentity();
        for ( Representation representation : model.fractionRepresentations ) {
            addChild( representation.toNode( transform ) );
        }
    }
}