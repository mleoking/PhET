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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

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

    String simElementName = "simulation";
    String simNameAttrib = "name";
    String simDescAttrib = "description";
    String simThumbnailAttib = "thumbnail";
    String simJnlpAttrib = "jnlp";

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
        List sims = new ArrayList( );
        List simSpecs = phetWebPage.getSimSpecs();
        for( int i = 0; i < simSpecs.size(); i++ ) {
            PhetWebPage.SimSpec simSpec = (PhetWebPage.SimSpec)simSpecs.get( i );

            String name = simSpec.getName();
//            String name = Integer.toString( i );
            String description = new String( name );
            ThumbnailResource thumbnailResource = null;
            URL jnlpUrl = null;
            try {
                thumbnailResource = new ThumbnailResource( new URL( simSpec.getThumbnailPath() ),
                                                           Configuration.instance().getLocalRoot());
                jnlpUrl = new URL( simSpec.getJnlpPath());
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

                JavaSimulation sim = new JavaSimulation( name, str, thumbnailResource, jnlpURL, localRoot );
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

