package edu.colorado.phet.build.translate;

import java.util.StringTokenizer;

import javax.swing.*;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.AbstractPhetTask;

/**
 * Created by: Sam
 * Jan 13, 2008 at 2:26:00 PM
 */
public class AddTranslationTask extends AbstractPhetTask {
    
    private static final String DIALOG_TITLE = "Add Translation task";
    
    private String simulationList;
    private String languageCode;
    private String username;
    private String password;

    public void execute() throws BuildException {
        super.execute();
        try {
            final String simulationList = promptIfNecessary( "Simulations (separate with spaces)", this.simulationList );
            final String languageCode = promptIfNecessary( "Language code:", this.languageCode );
            final String username = promptIfNecessary( "Production Server username:", this.username );
            final String password = promptIfNecessary( "Production Server password:", this.password );
            StringTokenizer st = new StringTokenizer( simulationList, " " );
            while ( st.hasMoreTokens() ) {
                new AddTranslation( getBaseDir() ).addTranslation( st.nextToken(),
                                                                                  languageCode,
                                                                                  username,
                                                                                  password );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private String promptIfNecessary( String prompt, String defaultValue ) {
        String returnValue = null;
        if ( defaultValue == null || defaultValue.trim().length() == 0 || defaultValue.startsWith( "${"  ) ) {
            returnValue = JOptionPane.showInputDialog( null, prompt, DIALOG_TITLE, JOptionPane.QUESTION_MESSAGE );
        }
        else {
            returnValue = defaultValue;
        }
        return returnValue;
    }

    //todo: handle simulation list more elegantly
    public void setSimulation( String simulationList ) {
        this.simulationList = simulationList;
    }

    public void setLanguage( String language ) {
        this.languageCode = language;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }
}
