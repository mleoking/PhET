/**
 * Class: XmlExercise
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:27:51 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.net.URL;

public class XmlMessage extends Message {

    public XmlMessage( Container parent, String filename ) {
        super( parent );
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
