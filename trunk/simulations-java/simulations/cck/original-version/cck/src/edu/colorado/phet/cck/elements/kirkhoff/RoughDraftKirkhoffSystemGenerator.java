/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.cck.elements.kirkhoff.equations.Equation;
import edu.colorado.phet.cck.graphtheory.DirectedPathElement;
import edu.colorado.phet.cck.graphtheory.Loop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 12:57:23 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class RoughDraftKirkhoffSystemGenerator {

    private double minResistance = 0;//TODO make sure zero is okay, used to be WireKitApp.MIN...
    private Logger logger;

    public RoughDraftKirkhoffSystemGenerator( Logger kirkhoffLogger ) {
        this.logger = kirkhoffLogger;
    }

    private Equation getVoltageEquation( Loop loop, CircuitGraph c, MatrixTable mt ) {
        Equation r = new Equation( mt.getFreeParameterCount() );
        for( int i = 0; i < loop.numBranches(); i++ ) {//TODO may be off by one, need to count last to start branch.
            DirectedPathElement b = loop.directedPathElementAt( i );
            boolean forward = b.isForward();
            Branch branch = (Branch)b.getEdge().getData();
            double sign = 1;
            if( !forward ) {
                sign = -1;
            }
            int index = c.indexOfBranch( branch );
            int vc = -1;
            if( !( branch instanceof Battery ) ) {
                vc = mt.getVoltageColumn( index );
            }
//            logger.fine("Looking at branch i=" + i + ", index=" + index +
//                    ", element=" + c.getCircuit().branchAt(index) + ", branch=" + branch +
//                    "voltage column=" + vc);
            if( branch instanceof HasResistance ) {
//                int vc = mt.getVoltageColumn(index);
                int ic = mt.getCurrentColumn( index );
                double resistance = ( (HasResistance)branch ).getResistance();
                if( resistance != 0 ) {
                    r.setEntry( ic, resistance * sign );
                }
//                    r.setEntry(vc, 1 * sign);
            }
            else if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                double internalResistance = batt.getInternalResistance();
                r.setEntry( mt.getCurrentColumn( index ), internalResistance * sign );
                r.addRHS( batt.getVoltageDrop() * sign );
            }
            else {
                throw new RuntimeException( "Type not found: " + branch.getClass() );
            }
        }
//        System.out.println("r = " + r);
        return r;
    }

    private Equation getOhmsLaw( CircuitGraph c, Branch branch, MatrixTable mt ) {
        if( !c.isLoopElement( branch ) )//TODO does this stay or go?
        {
            return null;
        }

        if( branch instanceof HasResistance ) {
            HasResistance hasr = (HasResistance)branch;
            double res = hasr.getResistance();
            if( res == 0 ) {
                return null;
            }
//                if (res!=0) {//only get an equation for V=IR reasonable.
            Equation equation = new Equation( mt.getFreeParameterCount() );
            int index = c.indexOf( branch );
//                O.d("Type="+b.getClass()+", resistance="+res+", min="+WireKitApplication.props.getMinResistance());
            if( res < minResistance ) {
                throw new RuntimeException( "Resistance out of bounds" );
            }
            equation.setEntry( mt.getCurrentColumn( index ), -res );
            equation.setEntry( mt.getVoltageColumn( index ), 1 );
            return equation;
        }
        else {
            return null;
            //No ohm's law equation for batteries.
        }
    }

    private Equation[] getOhmsLaw( CircuitGraph c, MatrixTable mt ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < c.numBranches(); i++ ) {
            Branch b = c.branchAt( i );
            Equation eq = getOhmsLaw( c, b, mt );
            if( eq != null ) {
                all.add( eq );
            }
        }
        return (Equation[])all.toArray( new Equation[0] );
    }

    private Equation getJunctionEquation( CircuitGraph c, JunctionGroup junction, MatrixTable mt ) {
        Branch[] b = c.getConnectionsWithLoopElements( junction );
        if( b.length >= 2 ) {
            Equation r = new Equation( mt.getFreeParameterCount() );
            for( int k = 0; k < b.length; k++ ) {
                int index = c.indexOf( b[k] );
                if( c.getJunctionGroup( b[k].getStartJunction() ) == junction ) {
                    r.setEntry( mt.getCurrentColumn( index ), -1 );
                }
//                    r.setCurrentCoefficient(c.indexOf(b[k]), -1);
                else {
                    r.setEntry( mt.getCurrentColumn( index ), 1 );
                }
//                    r.setCurrentCoefficient(c.indexOf(b[k]), 1);
            }
            return r;
        }
        else {
            return null;
        }
    }

    private Equation[] getLoopEquations( CircuitGraph c, MatrixTable mt ) {
//        O.d("LoopCount=" + loops.length);
        ArrayList all = new ArrayList();
        Loop[] loops = c.getLoops();
        for( int i = 0; i < loops.length; i++ ) {
            Equation le = getVoltageEquation( loops[i], c, mt );
            all.add( le );
//            if (!le.isAllZeros())
//            all.add(le);
        }
        return (Equation[])all.toArray( new Equation[0] );
    }

    /**
     * Computes the current and voltage in each component, and sets the values.
     */
    private Equation[] getJunctionEquations( CircuitGraph c, MatrixTable mt ) {
        ArrayList rows = new ArrayList();
        for( int i = 0; i < c.numVertices(); i++ ) {
            JunctionGroup v = c.junctionGroupAt( i );
            Equation equation = getJunctionEquation( c, v, mt );
            if( equation != null ) {
                rows.add( equation );
            }
        }
        return (Equation[])rows.toArray( new Equation[0] );
    }

    public Equation[] getSystem( CircuitGraph cg, MatrixTable mt ) {
//        cg.guessCurrentDirections();
        //Need a map from component index to matrix column.
        //Everybody gets a current, but only non-batteries get a voltage.
        //int cols = c.numBranches() * 2+1;//one column for I, one for V, and one for the rhs of the equation.
        ArrayList rows = new ArrayList();
//        cg.guessCurrentDirections();
        Equation[] je = getJunctionEquations( cg, mt );
        rows.addAll( Arrays.asList( je ) );
        Equation[] le = getLoopEquations( cg, mt );
        rows.addAll( Arrays.asList( le ) );
        Equation[] ol = getOhmsLaw( cg, mt );
        rows.addAll( Arrays.asList( ol ) );
//        O.d("je=" + je.length + ", le=" + le.length + ", ol=" + ol.length);
//        Matrix m = Row.toMatrixObject((Row[]) rows.toArray(new Row[0]));
//        return m;
        return (Equation[])rows.toArray( new Equation[0] );
    }

//    public void setLogger(Logger logger) {
//        this.logger=logger;
//    }

}
