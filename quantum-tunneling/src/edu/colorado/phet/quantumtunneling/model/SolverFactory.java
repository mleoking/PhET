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


/**
 * SolverFactory
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
    public static AbstractSolver createSolver( TotalEnergy te, AbstractPotentialSpace pe ) {
        AbstractSolver solver = null;
        if ( pe instanceof ConstantPotential ) {
            solver = new ConstantSolver( te, (ConstantPotential) pe );
        }
        else if ( pe instanceof StepPotential ) {
            solver = new StepSolver( te, (StepPotential) pe );
        }
        else if ( pe instanceof BarrierPotential ) {
            solver = new BarrierSolver( te, (BarrierPotential) pe );
        }
        else {
            throw new IllegalArgumentException( "unsuppported potential type: " + pe.getClass().getName() );
        }
        return solver;
    }
}
