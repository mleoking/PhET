/**
 * Class: PhetApplication
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.application;

import java.io.IOException;

import edu.colorado.phet.semiconductor.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.semiconductor.phetcommon.view.ApplicationDescriptor;
import edu.colorado.phet.semiconductor.phetcommon.view.ApplicationView;
import edu.colorado.phet.semiconductor.phetcommon.view.apparatuspanelcontainment.ApparatusPanelContainerFactory;
import edu.colorado.phet.semiconductor.phetcommon.view.apparatuspanelcontainment.SingleApparatusPanelContainer;

public class PhetApplication {
    private ApplicationView view;
    private ModuleManager moduleManager;
    private AbstractClock clock;
    private ApplicationDescriptor descriptor;
    private ApparatusPanelContainerFactory containerStrategy;

    public PhetApplication( ApplicationDescriptor descriptor, Module m, AbstractClock clock ) {
        this( descriptor, SingleApparatusPanelContainer.getFactory(), new Module[]{m}, clock );
    }

    public PhetApplication( ApplicationDescriptor descriptor,
                            ApparatusPanelContainerFactory containerStrategy,
                            Module[] modules, AbstractClock clock ) {
        // The clock reference must be set before the call
        this.clock = clock;
        this.descriptor = descriptor;
        this.containerStrategy = containerStrategy;
        moduleManager = new ModuleManager( this );

        try {
            view = new ApplicationView( this );
        }
        catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        moduleManager.addAllModules( modules );

        clock.addClockTickListener( moduleManager );
    }

    public void startApplication( Module initialModule ) {
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

    public ApparatusPanelContainerFactory getContainerStrategy() {
        return containerStrategy;
    }

    public AbstractClock getClock() {
        return this.clock;
    }

}
