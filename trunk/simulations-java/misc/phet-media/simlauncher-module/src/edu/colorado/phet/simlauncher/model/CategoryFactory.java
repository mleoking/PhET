/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/model/CategoryFactory.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.model;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryFactory
 * <p/>
 * Creates Category objects from the information in an XML file
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class CategoryFactory {

    String categoriesElementName = "categories";
    String categoryElementName = "category";
    String categoryNameAttrib = "name";
    private String simulationElementName = "simulation";
    private String simulationNameAttrib = "name";


    public List getCategories( File xmlFile ) {
        List categoryList = new ArrayList();
        try {
            // Build the document with SAX and Xerces, no validation
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build( xmlFile );

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
                    String simName = simElement.getAttribute( simulationNameAttrib ).getValue();

                    // DEBUG CHECK
                    if( Simulation.getSimulationForName( simName ) == null ) {
                        System.out.println( "CategoryFactory.getCategories: no simulation with name = " + simName );
                    }
                    else {
                        sims.add( Simulation.getSimulationForName( simName ) );
                    }
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
