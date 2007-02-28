/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import edu.colorado.phet.phetlauncher2.SimulationXMLEntry;
import edu.colorado.phet.phetlauncher2.SimulationXMLParser;
import net.n3.nanoxml.XMLException;

import java.io.File;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 8:20:26 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class TestSimulationXMLParser {
    public static void main( String[] args ) throws IllegalAccessException, XMLException, IOException, ClassNotFoundException, InstantiationException {
        File file = new File( "C:\\PhET\\projects-ii\\phetlauncher2\\data\\simulations.xml" );
        SimulationXMLEntry[] list = SimulationXMLParser.readEntries( file.toURL() );
        for( int i = 0; i < list.length; i++ ) {
            SimulationXMLEntry simulationXMLEntry = list[i];
            System.out.println( "simulationXMLEntry = " + simulationXMLEntry );
        }
    }
}
