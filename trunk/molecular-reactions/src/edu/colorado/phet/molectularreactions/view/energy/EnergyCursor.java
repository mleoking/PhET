/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molectularreactions.view.energy;

import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.Cursor;

/**
 * Cursor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class EnergyCursor extends RegisterablePNode implements SelectedMoleculeTracker.Listener {
    private double width = 10;
    private MouseHandler mouseHandler;
    private SimpleMolecule moleculeBeingTracked;
    private SimpleMolecule closestToTracked;
    private double minX;
    private double maxX;

    EnergyCursor( double height, double minX, double maxX, MRModel model ) {
        this.minX = minX;
        this.maxX = maxX;
        setRegistrationPoint( width / 2, 0 );
        Rectangle2D cursorShape = new Rectangle2D.Double( 0, 0, width, height );
        PPath cursorPPath = new PPath( cursorShape );
        cursorPPath.setStroke( new BasicStroke( 1 ) );
        cursorPPath.setStrokePaint( new Color( 200, 200, 200 ) );
        cursorPPath.setPaint( new Color( 200, 200, 200, 200 ) );
        addChild( cursorPPath );
        mouseHandler = new MouseHandler( this );

        model.addSelectedMoleculeTrackerListener( this );

        setManualControlEnabled( true );

    }

    public void setManualControlEnabled( boolean manualControlEnabled ) {
        if( manualControlEnabled ) {
            // Remove the mouse handler first, to make sure it isn't added more than once.
            removeInputEventListener( mouseHandler );
            addInputEventListener( mouseHandler );
        }
        else {
            removeInputEventListener( mouseHandler );
        }
    }

    /**
     * Handles mousing on the cursor
     */
    private class MouseHandler extends PBasicInputEventHandler {
        EnergyCursor energyCursor;

        public MouseHandler( EnergyCursor energyCursor ) {
            this.energyCursor = energyCursor;
        }

        public void mouseEntered( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( new Cursor( Cursor.W_RESIZE_CURSOR ) );
        }

        public void mouseExited( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor() );
        }

        public void mouseDragged( PInputEvent event ) {
            double dx = event.getDelta().getWidth();
            double x = energyCursor.getOffset().getX() + dx;
            x = Math.max( x, minX );
            x = Math.min( x, maxX );
            energyCursor.setOffset( x, energyCursor.getOffset().getY() );

            // Move the tracked and closestToTracked molecules

            // Get the unit vector between them
            Vector2D dm = new Vector2D.Double( moleculeBeingTracked.getPosition(),
                                               closestToTracked.getPosition() ).normalize();

            // Move them so they are as far from touching as the cursor is from the center of its
            // motion range
            double d = ( minX + maxX ) / 2 - energyCursor.getOffset().getX();
            Point2D midPt = new Point2D.Double( ( moleculeBeingTracked.getPosition().getX() + closestToTracked.getPosition().getX() ) / 2,
                                                ( moleculeBeingTracked.getPosition().getY() + closestToTracked.getPosition().getY() ) / 2 );
            dm.scale( d / 2 );
            if( moleculeBeingTracked.isPartOfComposite() ) {
                moleculeBeingTracked.getParentComposite().translate( (midPt.getX() - dm.getX()) - moleculeBeingTracked.getCM().getX(),
                                                                     (midPt.getY() - dm.getY()) - moleculeBeingTracked.getCM().getY());
            }
            else {
                moleculeBeingTracked.setPosition( midPt.getX() - dm.getX(), midPt.getY() - dm.getY() );
            }
            if( closestToTracked.isPartOfComposite() ) {
                closestToTracked.getParentComposite().translate( (midPt.getX() - dm.getX()) - closestToTracked.getCM().getX(),
                                                                 (midPt.getY() - dm.getY()) - closestToTracked.getCM().getY() );
            }
            else {
                closestToTracked.setPosition( midPt.getX() + dm.getX(), midPt.getY() + dm.getY() );
            }
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SelectedMoleculeTracker.Listener
    //--------------------------------------------------------------------------------------------------

    public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule ) {
        moleculeBeingTracked = newTrackedMolecule;
    }

    public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
        closestToTracked = newClosestMolecule;
    }
}
