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
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.plaf.LectureLookAndFeel2;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayModule;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.ProfileModificationModule;
import edu.colorado.phet.nuclearphysics.controller.SingleNucleusFissionModule;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class NuclearPhysicsApplication extends PhetApplication {

    public NuclearPhysicsApplication( ApplicationDescriptor descriptor, Module m, AbstractClock clock ) {
        super( descriptor, m, clock );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NuclearPhysicsApplication( ApplicationDescriptor descriptor, Module[] modules, AbstractClock clock ) {
        super( descriptor, modules, clock );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static void main( String[] args ) {

        try {
            UIManager.setLookAndFeel( new NuclearAppLookAndFeel() );
//            UIManager.setLookAndFeel( new LectureLookAndFeel() );
//            UIManager.setLookAndFeel( new ClientLookAndFeel() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        String desc = GraphicsUtil.formatMessage( "An investigation of\nnuclear fision and fusion" );
        ApplicationDescriptor appDesc = new ApplicationDescriptor( "Nuclear Physics",
                                                                   desc,
                                                                   "0.1" );
        // Note: a ThreadedClock here ends up looking balky
        AbstractClock clock = new SwingTimerClock( 10, 50, true );
        Module profileModificationModule = new ProfileModificationModule( clock );
        Module alphaModule = new AlphaDecayModule( clock );
        Module singleNucleusFissionModule = new SingleNucleusFissionModule( clock );
        Module multipleNucleusFissionModule = new MultipleNucleusFissionModule( clock );
        Module[] modules = new Module[]{alphaModule, singleNucleusFissionModule, multipleNucleusFissionModule};
        NuclearPhysicsApplication app = new NuclearPhysicsApplication( appDesc, modules, clock );
//        app.startApplication( multipleNucleusFissionModule );
//        app.startApplication( singleNucleusFissionModule );
        app.startApplication( alphaModule );
    }


    public static class NuclearAppLookAndFeel extends LectureLookAndFeel2 {

        protected void initComponentDefaults( UIDefaults table ) {


            super.initComponentDefaults( table );
            Font font = (Font)table.get( "Label.font" );
            Object[] defaults = {
                "TextField.font", font
                , "Spinner.font", font
                , "FormattedTextField", font
            };
            table.putDefaults( defaults );
        }
    }

    public static class ClientLookAndFeel extends MetalLookAndFeel {
        Color backgroundColor = new Color( 250, 250, 240 );
        Color buttonBackgroundColor = new Color( 220, 240, 220 );

        protected void initComponentDefaults( UIDefaults table ) {
            super.initComponentDefaults( table );
            Font controlFont = new Font( "Dialog", Font.BOLD, 16 );
            ColorUIResource background = new ColorUIResource( backgroundColor );
            ColorUIResource buttonBackground = new ColorUIResource( buttonBackgroundColor );
            Object[] defaults = {
                "Panel.background", background
                , "Menu.background", background
                , "MenuItem.background", background
                , "MenuBar.background", background
                , "Slider.background", background
                , "RadioButton.background", background
                , "CheckBox.background", background
                , "Button.background", buttonBackground
                , "TitledBorder.font", new Font( "Dialog", Font.BOLD, 18 )
                , "TextField.font", new Font( "Dialog", Font.BOLD, 16 )
                , "Label.font", new Font( "Dialog", Font.BOLD, 16 )
                , "Button.font", new Font( "Dialog", Font.BOLD, 16 )
            };
            table.putDefaults( defaults );
        }
    }
}
