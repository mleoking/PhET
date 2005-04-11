/**
 * Class: LostInSpaceApplication
 * Class: edu.colorado.games4education.lostinspace
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:47:35 AM
 */
package edu.colorado.games4education.lostinspace;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.games4education.lostinspace.controller.CockpitModule;

public class LostInSpaceApplication extends PhetApplication {

    public LostInSpaceApplication( ApplicationDescriptor descriptor, Module[] modules, AbstractClock clock ) {
        super( descriptor, modules, clock );
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to/nmeasure interstellar distances." );
        ApplicationDescriptor appDesc = new ApplicationDescriptor( "Lost In Space",
                                                                   desc,
                                                                   "0.1" );
        AbstractClock clock = new ThreadedClock( 10, 20, true );
        Module cockpitModule = new CockpitModule( clock );
        Module[] modules = new Module[]{cockpitModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );
    }
}
