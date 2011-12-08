// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule.Tab;

public abstract class LWJGLTab implements Tab {
    private final String title;
    public final Property<Boolean> active = new Property<Boolean>( false );

    public LWJGLTab( final LWJGLCanvas canvas, String title ) {
        this.title = title;

        // switch to this tab when active
        active.addObserver(
                new SimpleObserver() {
                    public void update() {
                        if ( active.get() ) {
                            canvas.switchToTab( LWJGLTab.this );
                        }
                    }
                }, false );
    }

    /**
     * Called before looping, in the LWJGL thread, when this tab is activated. This can
     * happen multiple times, but it is guaranteed to happen before loop()
     */
    public abstract void initialize();

    /**
     * A single iteration of the run-time loop. This will be called by the LWJGL thread
     */
    public abstract void loop();

    public String getTitle() {
        return title;
    }

    public void setActive( boolean active ) {
        this.active.set( active );
    }
}
