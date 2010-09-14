package edu.colorado.phet.workenergy.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Observable;

/**
 * @author Sam Reid
 */
public class MutableList<T> extends Observable<ArrayList<T>> {
    public MutableList() {
        super( new ArrayList<T>() );
    }
    public void add( T snapshot ) {
        ArrayList<T> copy = new ArrayList<T>( getValue() );
        copy.add( snapshot );
        setValue( copy );
    }
}
