package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

/**
 * Constructs an iterable list of simulations for use in ant-contrib.
 */
public class PhetListSimTask extends PhetAllSimTask {
    public final void execute() throws BuildException {
        buildList( getSimNames() );
//        getProject().setProperty( "phet.simlist","my simulation list");
    }

    public void buildList( String[] simNames ) {
        String string = "";
        for( int i = 0; i < simNames.length; i++ ) {
            string += simNames[i];
            if( i < simNames.length - 1 ) {
                string += ",";
            }
        }
        getProject().setProperty( "phet.simlist", string );
    }

}
