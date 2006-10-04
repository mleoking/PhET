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
import edu.colorado.phet.molecularreactions.view.AbstractSimpleMoleculeGraphic;

import java.util.Map;
import java.util.HashMap;

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
public class ProvisionalBond extends SimpleObservable implements ModelElement {

    SimpleMolecule[] molecules;
    private double maxBondLength;
    private MRModel model;
    private ReactionSpring spring;

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

        // create the spring
        double pe = model.getReaction().getThresholdEnergy( sm1.getFullMolecule(), sm2.getFullMolecule() );
        spring = new ReactionSpring( pe, this.maxBondLength, this.maxBondLength, new SimpleMolecule[]{sm1, sm2} );
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
