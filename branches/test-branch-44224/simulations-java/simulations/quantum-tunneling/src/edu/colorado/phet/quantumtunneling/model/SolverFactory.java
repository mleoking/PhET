/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.enums.Direction;


/**
 * SolverFactory is a factory for creating plane wave function solvers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolverFactory {

    /* Not intended for instantiation */
    private SolverFactory() {}
    
    /**
     * Creates a solver that matches the type of the potential energy.
     * 
     * @param te
     * @param pe
     * @return
     */
    public static AbstractPlaneSolver createSolver( TotalEnergy te, AbstractPotential pe, Direction direction ) {
        AbstractPlaneSolver solver = null;
        if ( pe instanceof ConstantPotential ) {
            solver = new ConstantSolver( te, (ConstantPotential) pe, direction );
        }
        else if ( pe instanceof StepPotential ) {
            solver = new StepSolver( te, (StepPotential) pe, direction );
        }
        else if ( pe instanceof SingleBarrierPotential ) {
            solver = new SingleBarrierSolver( te, (SingleBarrierPotential) pe, direction );
        }
        else if ( pe instanceof DoubleBarrierPotential ) {
            solver = new DoubleBarrierSolver( te, (DoubleBarrierPotential) pe, direction );
        }
        else {
            throw new IllegalArgumentException( "unsupported potential type: " + pe.getClass().getName() );
        }
        return solver;
    }
}
