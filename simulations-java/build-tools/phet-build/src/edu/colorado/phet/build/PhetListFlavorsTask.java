package edu.colorado.phet.build;

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
