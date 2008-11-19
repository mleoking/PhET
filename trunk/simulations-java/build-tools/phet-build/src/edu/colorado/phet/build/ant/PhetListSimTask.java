package edu.colorado.phet.build.ant;

import org.apache.tools.ant.BuildException;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.PhetBuildUtils;

/**
 * Constructs an iterable list of simulations for use in ant-contrib.
 */
public class PhetListSimTask extends PhetAllSimTask implements PropertyTask {
    private String property = "phet.simlist";

    public final void execute() throws BuildException {
        String[] simNames = PhetProject.getSimNames( getBaseDir() );
        simNames = subsample( simNames );
        buildList( simNames );
    }

    private String[] subsample( String[] simNames ) {
        return simNames;
    }

    public void buildList( String[] simNames ) {
        String string = PhetBuildUtils.convertArrayToList( simNames );

        getProject().setProperty( property, string );
    }

    public void setProperty( String property ) {
        this.property = property;
    }

}
