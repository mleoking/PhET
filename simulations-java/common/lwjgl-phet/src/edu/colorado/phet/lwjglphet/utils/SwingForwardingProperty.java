// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

//REVIEW might want to give a concrete example here of when you'd want to use this.
/**
 * A property that can be handled by the Swing EDT directly, that forwards to an underlying property controlled by the LWJGL thread.
 * <p/>
 * NOTE: to sidestep other threading issues, changes to the underlying property (for now) will not be reflected in this property
 */
public class SwingForwardingProperty<T> extends Property<T> {
    public SwingForwardingProperty( final Property<T> property ) {
        super( property.get() );

        addObserver( new ChangeObserver<T>() {
            public void update( final T newValue, T oldValue ) {
                LWJGLUtils.invoke( new Runnable() {
                    public void run() {
                        property.set( newValue );
                    }
                } );
            }
        } );
    }
}
