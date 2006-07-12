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

import edu.colorado.phet.simlauncher.resources.ThumbnailResource;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * SimFactory
 * <p/>
 * Builds Simulation instances from an XML file
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimFactory {

    private static boolean DEBUG = false;

    private static String JAVA_TYPE_STRING = "java";
    private static String FLASH_TYPE_STRING = "flash";

    public static class XmlCatalogException extends Exception {
        public XmlCatalogException() {
            super();
        }

        public XmlCatalogException( String message ) {
            super( message );
        }
    }

    String simElementName = "simulation";
    String simNameAttrib = "name";
    String simTypeAttrib = "type";
    String simDescAttrib = "description";
    String simThumbnailAttib = "thumbnail";
    String simJnlpAttrib = "jnlp";
    String simSwfAttrib = "swf";

    String categoriesElementName = "categories";
    String categoryElementName = "category";
    String categoryNameAttrib = "name";


    /**
     * Gets a list of Simulations given a PhetWebPage
     *
     * @param phetWebPage
     * @return
     */
    public List getSimulations( PhetWebPage phetWebPage ) {
        List sims = new ArrayList();
        List simSpecs = phetWebPage.getSimSpecs();
        for( int i = 0; i < simSpecs.size(); i++ ) {
            PhetWebPage.SimSpec simSpec = (PhetWebPage.SimSpec)simSpecs.get( i );

            String name = simSpec.getName();
            String description = new String( name );
            ThumbnailResource thumbnailResource = null;
            URL jnlpUrl = null;
            try {
                thumbnailResource = new ThumbnailResource( new URL( simSpec.getThumbnailPath() ),
                                                           Configuration.instance().getLocalRoot() );
                jnlpUrl = new URL( simSpec.getJnlpPath() );
            }
            catch( MalformedURLException e ) {
                e.printStackTrace();
            }
            JavaSimulation simulation = new JavaSimulation( name, description, thumbnailResource, jnlpUrl,
                                                            Configuration.instance().getLocalRoot()
            );
            sims.add( simulation );
        }
        return sims;
    }

    public List getSimulations( File xmlFile ) {
        File localRoot = Configuration.instance().getLocalRoot();
        List simList = new ArrayList();
        try {
            // Build the document with SAX and Xerces, no validation
            SAXBuilder builder = new SAXBuilder();
            // Create the document
            Document doc = builder.build( xmlFile );

            // Output the document, use standard formatter
            if( DEBUG ) {
                XMLOutputter fmt = new XMLOutputter();
                fmt.output( doc, System.out );
            }

            Element root = doc.getRootElement();
            List simElements = root.getChildren( simElementName );
            for( int i = 0; i < simElements.size(); i++ ) {
                Element element = (Element)simElements.get( i );

                // Simulation name
                Attribute nameAttrib = element.getAttribute( simNameAttrib );
                if( nameAttrib == null ) {
                    throw new XmlCatalogException( "Simulation has no name. Simulation number( starting at 0) = " + i );
                }
                String name = nameAttrib.getValue();

                // Simulation description
                Attribute descAttrib = element.getAttribute( simDescAttrib );
                if( descAttrib == null ) {
                    throw new XmlCatalogException( "Simulation has no description URL. name = " + name );
                }
                String descAddr = descAttrib.getValue();
                String str = getDescription( descAddr );

                // If the thumbnail isn't local, download it so we'll have a copy to display
                Attribute thumbnailAttrib = element.getAttribute( simThumbnailAttib );
                if( thumbnailAttrib == null ) {
                    throw new XmlCatalogException( "Simulation has no thumbnail URL. name = " + name );
                }
                String thumbnailUrl = thumbnailAttrib.getValue();

                ThumbnailResource thumbnailResource = null;
                try {
                    thumbnailResource = new ThumbnailResource( new URL( thumbnailUrl ), localRoot );
                    if( !thumbnailResource.getLocalFile().exists() ) {
                        thumbnailResource.download();
                    }
                }
                catch( Exception e ) {
                    System.out.println( "Bad thumbnail: " + thumbnailUrl );
                }

                // Get the simulation type
                Attribute typeAttrib = element.getAttribute( simTypeAttrib );
                if( typeAttrib == null ) {
                    throw new XmlCatalogException( "Simulation has no type attribute. name = " + name );
                }
                if( typeAttrib.getValue().toLowerCase().equals( JAVA_TYPE_STRING ) ) {
                    Attribute jnlpAttrib = element.getAttribute( simJnlpAttrib );
                    if( jnlpAttrib == null ) {
                        throw new XmlCatalogException( "Java simulation has no jnlp attribute. name = " + name );
                    }
                    String jnlpStr = jnlpAttrib.getValue();
                    URL jnlpURL = new URL( jnlpStr );
                    JavaSimulation sim = new JavaSimulation( name, str, thumbnailResource, jnlpURL, localRoot );
                    simList.add( sim );
                }

                else if( typeAttrib.getValue().toLowerCase().equals( FLASH_TYPE_STRING ) ) {
                    // Check for a Flash simulation
                    Attribute swfAttrib = element.getAttribute( simSwfAttrib );
                    if( swfAttrib == null ) {
                        throw new XmlCatalogException( "Flash simulation has no swf attribute. name = " + name );
                    }
                    String swfStr = swfAttrib.getValue();
                    URL swfURL = new URL( swfStr );
                    Simulation sim = new FlashSimulation( name, str, thumbnailResource, swfURL, localRoot );
                    simList.add( sim );
                }
                else {
                    throw new XmlCatalogException( "Simulation type unrecognized. name = " + name );
                }
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
            StringBuffer sb = new StringBuffer();
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

}

