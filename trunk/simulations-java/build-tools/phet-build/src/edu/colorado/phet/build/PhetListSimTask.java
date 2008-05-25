package edu.colorado.phet.build;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

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
//        return simNames;
        
        ArrayList a=new ArrayList( );
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            if (simName.compareToIgnoreCase( "nuclear-physics-2" )>=0){
                a.add(simName);
            }
        }
        simNames= (String[]) a.toArray( new String[0] );
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
