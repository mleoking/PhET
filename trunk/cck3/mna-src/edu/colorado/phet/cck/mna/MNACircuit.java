/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 12:45:27 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
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
        else {
            throw new RuntimeException( "Illegal component type: " + line );
        }
    }

    private void clear() {
        components.clear();
    }

    public static class MNAComponent {
        String name;
        int startJunction;
        int endJunction;

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
    }
}
