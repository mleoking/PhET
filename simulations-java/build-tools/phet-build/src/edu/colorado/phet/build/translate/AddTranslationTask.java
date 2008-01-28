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
    private String simulationList;
    private String language;
    private String username;
    private String password;
    private boolean deployEnabled = true;

    public void execute() throws BuildException {
        super.execute();
        try {
            final String simulation1 = promptIfNecessary( "simulation", simulationList );
            final String languageCode = promptIfNecessary( "language", language );
            final String username = promptIfNecessary( "username", this.username );
            final String password = promptIfNecessary( "password", this.password );
            StringTokenizer st = new StringTokenizer( simulation1, " " );
            while ( st.hasMoreTokens() ) {
                new AddTranslation( getBaseDir(), deployEnabled ).addTranslation( st.nextToken(),
                                                                                  languageCode,
                                                                                  username,
                                                                                  password );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private String promptIfNecessary( String variableName, String simulation ) {
        return simulation == null || simulation.trim().length() == 0 || simulation.startsWith( "${"  ) ?
               JOptionPane.showInputDialog( "Enter the " + variableName )
               : simulation;
    }

    public void setDeployEnabled( boolean deployEnabled ) {
        this.deployEnabled = deployEnabled;
    }
    
    //todo: handle simulation list more elegantly
    public void setSimulation( String simulation ) {
        this.simulationList = simulation;
    }

    public void setLanguage( String language ) {
        this.language = language;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public void setPassword( String password ) {
        this.password = password;
    }
}
