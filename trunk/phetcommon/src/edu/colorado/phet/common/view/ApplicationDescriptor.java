/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.framesetup.AbsoluteFrameSetup;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.framesetup.FullScreen;

/**
 * This class is essentially a data structure that contains specifications for the top-level
 * elements of a PhetApplication.
 */
public class ApplicationDescriptor {
    String windowTitle;
    String description;
    String version;
    FrameSetup frameSetup;
    Module[] modules;
    Module initialModule;
    AbstractClock clock;

    public ApplicationDescriptor( String windowTitle, String description, String version, int width, int height ) {
        this( windowTitle, description, version, width, height, false );
    }

    public ApplicationDescriptor( String windowTitle, String description, String version, int x, int y, boolean isInsetSpecified ) {
        this( windowTitle, description, version, getFrameSetup( isInsetSpecified, x, y ) );
    }

    public ApplicationDescriptor( String windowTitle, String description, String version ) {
        this( windowTitle, description, version, new FullScreen() );
    }

    public ApplicationDescriptor( String windowTitle, String description, String version, FrameSetup frameSetup ) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
    }

    private static FrameSetup getFrameSetup( boolean inset, int x, int y ) {
        if( inset ) {
            return new FrameCenterer( x, y );
        }
        else {
            return new AbsoluteFrameSetup( x, y );
        }
    }

    public void setModules( Module[] modules ) {
        this.modules = modules;
    }

    public void setModule( Module module ) {
        this.modules = new Module[]{module};
    }

    public void setInitialModule( Module initialModule ) {
        this.initialModule = initialModule;
    }

    public Module[] getModules() {
        return modules;
    }

    public Module getInitialModule() {
        return initialModule;
    }

    public AbstractClock getClock() {
        return clock;
    }

    public void setClock( AbstractClock clock ) {
        this.clock = clock;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public FrameSetup getFrameSetup() {
        return frameSetup;
    }
}
