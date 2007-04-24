/*  */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.TabbedModulePane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.qm.QWIFrameSetup;
import edu.colorado.phet.qm.QWIPhetLookAndFeel;

import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 *
 */

public class DGApplication extends PiccoloPhetApplication {
    public static String VERSION = "1.00";
    public static String TITLE = QWIStrings.getString( "davisson.germer.electron.diffraction" );
    public static String DESCRIPTION = MessageFormat.format( QWIStrings.getString( "simulate.the.original.experiment.that.proved.that.electrons.can.behave.as.waves.nwatch.electrons.diffract.off.a.crystal.of.atoms.interfering.with.themselves.nto.create.peaks.and.troughs.of.probability" ), new Object[0] );

    public DGApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, new QWIFrameSetup() );
        addModule( new DGModule( this, createClock() ) );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    protected PhetFrame createPhetFrame( PhetApplication phetApplication ) {
        return new PhetFrameWorkaround( phetApplication );
    }

    class MyTabbedModulePane extends TabbedModulePane {
        public MyTabbedModulePane( PhetApplication application, Module[] modules ) {
            super( application, modules );
        }
    }

    public static void main( String[] args ) {
//        PhetLookAndFeel.setLookAndFeel();
//        new PhetLookAndFeel().apply();
        new QWIPhetLookAndFeel().initLookAndFeel();
        DGApplication schrodingerApplication = new DGApplication( args );
        schrodingerApplication.startApplication();
//        System.out.println( "SchrodingerApplication.main" );
    }

}
