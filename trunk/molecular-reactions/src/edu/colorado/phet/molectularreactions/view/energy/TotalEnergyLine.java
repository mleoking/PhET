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

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.AbstractMolecule;
import edu.colorado.phet.molecularreactions.model.CompositeBody;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.ProvisionalBond;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * TotalEnergyLine
 * <p>
 * A line that indicates the total energy in the molecule being tracked, the molecule closest to it, and
 * any provisional bond that may be between them.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TotalEnergyLine extends PNode /*implements SimpleObserver*/ {
    private Line2D line;
    private PPath lineNode;
    private Stroke lineStroke = new BasicStroke( 1 );
    private Paint linePaint = Color.red;
    private Dimension bounds;
    private MRModel model;
    private AbstractMolecule moleculeBeingTracked;
    private AbstractMolecule nearestToMoleculeBeingTracked;
    private ProvisionalBond provisionalBond;
    private Reaction reaction;
    private double scale;
    private CompositeBody compositeBody = new CompositeBody();

    /**
     * @param bounds The bounds within which this line is to be drawn
     * @param model
     */
    public TotalEnergyLine( Dimension bounds, MRModel model, IClock clock ) {
        this.bounds = bounds;
        this.model = model;
        reaction = model.getReaction();

        // Add listeners to the model that we need to track the selected molecule and
        // any provisional bond that it might be involved in
//        model.addSelectedMoleculeTrackerListener( new SelectedMoleculeTracker() );
//        model.addListener( new ProvisionalBondMonitor() );

        line = new Line2D.Double();
        lineNode = new PPath( line );
        lineNode.setPaint( linePaint );
        lineNode.setStroke( lineStroke );
        lineNode.setStrokePaint( linePaint );
        scale = bounds.getHeight() / MRConfig.MAX_REACTION_THRESHOLD;

        addChild( lineNode );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                update();
            }
        });
    }

    public void update() {
        double peak = model.getEnergyProfile().getPeakLevel();
//        System.out.println( "peak = " + peak );
        double ke = model.getTotalKineticEnergy();
        double pe = model.getTotalPotentialEnergy();
//        System.out.println( "pe = " + pe );
        System.out.println( "ke = " + ke );
        double e = model.getTotalKineticEnergy() + model.getTotalPotentialEnergy();
        double y = Math.max( bounds.getHeight() - ( e * scale ), 0 );
        line.setLine( 0, y, bounds.getWidth(), y );
        lineNode.setPathTo( line );
    }
}
