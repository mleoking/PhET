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
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.*;
import java.text.DecimalFormat;

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
    private Stroke lineStroke = new BasicStroke( 3 );
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
    public TotalEnergyLine( Dimension bounds, MRModel model ) {
        this.bounds = bounds;
        this.model = model;
        reaction = model.getReaction();

        // Add listeners to the model that we need to track the selected molecule and
        // any provisional bond that it might be involved in
        model.addSelectedMoleculeTrackerListener( new SelectedMoleculeTracker() );
        model.addListener( new ProvisionalBondMonitor() );

        line = new Line2D.Double();
        lineNode = new PPath( line );
        lineNode.setPaint( linePaint );
//        lineNode.setStroke( lineStroke );
        lineNode.setStrokePaint( linePaint );
        scale = bounds.getHeight() / MRConfig.MAX_REACTION_THRESHOLD;

        addChild( lineNode );

        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
    }

    public void update() {
        if( moleculeBeingTracked != null && nearestToMoleculeBeingTracked != null ) {

//            double ke = moleculeBeingTracked.getFullKineticEnergy()
//                        + nearestToMoleculeBeingTracked.getFullKineticEnergy();
            // Get the kinetic energy of the molecules being watched, relative to their combined CM
            Vector2D vA = new Vector2D.Double( moleculeBeingTracked.getPosition(), compositeBody.getCM()).normalize();
            double sA = moleculeBeingTracked.getVelocity().dot(  vA );
            double keA = 0.5 * sA * sA  * moleculeBeingTracked.getFullMass();
            Vector2D vB = new Vector2D.Double( nearestToMoleculeBeingTracked.getPosition(), compositeBody.getCM()).normalize();
            double sB = moleculeBeingTracked.getVelocity().dot(  vB );
            double keB = 0.5 * sB * sB * nearestToMoleculeBeingTracked.getFullMass();
            double ke = keA + keB;

//            DecimalFormat df = new DecimalFormat( "#.000");
//            System.out.println( "sA = " + df.format( sA ) + "\tsB = " + df.format( sB ) );
//            System.out.println( "keA = " + df.format( keA ) + "\tkeB = " + df.format( keB ) );


            double pe = reaction.getPotentialEnergy( moleculeBeingTracked.getFullMolecule(),
                                                     nearestToMoleculeBeingTracked.getFullMolecule() );
//            double pe = reaction.getPotentialEnergy( moleculeBeingTracked.getParentComposite(),
//                                                     nearestToMoleculeBeingTracked.getParentComposite() );
            double te = ke + pe;

            double pbe = 0;
            if( provisionalBond != null ) {
                te += provisionalBond.getPotentialEnergy();
                pbe = provisionalBond.getPotentialEnergy();
            }
//            DecimalFormat df = new DecimalFormat( "#.000");
//            System.out.println( "te = " + df.format(te) + "\tke = " + df.format( ke ) + "\tpe = " + df.format( pe ) + "\tpbe = " + df.format( pbe) );

            te += model.getPotentialEnergy();

            double y = Math.max( bounds.getHeight() - ( te * scale ), 0 );
            line.setLine( 0, y, bounds.getWidth(), y );
            lineNode.setPathTo( line );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * A listener that catches changes in the selected molecule or the molecule closest to it
     */
    private class SelectedMoleculeTracker implements edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker.Listener {

        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule, SimpleMolecule prevTrackedMolecule ) {
            compositeBody.removeBody( prevTrackedMolecule );
            compositeBody.addBody( newTrackedMolecule );
            moleculeBeingTracked = newTrackedMolecule;
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            compositeBody.removeBody( prevClosestMolecule );
            compositeBody.addBody( newClosestMolecule );
            nearestToMoleculeBeingTracked = newClosestMolecule;
        }
    }


    /**
     * Recognizes when a provisional bond is created between the molecule being tracked and the nearest molecule to
     * it, or that provisional bond is destroyed.
     */
    private class ProvisionalBondMonitor implements PublishingModel.ModelListener {
        public void modelElementAdded( ModelElement element ) {
            if( moleculeBeingTracked != null && element instanceof ProvisionalBond ) {
                ProvisionalBond bond = (ProvisionalBond)element;
                if( ( bond.getMolecules()[0].getFullMolecule() == moleculeBeingTracked.getFullMolecule()
                      && bond.getMolecules()[1].getFullMolecule() == nearestToMoleculeBeingTracked.getFullMolecule() )
                    || ( bond.getMolecules()[1].getFullMolecule() == moleculeBeingTracked.getFullMolecule()
                         && bond.getMolecules()[0].getFullMolecule() == nearestToMoleculeBeingTracked.getFullMolecule() ) ) {
                    provisionalBond = bond;
                }
            }
        }

        public void modelElementRemoved( ModelElement element ) {
            if( moleculeBeingTracked != null && element instanceof ProvisionalBond ) {
                ProvisionalBond bond = (ProvisionalBond)element;
                if( ( bond.getMolecules()[0].getFullMolecule() == moleculeBeingTracked.getFullMolecule()
                      && bond.getMolecules()[1].getFullMolecule() == nearestToMoleculeBeingTracked.getFullMolecule() )
                    || ( bond.getMolecules()[1].getFullMolecule() == moleculeBeingTracked.getFullMolecule()
                         && bond.getMolecules()[0].getFullMolecule() == nearestToMoleculeBeingTracked.getFullMolecule() ) ) {
                    provisionalBond = null;
                }
            }
        }
    }
}
