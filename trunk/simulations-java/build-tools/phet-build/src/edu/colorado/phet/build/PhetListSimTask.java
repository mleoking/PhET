package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

/**
 * Constructs an iterable list of simulations for use in ant-contrib.
 */
public class PhetListSimTask extends PhetAllSimTask implements PropertyTask {
    private String property = "phet.simlist";

    public final void execute() throws BuildException {
        buildList( PhetProject.getSimNames( getBaseDir() ) );
//        getProject().setProperty( "phet.simlist","my simulation list");
    }

    public void buildList( String[] simNames ) {
        String string = PhetBuildUtils.convertArrayToList( simNames );

        getProject().setProperty( property, string );
    }

    public void setProperty( String property ) {
        this.property = property;
    }

}
