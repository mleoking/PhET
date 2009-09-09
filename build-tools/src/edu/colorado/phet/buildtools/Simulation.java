package edu.colorado.phet.buildtools;

import java.util.Arrays;

/**
 * A localized simulation for a project
 */
public class Simulation {
    private String name;
    private String[] args;
    private String mainclass;
    private String title;

    public Simulation( String name, String title, String mainclass, String[] args ) {
        this.name = name;
        this.args = args;
        this.mainclass = mainclass;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String[] getArgs() {
        return args;
    }

    public String getMainclass() {
        return mainclass;
    }

    public String toString() {
        return "title=" + title + ", mainclass=" + mainclass + ", args=" + Arrays.asList( args );
    }

    public String getJavaStyleName() {
        //delete hyphens and camel-case
        String name = upperCaseFirst( getName() );
        while ( name.indexOf( '-' ) >= 0 ) {
            int index = name.indexOf( '-' );
            name = name.substring( 0, index ) + upperCaseFirst( name.substring( index + 1 ) );
        }
        return name;
    }

    private String upperCaseFirst( String s ) {
        return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
    }
}
