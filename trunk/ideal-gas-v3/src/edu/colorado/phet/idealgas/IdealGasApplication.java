/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.HeliumBalloonModule;
import edu.colorado.phet.idealgas.controller.HotAirBalloonModule;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.RigidHollowSphereModule;
import edu.colorado.phet.idealgas.view.IdealGasLandF;

import javax.swing.*;
import java.util.Locale;

public class IdealGasApplication extends PhetApplication {

    static class IdealGasApplicationModel extends ApplicationModel {
        public IdealGasApplicationModel() {
            super(SimStrings.get("IdealGasApplication.title"),
                    SimStrings.get("IdealGasApplication.description"),
                    IdealGasConfig.VERSION,
                    IdealGasConfig.FRAME_SETUP);

            // Create the clock
            setClock(new SwingTimerClock(IdealGasConfig.s_timeStep,
                    IdealGasConfig.s_waitTime, true));

            // Create the modules
            Module idealGasModule = new IdealGasModule(getClock());
            Module rigidSphereModule = new RigidHollowSphereModule(getClock());
            Module heliumBalloonModule = new HeliumBalloonModule(getClock());
            Module hotAirBalloonModule = new HotAirBalloonModule(getClock());
            Module[] modules = new Module[]{
                idealGasModule,
                rigidSphereModule,
                heliumBalloonModule,
                hotAirBalloonModule
            };
            setModules(modules);
            //                        setInitialModule( rigidSphereModule );
            setInitialModule(idealGasModule);
        }
    }

    public IdealGasApplication() {
        super(new IdealGasApplicationModel());
        this.getApplicationView().getPhetFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.startApplication();
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new IdealGasLandF());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        String test1 = System.getProperty("java.vm.version");
        System.out.println("test1 = " + test1);
        String s = System.getProperty("javaws.locale");
        System.out.println("s = " + s);
        String applicationLocale = System.getProperty("javaws.locale");
        System.out.println("applicationLocale = " + applicationLocale);

        if (applicationLocale != null && !applicationLocale.equals("")) {
            Locale.setDefault(new Locale(applicationLocale));
        }
        String argsKey = "user.language=";
        System.out.println("args.length = " + args.length);
        if (args.length > 0 && args[0].startsWith(argsKey)) {
            String locale = args[0].substring(argsKey.length(), args[0].length());
            Locale.setDefault(new Locale(locale));
            System.out.println("locale = " + locale);
        }

        SimStrings.setStrings(IdealGasConfig.localizedStringsPath);
        new IdealGasApplication();
    }
}
