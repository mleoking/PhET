/**
 * Class: TestPhetApplication
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;

public class TestPhetApplication {
    static class MyModule extends Module {

        public MyModule( String name, AbstractClock clock, Color color ) {
            super( name );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel( clock ) );
            JTextArea ctrl = new JTextArea( 20, 20 );
            JPanel controls = new JPanel();
            controls.add( ctrl );
            getApparatusPanel().addGraphic( new ShapeGraphic( new Rectangle( 200, 100, 300, 100 ), color ) );
            setControlPanel( controls );
        }

        public void activate( PhetApplication app ) {
            super.activate( app );
        }
    }

    static class MyModule2 extends Module {

        public MyModule2( String name, AbstractClock clock, Color color ) {
            super( name );
            setApparatusPanel( new ApparatusPanel() );
            setModel( new BaseModel( clock ) );
            JButton ctrl = new JButton( "Click Me" );
            JPanel controls = new JPanel();
            controls.add( ctrl );
            getApparatusPanel().addGraphic( new ShapeGraphic( new Rectangle( 200, 100, 300, 100 ), color ) );
            setControlPanel( controls );
            JPanel monitorPanel = new JPanel();
            monitorPanel.add( new JCheckBox( "yes/no" ) );
            setMonitorPanel( monitorPanel );
        }

        public void activate( PhetApplication app ) {
            super.activate( app );
        }
    }

    public static void main( String[] args ) {

        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );
        Module module = new MyModule( "Testing", clock, Color.blue );
        Module module2 = new MyModule( "1ntht", clock, Color.red );
        Module module3 = new MyModule2( "Button", clock, Color.red );

        Module[] m = new Module[]{module, module2, module3};

        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor( "Test app", "My Test", ".10" );
        applicationDescriptor.setClock( clock );
        applicationDescriptor.setModules( m );
        applicationDescriptor.setInitialModule( module2 );

        PhetApplication app = new PhetApplication( applicationDescriptor );
        app.startApplication();
    }

}
