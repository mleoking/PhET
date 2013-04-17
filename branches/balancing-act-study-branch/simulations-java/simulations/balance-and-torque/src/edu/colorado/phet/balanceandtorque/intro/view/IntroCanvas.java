// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro.view;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.view.BasicBalanceCanvas;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Canvas for the "Intro" module.
 *
 * @author John Blanco
 */
public class IntroCanvas extends BasicBalanceCanvas {
    public IntroCanvas( final BalanceModel model ) {
        super( model );

        model.massList.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( final Mass mass ) {
                // Add a listener for when the user drops the mass.  This is
                // done here in this case, rather than in the model, because we
                // need to check whether or not the user dropped it on the
                // "stage" so that it isn't permanently dragged off of the screen.
                mass.userControlled.addObserver( new ChangeObserver<Boolean>() {
                    public void update( Boolean newValue, Boolean oldValue ) {
                        if ( oldValue && !newValue ) {
                            // The user has dropped this mass.
                            if ( !model.getPlank().addMassToSurface( mass ) ) {
                                // The attempt to add mass to surface of plank failed,
                                // probably because mass was dropped somewhere other
                                // than over the plank.
                                if ( mvt.modelToView( mass.getPosition() ).getX() > 0 && mvt.modelToView( mass.getPosition() ).getX() < DEFAULT_STAGE_SIZE.getWidth() ) {
                                    // Mass is in the visible area, so just
                                    // drop it on the ground.
                                    mass.setPosition( mass.getPosition().getX(), 0 );
                                }
                                else {
                                    // Mass is off stage.  Return it to its
                                    // original position.
                                    mass.resetPosition();
                                }
                            }
                        }
                    }
                } );
            }
        } );
    }
}
