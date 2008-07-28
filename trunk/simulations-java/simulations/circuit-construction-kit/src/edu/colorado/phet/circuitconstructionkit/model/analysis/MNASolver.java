/*  */
package edu.colorado.phet.circuitconstructionkit.model.analysis;

import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.circuitconstructionkit.model.mna.MNACircuit;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 1:32:44 AM
 */

public class MNASolver extends CircuitSolver {
    double dt = 0.01;
    //    private double capFudgeFactor = 4;//todo what's the cause of this fudge factor?
    //    private double capFudgeFactor = 3;//todo what's the cause of this fudge factor?
    //    private double capFudgeFactor = 2;//todo what's the cause of this fudge factor?
    private double capFudgeFactor = 2.5;//todo what's the cause of this fudge factor?
    private KirkhoffSolver.MatrixTable matrixTable;

    public void apply( Circuit circuit ) {
//        if( circuit.numBranches() < 2 ) {
//            //can't clear the circuit because of dynamic components.
////            clearCircuit();
//            return;
//        }
        MNACircuit mnaCircuit = new MNACircuit();
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            int startJunction = circuit.indexOf( branch.getStartJunction() );
            int endJunction = circuit.indexOf( branch.getEndJunction() );
            mnaCircuit.addComponent( toMNAComponent( branch, i, startJunction, endJunction ) );
        }
        MNACircuit.MNASolution solution = mnaCircuit.getCompanionModel( dt ).getSolution();
//        System.out.println( "solution = " + solution );
        if ( !solution.isLegalSolution() ) {
            System.out.println( "solution.isLegalSolution() = " + solution.isLegalSolution() );
            for ( int i = 0; i < circuit.numBranches(); i++ ) {
                Branch branch = circuit.branchAt( i );
                branch.setCurrent( 0.0 );
                if ( !( branch instanceof Battery ) ) {
                    branch.setVoltageDrop( 0.0 );
                }
            }
            return;
        }
//        System.out.println( "solution = " + solution );
//        int freeIndex = solution.getNumVoltages() + getBatteries( circuit ).length;//todo loose coupling, we'd like to get the current for the capacitor directly.

        /*This is meant to compute voltages at each node, so we can avoid a complex graph traversal later*/
//        for( int i = 0; i < circuit.numJunctions(); i++ ) {
//            System.out.println( "solution.getVoltage( i ) = " + solution.getVoltage( i ) );
//            circuit.junctionAt( i ).setVoltage( solution.getVoltage( i ) );
//        }
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            int startJunction = circuit.indexOf( branch.getStartJunction() );
            int endJunction = circuit.indexOf( branch.getEndJunction() );
//            mnaCircuit.addComponent( toMNAComponent( branch, i, startJunction, endJunction ) );
            double startVoltage = solution.getVoltage( startJunction );
            double endVoltage = solution.getVoltage( endJunction );
            if ( branch instanceof Capacitor ) {
                //we'd like to read the current from the companion model.
                Capacitor c = (Capacitor) branch;
                branch.setKirkhoffEnabled( false );
                double origVoltageDrop = branch.getVoltageDrop();
                branch.setVoltageDrop( endVoltage - startVoltage );
                double newVoltageDrop = branch.getVoltageDrop();
                double dv = newVoltageDrop - origVoltageDrop;
                branch.setCurrent( capFudgeFactor * c.getCapacitance() * dv / dt );//todo: linear approx, see if it's good enough
                branch.setKirkhoffEnabled( true );
            }
            else if ( branch instanceof Inductor ) {
                //we'd like to read the current from the companion model.
                Inductor L = (Inductor) branch;
                branch.setKirkhoffEnabled( false );
                double origCurrent = branch.getCurrent();
                double origVoltageDrop = branch.getVoltageDrop();
                branch.setVoltageDrop( endVoltage - startVoltage );
                double deltaCurrent = origVoltageDrop / L.getInductance() * dt;
                double newCurrent = origCurrent + deltaCurrent;
//                System.out.println( "origVoltageDrop = " + origVoltageDrop +", newDV="+ (endVoltage-startVoltage)+", origCurrent="+origCurrent+", newCurrent="+newCurrent);
                branch.setCurrent( newCurrent );//todo: linear approx, see if it's good enough
                branch.setKirkhoffEnabled( true );
            }
            else if ( !( branch instanceof Battery ) ) {
                branch.setKirkhoffEnabled( false );
                branch.setVoltageDrop( endVoltage - startVoltage );
                branch.setCurrent( branch.getVoltageDrop() / branch.getResistance() );
                branch.setKirkhoffEnabled( true );
            }
        }
        for ( int i = 0; i < solution.getNumCurrents() && i < getBatteries( circuit ).length; i++ ) {
            batteryAt( circuit, i ).setKirkhoffEnabled( false );
            batteryAt( circuit, i ).setCurrent( -solution.getCurrent( i ) );//todo there is a minus sign here
            batteryAt( circuit, i ).setKirkhoffEnabled( true );
        }
        dischargeBogusInductors( circuit );
        fireCircuitSolved();
    }

    private void dischargeBogusInductors( Circuit circuit ) {
        matrixTable = new KirkhoffSolver.MatrixTable( circuit );
        //Discharge all inductors not in a loop.
        for ( int i = 0; i < circuit.getInductorCount(); i++ ) {
            Inductor inductor = circuit.getInductor( i );
            if ( !isInLoop( inductor ) ) {
                inductor.discharge();
            }
        }
    }

    private boolean isInLoop( Inductor inductor ) {
        return matrixTable.isLoopElementIncludingSwitches( inductor );
    }

    private Branch batteryAt( Circuit circuit, int i ) {
        return getBatteries( circuit )[i];
    }

    private Battery[] getBatteries( Circuit circuit ) {
        ArrayList all = new ArrayList();
        Branch[] branches = circuit.getBranches();
        for ( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            if ( branch instanceof Battery ) {
                all.add( branch );
            }
        }
        return (Battery[]) all.toArray( new Battery[all.size()] );
    }

    private MNACircuit.MNAComponent toMNAComponent( Branch branch, int index, int start, int end ) {
        if ( branch instanceof Battery ) {
            return new MNACircuit.MNAVoltageSource( "v_" + index, start, end, branch.getVoltageDrop() );
        }
        else if ( branch instanceof Capacitor ) {
            Capacitor c = (Capacitor) branch;
            return new MNACircuit.MNACapacitor( "c_" + index, start, end, c.getCapacitance(), -c.getVoltageDrop(), c.getCurrent() / capFudgeFactor );
        }
        else if ( branch instanceof Resistor ) {
            return new MNACircuit.MNAResistor( "r_" + index, start, end, branch.getResistance() );
        }
        else if ( branch instanceof Inductor ) {
            Inductor L = (Inductor) branch;
            return new MNACircuit.MNAInductor( "L_" + index, start, end, L.getInductance(), L.getVoltageDrop(), L.getCurrent() );
        }
        else if ( branch != null ) {
            return new MNACircuit.MNAResistor( "r_" + index, start, end, branch.getResistance() );
        }
        else {
            throw new RuntimeException( "Component not recognized: " + branch );
        }
    }
}
