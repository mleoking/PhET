/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.util.FrameRateReporter;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.view.IdealGasLandF;
import edu.colorado.phet.idealgas.view.WiggleMeGraphic;
import edu.colorado.phet.idealgas.model.SimulationClock;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class IdealGasApplication extends PhetApplication {

    public IdealGasApplication( String[] args ) {
        super( args,
               SimStrings.get( "IdealGasApplication.title" ),
               SimStrings.get( "IdealGasApplication.description" ),
               IdealGasConfig.VERSION,
//               new SwingClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
//               true,
               IdealGasConfig.FRAME_SETUP );

//        SimulationClock clock = new SimulationClock( 20, IdealGasConfig.TIME_STEP);
        SimulationClock clock = new SimulationClock( IdealGasConfig.WAIT_TIME, IdealGasConfig.TIME_STEP);

//        FrameRateReporter frameRateReporter = new FrameRateReporter( clock );

        final IdealGasModule idealGasModule = new IdealGasModule( clock );
        Module[] modules = new Module[]{
            idealGasModule,
        };
        setModules( modules );

        final WiggleMeGraphic wiggleMeGraphic;
        wiggleMeGraphic = new WiggleMeGraphic( idealGasModule.getApparatusPanel(),
                                               new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 480, IdealGasConfig.Y_BASE_OFFSET + 170 ),
                                               idealGasModule.getModel() );
        wiggleMeGraphic.start();
        idealGasModule.addGraphic( wiggleMeGraphic, 40 );
        idealGasModule.getPump().addObserver( new SimpleObserver() {
            public void update() {
                if( wiggleMeGraphic != null ) {
                    wiggleMeGraphic.kill();
                    idealGasModule.getApparatusPanel().removeGraphic( wiggleMeGraphic );
                    idealGasModule.getPump().removeObserver( this );
                }
            }
        } );

        super.startApplication();
    }


    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-B" ) ) {
                PhetGraphicsModule[] modules = (PhetGraphicsModule[])this.getModules();
                for( int j = 0; j < modules.length; j++ ) {
                    ApparatusPanel ap = modules[j].getApparatusPanel();
                    ap.setBackground( Color.black );
                    ap.paintImmediately( ap.getBounds() );
                }
            }
        }
    }

    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( new IdealGasLandF() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        SimStrings.init( args, IdealGasConfig.localizedStringsPath );
        new IdealGasApplication( args );
    }
}
