package edu.colorado.phet.build;

import org.apache.tools.ant.Task;

import java.io.File;

/**
 * Author: Sam Reid
 * May 14, 2007, 3:46:24 PM
 */
public class AbstractPhetTask extends Task implements AntTaskRunner {

    public void runTask( Task childTask ) {
        childTask.setProject( getProject() );
        childTask.setLocation( getLocation() );
        childTask.setOwningTarget( getOwningTarget() );
        childTask.init();
        childTask.execute();
    }

    protected void echo( String string ) {
        PhetBuildUtils.antEcho( this, string, getClass() );
    }


    public File getBaseDir() {
        return getProject().getBaseDir();
    }

}
