/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.molecularreactions.model.Launcher;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * LauncherGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherGraphic extends RegisterablePNode implements SimpleObserver {
    private double minTheta = -Math.PI / 3;
    private double maxTheta = Math.PI / 3;
    private Launcher launcher;
    private PImage plungerNode;


    public LauncherGraphic( Launcher launcher ) {
        this.launcher = launcher;
        plungerNode = PImageFactory.create( "images/launcher-plunger.png" );
        double scale = 100.0 / plungerNode.getImage().getHeight( null );
        plungerNode.scale( scale );
        addChild( plungerNode );
        setRegistrationPoint( getFullBounds().getWidth() / 2, 0 );

        // Add mouse handler
        plungerNode.addInputEventListener( new PlungerMouseHandler() );

        launcher.addObserver( this );
        update();
    }

    public void update() {
        plungerNode.setOffset( launcher.getTipLocation() );
        this.rotateAboutPoint( launcher.getTheta() - this.getRotation(), launcher.getPivotPoint() );
    }


    /**
     * Mouse handler
     */
    private class PlungerMouseHandler extends PBasicInputEventHandler {
        double dySinceLastMolecule;

        public void mouseEntered( PInputEvent event ) {
            if( launcher.isEnabled() ) {
                if( launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
                    PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
                }
                else if( launcher.getMovementType() == Launcher.ONE_DIMENSIONAL ) {
                    PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
                }
            }
        }

        public void mouseExited( PInputEvent event ) {
            if( launcher.isEnabled() ) {
                PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        }

        public void mousePressed( PInputEvent event ) {
        }

        public void mouseReleased( PInputEvent event ) {
            if( launcher.isEnabled() ) {
                launcher.release();
                PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        }

        /**
         * Moves the pump handle graphic and adds molecules to the model
         *
         * @param event
         */
        public void mouseDragged( PInputEvent event ) {
            if( launcher.isEnabled() ) {
                double dy = event.getDelta().getHeight();
                double yLoc = plungerNode.getOffset().getY() + dy;

                // Constrain the motion of the handle to be within the bounds of the PNode containing
                // the PumpGraphic, and the initial location of the handle.
                if( yLoc >= launcher.getPivotPoint().getY() ) {
                    launcher.translate( 0, dy );
                }

                // Rotate the plunger if the  mouse move left or right
                double dx = event.getDelta().getWidth();
                if( dx != 0 && launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
                    Point2D p = event.getPositionRelativeTo( LauncherGraphic.this );
                    double r = Math.sqrt( getFullBounds().getHeight() * getFullBounds().getHeight()
                                          + getFullBounds().getWidth() * getFullBounds().getWidth() );
                    double dTheta = Math.asin( -dx / r );
                    double theta = Math.min( maxTheta, Math.max( minTheta, launcher.getTheta() + dTheta ) );
                    launcher.setTheta( theta );
                }
            }
        }
    }

}
