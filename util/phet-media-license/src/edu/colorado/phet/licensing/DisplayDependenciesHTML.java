package edu.colorado.phet.licensing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DisplayDependenciesHTML {
    private static File trunk = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2" );

    public static void main( String[] args ) throws IOException {
        new DisplayDependenciesHTML().start();
    }

    private void start() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\util\\phet-media-license\\report.html" ) ) );


        File baseDir = new File( trunk, "simulations-java" );
        String[] simNames = PhetProject.getSimNames( baseDir );

        String content = "";
        for ( int i = 0; i < simNames.length; i++ ) {
            content += visitSim( simNames[i] );
        }
        bufferedWriter.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
                              "    \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                              "<html>\n" +
                              "<head>\n" +
                              "  <title>" + "PhET Java Software Dependencies</title>\n" +
                              "</head>\n" +
                              "<body>\n" +
                              "\n" +
                              "PhET Java Software Dependencies<br>" + new Date() + "<br><br><br>" +
                              content +
                              "\n" +
                              "</body>\n" +
                              "</html>" );
        bufferedWriter.close();
    }

    private String visitSim( String simName ) throws IOException {
        SimInfo issues = SimInfo.getSimInfo( trunk, simName ).getIssues();
        String html = issues.toHTML() + "<br><HR WIDTH=100% ALIGN=CENTER><br>";
        //todo: copy images
        return html;
    }
}