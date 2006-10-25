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
import edu.colorado.phet.molecularreactions.model.Launcher;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * LauncherGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherGraphic extends PNode implements SimpleObserver {
//public class LauncherGraphic extends RegisterablePNode implements SimpleObserver {
    private double minTheta = -Math.PI / 3;
    private double maxTheta = Math.PI / 3;
    private Launcher launcher;
    private RegisterablePNode plungerNode;
    private RegisterablePNode plungerFrameNode;
    private RegisterablePNode plunger2DFrameNode;
    private RegisterablePNode plunger2DFrameStrutsNode;

    private String baseImagePath = "images/";
    private String plungerImageFile = baseImagePath + "plunger.png";
    private String frameImageFile = baseImagePath + "frame.png";
    private String frame2DImageFile = baseImagePath + "2D-frame.png";
    private String strutsImageFile = baseImagePath + "struts.png";
    private Point2D pivotPt;


    public LauncherGraphic( Launcher launcher ) {
        this.launcher = launcher;

//        plungerNode = PImageFactory.create( "images/launcher-plunger.png" );
        plungerNode = new RegisterablePNode( PImageFactory.create( plungerImageFile ) );
        double scale = 100.0 / plungerNode.getFullBounds().getHeight();
        plungerNode.scale( scale );
//        plungerNode.setRegistrationPoint( RegisterablePNode.NORTH );
//        plungerNode.setRegistrationPoint( plungerNode.getFullBounds().getWidth(), 0 );

//        plunger2DFrameStrutsNode = PImageFactory.create( "images/2D-struts.png" );
        plunger2DFrameStrutsNode = new RegisterablePNode( PImageFactory.create( strutsImageFile ) );
        plunger2DFrameStrutsNode.scale( scale );
//        plunger2DFrameStrutsNode.setRegistrationPoint( RegisterablePNode.NORTH );
//        plunger2DFrameStrutsNode.setRegistrationPoint( -60, 0 );

        plungerFrameNode = new RegisterablePNode( PImageFactory.create( frameImageFile ) );
//        plungerFrameNode = PImageFactory.create( "images/plunger-frame.png" );
        plungerFrameNode.scale( scale );
//        plungerFrameNode.setRegistrationPoint( RegisterablePNode.NORTH );
        plungerFrameNode.setPickable( false );

        plunger2DFrameNode = new RegisterablePNode( PImageFactory.create( frame2DImageFile ) );
        plunger2DFrameNode.scale( scale );
//        plunger2DFrameNode.setRegistrationPoint( RegisterablePNode.NORTH );
//        plunger2DFrameNode = PImageFactory.create( "images/2D-plunger-frame.png" );

        addChild( plunger2DFrameStrutsNode );
        addChild( plungerNode );
        addChild( plunger2DFrameNode );
        addChild( plungerFrameNode );

        pivotPt = new Point2D.Double( getFullBounds().getWidth() / 2, 0 );
        double centerX = getFullBounds().getWidth() / 2;
        plungerNode.setOffset( centerX - plungerNode.getFullBounds().getWidth() / 2, 0 );
        plunger2DFrameStrutsNode.setOffset( centerX - plunger2DFrameStrutsNode.getFullBounds().getWidth() / 2, 0 );
        plungerFrameNode.setOffset( centerX - plungerFrameNode.getFullBounds().getWidth() / 2, 0 );
        plunger2DFrameNode.setOffset( centerX - plunger2DFrameNode.getFullBounds().getWidth() / 2, 0 );
        plunger2DFrameStrutsNode.setOffset( centerX - plunger2DFrameStrutsNode.getFullBounds().getWidth() / 2, 0 );


        setOffset( launcher.getRestingTipLocation().getX() - getFullBounds().getWidth() / 2,
                   launcher.getRestingTipLocation().getY() );

        // Add mouse handler
        plungerNode.addInputEventListener( new PlungerMouseHandler() );

        launcher.addObserver( this );
        update();
    }

    public void update() {
        plungerNode.setOffset( getFullBounds().getWidth() / 2 - plungerNode.getFullBounds().getWidth() / 2,
                               launcher.getTipLocation().getY() - launcher.getRestingTipLocation().getY() );
        plungerNode.rotateAboutPoint( launcher.getTheta() - plungerNode.getRotation(), pivotPt );
        plungerFrameNode.rotateAboutPoint( launcher.getTheta() - plungerFrameNode.getRotation(), pivotPt );
        boolean twoD = launcher.getMovementType() == Launcher.TWO_DIMENSIONAL;
        plunger2DFrameStrutsNode.setVisible( twoD );
        plunger2DFrameNode.setVisible( twoD );
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
                if( yLoc >= 1
                    && yLoc <= launcher.getPivotPoint().getY() + plungerFrameNode.getHeight() - 20 ) {
                    launcher.translate( 0, dy );
                }

                // Rotate the plunger if the  mouse move left or right
                double dx = event.getDelta().getWidth();
                if( dx != 0 && launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
                    double r = Math.sqrt( plungerNode.getFullBounds().getHeight() * plungerNode.getFullBounds().getHeight()
                                          + plungerNode.getFullBounds().getWidth() * plungerNode.getFullBounds().getWidth() );
                    double dTheta = Math.asin( -dx / r );
                    double theta = Math.min( maxTheta, Math.max( minTheta, launcher.getTheta() + dTheta ) );
                    launcher.setTheta( theta );
                }
            }
        }
    }

}
