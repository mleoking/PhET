// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import java.awt.Cursor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.Launcher;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * LauncherGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LauncherGraphic extends PNode implements SimpleObserver {
    private double minTheta = -Math.PI / 3;
    private double maxTheta = Math.PI / 3;
    private Launcher launcher;
    private PNode plungerNode;
    private PNode plungerFrameNode;
    private PNode plunger2DFrameNode;
    private PNode plunger2DFrameStrutsNode;

    private String baseImagePath = "reactions-and-rates/images/";
    private String plungerImageFile = baseImagePath + "plunger.png";
    private String frameImageFile = baseImagePath + "frame.png";
    private String frame2DImageFile = baseImagePath + "2D-frame.png";
    private String strutsImageFile = baseImagePath + "struts.png";
    private Point2D pivotPt;
    private double scale;
    private volatile boolean temperatureBeingAdjusted = false;


    public LauncherGraphic( Launcher launcher ) {
        this.launcher = launcher;

        plungerNode = PImageFactory.create( plungerImageFile );
        scale = 100.0 / plungerNode.getFullBounds().getHeight();
        plungerNode.scale( scale );

        plungerFrameNode = PImageFactory.create( frameImageFile );
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
        double d = launcher.getExtension() / scale;
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
    }

    public boolean isTemperatureBeingAdjusted() {
        return temperatureBeingAdjusted;
    }

    /**
     * Mouse handler
     */
    private class PlungerMouseHandler extends PBasicInputEventHandler {
        boolean mousePressed, mouseInside;
        double dySinceLastMolecule;
        double originalAngle;
        double originalR;
        Point2D startPoint;

        public void mouseEntered( PInputEvent event ) {
            mouseInside = true;
            updateCursor();
        }

        public void mouseExited( PInputEvent event ) {
            mouseInside = false;
            updateCursor();
        }

        public void mousePressed( PInputEvent event ) {
            mousePressed = true;
            if ( launcher.isEnabled() ) {
                temperatureBeingAdjusted = true;
                originalAngle = launcher.getTheta();
                originalR = launcher.getExtension();
                this.startPoint = event.getPositionRelativeTo( LauncherGraphic.this.getParent() );
            }
        }

        public void mouseDragged( PInputEvent event ) {
            if ( launcher.isEnabled() ) {
                double extension = 0;
                Point2D end = event.getPositionRelativeTo( LauncherGraphic.this.getParent() );
                // if we're dragging below the tip...
                if ( end.getY() > launcher.getRestingTipLocation().getY() ) {
                    MutableVector2D v1 = new MutableVector2D( launcher.getRestingTipLocation(), startPoint );
                    MutableVector2D v2 = new MutableVector2D( launcher.getRestingTipLocation(), end );

                    // If the launcher supports 2D motion, compute its angle
                    if ( launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
                        double dTheta = v2.getAngle() - v1.getAngle();
                        launcher.setTheta( originalAngle + dTheta );
                    }

                    double dr = v2.magnitude() - v1.magnitude();
                    extension = Math.min( MRConfig.LAUNCHER_MAX_EXTENSION, originalR + dr );

                }
                launcher.setExtension( extension );
            }

        }

        public void mouseReleased( PInputEvent event ) {
            if ( launcher.isEnabled() ) {
                temperatureBeingAdjusted = false;
                launcher.release();
            }
            mousePressed = false;
            updateCursor();
        }

        private void updateCursor() {
            if ( mouseInside && launcher.isEnabled() ) {
                if ( launcher.getMovementType() == Launcher.TWO_DIMENSIONAL ) {
                    setCursor( Cursor.HAND_CURSOR );
                }
                else if ( launcher.getMovementType() == Launcher.ONE_DIMENSIONAL ) {
                    setCursor( Cursor.N_RESIZE_CURSOR );
                }
            }
            else if ( !mouseInside && !mousePressed ) {
                setCursor( Cursor.DEFAULT_CURSOR );
            }
        }

        private void setCursor( int cursorType ) {
            PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( cursorType ) );
        }
    }
}
