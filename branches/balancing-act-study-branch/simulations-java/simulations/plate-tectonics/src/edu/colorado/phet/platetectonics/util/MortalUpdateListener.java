// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.util;

import edu.colorado.phet.common.phetcommon.model.event.INotifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;

/**
 * Listener workaround so that we can stop listening to a notifier when another notifier fires. This is useful to prevent memory leaks.
 */
public abstract class MortalUpdateListener extends UpdateListener {
    protected MortalUpdateListener( final INotifier<?> sourceNotifier, INotifier<?> killNotifier ) {
        killNotifier.addUpdateListener( new UpdateListener() {
            public void update() {
                sourceNotifier.removeListener( MortalUpdateListener.this );
            }
        }, false );
    }
}
