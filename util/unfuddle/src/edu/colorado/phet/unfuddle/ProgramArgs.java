package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


public class ProgramArgs {
    
    private static final String TOKEN_DELIMITER = " ";
    private static final int NUMBER_OF_ARGS = 9;
    
    private String unfuddleUsername;
    private String unfuddlePassword;
    private String emailFromAddress;
    private String emailServer;
    private String emailUsername;
    private String emailPassword;
    private String svnTrunk;
    private String xmlDumpPath;
    private boolean sendMailEnabled;
    
    public ProgramArgs( String[] args ) throws IOException {
        if ( args == null || args.length == 0 ) {
            promptForArgs();
        }
        else if ( args.length == 1 ) {
            readArgsFromFile( args[0], NUMBER_OF_ARGS, TOKEN_DELIMITER );
        }
        else {
            throw new IllegalArgumentException( "wrong number of args" );
        }
    }
    
    public String getUnfuddleUsername() {
        return unfuddleUsername;
    }
    
    public String getUnfuddlePassword() {
        return unfuddlePassword;
    }
    
    public String getEmailFromAddress() {
        return emailFromAddress;
    }

    public String getEmailServer() {
        return emailServer;
    }
    
    public String getEmailUsername() {
        return emailUsername;
    }
    
    public String getEmailPassword() {
        return emailPassword;
    }
    
    public String getSvnTrunk() {
        return svnTrunk;
    }

    public String getXmlDumpPath() {
        return xmlDumpPath;
    }
    
    public boolean isSendMailEnabled() {
        return sendMailEnabled;
    }
    
    public String toString() {
        String s = getClass().getName() + "[";
        s += ( "unfuddleUsername=" + unfuddleUsername ) + ",";
        s += ( "unfuddlePassword=" + unfuddlePassword ) + ",";
        s += ( "emailFromAddress=" + emailFromAddress ) + ",";
        s += ( "emailServer=" + emailServer ) + ",";
        s += ( "emailUsername=" + emailUsername ) + ",";
        s += ( "emailPassword=" + emailPassword ) + ",";
        s += ( "svnTrunk=" + svnTrunk ) + ",";
        s += ( "xmlDumpath=" + xmlDumpPath ) + ",";
        s += ( "sendMailEnabled=" + sendMailEnabled );
        s += "]";
        return s;
    }
    
    /*
     * Reads program arguments from a file.
     * Sample file format:
     * @unfuddle-id@ @unfuddle-password@ phetmail@comcast.net smtp.comcast.net phetmail @phet-mail-password@ C:/phet/svn C:/phet/unfuddled.xml true
     */
    private void readArgsFromFile( String filename, int numberOfArgs, String delimiter ) throws IOException {
        File file = new File( filename );
        if ( file.exists() && file.canRead() ) {
            String text = FileUtils.loadFileAsString( file );
            StringTokenizer st = new StringTokenizer( text, delimiter );
            try {
                unfuddleUsername = st.nextToken();
                unfuddlePassword = st.nextToken();
                emailFromAddress = st.nextToken();
                emailServer = st.nextToken();
                emailUsername = st.nextToken();
                emailPassword = st.nextToken();
                svnTrunk = st.nextToken();
                xmlDumpPath = st.nextToken();
                sendMailEnabled = Boolean.parseBoolean( st.nextToken() );
            }
            catch ( NoSuchElementException e ) {
                throw new IOException( "file is missing one or more args" );
            }
            //TODO remove '\n' on end of last token in Unix files
        }
        else {
            throw new IOException( "cannot read file " + filename );
        }
    }
    
    /*
     * Prompts the user for program arguments.
     */
    private void promptForArgs() {
        //TODO: this is a painful way to prompt, use 1 dialog
        //TODO: Cancel button is ignored in each of these JOptionPane calls
        unfuddleUsername = JOptionPane.showInputDialog( "Unfuddle username" );
        unfuddlePassword = JOptionPane.showInputDialog( "Unfuddle password" ); //TODO don't use clear text for passwords
        emailFromAddress = JOptionPane.showInputDialog( "Email from address", "phetmail@comcast.net" );
        emailServer = JOptionPane.showInputDialog( "Email server", "smtp.comcast.net" );
        emailUsername = JOptionPane.showInputDialog( "Email username", "phetmail" );
        emailPassword = JOptionPane.showInputDialog( "Email password" ); //TODO don't use clear text for passwords
        svnTrunk = JOptionPane.showInputDialog( "SVN-trunk dir (e.g. C:/phet/svn/trunk)" );
        xmlDumpPath = JOptionPane.showInputDialog( "Path to Unfuddle XML dump (e.g. C:/phet-unfuddled.xml)" );
        sendMailEnabled = Boolean.parseBoolean( JOptionPane.showInputDialog( "Send mail? (true=yes false=test only)", "true" ) );
    }
}
