// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.InjectorNode;
import edu.colorado.phet.fluidpressureandflow.flow.model.Pipe;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendButtonPressed;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.gridInjectorButton;

/**
 * Injects a grid of particles.
 *
 * @author Sam Reid
 */
public class GridInjectorNode extends InjectorNode {

    public GridInjectorNode( final ModelViewTransform mvt, double rotationAngle, final SimpleObserver squirt, final Pipe pipe ) {
        super( rotationAngle, new SimpleObserver() {
            public void update() {
                squirt.update();
            }
        } );

        final SimpleObserver updateLocation = new SimpleObserver() {
            public void update() {

                //It might be better to rewrite this to be a function of x instead of array index
                final Point2D site = mvt.modelToView( pipe.getSplineCrossSections().get( 11 ).getTop() );
                setOffset( site.getX(), site.getY() - distanceCenterToTip + 5 );
            }
        };
        pipe.addShapeChangeListener( updateLocation );
        updateLocation.update();

        //Our own mouse listener and cursor handler is necessary so the user can't press the button repeatedly
        buttonImageNode.addInputEventListener( new PBasicInputEventHandler() {
            public boolean pushed = false;
            public boolean entered = false;
            public boolean disabled = false;

            @Override public void mouseEntered( PInputEvent event ) {
                if ( !disabled ) {
                    checkPush( event );
                }
                entered = true;
            }

            private void checkPush( PInputEvent event ) {
                if ( !pushed ) {
                    event.getComponent().pushCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                    pushed = true;
                }
            }

            @Override public void mouseExited( PInputEvent event ) {
                checkPop( event );
                entered = false;
            }

            private void checkPop( PInputEvent event ) {
                if ( pushed ) {
                    event.getComponent().popCursor();
                    pushed = false;
                }
            }

            @Override public void mousePressed( PInputEvent event ) {
                buttonImageNode.setImage( pressedButtonImage );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                if ( !disabled ) {

                    sendButtonPressed( gridInjectorButton );

                    inject.update();
                    checkPop( event );

                    disabled = true;

                    //After 5 sec, re-enable the button and update the cursor
                    new Timer( 5000, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            buttonImageNode.setImage( unpressedButtonImage );
                            if ( entered ) {
                                checkPush( event );
                            }
                            disabled = false;
                        }
                    } ) {{setRepeats( false );}}.start();
                }
            }
        } );
    }
}
