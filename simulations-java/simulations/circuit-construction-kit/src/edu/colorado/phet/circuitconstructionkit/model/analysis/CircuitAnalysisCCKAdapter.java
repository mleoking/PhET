/*  */
package edu.colorado.phet.circuitconstructionkit.model.analysis;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.colorado.phet.circuitconstructionkit.model.*;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class is responsible for separating the circuit into strong components
 * and ensuring any constraints on the circuit before delegating the actual solution.
 */
public class CircuitAnalysisCCKAdapter extends CircuitSolver {
    public static boolean debugging = false;
    private CircuitSolver circuitSolver;

    public CircuitAnalysisCCKAdapter( CircuitSolver circuitSolver ) {
        this.circuitSolver = circuitSolver;
    }

    public synchronized void apply( final Circuit circuit ) {
        ArrayList strongComponents = getStrongComponents( circuit );
        for ( int i = 0; i < strongComponents.size(); i++ ) {
            Circuit subCircuit = createSubCircuit( (Branch[]) strongComponents.get( i ) );
            solve( subCircuit );
        }
        fireCircuitSolved();
    }

    private Circuit createSubCircuit( Branch[] branchs ) {
        Circuit subCircuit = new Circuit();
        subCircuit.setAllowUserEdits( false );
        for ( int j = 0; j < branchs.length; j++ ) {
            Branch branch = branchs[j];
            subCircuit.addBranch( branch );
        }
        return subCircuit;
    }

