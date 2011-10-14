// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas;
import edu.colorado.phet.sugarandsaltsolutions.common.view.RemoveSoluteButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.REMOVE_SALT;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.REMOVE_SUGAR;

/**
 * Node that contains one button to remove salt (if there is any salt) and another button for sugar.
 *
 * @author Sam Reid
 */
public class RemoveSoluteControlNode extends PNode {

    public RemoveSoluteControlNode( final MacroModel model ) {

        //Button to remove salt, only shown if there is any salt
        RemoveSoluteButtonNode saltButton = new RemoveSoluteButtonNode( REMOVE_SALT, model.isAnySaltToRemove(), new VoidFunction0() {
            public void apply() {
                model.removeSalt();
            }
        } );
        addChild( saltButton );

        //Button to remove sugar, only shown if there is any sugar
        RemoveSoluteButtonNode sugarButton = new RemoveSoluteButtonNode( REMOVE_SUGAR, model.isAnySugarToRemove(), new VoidFunction0() {
            public void apply() {
                model.removeSugar();
            }
        } );
        addChild( sugarButton );

        //Put the buttons next to each other, leaving the origin at (0,0) so it can be positioned easily by the client
        sugarButton.setOffset( saltButton.getFullBounds().getMaxX() + BeakerAndShakerCanvas.INSET, 0 );
    }
}