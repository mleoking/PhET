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

public class XmlExercise extends Exercise {

    public XmlExercise( String filename ) {
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
            Element question = root.getChild( "question" );
            String questionText = question.getText();
            List choices = root.getChildren( "choice" );
            Answer[] choiceArray = new Answer[choices.size()];
            for( int i = 0; i < choices.size(); i++ ) {
                Element choice = (Element)choices.get( i );
                String id = choice.getAttributeValue( "id" );
                String text = choice.getChild( "text" ).getText();
                Answer answer = new Answer( id, text );
                choiceArray[i] = answer;
            }
            String correctAnswerId = root.getChild( "answer" ).getAttributeValue( "id" );
            Answer correctAnswer = null;
            for( int i = 0; i < choiceArray.length; i++ ) {
                Answer answer = choiceArray[i];
                if( correctAnswerId.equals( answer.getId() )) {
                    correctAnswer = answer;
                }
            }

            setQuestion( questionText );
            setChoices( choiceArray );
            setCorrectAnswer( correctAnswer );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

}
