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

public class HtmlMessage extends Message {

    public HtmlMessage( String filename ) {
        StringResourceReader srr = new StringResourceReader();
        String html = srr.read( filename );
        super.setText( html );
    }
}
