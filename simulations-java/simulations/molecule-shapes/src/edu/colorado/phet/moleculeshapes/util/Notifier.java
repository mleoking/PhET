// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.util.ArrayList;
import java.util.List;

public class Notifier<T> implements Fireable<T> {

    private final List<Fireable<? super T>> targets = new ArrayList<Fireable<? super T>>();

    public void fire( T param ) {
        for ( Fireable listener : targets ) {
            listener.fire( param );
        }
    }

    public void addTarget( Fireable<? super T> listener ) {
        targets.add( listener );
    }

    public void removeTarget( Fireable<? super T> listener ) {
        targets.remove( listener );
    }
}
