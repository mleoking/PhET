// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

public class CompositeNotifier<T> extends Notifier<T> {
    public CompositeNotifier( Notifier<? extends T>... notifiers ) {
        for ( Notifier<? extends T> notifier : notifiers ) {
            notifier.addTarget( this );
        }
    }
}
