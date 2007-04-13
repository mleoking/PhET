/**
 * Class: XmlExercise
 * Class: edu.colorado.games4education.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:27:51 PM
 */
package edu.colorado.games4education.exercise;

import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.Document;
import org.jdom.Element;

import java.io.File;
import java.net.URL;
import java.util.List;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

public class XmlMessage extends Message {

    public XmlMessage( String filename ) {
        try {
            // Build the document with SAX and Xerces, no validation
            SAXBuilder builder = new SAXBuilder();
            // Create the document
            ClassLoader cl = this.getClass().getClassLoader();
            URL url = cl.getResource( filename );
            Document doc = builder.build( url );
            // Output the document, use standard formatter
            XMLOutputter fmt = new XMLOutputter();
            fmt.output( doc, System.out );

            Element root = doc.getRootElement();
            String messageText = root.getText();
            super.setText( messageText );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
