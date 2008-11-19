package edu.colorado.phet.build.translate;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.ant.AbstractPhetTask;

public class CheckTranslationsTask extends AbstractPhetTask {
    private boolean verbose;

    public void setVerbose( boolean verbose ) {
        this.verbose = verbose;
    }

    public void execute() throws BuildException {
        super.execute();
        try {
            new CheckTranslations( verbose ).checkTranslationsAllSims( getBaseDir() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

}
