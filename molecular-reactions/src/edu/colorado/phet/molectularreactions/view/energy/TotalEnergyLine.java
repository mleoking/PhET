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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.*;

/**
 * TotalEnergyLine
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TotalEnergyLine extends PNode implements SimpleObserver {
    private Line2D line;
    private PPath lineNode;
    private Stroke lineStroke = new BasicStroke( 3 );
    private Paint linePaint = Color.red;
    private Dimension bounds;
    private Molecule moleculeBeingTracked;
    private Molecule nearestToMoleculeBeingTracked;
    private Reaction reaction;
    private double scale;

    /**
     * @param bounds The bounds within which this line is to be drawn
     * @param model
     */
    public TotalEnergyLine( Dimension bounds, MRModel model ) {
        this.bounds = bounds;
        reaction = model.getReaction();
        model.addSelectedMoleculeTrackerListener( new SelectedMoleculeTracker() );
        line = new Line2D.Double();
        lineNode = new PPath( line );
        lineNode.setPaint( linePaint );
//        lineNode.setStroke( lineStroke );
        lineNode.setStrokePaint( linePaint );
        scale = bounds.getHeight() / MRConfig.MAX_REACTION_THRESHOLD;

        addChild( lineNode );
    }

    public void update() {
        if( moleculeBeingTracked != null && nearestToMoleculeBeingTracked != null ) {
            double ke = moleculeBeingTracked.getFullKineticEnergy()
                        + nearestToMoleculeBeingTracked.getFullKineticEnergy();
            double pe = reaction.getPotentialEnergy( moleculeBeingTracked.getParentComposite(),
                                                     nearestToMoleculeBeingTracked.getParentComposite() );
            double te = ke + pe;
            double y = Math.max( bounds.getHeight() - (te * scale), 0 );
            line.setLine( 0, y, bounds.getWidth(), y );
            lineNode.setPathTo( line );
        }
    }

    private class SelectedMoleculeTracker implements edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule ) {
            if( moleculeBeingTracked != null ) {
                moleculeBeingTracked.removeObserver( TotalEnergyLine.this );
            }
            moleculeBeingTracked = newTrackedMolecule;
            moleculeBeingTracked.addObserver( TotalEnergyLine.this );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            if( nearestToMoleculeBeingTracked != null ) {
                nearestToMoleculeBeingTracked.removeObserver( TotalEnergyLine.this );
            }
            nearestToMoleculeBeingTracked = newClosestMolecule;
            nearestToMoleculeBeingTracked.addObserver( TotalEnergyLine.this );
        }
    }
}
