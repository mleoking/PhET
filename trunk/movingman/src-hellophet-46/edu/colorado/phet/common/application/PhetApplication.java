/**
 * Class: PhetApplication
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.ApplicationModel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.ApplicationView;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.common.view.apparatuspanelcontainment.SingleApparatusPanelContainer;
import edu.colorado.phet.common.view.apparatuspanelcontainment.TabbedApparatusPanelContainer;

import java.io.IOException;

public class PhetApplication {
    ApplicationView view;
    private ModuleManager moduleManager;
    private ApplicationModel model;

    public ApplicationDescriptor getDescriptor() {
        return descriptor;
    }

    private ApplicationDescriptor descriptor;
    private ApparatusPanelContainerFactory containerStrategy;

    public ApparatusPanelContainerFactory getContainerStrategy() {
        return containerStrategy;
    }

    public PhetApplication( ApplicationDescriptor descriptor, Module m ) throws IOException {
        this( descriptor, SingleApparatusPanelContainer.getFactory(), new Module[]{m} );
    }

    /**
     * Create a PhET Application that uses a TabbedApparatusPanelStrategy.
     */
    public PhetApplication( ApplicationDescriptor descriptor, Module[] modules ) throws IOException {
        this( descriptor, new TabbedApparatusPanelContainer.Factory(), modules );
    }

    public PhetApplication( ApplicationDescriptor descriptor,
                            ApparatusPanelContainerFactory containerStrategy, Module[] modules ) throws IOException {
        this.descriptor = descriptor;
        this.containerStrategy = containerStrategy;
        model = new ApplicationModel();
        moduleManager = new ModuleManager( this );

        view = new ApplicationView( this );
        setModuleObservers();

        moduleManager.addAllModules( modules );
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
     *
     * @param moduleClass
     */
    public void activateModuleOfClass( Class moduleClass ) {
        moduleManager.activateModuleOfClass( moduleClass );
    }
}
