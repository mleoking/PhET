/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.framesetup.FullScreen;

/**
 * This class is essentially a data structure that contains specifications for the top-level
 * elements of a PhetApplication.
 */
public class ApplicationModel {
    private String windowTitle;
    private String description;
    private String version;
    private FrameSetup frameSetup;
    private Module[] modules;
    private Module initialModule;
    private AbstractClock clock;
    boolean useClockControlPanel = true;

    public ApplicationModel( String windowTitle, String description, String version ) {
        this( windowTitle, description, version, new FrameCenterer( 200, 200 ) );
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup ) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
    }

    public static FrameSetup getDefaultFrameSetup() {
        return new FullScreen();
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module[] m, AbstractClock clock ) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
        setClock( clock );
        setModules( m );
        setInitialModule( m[0] );
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module m, AbstractClock clock ) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
        setClock( clock );
        setModule( m );
        setInitialModule( m );
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

    public void start() {
        clock.start();
    }

    public int numModules() {
        return modules.length;
    }

    public Module moduleAt( int i ) {
        return modules[i];
    }

    public boolean getUseClockControlPanel() {
        return useClockControlPanel;
    }

    public void setUseClockControlPanel( boolean useClockControlPanel ) {
        this.useClockControlPanel = useClockControlPanel;
    }

    public void setFrameSetup( FrameSetup frameSetup ) {
        this.frameSetup = frameSetup;
    }
}
