// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class StartSession implements Serializable {
    public final String simName;
    public final String project;
    public final String flavor;
    public final String studentID;

    public StartSession( String simName, String project, String flavor, String studentID ) {
        this.simName = simName;
        this.project = project;
        this.flavor = flavor;
        this.studentID = studentID;
    }
}
