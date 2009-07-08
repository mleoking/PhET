package edu.colorado.phet.licensing;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.licensing.util.FileUtils;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DependencyReport {

    private File trunk;

    public DependencyReport( File trunk ) {
        this.trunk = trunk;
    }

    public static void main( String[] args ) throws IOException {
        String trunkPath;
        if ( args.length > 0 ) {
            trunkPath = args[0];
        }
        else {
            trunkPath = Config.TRUNK_PATH;
        }
        File trunkFile = new File( trunkPath );
        new DependencyReport( trunkFile ).start();
    }

    public void start() throws IOException {
        generateSimReport();
        generateRuleSet();
        generateIndex();
        generateContribReport();
    }

    private void generateContribReport() {
        File contribDir = new File( trunk, "simulations-java/contrib" );

        File[] f = contribDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return !pathname.getName().startsWith( "." ) && pathname.isDirectory();
            }
        } );
        String content = "";
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            content += getContribHTML( file ) + "\n";
        }

        String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
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
                      "</html>";

        try {
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( new File( trunk, "util/licensing/deploy/contrib-report.html" ) ) );
            bufferedWriter.write( html );
            bufferedWriter.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private String getContribHTML( File dir ) {
        File licenseInfoFile = new File( dir, "license-info.txt" );
        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader( licenseInfoFile ) );
            String line = bufferedReader.readLine();
            ResourceAnnotation a = ResourceAnnotation.parseElement( line );
            String license = a.getLicense();
            String licenseFileName = a.getLicensefile();

            String notes = a.getNotes();
            if ( notes == null || notes.trim().length() == 0 ) {
                notes = "";
            }
            else {
                notes = ", notes=" + notes;
            }
            String href = dir.getName() + ": <a href=\"" + "licenses/" + dir.getName() + "/" + licenseFileName + "\">" + license + "</a>" + notes + "<br>";

            File licenseFile = new File( dir, licenseFileName );
            File dest = new File( trunk, "util/licensing/deploy/licenses/" + dir.getName() + "/" + licenseFileName );
            dest.getParentFile().mkdirs();
            FileUtils.copy( licenseFile, dest );
//            return file.getName()+": "+license+"<br>";
            return href;
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRuleSetFilename() {
        return "PhetRuleSet.txt";
    }

    private void generateRuleSet() throws IOException {
        FileUtils.copy( new File( trunk, "util/licensing/src/edu/colorado/phet/licensing/PhetRuleSet.java" ), new File( trunk, "util/licensing/deploy/" + getRuleSetFilename() ) );//make txt so as not to confuse browsers
    }

    private void generateSimReport() throws IOException {
        File file = new File( trunk, "util/licensing/deploy/report.html" );
        file.getParentFile().mkdirs();
        file.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );


        File baseDir = new File( trunk, "simulations-java" );
        String[] simNames = PhetProject.getSimNames( baseDir );

        ArrayList simHTMLs = new ArrayList();
        int count = 0;
        for ( int i = 0; i < simNames.length; i++ ) {
            DependencyReport.SimHTML info = visitSim( simNames[i] );
            simHTMLs.add( info );
            count += info.getIssues().getIssueCount();
        }

        String content = "Sims with no known issues:<br>";

        for ( int i = 0; i < simHTMLs.size(); i++ ) {
            SimHTML html = (SimHTML) simHTMLs.get( i );
            if ( html.getIssues().isEmpty() ) {
                content += html.getIssues().getProjectName() + "<br>";
            }
        }
        content += "<br><br>";

        content += "Sims with known issues (" + count + " issues found):<br>";
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

    private void generateIndex() throws IOException {
        String index = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
                       "    \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                       "<html>\n" +
                       "<head>\n" +
                       "  <title>" + "PhET Licensing Report</title>\n" +
                       "</head>\n" +
                       "<body>\n" +
                       "\n" +
                       "PhET Licensing Report<br>" + new Date() + "<br><br><br>" +
                       "<a href=\"" + getRuleSetFilename() + "\">Rule Set</a><br>" +
                       "<a href=\"report.html\">Sim Report</a><br>" +
                       "<a href=\"contrib-report.html\">Contrib Report</a><br>" +
                       "\n" +
                       "</body>\n" +
                       "</html>";
        BufferedWriter bw = new BufferedWriter( new FileWriter( getIndexFile() ) );
        bw.write( index );
        bw.close();
    }

    public File getIndexFile() {
        return new File( trunk, "util/licensing/deploy/index.html" );
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
        for ( int i = 0; i < issues.getResources().length; i++ ) {
            AnnotatedFile x = issues.getResources()[i];
            File target = new File( trunk.getAbsolutePath() + "/util/licensing/deploy/", issues.getHTMLFileLocation( x ) );
            target.getParentFile().mkdirs();
            if ( target.exists() && !FileUtils.contentEquals( target, x.getFile() ) ) {
                System.out.println( "Target exists, and has different content: " + target.getAbsolutePath() );
                System.out.println( "Skipping copy:" );
            }
            else {
                System.out.println( "Copying: " + x.getFile() + " to " + target.getAbsolutePath() );
                FileUtils.copy( x.getFile(), target );
            }
        }

        return new SimHTML( issues, header, body );
    }
}