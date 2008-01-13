package edu.colorado.phet.build.translate;

import javax.swing.*;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.AbstractPhetTask;

/**
 * Created by: Sam
 * Jan 13, 2008 at 2:26:00 PM
 */
public class AddTranslationTask extends AbstractPhetTask {
    private String simulation;
    private String language;
    private String username;
    private String password;
    private boolean deployEnabled = true;

    public void execute() throws BuildException {
        super.execute();
        try {
            new AddTranslation( getBaseDir(), deployEnabled ).addTranslation( promptIfNecessary( "simulation", simulation ),
                                                                                     promptIfNecessary( "language", language ),
                                                                                     promptIfNecessary( "username", username ),
                                                                                     promptIfNecessary( "password", password ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private String promptIfNecessary( String variableName, String simulation ) {
        return simulation == null || simulation.trim().length() == 0 || simulation.equals( "${" + variableName + "}" ) ?
               JOptionPane.showInputDialog( "Enter the " + variableName )
               : simulation;
    }

    public void setDeployEnabled( boolean deployEnabled ) {
        this.deployEnabled = deployEnabled;
    }

    public void setSimulation( String simulation ) {
        this.simulation = simulation;
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
