/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * ControlPanelTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest {
    public static void main( String[] args ) {

        ApplicationModel appModel = new ApplicationModel( "", "", "" );
        SwingTimerClock clock = new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND );
        appModel.setClock( clock );
        TestModule testModuleA = new TestModule( "A", clock );
        appModel.setModules( new Module[]{testModuleA, new TestModule( "B", clock ), new TestModule( "C", clock )} );
        appModel.setInitialModule( testModuleA );
        PhetApplication app = new PhetApplication( appModel );
//        app.startApplication();
        try {
            Robot robot = new Robot();

            // Simulate a mouse click
            app.getPhetFrame().requestFocus();
            robot.mouseMove( app.getPhetFrame().getLocation().x + 30, app.getPhetFrame().getLocation().y );
            robot.mousePress( InputEvent.BUTTON1_MASK );
            robot.mouseRelease( InputEvent.BUTTON1_MASK );

            // Simulate a key press
            robot.keyPress( KeyEvent.VK_A );
            robot.keyRelease( KeyEvent.VK_A );
        }
        catch( AWTException e ) {
        }
        app.startApplication();
    }

    public class AP2 extends ApparatusPanel2 {
        public AP2( AbstractClock clock ) {
            super( clock );
        }

        public void setReferenceSize() {
            System.out.println( "ControlPanelTest$AP2.setReferenceSize" );
            super.setReferenceSize();
        }

        public void setReferenceSize( Dimension renderingSize ) {
            System.out.println( "ControlPanelTest$AP2.setReferenceSize" );
            super.setReferenceSize( renderingSize );
        }

        public void setReferenceSize( int width, int height ) {
            System.out.println( "ControlPanelTest$AP2.setReferenceSize" );
            super.setReferenceSize( width, height );
        }

        public void setScale( double scale ) {
            System.out.println( "ControlPanelTest$AP2.setScale" );
            super.setScale( scale );
        }


    }


    static class TestModule extends Module {
        protected TestModule( String name, AbstractClock clock ) {
            super( name, clock );
            ApparatusPanel2 ap = new ApparatusPanel2( clock );
            ap.setBackground( Color.white );
            ap.setDisplayBorder( true );
            ap.setPreferredSize( new Dimension( 200, 200 ) );
            ap.addGraphic( new PhetShapeGraphic( ap, new Rectangle( 100, 100, 50, 50 ), Color.red ) );
            setApparatusPanel( ap );

            setModel( new BaseModel() );

            ControlPanel cp = new ControlPanel( this );
            JTextField textField = new JTextField( "012345678901234567890123456789" );

            cp.add( textField );

//            TestPanel tp = new TestPanel();
//            tp.add( textField );

//            setControlPanel( tp );
            setControlPanel( cp );
        }

        public boolean hasHelp() {
            return true;
        }
    }

    static class TestPanel extends JPanel {
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints gbc2 = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
                                                          GridBagConstraints.NORTHWEST,
                                                          GridBagConstraints.NONE,
                                                          new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel jp = new JPanel( new GridBagLayout() );

        public TestPanel() {
            setLayout( new GridBagLayout() );
            add( jp, gbc2 );
        }

        public void addComp( Component comp ) {
            jp.add( comp, gbc );
//            add( comp, gbc );
        }
    }
}
