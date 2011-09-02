// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

public class VoidNotifier extends Notifier<Void> {
    public void fire() {
        fire( null );
    }
}
