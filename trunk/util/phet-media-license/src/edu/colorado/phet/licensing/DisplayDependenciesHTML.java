package edu.colorado.phet.licensing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.licensing.media.FileUtils;

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

        ArrayList simHTMLs = new ArrayList();
        for ( int i = 0; i < simNames.length; i++ ) {
            simHTMLs.add( visitSim( simNames[i] ) );
        }

        String content = "Sims with no known issues:<br>";

        for ( int i = 0; i < simHTMLs.size(); i++ ) {
            SimHTML html = (SimHTML) simHTMLs.get( i );
            if ( html.getIssues().isEmpty() ) {
                content += html.getIssues().getProjectName() + "<br>";
            }
        }
        content += "<br><br>";

        content += "Sims with known issues:<br>";
        for ( int i = 0; i < simHTMLs.size(); i++ ) {
            SimHTML html = (SimHTML) simHTMLs.get( i );
            if ( !html.getIssues().isEmpty() ) {
                content += html.getHeader() + "<br>";
            }
        }
        content += "<br><br>";
        for ( int i = 0; i < simHTMLs.size(); i++ ) {
            content += ( (SimHTML) simHTMLs.get( i ) ).getBody();
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

    private class SimHTML {
        private SimInfo issues;
        private String header;
        private String body;

        private SimHTML( SimInfo issues, String header, String body ) {
            this.issues = issues;
            this.header = header;
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public String getHeader() {
            return header;
        }

        public SimInfo getIssues() {
            return issues;
        }
    }

    private SimHTML visitSim( String simName ) throws IOException {
        SimInfo issues = SimInfo.getSimInfo( trunk, simName ).getIssues();

        String header = issues.getHTMLHeader();
        String body = issues.getHTMLBody() + "<br><HR WIDTH=100% ALIGN=CENTER><br>";
        //todo: copy images
        for ( int i = 0; i < issues.getResources().length; i++ ) {
            AnnotatedFile x = issues.getResources()[i];
            File target = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\util\\phet-media-license\\", issues.getHTMLFileLocation( x ) );
            target.getParentFile().mkdirs();
            if ( target.exists() && !FileUtils.contentEquals( target, x.getFile() ) ) {
                System.out.println( "Target exists, and has different content: " + target.getAbsolutePath() );
                System.out.println( "Skipping copy:" );
//                FileUtils.copy( target, new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\util\\phet-media-license\\annotated-data\\", "Copy of " + target.getName() ) );
            }
            FileUtils.copy( x.getFile(), target );
        }

        return new SimHTML( issues, header, body );
    }
}