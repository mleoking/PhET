// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.Font;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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

        // layout
        underConstructionNode.setOffset( 0, 0 );

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
