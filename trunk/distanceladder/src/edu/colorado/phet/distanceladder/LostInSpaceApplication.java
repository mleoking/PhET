/**
 * Class: LostInSpaceApplication
 * Class: edu.colorado.phet.distanceladder.lostinspace
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:47:35 AM
 */
package edu.colorado.phet.distanceladder;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.distanceladder.controller.CockpitModule;
import edu.colorado.phet.distanceladder.controller.StarMapModule;
import edu.colorado.phet.distanceladder.model.StarField;
import edu.colorado.phet.distanceladder.model.UniverseModel;

public class LostInSpaceApplication extends PhetApplication {
    ApplicationDescriptor appDesc;

    public LostInSpaceApplication( ApplicationDescriptor descriptor, Module[] modules, AbstractClock clock ) {
        super( descriptor, modules, clock );
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to\nmeasure interstellar distances." );
        ApplicationDescriptor appDesc = new ApplicationDescriptor( "Lost In Space",
                                                                   desc,
                                                                   "0.1" );
        AbstractClock clock = new ThreadedClock( 10, 20, true );
        StarField starField = new StarField();
        UniverseModel model = new UniverseModel( starField, clock );
        Module cockpitModule = new CockpitModule( model );
        Module starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );
    }


}
