/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Cursor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyCursor extends RegisterablePNode implements SelectedMoleculeTracker.Listener {
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
        mouseHandler = new MouseHandler( this, model.getReaction() );

        // Track the selected molecule
        model.addSelectedMoleculeTrackerListener( this );

        setManualControlEnabled( false );
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
        private Reaction reaction;

        public MouseHandler( EnergyCursor energyCursor, Reaction reaction ) {
            this.energyCursor = energyCursor;
            this.reaction = reaction;
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
            setMoleculePositions();
        }

        private void setMoleculePositions() {

            // One of the molecules must be a composite, and the other a simple one. Get references to them, and
            // get a reference to the B molecule in the composite
            CompositeMolecule cm = moleculeBeingTracked.isPartOfComposite()
                                   ? moleculeBeingTracked.getParentComposite()
                                   : closestToTracked.getParentComposite();
            SimpleMolecule sm = !moleculeBeingTracked.isPartOfComposite()
                                ? moleculeBeingTracked
                                : closestToTracked;
            Vector2D v = reaction.getCollisionVector( cm, sm );
            double d = Math.abs( ( minX + maxX ) / 2 - energyCursor.getOffset().getX() );
            double dx = ( d - v.getMagnitude() ) / 2;
            cm.translate( -dx, 0 );
            sm.translate( dx, 0 );
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
