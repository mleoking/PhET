/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.kirkhoff;

import Jama.Matrix;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 4:14:19 PM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class NodeAnalysis extends KirkhoffSolver {
    public static boolean debugging = false;

    /*
The x matrix:
is an (n+m)x1 vector that holds the unknown quantities
(node voltages and the currents through the independent voltage sources).
the top n elements are the n node voltages.
the bottom m elements represent the currents
through the m independent voltage sources in the circuit.
*/
    public void apply( final Circuit circuit ) {
        clear( circuit );
        if( getBatteries( circuit ).length > 0 ) {

//1. choose a ground.  Vertex 0 shall be the ground.

//2. Generate A.
            Matrix a = generateA( circuit );
            System.out.println( "a=" );
            a.print( 5, 5 );

//3. Generate z for x=A-1 z .
            Matrix z = generateZ( circuit );
            System.out.println( "Z=" );
            z.print( 5, 5 );
            try {

                Matrix x = a.solve( z );
                System.out.println( "x = " + x );
                x.print( 5, 5 );

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
                    else {
                        branch.setKirkhoffEnabled( false );
                        branch.setVoltageDrop( dv );
                        branch.setCurrent( dv / branch.getResistance() );
                        branch.setKirkhoffEnabled( true );
//                    if (branch instanceof Bulb){
//                        Bulb bulb=(Bulb)branch;
//                        bulb.notifyObservers();
//                    }
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
            catch( RuntimeException re ) {
                System.out.println( "re = " + re );
            }
        }
        fireKirkhoffSolved();
    }

    private void clear( Circuit circuit ) {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            branch.setKirkhoffEnabled( false );
            branch.setCurrent( 0 );
            if( ( branch instanceof Battery ) ) {
                Battery batt = (Battery)branch;
            }
            else {
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
        System.out.print( "g=" );
        g.print( 5, 5 );
        Matrix b = generateB( circuit );
        System.out.println( "b=" );
        b.print( 5, 5 );
        Matrix a = new Matrix( m + n, m + n );

        for( int row = 0; row < b.getRowDimension(); row++ ) {
            for( int col = n; col < a.getColumnDimension(); col++ ) {
                System.out.println( "row = " + row + ", col=" + col );
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
                    double term = 1.0 / b[k].getResistance();//should ignore batteries,non?  We'll have to think of internal resistance.
                    value += term;
//                    if( b[k].getStartJunction() == j ) {
//                        value += term;
//                    }
//                    else {
//                        value -= term;
//                    }
                }
            }
            int diag = i - 1;
            System.out.println( "diag = " + diag + ", value=" + value );
            g.set( i - 1, i - 1, value );
        }
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch b = circuit.branchAt( i );
            if( !( b instanceof Battery ) ) {
                Junction start = b.getStartJunction();
                Junction end = b.getEndJunction();
                int startIndex = circuit.indexOf( start );
                int endIndex = circuit.indexOf( end );
                if( startIndex != 0 && endIndex != 0 ) {
                    g.set( startIndex - 1, endIndex - 1, -1.0 / b.getResistance() );
                    g.set( endIndex - 1, startIndex - 1, -1.0 / b.getResistance() );
                }
            }
        }
        return g;
    }

    private Battery[] getBatteries( Circuit circuit ) {
        ArrayList all = new ArrayList();
        Branch[] branches = circuit.getBranches();
        for( int i = 0; i < branches.length; i++ ) {
            Branch branche = branches[i];
            if( branche instanceof Battery ) {
                all.add( branche );
            }
        }
        return (Battery[])all.toArray( new Battery[0] );
    }
}
