// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends BCECanvas {

    public GameCanvas( GameModel model, BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        // Under Construction notice
        PText underConstructionNode = new PText( "Under Construction" );
        underConstructionNode.setFont( new PhetFont( Font.BOLD, 20 ) );
        addChild( underConstructionNode );

        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, globalProperties.getFrame(), 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.addResettable( globalProperties );
        addChild( resetAllButtonNode );

        // layout
        double x = 0;
        double y = 0;
        underConstructionNode.setOffset( x, y );
        x = underConstructionNode.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 );
        y = underConstructionNode.getFullBoundsReference().getMaxY() + 100;
        resetAllButtonNode.setOffset( x, y );

        // Observers
        globalProperties.getMoleculesVisibleProperty().addObserver( new SimpleObserver() {
            public void update() {
                // TODO hide molecules
            }
        } );
    }

    public void reset() {
        //XXX
    }
}
