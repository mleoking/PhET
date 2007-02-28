/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import net.n3.nanoxml.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 8:04:30 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class TestXMLParser {
    public static void main( String[] args ) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, XMLException {
        File file = new File( "C:\\PhET\\projects-ii\\phetlauncher2\\data\\simulations.xml" );
//        File file = new File( "C:\\PhET\\projects-ii\\phetlauncher2\\NanoXML-2.2.1\\Examples\\Java\\Chapter02-01\\test.xml" );
        IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
        StdXMLReader reader = new StdXMLReader( new FileReader( file ) );
        parser.setReader( reader );
        XMLElement xml = (XMLElement)parser.parse();
        System.out.println( "xml.getFullName() = " + xml.getFullName() );
        XMLWriter writer = new XMLWriter( System.out );
        writer.write( xml );
    }
}
