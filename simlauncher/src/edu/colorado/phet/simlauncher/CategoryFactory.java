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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryFactory
 * <p>
 * Creates Category objects from the information in an XML file
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CategoryFactory {

    String categoriesElementName = "categories";
    String categoryElementName = "category";
    String categoryNameAttrib = "name";
    private String simulationElementName = "simulation";
    private String simulationNameAttrib = "name";

    public List getCategories( String xmlFile ) {
        List categoryList = new ArrayList();
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
//            XMLOutputter fmt = new XMLOutputter();
//            fmt.output( doc, System.out );

            Element root = doc.getRootElement();
            List categoryElements = root.getChild( categoriesElementName ).getChildren( categoryElementName );
            for( int i = 0; i < categoryElements.size(); i++ ) {
                Element catElement = (Element)categoryElements.get( i );
                String catName = catElement.getAttribute( categoryNameAttrib ).getValue();

                // Get the simulations in this category
                List simElements = catElement.getChildren( simulationElementName );
                List sims = new ArrayList();
                for( int j = 0; j < simElements.size(); j++ ) {
                    Element simElement = (Element)simElements.get( j );
                    String simName = simElement.getAttribute( simulationNameAttrib).getValue();
                    sims.add( Catalog.instance().getSimulationForName( simName ));
                }
                categoryList.add( new Category( catName, sims ) );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return categoryList;
    }

}
