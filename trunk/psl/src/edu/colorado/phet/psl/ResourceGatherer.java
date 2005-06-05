/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.psl;

import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.Element;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/**
 * Gathers the names of all the resources in a JNLP file
 */
public class ResourceGatherer {

    public List getResources( String jnlpFileName ) {
        ArrayList resourceNames = new ArrayList( );
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(new File(jnlpFileName));
        }
        catch( JDOMException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Get the codebase. It is the value of the codebase attribute of the
        // root (<jnlp>) element
        Element jnlpElement = doc.getRootElement();
        String codebase = jnlpElement.getAttributeValue( "codebase" );

        // Get all the <jar> elements under the <resource> element, and add their
        // names (specified in the href attribute) to the result list, preconcatenated
        // with the codebase
        List resourceElements = doc.getRootElement().getChild("resources").getChildren( "jar" );
        for( int i = 0; i < resourceElements.size(); i++ ) {
            Element element = (Element)resourceElements.get( i );
            String resourceName = codebase.concat("/").concat( element.getAttributeValue( "href" ));
            resourceNames.add( resourceName );
        }
        return resourceNames;
    }

    public static void main( String[] args ) {
        List resourceElements = new ResourceGatherer().getResources( args[0]  );
        for( int i = 0; i < resourceElements.size(); i++ ) {
            String resource = (String)resourceElements.get( i );
            System.out.println( "resource = " + resource );
        }
    }
}
