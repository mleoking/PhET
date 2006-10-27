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
import edu.colorado.phet.molecularreactions.model.Launcher;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

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
    private PNode plungerNode;
    private PNode plungerFrameNode;
    private PNode plunger2DFrameNode;
    private PNode plunger2DFrameStrutsNode;

    private String baseImagePath = "images/";
    private String plungerImageFile = baseImagePath + "plunger.png";
    private String frameImageFile = baseImagePath + "frame.png";
    private String frame2DImageFile = baseImagePath + "2D-frame.png";
    private String strutsImageFile = baseImagePath + "struts.png";
    private Point2D pivotPt;
    private double scale;


    public LauncherGraphic( Launcher launcher ) {
        this.launcher = launcher;

        plungerNode = PImageFactory.create( plungerImageFile );
        scale = 100.0 / plungerNode.getFullBounds().getHeight();
        plungerNode.scale( scale );

        plungerFrameNode = PImageFactory.create( frameImageFile );
//        plungerFrameNode = new PPath( new Rectangle2D.Double( 0,0,50,50));
        plungerFrameNode.scale( scale );
        plungerFrameNode.setPickable( false );
        plungerFrameNode.setChildrenPickable( false );

        // Objects for the 2D plunger
        plunger2DFrameStrutsNode = PImageFactory.create( strutsImageFile );
        plunger2DFrameStrutsNode.scale( scale );

        plunger2DFrameNode = PImageFactory.create( frame2DImageFile );
        plunger2DFrameNode.scale( scale );

        addChild( plunger2DFrameStrutsNode );
        addChild( plungerNode );
        addChild( plunger2DFrameNode );
        addChild( plungerFrameNode );
        plunger2DFrameNode.setVisible( false );
        plunger2DFrameStrutsNode.setVisible( false );

        // The pivot point
        double centerX = getFullBounds().getWidth() / 2;
        plungerNode.setOffset( centerX - plungerNode.getFullBounds().getWidth() / 2, 0 );
        plunger2DFrameStrutsNode.setOffset( centerX - plunger2DFrameStrutsNode.getFullBounds().getWidth() / 2, 0 );
        plungerFrameNode.setOffset( centerX - plungerFrameNode.getFullBounds().getWidth() / 2, 0 );

        plunger2DFrameNode.setOffset( centerX - plunger2DFrameNode.getFullBounds().getWidth() / 2, 0 );
        plunger2DFrameStrutsNode.setOffset( centerX - plunger2DFrameStrutsNode.getFullBounds().getWidth() / 2, 0 );

        // Determine the pivot point of the launcher
        pivotPt = new Point2D.Double( centerX, 0 );

        setOffset( launcher.getRestingTipLocation().getX() - getFullBounds().getWidth() / 2,
                   launcher.getRestingTipLocation().getY() );

        // Add mouse handler
        plungerNode.addInputEventListener( new PlungerMouseHandler() );

        launcher.addObserver( this );
        update();
    }

    public void update() {

        double d = launcher.getExtension()/ scale;
        updateTransform( plungerNode, d, launcher.getTheta() );
        updateTransform( plungerFrameNode, 0, launcher.getTheta() );

        boolean twoD = launcher.getMovementType() == Launcher.TWO_DIMENSIONAL;
        plunger2DFrameStrutsNode.setVisible( twoD );
        plunger2DFrameNode.setVisible( twoD );
    }

    private void updateTransform( PNode node, double d, double theta ) {
        node.setTransform( new AffineTransform() );
        node.translate( pivotPt.getX() - ( node.getFullBounds().getWidth() / 2 * scale ), pivotPt.getY() );
        node.rotateAboutPoint( theta, node.getFullBounds().getWidth() / 2 * scale, 0 );
        node.setScale( scale );
        node.translate( 0, d );
//        plungerNode.setTransform( new AffineTransform( ) );
//        plungerNode.translate( pivotPt.getX() - (plungerNode.getFullBounds().getWidth()/2 * scale), pivotPt.getY() );
//        plungerNode.rotateAboutPoint( launcher.getTheta(), plungerNode.getFullBounds().getWidth()/2 * scale, 0 );
//        plungerNode.setScale( scale );
//        double d = launcher.getRestingTipLocation().distance( launcher.getTipLocation() ) / scale;
//        plungerNode.translate( 0, d );
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

        double originalAngle;
        double originalR;
        Point2D startPoint;

        public void mousePressed( PInputEvent event ) {
            originalAngle = launcher.getTheta();
            originalR = launcher.getExtension();
            this.startPoint = event.getPositionRelativeTo( LauncherGraphic.this.getParent() );
        }

        public void mouseDragged( PInputEvent event ) {
            Point2D end = event.getPositionRelativeTo( LauncherGraphic.this.getParent() );
            Vector2D.Double v1 = new Vector2D.Double( launcher.getRestingTipLocation(), startPoint );
            Vector2D.Double v2 = new Vector2D.Double( launcher.getRestingTipLocation(), end );

            double dTheta = v2.getAngle() - v1.getAngle();
            launcher.setTheta( originalAngle + dTheta );

            double dr = v2.getMagnitude() - v1.getMagnitude();
            launcher.setExtension( originalR + dr );
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
//        public void mouseDragged( PInputEvent event ) {
//            if( launcher.isEnabled() ) {
//                double dy = event.getDelta().getHeight();
//                double yLoc = plungerNode.getOffset().getY() + dy;
//
//                // Constrain the motion of the handle to be within the bounds of the PNode containing
//                // the PumpGraphic, and the initial location of the handle.
//                if( yLoc >= 1
//                    && yLoc <= launcher.getPivotPoint().getY() + plungerFrameNode.getHeight() - 20 ) {
//                    launcher.translate( 0, dy );
//                }
//
//                // Rotate the plunger if the  mouse move left or right
//                double dx = event.getDelta().getWidth();
//                if( dx != 0 && launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
//                    Point2D eventLoc = event.getPositionRelativeTo( LauncherGraphic.this );
//                    double r = eventLoc.distance( pivotPt );
////                    double r = Math.sqrt( plungerNode.getFullBounds().getHeight() * plungerNode.getFullBounds().getHeight()
////                                          + plungerNode.getFullBounds().getWidth() * plungerNode.getFullBounds().getWidth() );
////                    double dTheta = Math.asin( -dx / r );
////                    double theta = Math.min( maxTheta, Math.max( minTheta, launcher.getTheta() + dTheta ) );
//                    double theta = Math.atan2( eventLoc.getY() - pivotPt.getY(), eventLoc.getX() - pivotPt.getX());
//                    launcher.setTheta( theta - Math.PI /2);
//                }
//            }
//        }

    }

}
