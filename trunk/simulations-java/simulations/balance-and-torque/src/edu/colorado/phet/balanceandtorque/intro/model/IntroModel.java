// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.masses.FireExtinguisher;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.model.masses.SmallTrashCan;

/**
 * Primary model class for the intro tab in the balancing act simulation.
 * This model depicts a plank on a fulcrum with a couple of masses that the
 * user can move around.
 *
 * @author John Blanco
 */
public class IntroModel extends BalanceModel {

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public IntroModel() {
        // Add the initial masses and save their initial positions.
        addMass( new FireExtinguisher( new Point2D.Double( 2.7, 0 ), false ) );
        addMass( new FireExtinguisher( new Point2D.Double( 3.2, 0 ), false ) );
        addMass( new SmallTrashCan( new Point2D.Double( 3.7, 0 ), false ) );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public void reset() {
        super.reset();

        // Move each mass back to its original position.
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            mass.resetPosition();
        }
    }
}