/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.analysis;

import Jama.Matrix;
import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.common_cck.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 4:14:19 PM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class ModifiedNodalAnalysis_Orig extends CircuitSolver {
    public static boolean debugging = false;

    public synchronized void apply( final Circuit circuit ) {
        ArrayList strongComponents = getStrongComponents( circuit );
        for( int i = 0; i < strongComponents.size(); i++ ) {
            Circuit subCircuit = createSubCircuit( (Branch[])strongComponents.get( i ) );
            solve( subCircuit );
        }
        fireCircuitSolved();
    }

    private Circuit createSubCircuit( Branch[] branchs ) {
        Circuit subCircuit = new Circuit();
        for( int j = 0; j < branchs.length; j++ ) {
            Branch branch = branchs[j];
            subCircuit.addBranch( branch );
        }
        return subCircuit;
    }

    private ArrayList getStrongComponents( Circuit circuit ) {
        ArrayList strongComponents = new ArrayList();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if( isAssigned( strongComponents, branch ) ) {
                //ignore
            }
            else {
                Branch[] sc = circuit.getConnectedSubgraph( branch.getStartJunction() );
                strongComponents.add( sc );
            }
        }
        if( debugging ) {
            debugStrongComponents( strongComponents );
        }
        return strongComponents;
    }

    private void debugStrongComponents( ArrayList strongComponents ) {
        System.out.println( "strongComponents.size() = " + strongComponents.size() );
        for( int i = 0; i < strongComponents.size(); i++ ) {
            Branch[] branchs = (Branch[])strongComponents.get( i );
            System.out.println( "i=" + i + ", Arrays.asList( branchs ) = " + Arrays.asList( branchs ) );
        }
    }

    private void solve( Circuit circuit ) {
        EquivalentCircuit equivalentCircuit = getEquivalentCircuit( circuit );
        applyMNA( equivalentCircuit.getCircuit() );
        Enumeration keys = equivalentCircuit.branchMap.keys();
        while( keys.hasMoreElements() ) {
            Branch branch = (Branch)keys.nextElement();
            Branch value = (Branch)equivalentCircuit.branchMap.get( branch );
            branch.setKirkhoffEnabled( false );
            branch.setCurrent( value.getCurrent() );
            branch.setVoltageDrop( value.getVoltageDrop() );
            branch.setKirkhoffEnabled( true );
        }
    }

    private boolean isAssigned( ArrayList strongComponents, Branch branch ) {
        for( int i = 0; i < strongComponents.size(); i++ ) {
            Branch[] br = (Branch[])strongComponents.get( i );
            for( int j = 0; j < br.length; j++ ) {
                Branch branch1 = br[j];
                if( branch1 == branch ) {
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

    private EquivalentCircuit getEquivalentCircuit( Circuit circuit ) {
        EquivalentCircuit equivalentCircuit = new EquivalentCircuit();

        Circuit c = new Circuit( new CompositeCircuitChangeListener() );
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                Battery idealBattery = new Battery( batt.getVoltageDrop(), 0 );
                equivalentCircuit.branchMap.put( branch, idealBattery );
                double internalResistance = CCKModule.MIN_RESISTANCE;
                if( batt.isInternalResistanceOn() ) {
                    internalResistance = batt.getInteralResistance();
                }
                InternalResistor fakeResistor = new InternalResistor( internalResistance );
                c.addBranch( fakeResistor );
                c.addBranch( idealBattery );
                equivalentCircuit.internalMap.put( idealBattery, fakeResistor );
            }
            else {
                Resistor fakeResistor = new Resistor( branch.getResistance() );
                equivalentCircuit.branchMap.put( branch, fakeResistor );
                c.addBranch( fakeResistor );
            }
        }
        while( c.numJunctions() > 0 ) {
            c.remove( c.junctionAt( 0 ) );
        }
        for( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            Branch[] neighbors = circuit.getAdjacentBranches( j );
            Junction jBar = new Junction( 0, 0 );
            //            System.out.println( "Adding junction jbar=" + jBar );
            c.addJunction( jBar );
            for( int k = 0; k < neighbors.length; k++ ) {
                Branch neighbor = neighbors[k];
                Branch neighborBar = (Branch)equivalentCircuit.branchMap.get( neighbor );
                if( neighbor.getStartJunction() == j ) {
                    neighborBar.setStartJunction( jBar );
                }
                if( neighbor.getEndJunction() == j ) {
                    neighborBar.setEndJunction( jBar );
                }
            }
        }
        //all wired up, now fix the batteries.
        Enumeration en = equivalentCircuit.internalMap.keys();
        while( en.hasMoreElements() ) {
            Battery idealBatt = (Battery)en.nextElement();
            InternalResistor internalResistor = (InternalResistor)equivalentCircuit.internalMap.get( idealBatt );
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

    public void applyMNA( final Circuit circuit ) {
        clear( circuit );
//        ArrayList bonusElements = new ArrayList();
//        for( int i = 0; i < circuit.numBranches(); i++ ) {
//            Branch branch = circuit.branchAt( i );
//            if( branch instanceof Capacitor ) {
//                HelperBattery parallelShort = new HelperBattery( 0, 0, branch );
//                parallelShort.setStartJunction( branch.getStartJunction() );
//                parallelShort.setEndJunction( branch.getEndJunction() );
//                circuit.addBranch( parallelShort );
//                bonusElements.add( parallelShort );
//            }
//        }

        if( getBatteries( circuit ).length > 0 && circuit.numJunctions() > 2 ) {
            //1. choose a ground.  
            // Vertex 0 shall be the ground.

            //2. Generate A.
            Matrix a = generateA( circuit );
//            System.out.println( "a=" );
//            a.print( 5, 5 );

            //3. Generate z for x=A-1 z .
            Matrix z = generateZ( circuit );
//            System.out.println( "Z=" );
//            z.print( 5, 5 );

            try {
                Matrix x = a.solve( z );
                //                System.out.println( "x = " + x );
                //                x.print( 5, 5 );
                applySolutionToCircuit( circuit, x );
//                while( bonusElements.size() > 0 ) {
//                    circuit.remove( (Branch)bonusElements.get( 0 ) );
//                    bonusElements.remove( 0 );
//                }
            }
            catch( RuntimeException re ) {
                System.out.println( "re = " + re );
            }
        }
    }

    private void applySolutionToCircuit( Circuit circuit, Matrix x ) {
        int n = circuit.numJunctions() - 1;
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            int startIndex = circuit.indexOf( branch.getStartJunction() );
            int endIndex = circuit.indexOf( branch.getEndJunction() );
            double startVolts = 0;
            if( startIndex > 0 ) {
                startVolts = x.get( startIndex - 1, 0 );
            }
            double endVolts = 0;
            if( endIndex > 0 ) {
                endVolts = x.get( endIndex - 1, 0 );
            }
            double dv = endVolts - startVolts;
            if( branch instanceof Battery ) {
            }
//            else if( branch instanceof Capacitor ) {
//                branch.setKirkhoffEnabled( false );
//                branch.setVoltageDrop( dv );
//                branch.setCurrent( 0 );
//                branch.setKirkhoffEnabled( true );
//            }
            else {
                branch.setKirkhoffEnabled( false );
                branch.setVoltageDrop( dv );
                branch.setCurrent( dv / branch.getResistance() );
                branch.setKirkhoffEnabled( true );
            }
        }
        Battery[] batt = getBatteries( circuit );
        for( int i = 0; i < batt.length; i++ ) {
            Battery battery = batt[i];
            int index = i + n;
            double current = x.get( index, 0 );
            battery.setCurrent( -current );
        }
    }

    private void clear( Circuit circuit ) {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            branch.setKirkhoffEnabled( false );
            branch.setCurrent( 0 );
            if( !( branch instanceof Battery ) ) {
                branch.setVoltageDrop( 0.0 );
            }
            branch.setKirkhoffEnabled( true );
        }
    }

    private Matrix generateZ( Circuit circuit ) {
        Battery[] batt = getBatteries( circuit );
        int n = circuit.numJunctions() - 1;
        int m = getBatteries( circuit ).length;
        Matrix z = new Matrix( m + n, 1 );
        for( int i = 0; i < batt.length; i++ ) {
            z.set( i + n, 0, batt[i].getVoltageDrop() );
        }
        return z;
    }

    /*
    The A matrix is (m+n)x(m+n) (n is the number of nodes, and m is the number of independent voltage sources) and:

the G matrix is nxn and is determined by the interconnections between the passive circuit elements (resistors)
the B matrix is mxn and is determined by the connection of the voltage sources.
the C matrix is nxm and is determined by the connection of the voltage sources.  (B and C are closely related, particularly when only independent sources are considered).
the D matrix is mxm and is zero if only independent sources are considered.
*/
    private Matrix generateA( Circuit circuit ) {
        Battery[] batt = getBatteries( circuit );
        int m = batt.length;
        int n = circuit.numJunctions() - 1;
        Matrix g = generateG( circuit );
        //        System.out.print( "g=" );
        //        g.print( 5, 5 );
        Matrix b = generateB( circuit );
        //        System.out.println( "b=" );
        //        b.print( 5, 5 );
        Matrix a = new Matrix( m + n, m + n );

        for( int row = 0; row < b.getRowDimension(); row++ ) {
            for( int col = n; col < a.getColumnDimension(); col++ ) {
                //                System.out.println( "row = " + row + ", col=" + col );
                double value = b.get( row, col - n );
                a.set( row, col, value );
            }
        }
        a = a.plus( a.transpose() );//b + c

        //now add g
        for( int row = 0; row < g.getRowDimension(); row++ ) {
            for( int col = 0; col < g.getColumnDimension(); col++ ) {
                a.set( row, col, g.get( row, col ) );
            }
        }
//        System.out.println( "a = " + a );
//        a.print( 3,3);
        return a;
    }

    /*
    The B matrix is an mxn matrix with only 0, 1 and -1 elements.
    Each location in the matrix corresponds to a particular voltage source
    (first dimension) or a node (second dimension).
    If the positive terminal of the ith voltage source is connected to node k,
    then the element (i,k) in the B matrix is a 1.
    If the negative terminal of the ith voltage source is connected to node k,
    then the element (i,k) in the B matrix is a -1.
    Otherwise, elements of the B matrix are zero.
    */
    private Matrix generateB( Circuit circuit ) {
        Battery[] batteries = getBatteries( circuit );
        int m = batteries.length;
        int n = circuit.numJunctions() - 1;
        Matrix b = new Matrix( n, m );
        for( int i = 0; i < m; i++ ) {
            Battery battery = batteries[i];
            int startIndex = circuit.indexOf( battery.getStartJunction() );
            int endIndex = circuit.indexOf( battery.getEndJunction() );
            if( startIndex != 0 ) {
                b.set( startIndex - 1, i, 1 );
            }
            if( endIndex != 0 ) {
                b.set( endIndex - 1, i, -1 );//may have the sign wrong here
            }
        }
        return b;
    }

    /*
    Each element in the diagonal matrix is equal to the sum of the conductance
    (one over the resistance) of each element connected to the corresponding
    node.  So the first diagonal element is the sum of conductances
    connected to node 1, the second diagonal element is the sum of
    conductances connected to node 2, and so on.
    */
    private Matrix generateG( Circuit circuit ) {
        int n = circuit.numJunctions() - 1;//0 is the reference voltage.
        Matrix g = new Matrix( n, n );
        for( int i = 1; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            Branch[] b = circuit.getAdjacentBranches( j );
            double value = 0;
            for( int k = 0; k < b.length; k++ ) {
                if( !( b[k] instanceof Battery ) ) {
                    double term = 1.0 / b[k].getResistance();
//                    if( i == 1 ) {
//                        System.out.println( "term = " + term );
//                    }
                    value += term;
                }
            }
            g.set( i - 1, i - 1, value );
        }
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch b = circuit.branchAt( i );
            if( !( b instanceof Battery ) ) {
                Junction start = b.getStartJunction();
                Junction end = b.getEndJunction();
                int startIndex = circuit.indexOf( start );
                int endIndex = circuit.indexOf( end );
                if( startIndex == -1 ) {
                    throw new RuntimeException( "No such junction: " + start );
                }
                if( endIndex == -1 ) {
                    throw new RuntimeException( "No such junction: " + end );
                }
                if( startIndex != 0 && endIndex != 0 ) {
                    g.set( startIndex - 1, endIndex - 1, -1.0 / b.getResistance() );
                    g.set( endIndex - 1, startIndex - 1, -1.0 / b.getResistance() );
                }
            }
        }
//        System.out.println( "g = " + g );
//        g.print( 3, 3 );
        return g;
    }

    private Battery[] getBatteries( Circuit circuit ) {
        ArrayList all = new ArrayList();
        Branch[] branches = circuit.getBranches();
        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            if( branch instanceof Battery ) {
                all.add( branch );
            }
        }
        return (Battery[])all.toArray( new Battery[0] );
    }
}
