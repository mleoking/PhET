/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.ApplicationView;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.apparatuspanelcontainment.TabbedApparatusPanelContainer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The top-level class for all PhET applications. It contains an ApplicationView and
 */
public class PhetApplication {
    private ApplicationView view;
    private ModuleManager moduleManager;
    private AbstractClock clock;
    private ApplicationDescriptor descriptor;
    private Module initialModule;

    public PhetApplication( ApplicationDescriptor descriptor ) {
        if( descriptor.getModules() == null ) {
            throw new RuntimeException( "Module(s) not specified in ApplicationDescriptor" );
        }
        if( descriptor.getClock() == null ) {
            throw new RuntimeException( "Clock not specified in ApplicationDescriptor" );
        }
        init( descriptor, descriptor.getModules(), descriptor.getClock() );
    }

    /**
     * Creates an application that has a single module
     *
     * @param descriptor
     * @param m
     * @param clock
     * @deprecated
     */
    public PhetApplication( ApplicationDescriptor descriptor, Module m, AbstractClock clock ) {
        init( descriptor, new Module[]{m}, clock );
    }

    /**
     * Create a PhET Application that has multiple modules, selectable by a tabbed pane
     *
     * @deprecated
     */
    public PhetApplication( ApplicationDescriptor descriptor, Module[] modules, AbstractClock clock ) {
        init( descriptor, modules, clock );
    }

    private void init( ApplicationDescriptor descriptor,
                       Module[] modules, AbstractClock clock ) {
        // The clock reference must be set before the call
        this.clock = clock;
        this.descriptor = descriptor;

        moduleManager = new ModuleManager( this );
        JComponent apparatusPanelContainer = null;
        if( modules.length == 1 ) {
            apparatusPanelContainer = new JPanel();
            apparatusPanelContainer.setLayout( new GridLayout( 1, 1 ) );
            apparatusPanelContainer.add( modules[0].getApparatusPanel() );
        }
        else {
            apparatusPanelContainer = new TabbedApparatusPanelContainer( moduleManager );
        }

        try {
            view = new ApplicationView( this.getApplicationDescriptor(), apparatusPanelContainer, clock );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        moduleManager.addAllModules( modules );
        clock.addClockTickListener( moduleManager );
        s_instance = this;

        this.initialModule = descriptor.getInitialModule();
    }

    public void setClock( AbstractClock clock ) {
        this.clock = clock;
    }

    /**
     * @param initialModule
     * @deprecated
     */
    public void startApplication( Module initialModule ) {
        this.initialModule = initialModule;
        startApplication();
        //        moduleManager.setActiveModule( initialModule );
        //        clock.start();
        //        view.setVisible( true );
    }

    public void startApplication() {
        if( initialModule == null ) {
            throw new RuntimeException( "Initial module not specified." );
        }
        moduleManager.setActiveModule( initialModule );
        clock.start();
        view.setVisible( true );
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationView getApplicationView() {
        return view;
    }

    public ApplicationDescriptor getApplicationDescriptor() {
        return this.descriptor;
    }

    public ApplicationDescriptor getDescriptor() {
        return descriptor;
    }

    public AbstractClock getClock() {
        return this.clock;
    }

    //
    // Static fields and methods
    //
    private static PhetApplication s_instance = null;

    public static PhetApplication instance() {
        return s_instance;
    }

    public ApparatusPanelContainer getApparatusPanelContainer() {
        return null;
    }

}