    private ArrayList getStrongComponents( Circuit circuit ) {
        ArrayList strongComponents = new ArrayList();
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if ( isAssigned( strongComponents, branch ) ) {
                //ignore
            }
            else {
                Branch[] sc = circuit.getConnectedSubgraph( branch.getStartJunction() );
                strongComponents.add( sc );
            }
        }
        if ( CircuitAnalysisCCKAdapter.debugging ) {
            debugStrongComponents( strongComponents );
        }
        return strongComponents;
    }

    private void debugStrongComponents( ArrayList strongComponents ) {
        System.out.println( "strongComponents.size() = " + strongComponents.size() );
        for ( int i = 0; i < strongComponents.size(); i++ ) {
            Branch[] branchs = (Branch[]) strongComponents.get( i );
            System.out.println( "i=" + i + ", Arrays.asList( branchs ) = " + Arrays.asList( branchs ) );
        }
    }

    private void solve( Circuit circuit ) {
        CircuitAnalysisCCKAdapter.EquivalentCircuit equivalentCircuit = getEquivalentCircuit( circuit );
        applyRootSolver( equivalentCircuit.getCircuit() );
        Enumeration keys = equivalentCircuit.branchMap.keys();
        while ( keys.hasMoreElements() ) {
            Branch branch = (Branch) keys.nextElement();
            Branch value = (Branch) equivalentCircuit.branchMap.get( branch );
            branch.setKirkhoffEnabled( false );
            branch.setCurrent( value.getCurrent() );
            branch.setVoltageDrop( value.getVoltageDrop() );
            branch.setKirkhoffEnabled( true );
        }
    }

    private boolean isAssigned( ArrayList strongComponents, Branch branch ) {
        for ( int i = 0; i < strongComponents.size(); i++ ) {
            Branch[] br = (Branch[]) strongComponents.get( i );
            for ( int j = 0; j < br.length; j++ ) {
                Branch branch1 = br[j];
                if ( branch1 == branch ) {
                    return true;
                }
            }
        }
        return false;
    }

    static class InternalResistor extends Resistor {
        public InternalResistor( double resistance ) {
            super( new Point2D.Double(), new Vector2D.Double(), 1, 1, new CircuitChangeListener() {
                public void circuitChanged() {
                }
            } );
            setResistance( resistance );
        }
    }

    class EquivalentCircuit {
        private Hashtable branchMap;
        private Hashtable internalMap;
        private Circuit circuit;

        public EquivalentCircuit() {
            this.branchMap = new Hashtable();
            this.internalMap = new Hashtable();
        }

        public void setCircuit( Circuit circuit ) {
            this.circuit = circuit;
        }

        public Circuit getCircuit() {
            return circuit;
        }

        public Hashtable getBranchMap() {
            return branchMap;
        }

        public Hashtable getInternalMap() {
            return internalMap;
        }
    }

    private CircuitAnalysisCCKAdapter.EquivalentCircuit getEquivalentCircuit( Circuit circuit ) {
        CircuitAnalysisCCKAdapter.EquivalentCircuit equivalentCircuit = new CircuitAnalysisCCKAdapter.EquivalentCircuit();

        Circuit c = new Circuit( new CompositeCircuitChangeListener() );
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if ( branch instanceof Battery ) {
                Battery batt = (Battery) branch;
                Battery idealBattery = new Battery( batt.getVoltageDrop(), 0 );
                equivalentCircuit.branchMap.put( branch, idealBattery );
                double internalResistance = CCKModel.MIN_RESISTANCE;
                if ( batt.isInternalResistanceOn() ) {
                    internalResistance = batt.getInteralResistance();
                }
                CircuitAnalysisCCKAdapter.InternalResistor fakeResistor = new CircuitAnalysisCCKAdapter.InternalResistor( internalResistance );
                c.addBranch( fakeResistor );
                c.addBranch( idealBattery );
                equivalentCircuit.internalMap.put( idealBattery, fakeResistor );
            }
            else if ( branch instanceof Capacitor ) {
                Capacitor fakeCapacitor = new Capacitor( branch.getResistance() );
                fakeCapacitor.setCapacitance( ( (Capacitor) branch ).getCapacitance() );
                fakeCapacitor.setCurrent( branch.getCurrent() );
                fakeCapacitor.setVoltageDrop( branch.getVoltageDrop() );
                equivalentCircuit.branchMap.put( branch, fakeCapacitor );
                c.addBranch( fakeCapacitor );
            }
            else if ( branch instanceof Inductor ) {
                Inductor fakeInductor = new Inductor( branch.getResistance() );
                fakeInductor.setInductance( ( (Inductor) branch ).getInductance() );
                fakeInductor.setCurrent( branch.getCurrent() );
                fakeInductor.setVoltageDrop( branch.getVoltageDrop() );
                equivalentCircuit.branchMap.put( branch, fakeInductor );
                c.addBranch( fakeInductor );
            }
            else {
                Resistor fakeResistor = new Resistor( branch.getResistance() );
                equivalentCircuit.branchMap.put( branch, fakeResistor );
                c.addBranch( fakeResistor );
            }
        }
        while ( c.numJunctions() > 0 ) {
            c.removeJunction( c.junctionAt( 0 ) );
        }
        for ( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            Branch[] neighbors = circuit.getAdjacentBranches( j );
            Junction jBar = new Junction( 0, 0 );
            //            System.out.println( "Adding junction jbar=" + jBar );
            c.addJunction( jBar );
            for ( int k = 0; k < neighbors.length; k++ ) {
                Branch neighbor = neighbors[k];
                Branch neighborBar = (Branch) equivalentCircuit.branchMap.get( neighbor );
                if ( neighbor.getStartJunction() == j ) {
                    neighborBar.setStartJunction( jBar );
                }
                if ( neighbor.getEndJunction() == j ) {
                    neighborBar.setEndJunction( jBar );
                }
            }
        }
        //all wired up, now fix the batteries.
        Enumeration en = equivalentCircuit.internalMap.keys();
        while ( en.hasMoreElements() ) {
            Battery idealBatt = (Battery) en.nextElement();
            CircuitAnalysisCCKAdapter.InternalResistor internalResistor = (CircuitAnalysisCCKAdapter.InternalResistor) equivalentCircuit.internalMap.get( idealBatt );
            Junction internal = new Junction( 0, 0 );
            Junction start = idealBatt.getStartJunction();
            internalResistor.setStartJunction( start );
            internalResistor.setEndJunction( internal );
            idealBatt.setStartJunction( internal );
            c.addJunction( internal );
        }
        equivalentCircuit.setCircuit( c );
        return equivalentCircuit;
    }

    public void applyRootSolver( final Circuit circuit ) {
        //when requiring numJunctions>2, a lone capacitor gets cleared.
//        if( ( getCapacitorCount( circuit ) > 0 || getBatteries( circuit ).length > 0 ) && circuit.numJunctions() > 2 ) {
        if ( ( circuit.getCapacitorCount() > 0 || getBatteries( circuit ).length > 0 ) || circuit.getInductorCount() > 0 ) {
//            System.out.println( "Applying root solver" );
            circuitSolver.apply( circuit );
        }
        else {
//            System.out.println( "Clearing circuit" );
            clear( circuit );
        }
    }


    private void clear( Circuit circuit ) {
        for ( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            branch.setKirkhoffEnabled( false );
            branch.setCurrent( 0 );
            if ( !( branch instanceof Battery ) ) {
                branch.setVoltageDrop( 0.0 );
            }
            branch.setKirkhoffEnabled( true );
        }
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
        return (Battery[]) all.toArray( new Battery[0] );
    }
}
