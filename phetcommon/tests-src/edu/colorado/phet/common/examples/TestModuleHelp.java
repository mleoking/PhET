/**
 * Class: TestModuleHelp
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 25, 2004
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.FrameSetup;

public class TestModuleHelp {
    static class MyModule extends Module {
        public MyModule( String name ) {
            super( name );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel() );
            HelpItem help = new HelpItem( "Help me please\nI implore you.", 100, 100 );
            addHelpItem( help );
            HelpItem help2 = new HelpItem( "Why not?", 200, 300 );
            addHelpItem( help2 );
            setControlPanel( new HelpPanel( this ) );
        }
    }

    public static void main( String[] args ) {
        MyModule mm = new MyModule( "Test Module" );
        ApplicationModel model = new ApplicationModel( "test", "test", "test", new FrameSetup.CenteredWithSize( 400, 400 ), mm, new SwingTimerClock( 1, 30, true ) );
        PhetApplication pa = new PhetApplication( model );
        pa.startApplication();
    }
}
