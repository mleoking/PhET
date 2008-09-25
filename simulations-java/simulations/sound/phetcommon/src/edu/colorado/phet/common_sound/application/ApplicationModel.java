/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common_sound.application;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_sound.model.clock.AbstractClock;

/**
 * This class is essentially a data structure that contains specifications for the top-level
 * elements of a PhetApplication.
 *
 * @author ?
 * @version $Revision$
 */
public class ApplicationModel {
    private String name;
    private String windowTitle;
    private String description;
    private String version;
    private FrameSetup frameSetup;
    private Module[] modules;
    private Module initialModule;
    private AbstractClock clock;
    boolean useClockControlPanel = true;

    public ApplicationModel( String windowTitle, String description, String version ) {
        this( windowTitle, description, version, new FrameSetup.CenteredWithInsets( 200, 200 ) );
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup ) {
        this.windowTitle = windowTitle;
        this.name = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
        SimStrings.setStrings( "localization/CommonStrings" );
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module[] m, AbstractClock clock ) {
        this( windowTitle, description, version, frameSetup );
        setClock( clock );
        setModules( m );
        setInitialModule( m[0] );
    }

    public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module m, AbstractClock clock ) {
        this( windowTitle, description, version, frameSetup );
        setClock( clock );
        setModule( m );
        setInitialModule( m );
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Sets the modules for this application.
     * The initial module defaults to the first module in the specified array.
     *
     * @param modules
     */
    public void setModules( Module[] modules ) {
        this.modules = modules;
        this.initialModule = modules[0];
    }

    /**
     * Sets the module for this application.
     *
     * @param module
     */
    public void setModule( Module module ) {
        this.modules = new Module[]{module};
        this.initialModule = module;
    }

    /**
     * Sets the initial module for this application,
     * the first module that is displayed when the application is started.
     * The initial module must be one of the modules that was specified
     * using setModules or setModule.
     *
     * @param initialModule
     * @throws IllegalStateException    if no modules have been set
     * @throws IllegalArgumentException if the module is not one of the application's modules
     */
    public void setInitialModule( Module initialModule ) {
        if( modules == null ) {
            throw new IllegalStateException( "no modules have been set" );
        }

        boolean found = false;
        for( int i = 0; i < modules.length; i++ ) {
            if( initialModule == modules[i] ) {
                found = true;
                break;
            }
        }
        if( !found ) {
            throw new IllegalArgumentException( "module is not part of this ApplicationModel" );
        }

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

    public void setFrameCenteredSize( int width, int height ) {
        setFrameSetup( new FrameSetup.CenteredWithSize( width, height ) );
    }

    public void setFrameCenteredInsets( int insetX, int insetY ) {
        setFrameSetup( new FrameSetup.CenteredWithInsets( insetX, insetY ) );
    }

    /**
     * The insets are a fallback-plan, when the frame is un-max-extented,
     * the frame will be centered with these specified insets.
     *
     * @param insetX
     * @param insetY
     */
    public void setFrameMaximized( int insetX, int insetY ) {
        setFrameSetup( new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( insetX, insetY ) ) );
    }

    public String getName() {
        return name;
    }

}
