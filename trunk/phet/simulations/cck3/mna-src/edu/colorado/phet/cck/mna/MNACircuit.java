/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

import Jama.Matrix;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Assumes nodes are numbered consecutively: i.e. this netlist:
 * i1 0 3 1.0
 * r1 1 3 1.0 would produce incorrect results.
 * //todo add a test for this.
 * <p/>
 * References:
 * 1. Electronic Circuit and System Simulation Methods, Pillage L.T. Rohrer, R.A., Visweswariah, C.
 */

public class MNACircuit {
    private ArrayList components = new ArrayList();

    public void addComponent( MNAComponent component ) {
        components.add( component );
    }

    public String toString() {
        return components.toString();
    }

    public void parseNetList( String[]netlist ) {
        clear();
        for( int i = 0; i < netlist.length; i++ ) {
            String line = netlist[i];
            addComponent( parseLine( line ) );
        }
    }

    private MNAComponent parseLine( String line ) {
        StringTokenizer st = new StringTokenizer( line, " " );
        String name = st.nextToken();
        int start = Integer.parseInt( st.nextToken() );
        int end = Integer.parseInt( st.nextToken() );
        ArrayList details = new ArrayList();
        while( st.hasMoreTokens() ) {
            details.add( st.nextToken() );
        }
        String[]detailArray = (String[])details.toArray( new String[0] );
        if( name.toLowerCase().startsWith( "r" ) ) {
            return new MNAResistor( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else if( name.toLowerCase().startsWith( "i" ) ) {
            return new MNACurrentSource( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else if( name.toLowerCase().startsWith( "v" ) ) {
            return new MNAVoltageSource( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else if( name.toLowerCase().startsWith( "c" ) ) {
            double voltage = detailArray.length > 1 ? Double.parseDouble( detailArray[1] ) : 0.0;
            double current = detailArray.length > 2 ? Double.parseDouble( detailArray[2] ) : 0.0;
            return new MNACapacitor( name, start, end, Double.parseDouble( detailArray[0] ), voltage, current );
        }
        else if( name.toUpperCase().startsWith( "L" ) ) {
            double voltage = detailArray.length > 1 ? Double.parseDouble( detailArray[1] ) : 0.0;
            double current = detailArray.length > 2 ? Double.parseDouble( detailArray[2] ) : 0.0;
            return new MNAInductor( name, start, end, Double.parseDouble( detailArray[0] ), voltage, current );
        }
        else {
            throw new RuntimeException( "Illegal component type: " + line );
        }
    }

    private void clear() {
        components.clear();
    }

    public void parseNetList( String netlist ) {
        StringTokenizer st = new StringTokenizer( netlist, "\n" + System.getProperty( "line.separator" ) );
        ArrayList list = new ArrayList();
        while( st.hasMoreTokens() ) {
            list.add( st.nextToken() );
        }
        parseNetList( (String[])list.toArray( new String[0] ) );
    }

    /*
     * Default implementation; other strategies could be used.
     *todo: adding batteries is altering the number of solution currents; we'll have to keep track of this.
     */
    public MNACircuit getCompanionModel( double dt ) {
        MNACircuit circuit = new MNACircuit();
        int freeIndex = getNodeCount();
        ArrayList companionVoltageSources = new ArrayList();//add them last so they don't interfere with mapping of original batteries
        for( int i = 0; i < getComponentCount(); i++ ) {
            if( getComponent( i ) instanceof MNACapacitor ) {
                //Use Thevenin form of Trapezoidal companion model, see [1] p 83
                MNACapacitor c = (MNACapacitor)getComponent( i );
                MNAComponent veq = new MNAVoltageSource( "veq[companion to" + c.getName() + "]", c.getStartJunction(), freeIndex,
                                                         c.getVoltage() + dt / 2 / c.getCapacitance() * c.getCurrent() );
                MNAComponent req = new MNAResistor( "req[companion to" + c.getName() + "]", freeIndex, c.getEndJunction(),
                                                    dt / 2 / c.getCapacitance() );
//                circuit.addComponent( veq );
                circuit.addComponent( req );
                companionVoltageSources.add( veq );
                freeIndex ++;
            }
            else if( getComponent( i ) instanceof MNAInductor ) {
                //Use Norton form of Trapezoidal companion model, see [1] p 86
                MNAInductor L = (MNAInductor)getComponent( i );
                MNAComponent veq = new MNAVoltageSource( "veq[companion to" + L.getName() + "]", L.getStartJunction(), freeIndex,
                                                         L.getVoltage() + 2 * L.getInductance() * L.getCurrent() / dt );
                MNAComponent req = new MNAResistor( "req[companion to" + L.getName() + "]", freeIndex, L.getEndJunction(),
                                                    2 * L.getInductance() / dt );
//                circuit.addComponent( veq );
                companionVoltageSources.add( veq );
                circuit.addComponent( req );
                freeIndex ++;
            }
            else {
                circuit.addComponent( (MNAComponent)getComponent( i ).clone() );
            }
        }
        for( int i = 0; i < companionVoltageSources.size(); i++ ) {
            MNAVoltageSource mnaVoltageSource = (MNAVoltageSource)companionVoltageSources.get( i );
            circuit.addComponent( mnaVoltageSource );
        }
        return circuit;
    }

    private MNAComponent getComponent( int i ) {
        return (MNAComponent)components.get( i );
    }

    private int getComponentCount() {
        return components.size();
    }

    public static abstract class MNAComponent implements Cloneable {
        private String name;
        private int startJunction;
        private int endJunction;

        public MNAComponent( String name, int startJunction, int endJunction ) {
            this.name = name;
            this.startJunction = startJunction;
            this.endJunction = endJunction;
        }

        public String getName() {
            return name;
        }

        public int getStartJunction() {
            return startJunction;
        }

        public int getEndJunction() {
            return endJunction;
        }

        public String toString() {
            return name + " " + startJunction + " " + endJunction;
        }

        public int getCurrentVariableCount() {
            return 0;
        }

        public abstract void stamp( MNASystem system );

        public Object clone() {
            try {
                MNAComponent clone = (MNAComponent)super.clone();
                clone.name = name;
                clone.startJunction = startJunction;
                clone.endJunction = endJunction;
                return clone;
            }
            catch( CloneNotSupportedException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public abstract static class MNADynamicComponent extends MNAComponent {
        private double voltage = 0.0;
        private double current = 0.0;//todo shouldn't this be modeled by a dc analysis as a short circuit to start?

        public MNADynamicComponent( String name, int startJunction, int endJunction, double voltage, double current ) {
            super( name, startJunction, endJunction );
            this.current = current;
            this.voltage = voltage;
        }

        public Object clone() {
            MNADynamicComponent dynamicComponent = (MNADynamicComponent)super.clone();
            dynamicComponent.voltage = voltage;
            dynamicComponent.current = current;
            return dynamicComponent;
        }

        public double getVoltage() {
            return voltage;
        }

        public double getCurrent() {
            return current;
        }
    }

    public static class MNACapacitor extends MNADynamicComponent {
        private double capacitance;

        public MNACapacitor( String name, int startJunction, int endJunction, double capacitance, double voltage, double current ) {
            super( name, startJunction, endJunction, voltage, current );
            this.capacitance = capacitance;
        }

        public double getCapacitance() {
            return capacitance;
        }

        public String toString() {
            return super.toString() + " " + capacitance + " voltage=" + getVoltage() + ", current=" + getCurrent();
        }

        public void stamp( MNASystem system ) {
            throw new RuntimeException( "Capacitors cannot stamp; use a companion model." );
        }

        public Object clone() {
            MNACapacitor capacitor = (MNACapacitor)super.clone();
            capacitor.capacitance = capacitance;
            return capacitor;
        }
    }

    public static class MNAInductor extends MNADynamicComponent {
        private double inductance;

        public MNAInductor( String name, int startJunction, int endJunction, double inductance, double voltage, double current ) {
            super( name, startJunction, endJunction, voltage, current );
            this.inductance = inductance;
        }

        public double getInductance() {
            return inductance;
        }

        public String toString() {
            return super.toString() + " " + inductance + " voltage=" + getVoltage() + ", current=" + getCurrent();
        }

        public void stamp( MNASystem system ) {
            throw new RuntimeException( "Capacitors cannot stamp; use a companion model." );
        }

        public Object clone() {
            MNAInductor inductor = (MNAInductor)super.clone();
            inductor.inductance = inductance;
            return inductor;
        }
    }

    public static class MNAResistor extends MNAComponent {
        private double resistance;

        public MNAResistor( String name, int startJunction, int endJunction, double resistance ) {
            super( name, startJunction, endJunction );
            this.resistance = resistance;
        }

        public double getResistance() {
            return resistance;
        }

        public String toString() {
            return super.toString() + " " + resistance;
        }

        public void stamp( MNASystem s ) {
            int i = getStartJunction();
            int j = getEndJunction();
            s.addAdmittance( i, i, 1 / resistance );
            s.addAdmittance( j, j, 1 / resistance );
            s.addAdmittance( i, j, -1 / resistance );
            s.addAdmittance( j, i, -1 / resistance );
        }

        public Object clone() {
            MNAResistor clone = (MNAResistor)super.clone();
            clone.resistance = resistance;
            return clone;
        }
    }

    public static class MNACurrentSource extends MNAComponent {
        double current;

        public MNACurrentSource( String name, int startJunction, int endJunction, double current ) {
            super( name, startJunction, endJunction );
            this.current = current;
        }

        public double getCurrent() {
            return current;
        }

        public String toString() {
            return super.toString() + " " + current;
        }

        public void stamp( MNASystem system ) {
            system.addSource( getStartJunction(), -current );
            system.addSource( getEndJunction(), current );
        }

        public Object clone() {
            MNACurrentSource clone = (MNACurrentSource)super.clone();
            clone.current = current;
            return clone;
        }
    }

    public static class MNAVoltageSource extends MNAComponent {
        double voltage;

        public MNAVoltageSource( String name, int startJunction, int endJunction, double voltage ) {
            super( name, startJunction, endJunction );
            this.voltage = voltage;
        }

        public double getVoltage() {
            return voltage;
        }

        public String toString() {
            return super.toString() + " " + voltage;
        }

        public int getCurrentVariableCount() {
            return 1;
        }

        public void stamp( MNASystem system ) {
            system.addVoltageTerm( this );
        }

        public Object clone() {
            MNAVoltageSource clone = (MNAVoltageSource)super.clone();
            clone.voltage = voltage;
            return clone;
        }
    }

    /**
     * Admittance * x = source.
     * We construct the Full MNA equations,
     * the 0th row and column should be discarded prior to use, since we will choose
     * the voltage of the 0th node to be zero.
     */
    public static class MNASystem {
        private Matrix admittance;
        private Matrix source;
        private int numVoltageVariables;
        private int numCurrentVariables;
        private ArrayList voltageSources = new ArrayList();

        public MNASystem( int numVoltageVariables, int numCurrentVariables ) {
            this.numVoltageVariables = numVoltageVariables;
            this.numCurrentVariables = numCurrentVariables;

            admittance = new Matrix( getNumVariables(), getNumVariables() );
            source = new Matrix( getNumVariables(), 1 );
        }

        public Matrix getAdmittanceMatrix() {
            return admittance;
        }

        public Matrix getSourceMatrix() {
            return source;
        }

        private int getNumVariables() {
            return numVoltageVariables + numCurrentVariables;
        }

        public int getNumVoltageVariables() {
            return numVoltageVariables;
        }

        public void addAdmittance( int srcNode, int dstNode, double value ) {
            admittance.set( srcNode, dstNode, admittance.get( srcNode, dstNode ) + value );
        }

        public void addSource( int row, double v ) {
            source.set( row, 0, source.get( row, 0 ) + v );
        }

        public String toString() {
            return toString( 3, 3 );
        }

        private String toString( int w, int d ) {
            StringWriter admString = new StringWriter();
            admittance.print( new PrintWriter( admString ), w, d );
            StringWriter sourceString = new StringWriter();
            source.print( new PrintWriter( sourceString ), w, d );
            return admString + "\n" + sourceString;
        }

        public void addVoltageTerm( MNAVoltageSource voltageSource ) {
            voltageSources.add( voltageSource );
            int k = voltageSource.getStartJunction();
            int L = voltageSource.getEndJunction();
            int at = numVoltageVariables + voltageSources.size() - 1;
            admittance.set( at, k, 1 );
            admittance.set( at, L, -1 );

            admittance.set( k, at, 1 );
            admittance.set( L, at, -1 );

            source.set( at, 0, voltageSource.getVoltage() );
        }

        public Matrix getReducedAdmittanceMatrix() {
            Matrix reduced = JamaUtil.deleteRow( admittance, 0 );
            reduced = JamaUtil.deleteColumn( reduced, 0 );
            return reduced;
        }

        public Matrix getReducedSourceMatrix() {
            return JamaUtil.deleteRow( this.source, 0 );
        }

        public MNASolution getSolution() {
            return new MNASolution( this, getSolutionMatrix() );
        }

        public Matrix getSolutionMatrix() {
            Matrix reducedAdmittance = getReducedAdmittanceMatrix();
            Matrix reducedSource = getReducedSourceMatrix();
            try {
                Matrix result = reducedAdmittance.solve( reducedSource );
                Matrix paddedResult = new Matrix( result.getRowDimension() + 1, result.getColumnDimension() );
                for( int i = 1; i < paddedResult.getRowDimension(); i++ ) {
                    paddedResult.set( i, 0, result.get( i - 1, 0 ) );//set the 0th node voltage equal to zero
                }
                return paddedResult;
            }
            catch( RuntimeException re ) {
                if( re.getMessage().toLowerCase().indexOf( "singular" ) > 0 ) {
                    //ignore
                    //todo: how should error handling work here?
                    return new Matrix( getNumVariables(), 1 );
                }
                else {
                    throw re;
                }
            }
        }

        public int getNumCurrentVariables() {
            return numCurrentVariables;
        }
    }

    public static class MNASolution {
        private MNASystem mnaSystem;
        private Matrix solutionMatrix;

        public MNASolution( MNASystem mnaSystem, Matrix solutionMatrix ) {
            this.mnaSystem = mnaSystem;
            this.solutionMatrix = solutionMatrix;
        }

        public double getVoltage( int node ) {
            return solutionMatrix.get( node, 0 );
        }

        public double getCurrent( int batteryIndex ) {
            return solutionMatrix.get( batteryIndex + mnaSystem.getNumVoltageVariables(), 0 );
        }

        public MNASystem getMnaSystem() {
            return mnaSystem;
        }

        public Matrix getSolutionMatrix() {
            return solutionMatrix;
        }

        public int getNumVoltages() {
            return mnaSystem.getNumVoltageVariables();
        }

        public int getNumCurrents() {
            return mnaSystem.getNumCurrentVariables();
        }

        public String toString() {
            ArrayList v = new ArrayList();
            for( int i = 0; i < getNumVoltages(); i++ ) {
                v.add( new Double( getVoltage( i ) ) );
            }
            ArrayList c = new ArrayList();
            for( int i = 0; i < getNumCurrents(); i++ ) {
                c.add( new Double( getCurrent( i ) ) );
            }
            return "voltage=" + v.toString() + ", current=" + c.toString();
//            StringWriter admString = new StringWriter();
//            solutionMatrix.print( new PrintWriter( admString ), 3, 3 );
//            return "solution=" + admString.toString();
        }

        public boolean isLegalSolution() {
            for( int i = 0; i < solutionMatrix.getRowDimension(); i++ ) {
                for( int j = 0; j < solutionMatrix.getColumnDimension(); j++ ) {
                    double value = solutionMatrix.get( i, j );
                    if( Double.isNaN( value ) || Double.isInfinite( value ) || Math.abs( value ) > 10E10 )
                    {//this change is recent; if we get more than 1 billion amps, we have a problem (maybe) 
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public MNASolution getSolution() {
        return getMNASystem().getSolution();
    }

    public MNASystem getMNASystem() {
        assert getNodeCount() >= 2;
        MNASystem system = new MNASystem( getNodeCount(), getCurrentVariableCount() );
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            component.stamp( system );
        }
        return system;
    }

    private int getCurrentVariableCount() {
        int sum = 0;
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            sum += component.getCurrentVariableCount();
        }
        return sum;
    }

    private int getNodeCount() {
        HashSet hashSet = new HashSet();
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            hashSet.add( new Integer( component.getStartJunction() ) );
            hashSet.add( new Integer( component.getEndJunction() ) );
        }
        return hashSet.size();
    }

}
