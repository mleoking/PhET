/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.controller.FisionModule;

public class NuclearPhysicsApplication extends PhetApplication {

    public NuclearPhysicsApplication( ApplicationDescriptor descriptor, Module m, AbstractClock clock ) {
        super( descriptor, m, clock );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "An investigation of\nnuclear fision and fusion" );
        ApplicationDescriptor appDesc = new ApplicationDescriptor( "Nuclear Physics",
                                                                   desc,
                                                                   "0.1" );
        AbstractClock clock = new ThreadedClock( 20, 50, true );
        Module module = new FisionModule( clock );
        NuclearPhysicsApplication app = new NuclearPhysicsApplication( appDesc, module, clock );
        app.startApplication( module );
    }
}
