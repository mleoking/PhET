// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetRecording implements Serializable {
    private final String filename;

    public GetRecording( String filename ) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
