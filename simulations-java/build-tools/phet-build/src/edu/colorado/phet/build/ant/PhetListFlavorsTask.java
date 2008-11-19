package edu.colorado.phet.build.ant;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.PhetBuildUtils;

/**
 * Constructs an iterable list of simulations for use in ant-contrib.
 */
public class PhetListFlavorsTask extends AbstractPhetBuildTask implements PropertyTask {
    private String property = "sim.flavors";

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        String flavorsList = PhetBuildUtils.convertArrayToList( phetProject.getFlavorNames() );

        getProject().setProperty( property, flavorsList );
    }

    public void setProperty( String property ) {
        this.property = property;
    }
}
