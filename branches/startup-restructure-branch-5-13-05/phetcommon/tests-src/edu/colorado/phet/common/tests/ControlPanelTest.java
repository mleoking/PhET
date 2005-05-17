package edu.colorado.phet.common.tests;

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
import edu.colorado.phet.common.view.*;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * edu.colorado.phet.common.tests.ControlPanelTest
 * A test for working with the layout of the control panel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest {
    public static void main( String[] args ) {

        if( true ) {
            JFrame frame = new JFrame( "test" );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            GridBagConstraints gbc1 = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
            JPanel jp2 = new JPanel( new GridBagLayout() );
//            JTextField textField = new JTextField( "012345678901234567890123456789" );
//            GridBagConstraints gbc2 = new GridBagConstraints( 0,0, 1,1,1,1, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,
//                                                              new Insets( 0,0,0,0), 0,0
//                                                              );
//            jp2.add( textField, gbc2 );
//            JScrollPane jsp = new JScrollPane( jp2 );


            SwingTimerClock clock = new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND );
            TestModule module = new TestModule( "A", clock );
//            TestModule module = new TestModule( "A", new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND ) );
            JPanel jp = new JPanel( new GridBagLayout() );

//            ApparatusPanel appPnl = new ApparatusPanel();
//            appPnl.setPreferredSize( new Dimension( 300, 200 ) );
//            appPnl.setBackground( Color.white );
//            appPnl.setDisplayBorder( true );
//            appPnl.addGraphic( new PhetShapeGraphic( appPnl, new Rectangle( 100, 100, 50, 50 ), Color.red ) );
//            appPnl.addGraphic( new PhetImageGraphic( appPnl, "images/Phet-logo-48x48.gif" ) );
//
            ClockControlPanel appControl = null;
            try {
                appControl = new ClockControlPanel( clock );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            jp = new ContentPanel( module.getApparatusPanel(), module.getControlPanel(), null, appControl );
            frame.setContentPane( jp );
//            ( (ContentPanel)jp ).setControlPanel( module.getControlPanel() );
            frame.setContentPane( module.getControlPanel() );

//            frame.pack();
//            frame.setVisible( true );

            PhetApplication app = new PhetApplication( args, "Control Panel Test",
                                                       "<html>A PhetApplication to test<br>the control panel layout",
//                                                   "0.1", clock, null, false );
                                                       "0.1", clock, false, new FrameSetup.CenteredWithSize( 800, 600 ) );
            app.setModules( new Module[]{ module, new TestModule( "B", clock ), new TestModule( "C", clock )  } );
            app.getPhetFrame().getBasicPhetPanel().setControlPanel( module.getControlPanel() );

            app.getPhetFrame().setVisible( true );


            return;
        }


        ApplicationModel appModel = new ApplicationModel( "", "", "" );
        SwingTimerClock clock = new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND );
        appModel.setClock( clock );
        TestModule testModuleA = new TestModule( "A", clock );
//        appModel.setModules( new Module[]{testModuleA, new TestModule( "B", clock ), new TestModule( "C", clock )} );
//        appModel.setInitialModule( testModuleA );
        PhetApplication app = new PhetApplication( args, "Control Panel Test",
                                                   "<html>A PhetApplication to test<br>the control panel layout",
//                                                   "0.1", clock, null, false );
                                                   "0.1", clock, false, new FrameSetup.CenteredWithSize( 800, 600 ) );
        app.setModules( new Module[]{testModuleA} );
//        app.setModules( new Module[]{testModuleA, new TestModule( "B", clock ), new TestModule( "C", clock )} );
        app.setInitialModule( testModuleA );


//        PhetApplication app = new PhetApplication( appModel );

        app.startApplication();
    }

    static class AP2 extends ApparatusPanel2 {
        private String name;

        public AP2( AbstractClock clock, String name ) {
            super( clock );
            this.name = name;
        }

        public void setReferenceSize() {
            System.out.println( "edu.colorado.phet.common.tests.ControlPanelTest$AP2.setReferenceSize: " + name  );
            super.setReferenceSize();
        }

        public void setReferenceSize( Dimension renderingSize ) {
            System.out.println( "edu.colorado.phet.common.tests.ControlPanelTest$AP2.setReferenceSize: "  + name);
            super.setReferenceSize( renderingSize );
        }

        public void setReferenceSize( int width, int height ) {
            System.out.println( "edu.colorado.phet.common.tests.ControlPanelTest$AP2.setReferenceSize: " + name );
            super.setReferenceSize( width, height );
        }

        public void setScale( double scale ) {
            super.setScale( scale );
            System.out.println( "edu.colorado.phet.common.tests.ControlPanelTest$AP2.setScale: " + name + "  scale: " + getScale() );
        }


    }


    static class TestModule extends Module {
        protected TestModule( String name, AbstractClock clock ) {
            super( name, clock );
            ApparatusPanel ap = new ApparatusPanel();
//            ApparatusPanel2 ap = new AP2( clock, name );
//            ApparatusPanel2 ap = new ApparatusPanel2( clock );
            ap.setBackground( Color.white );
            ap.setDisplayBorder( true );
            ap.setPreferredSize( new Dimension( 200, 200 ) );
            ap.addGraphic( new PhetShapeGraphic( ap, new Rectangle( 100, 100, 50, 50 ), Color.red ) );
            ap.addGraphic( new PhetImageGraphic( ap, "images/Phet-logo-48x48.gif" ) );
            PhetTextGraphic textGraphic = new PhetTextGraphic( ap, new Font( "Lucida-sans", Font.BOLD, 30 ), name, Color.blue);
            textGraphic.setLocation( 50, 50 );
            ap.addGraphic( textGraphic );

            setApparatusPanel( ap );

            setModel( new BaseModel() );

            ControlPanel cp = new ControlPanel( this );
            JTextField textField = new JTextField( "012345678901234567890123456789" );

            cp.add( textField );
            cp.add( new JTextField( "012345678901234567890123456789" ) );
            cp.add( new JTextField( "012345678901234567890123456789" ) );
            cp.add( new JTextField( "012345678901234567890123456789" ) );
            cp.add( new JTextField( "012345678901234567890123456789" ));
            cp.add( new JTextField( "012345678901234567890123456789" ) );

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
