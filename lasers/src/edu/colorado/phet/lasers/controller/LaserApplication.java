/**
 * Class: LaserApplication
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.lasers.view.*;

public class LaserApplication extends PhetApplication {

    static class LaserAppModel extends ApplicationModel {
        public LaserAppModel() {
            super( "Lasers", "Lasers", "0.1" );

            AbstractClock clock = new SwingTimerClock( 10, 20 );

            Module singleAtomModule = new OneAtomTwoLevelsModule( clock );
            Module oneAtomThreeLevelsModule = new OneAtomThreeLevelsModule( clock );
            Module multipleAtomTwlLevelModule = new MultipleAtomTwoLevelModule( clock );
            Module multipleAtomThreeLevelModule = new MultipleAtomThreeLevelModule( clock );
            Module testApparatusModule = new TestApparatusModule();
            Module[] modules = new Module[] {
                singleAtomModule,
                oneAtomThreeLevelsModule,
                multipleAtomTwlLevelModule,
                multipleAtomThreeLevelModule,
                testApparatusModule
            };
            setModules( modules );
            setInitialModule( singleAtomModule );
        }
    }

    public LaserApplication() {
        super( new LaserAppModel() );
    }


//    protected PhetMainPanel createMainPanel() {
//        return new LaserMainPanel( this );
//    }
//
//    protected JMenu createControlsMenu( PhetFrame phetFrame ) {
//        return new ControlsMenu( phetFrame, this );
//    }
//
//
//    protected JMenu createTestMenu() {
//        return null;
//    }
//
//    public GraphicFactory getGraphicFactory() {
//        return LaserGraphicFactory.instance();
//    }
//
//    protected PhetAboutDialog getAboutDialog( PhetFrame phetFrame ) {
//        return null;
//    }

//    protected Config getConfig() {
//        return LaserConfig.instance();
//    }


    public void displayHighToMidEmission( boolean selected ) {
        throw new RuntimeException( "TBI" );
//        LaserGraphicFactory.instance().setHighToMidEmissionsVisible( selected );
    }

    //
    // Static fields and methods
    //
    public static void main( String[] args ) {
        LaserApplication application = new LaserApplication();
        application.startApplication();
    }
}
