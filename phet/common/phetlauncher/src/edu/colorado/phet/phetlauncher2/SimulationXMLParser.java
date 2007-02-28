/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import net.n3.nanoxml.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 8:14:27 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class SimulationXMLParser {
    public static SimulationXMLEntry[]readEntries( URL url ) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, XMLException {
        ArrayList list = new ArrayList();

        IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
        StdXMLReader reader = new StdXMLReader( new InputStreamReader( url.openStream() ) );
        parser.setReader( reader );
        XMLElement xml = (XMLElement)parser.parse();
        System.out.println( "xml.getFullName() = " + xml.getFullName() );
        for( int i = 0; i < xml.getChildrenCount(); i++ ) {
            IXMLElement child = xml.getChildAtIndex( i );
            SimulationXMLEntry entry = parseEntry( child );
            list.add( entry );
        }
        return (SimulationXMLEntry[])list.toArray( new SimulationXMLEntry[0] );
    }

    private static SimulationXMLEntry parseEntry( IXMLElement child ) {
        String jnlp = child.getAttribute( "url", "" );
        String thumb = child.getAttribute( "thumbnail", "" );
        String abstractStr = child.getAttribute( "abstract", "" );
        String title = child.getAttribute( "title", "" );
        return new SimulationXMLEntry( jnlp, abstractStr, thumb, title );
    }
}
