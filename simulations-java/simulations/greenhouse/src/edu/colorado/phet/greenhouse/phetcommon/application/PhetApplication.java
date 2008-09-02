/**
 * Class: PhetApplication
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.application;

import edu.colorado.phet.greenhouse.phetcommon.model.ApplicationModel;
import edu.colorado.phet.greenhouse.phetcommon.model.IClock;
import edu.colorado.phet.greenhouse.phetcommon.view.ApplicationDescriptor;
import edu.colorado.phet.greenhouse.phetcommon.view.ApplicationView;
import edu.colorado.phet.greenhouse.phetcommon.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.greenhouse.phetcommon.view.apparatuspanelcontainment.TabbedApparatusPanelContainer;

public class PhetApplication {
    ApplicationView view;
    private ModuleManager moduleManager;
    private ApplicationModel model;

    private ApplicationDescriptor descriptor;
    private ApparatusPanelContainerFactory containerStrategy;

    public ApplicationDescriptor getDescriptor() {
        return descriptor;
    }

    public ApparatusPanelContainerFactory getContainerStrategy() {
        return containerStrategy;
    }

    /**
     * Create a PhET Application that uses a TabbedApparatusPanelStrategy.
     */
    public PhetApplication( ApplicationDescriptor descriptor, Module[] modules, IClock clock ) {
        this( descriptor, new TabbedApparatusPanelContainer.Factory(), modules, clock );
    }

    public PhetApplication( ApplicationDescriptor descriptor,
                            ApparatusPanelContainerFactory containerStrategy, Module[] modules, IClock clock ) {
        this.descriptor = descriptor;
        this.containerStrategy = containerStrategy;
        model = new ApplicationModel( clock );
        moduleManager = new ModuleManager( this );

        view = new ApplicationView( this );
        setModuleObservers();
        moduleManager.addAllModules( modules );
        s_instance = this;
    }


    private void setModuleObservers() {

        moduleManager.addModuleObserver( new ModuleObserver() {
            public void moduleAdded( Module m ) {
            }

            public void activeModuleChanged( Module m ) {
                model.setBaseModel( m.getModel() );
            }
        } );

    }

    public void startApplication( Module initialModule ) {
        moduleManager.setActiveModule( initialModule );
        model.start();
        view.setVisible( true );
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationView getApplicationView() {
        return view;
    }

    public ApplicationModel getApplicationModel() {
        return model;
    }

    public ApplicationDescriptor getApplicationDescriptor() {
        return this.descriptor;
    }

    /**
     * TODO: Added by RJL, 6/20/03
     * @param moduleClass
     */
    public void activateModuleOfClass( Class moduleClass ) {
        moduleManager.activateModuleOfClass( moduleClass );
    }


    //
    // Static fields and methods
    //
    private static PhetApplication s_instance = null;
    public static PhetApplication instance() {
        return s_instance;
    }
}
