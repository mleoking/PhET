package edu.colorado.phet.build.ant;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.PhetBuildUtils;

/**
 * Constructs an iterable list of dependencies for use in ant-contrib.
 */
public class PhetListDependsTask extends AbstractPhetBuildTask implements PropertyTask {
    private String property = "phet.dependslist";
    private boolean commandLineFormat = false;

    protected void executeImpl( PhetProject phetProject ) throws Exception {
        PhetProject[] projects = phetProject.getAllDependencies();

        List dependPaths = new ArrayList();

        for ( int i = 0; i < projects.length; i++ ) {
            dependPaths.add( projects[i].getProjectDir().getPath() );
        }

        buildList( (String[]) dependPaths.toArray( new String[dependPaths.size()] ) );
    }

    public void buildList( String[] dependPaths ) {
        if ( !commandLineFormat ) {
            String string = PhetBuildUtils.convertArrayToList( dependPaths );

            getProject().setProperty( property, string );
        }
        else {
            StringBuffer buffer = new StringBuffer();

            for ( int i = 0; i < dependPaths.length; i++ ) {
                String path = dependPaths[i];

                if ( i != 0 ) {
                    buffer.append( " " );
                }

                buffer.append( "\"" + path + "\"" );
            }

            getProject().setProperty( property, buffer.toString() );
        }
    }

    public void setCommandLineFormat( boolean commandLineFormat ) {
        this.commandLineFormat = commandLineFormat;
    }

    public void setProperty( String property ) {
        this.property = property;
    }
}
