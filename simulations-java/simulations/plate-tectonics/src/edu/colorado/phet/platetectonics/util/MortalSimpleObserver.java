// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.util;

import edu.colorado.phet.common.phetcommon.model.event.INotifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Listener workaround so that we can stop listening to a property when a notifier fires. This is useful to prevent memory leaks.
 */
public abstract class MortalSimpleObserver implements SimpleObserver {
    protected MortalSimpleObserver( final ObservableProperty<?> property, INotifier<?> killNotifier ) {
        killNotifier.addUpdateListener( new UpdateListener() {
            public void update() {
                property.removeObserver( MortalSimpleObserver.this );
            }
        }, false );
    }
}
