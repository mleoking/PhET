// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class GetRecordingSample implements Serializable {
    private final String filename;
    private final int index;

    public GetRecordingSample( String filename, int index ) {
        this.filename = filename;
        this.index = index;
    }

    public String getFilename() {
        return filename;
    }

    public int getIndex() {
        return index;
    }
}
