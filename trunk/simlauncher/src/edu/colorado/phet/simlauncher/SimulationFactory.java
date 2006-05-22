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

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

/**
 * SimlationFactory
 * <p/>
 * Builds Simulation instances from an XML file
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationFactory {

    String simElementName = "simulation";
    String simNameAttrib = "name";
    String simDescAttrib = "description";
    String simThumbnailAttib = "thumbnail";
    String simJnlpAttrib = "jnlp";

    String categoriesElementName = "categories";
    String categoryElementName = "category";
    String categoryNameAttrib = "name";


    public List getSimulations( String xmlFile, File localRoot ) {
        List simList = new ArrayList();
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
            List simElements = root.getChildren( simElementName );
            for( int i = 0; i < simElements.size(); i++ ) {
                Element element = (Element)simElements.get( i );
                String name = element.getAttribute( simNameAttrib ).getValue();
                String descAddr = element.getAttribute( simDescAttrib ).getValue();
                String str = getDescription( descAddr );

                // If the thumbnail isn't local, download it so we'll have a copy to display
                String thumbnailUrl = element.getAttribute( simThumbnailAttib ).getValue();
                ThumbnailResource thumbnailResource = new ThumbnailResource( new URL( thumbnailUrl ), localRoot );
                if( !thumbnailResource.getLocalFile().exists() ) {
                    thumbnailResource.download();
                }

                String jnlpStr = element.getAttribute( simJnlpAttrib ).getValue();
                URL jnlpURL = new URL( jnlpStr );

                Simulation sim = new Simulation( name, str, thumbnailResource, jnlpURL, localRoot );
//                Simulation sim = new Simulation( name, str, imageIcon, jnlpURL, localRoot );
                simList.add( sim );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return simList;
    }

    private String getDescription( String descAddr ) {
        String str = "";
        try {
            // Create a URL for the desired page
            URL descUrl = new URL( descAddr );

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader( new InputStreamReader( descUrl.openStream() ) );
            StringBuffer sb = new StringBuffer( );
            while( ( str = in.readLine() ) != null ) {
                // str is one line of text; readLine() strips the newline character(s)
                sb.append( str );
                sb.append( '\n' );
            }
            in.close();
            str = sb.toString();
        }
        catch( MalformedURLException e ) {
        }
        catch( IOException e ) {
        }
        return str;
    }

//    public List getCategories( String xmlFile ) {
//        List simList = new ArrayList();
//        try {
//            // Build the document with SAX and Xerces, no validation
//            SAXBuilder builder = new SAXBuilder();
//            // Create the document
//            ClassLoader cl = this.getClass().getClassLoader();
//            URL simsUrl = cl.getResource( xmlFile );
//            if( simsUrl == null ) {
//                throw new IOException( "Null URL for resource name=" + xmlFile );
//            }
//
//            Document doc = builder.build( simsUrl );
//
//            // Output the document, use standard formatter
////            XMLOutputter fmt = new XMLOutputter();
////            fmt.output( doc, System.out );
//
//            Element root = doc.getRootElement();
//            List simElements = root.getChild( categoriesElementName ).getChildren( categoryElementName );
//            for( int i = 0; i < simElements.size(); i++ ) {
//                Element element = (Element)simElements.get( i );
//                String name = element.getAttribute( categoryNameAttrib ).getValue();
//                simList.add( new Category( name ) );
//            }
//        }
//        catch( Exception e ) {
//            e.printStackTrace();
//        }
//        return simList;
//    }

    public static void main( String[] args ) {
        new SimulationFactory().getSimulations( "simulations.xml", new File( "/phet/temp" ) );
    }

}

