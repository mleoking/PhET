/**
 * Class: XmlExercise
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:27:51 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import edu.colorado.phet.coreadditions.StringResourceReader;

import java.util.ArrayList;

public class HtmlExercise extends ExerciseModel {

    public HtmlExercise( String filename ) {
        StringResourceReader srr = new StringResourceReader();
        String html = srr.read( filename );

        ArrayList answers = new ArrayList();
        int choiceIdx = 0;
        for( choiceIdx = html.indexOf( "type=\"radio\"", choiceIdx );
             choiceIdx != -1;
             choiceIdx = html.indexOf( "type=\"radio\"", choiceIdx ) ) {
            choiceIdx++;
            int startIdx = html.indexOf( ">", choiceIdx ) + 1;
            int endIdx = html.indexOf( "<", startIdx );
            String choice = html.substring( startIdx, endIdx );
            while( choice.charAt( 0 ) == '\n' || choice.charAt( 0 ) == '\r' ) {
                choice = choice.substring( 2, choice.length() );
            }
            choice = choice.trim();
        }
    }

    public boolean evaluate( Answer choice ) {
        throw new RuntimeException( "Not implemented");
    }
}
