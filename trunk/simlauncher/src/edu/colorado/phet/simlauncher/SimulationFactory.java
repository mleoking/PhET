/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * SimlationFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationFactory {

    String simElementName = "simulation";
    String simNameAttrib = "name";
    String simDescAttrib = "description";
    String simThumbnailAttib = "thumbnail";

    String categoriesElementName = "categories";
    String categoryElementName = "category";
    String categoryNameAttrib = "name";


    public List getSimulations( String xmlFile ) {
        List simList = new ArrayList( );
        try {
            // Build the document with SAX and Xerces, no validation
            SAXBuilder builder = new SAXBuilder();
            // Create the document
            ClassLoader cl = this.getClass().getClassLoader();
            URL simsUrl = cl.getResource( xmlFile );
            if( simsUrl == null ) {
                throw new IOException( "Null URL for resource name=" + xmlFile );
            }

            Document doc = builder.build( simsUrl );

            // Output the document, use standard formatter
            XMLOutputter fmt = new XMLOutputter();
            fmt.output( doc, System.out );

            Element root = doc.getRootElement();
            List simElements = root.getChildren( simElementName );
            for( int i = 0; i < simElements.size(); i++ ) {
                Element element = (Element)simElements.get( i );
                String name = element.getAttribute( simNameAttrib ).getValue();
                String desc = element.getAttribute( simDescAttrib ).getValue();
                String thumbnail = element.getAttribute( simThumbnailAttib ).getValue();
                Simulation sim = new Simulation( name, desc, null );
                simList.add( sim );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return simList;
    }

    public List getCategories( String xmlFile ) {
        List simList = new ArrayList( );
        try {
            // Build the document with SAX and Xerces, no validation
            SAXBuilder builder = new SAXBuilder();
            // Create the document
            ClassLoader cl = this.getClass().getClassLoader();
            URL simsUrl = cl.getResource( xmlFile );
            if( simsUrl == null ) {
                throw new IOException( "Null URL for resource name=" + xmlFile );
            }

            Document doc = builder.build( simsUrl );

            // Output the document, use standard formatter
            XMLOutputter fmt = new XMLOutputter();
            fmt.output( doc, System.out );

            Element root = doc.getRootElement();
            List simElements = root.getChild( categoriesElementName).getChildren( categoryElementName );
            for( int i = 0; i < simElements.size(); i++ ) {
                Element element = (Element)simElements.get( i );
                String name = element.getAttribute( categoryNameAttrib ).getValue();
                simList.add( new Category( name) );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return simList;
    }

    public static void main( String[] args ) {
        new SimulationFactory().getSimulations( "simulations.xml" );
    }

}

