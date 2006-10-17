/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.molecularreactions.model.collision.ReactionSpring;
import edu.colorado.phet.molecularreactions.model.collision.ReleasingReactionSpring;

import java.awt.geom.Point2D;

/**
 * ProvisionalBond
 * <p/>
 * A ProvisionalBond is a bond that exists between two SimpleMolecules that are not yet completely bonded, or
 * were bonded and are now separating.
 * <p/>
 * A component of the provisional bond is a spring that works to push the two molecules apart. This is modeled
 * as a CompoundSpring
 * <p/>
 * This is a ModelElement. At each time step, it checks to see if distance between the molecules it is
 * bonding is less than or equal to the maximum bond length. If it is, the bond removes itself from the
 * model.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBond extends SimpleObservable implements ModelElement, PotentialEnergySource {

    SimpleMolecule[] molecules;
    private double maxBondLength;
    private MRModel model;
    private ReactionSpring spring;
    private Point2D pUtilA = new Point2D.Double();
    private Point2D pUtilB = new Point2D.Double();

    /**
     * @param sm1
     * @param sm2
     * @param maxBondLength
     * @param model
     */
    public ProvisionalBond( SimpleMolecule sm1, SimpleMolecule sm2, double maxBondLength, MRModel model ) {
        this.maxBondLength = maxBondLength;
//        this.maxBondLength =  model.getReaction().getCollisionDistance( sm1.getFullMolecule(), sm2.getFullMolecule() );
        this.model = model;
        molecules = new SimpleMolecule[]{sm1, sm2};

        // If the molecules are moving toward each other, create one sort of spring. If
        // they're moving apart, create a releasing spring
        pUtilA.setLocation( sm1.getPosition().getX() + sm1.getVelocity().getX(),
                            sm1.getPosition().getY() + sm1.getVelocity().getY() );
        pUtilB.setLocation( sm2.getPosition().getX() + sm2.getVelocity().getX(),
                            sm2.getPosition().getY() + sm2.getVelocity().getY() );

        // todo:  Currently, I'm only doing this so that we won't make a spring for molecules that are
        // released from a reaction. There should be a better way
//        System.out.println( "ProvisionalBond.ProvisionalBond: bond created" );
        if( sm1.getPosition().distance( sm2.getPosition() )
            > pUtilA.distance( pUtilB ) ) {

//            System.out.println( "ProvisionalBond.ProvisionalBond:          spring created " );

//        if( sm1.getPosition().distance( sm2.getPosition() )
//            < sm1.getPositionPrev().distance( sm2.getPositionPrev() ) ) {
            double pe = model.getReaction().getThresholdEnergy( sm1.getFullMolecule(), sm2.getFullMolecule() );
            spring = new ReactionSpring( pe, this.maxBondLength, this.maxBondLength, new SimpleMolecule[]{sm1, sm2} );
            model.addModelElement( spring );
            CompositeStateMonitor compositeStateMonitor = new CompositeStateMonitor();
            sm1.addListener( compositeStateMonitor );
            sm2.addListener( compositeStateMonitor );
        }
    }

    public ProvisionalBond( SimpleMolecule sm1, SimpleMolecule sm2, double maxBondLength, MRModel model, double pe, boolean isCompressed ) {
        this.maxBondLength = maxBondLength;
        this.model = model;
        molecules = new SimpleMolecule[]{sm1, sm2};
        EnergyProfile energyProfile = model.getReaction().getEnergyProfile();
//        double pe = model.getReaction().getThresholdEnergy( sm1.getFullMolecule(), sm2.getFullMolecule() );
        ReleasingReactionSpring spring = new ReleasingReactionSpring( pe,
                                                                      energyProfile.getThresholdWidth() / 2,
                                                                      energyProfile.getThresholdWidth() / 2,
                                                                      new SimpleMolecule[]{sm1, sm2} );
        model.addModelElement( spring );
        CompositeStateMonitor compositeStateMonitor = new CompositeStateMonitor();
        sm1.addListener( compositeStateMonitor );
        sm2.addListener( compositeStateMonitor );
    }


    /**
     * If the molecules in the bond get too far apart the bond should go away
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        double dist = model.getReaction().getCollisionDistance( molecules[0].getFullMolecule(), molecules[1].getFullMolecule() );
        if( dist > maxBondLength ) {
            model.removeModelElement( this );
            model.removeModelElement( spring );

            // If there is some potential energy left in the spring, adjust the kinetic energies of the
            // molecules
            if( spring != null && spring.getPotentialEnergy() != 0 ) {
                double peRes = spring.getPotentialEnergy();
                double vM0i = getMolecules()[0].getFullMolecule().getVelocity().getMagnitude();
                double vM0f = Math.sqrt( peRes / getMolecules()[0].getFullMass() + vM0i * vM0i );
                getMolecules()[0].getFullMolecule().setVelocity( getMolecules()[0].getFullMolecule().getVelocity().normalize().scale( vM0f ) );

                double vM1i = getMolecules()[1].getFullMolecule().getVelocity().getMagnitude();
                double vM1f = Math.sqrt( peRes / getMolecules()[1].getFullMass() + vM1i * vM1i );
                getMolecules()[1].getFullMolecule().setVelocity( getMolecules()[1].getFullMolecule().getVelocity().normalize().scale( vM1f ) );
            }
        }

        notifyObservers();
    }

    /**
     * Gets the molecules that participate in the bond
     *
     * @return an array with references to the molecules
     */
    public SimpleMolecule[] getMolecules() {
        return molecules;
    }

    /**
     * Gets the maximum length of the bond
     *
     * @return the max length of the bond
     */
    public double getMaxBondLength() {
        return maxBondLength;
    }

    public double getPotentialEnergy() {
        double e = 0;
        if( spring != null ) {
            e = spring.getPotentialEnergy();
        }
        return e;
    }

    public double getPE() {
        return getPotentialEnergy();
    }

    /**
     * Removes a provisional bond from the model when a s
     */
    private class CompositeStateMonitor implements AbstractMolecule.ChangeListener {

        public void compositeStateChanged( AbstractMolecule molecule ) {
            if( molecule.isPartOfComposite() ) {
                molecule.removeListener( this );
                model.removeModelElement( ProvisionalBond.this );
                model.removeModelElement( ProvisionalBond.this.spring );
            }
        }
    }
}
