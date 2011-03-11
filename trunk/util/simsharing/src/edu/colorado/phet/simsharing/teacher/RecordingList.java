// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class RecordingList implements Serializable {
    private ArrayList<String> list = new ArrayList<String>();

    public void add( File file ) {
        list.add( file.getAbsolutePath() );
    }

    public int size() {
        return list.size();
    }

    public String get( int i ) {
        return list.get( i );
    }

    public Object[] toArray() {
        return list.toArray( new String[0] );
    }
}
