/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.plaf.LectureLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayModule;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.SingleNucleusFissionModule;

import javax.swing.*;
import java.awt.*;

public class NuclearPhysicsApplication extends PhetApplication {

    public NuclearPhysicsApplication( ApplicationModel descriptor ) {
        super( descriptor );
    }

    public static void main( String[] args ) {

        try {
            UIManager.setLookAndFeel( new NuclearAppLookAndFeel() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        String desc = GraphicsUtil.formatMessage( "An investigation of\nnuclear fision and fusion" );
        ApplicationModel appDesc = new ApplicationModel( "Nuclear Physics",
                                                         desc,
                                                         "0.1",
                                                         new FrameSetup.MaxExtent() );
        // Note: a ThreadedClock here ends up looking balky
        SwingTimerClock clock = new SwingTimerClock( 10, 50, true );
        //        Module profileModificationModule = new ProfileModificationModule( clock );
        Module alphaModule = new AlphaDecayModule( clock );
        Module singleNucleusFissionModule = new SingleNucleusFissionModule( clock );
        Module multipleNucleusFissionModule = new MultipleNucleusFissionModule( clock );
        Module[] modules = new Module[]{alphaModule, singleNucleusFissionModule, multipleNucleusFissionModule};
        appDesc.setModules( modules );
        appDesc.setInitialModule( alphaModule );
        appDesc.setClock( clock );
        //        app.startApplication( multipleNucleusFissionModule );
        //        app.startApplication( singleNucleusFissionModule );

        NuclearPhysicsApplication app = new NuclearPhysicsApplication( appDesc );
        app.startApplication();
    }


    public static class NuclearAppLookAndFeel extends LectureLookAndFeel {

        protected void initComponentDefaults( UIDefaults table ) {


            super.initComponentDefaults( table );
            Font font = (Font)table.get( "Label.font" );
            Color color = (Color)table.get( "Label.foreground" );
            Object[] defaults = {
                "TextField.font", font
                , "Spinner.font", font
                , "FormattedTextField.font", font
                , "TitledBorder.font", font
                , "TitledBorder.titleColor", color
            };
            table.putDefaults( defaults );
        }
    }
}
