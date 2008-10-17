/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.test.help;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.EventObject;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.help.GlassPaneCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * TestGlassPaneCanvas tests GlassPaneCanvas.
 * <p/>
 * Pressing the Help button will display a GlassPaneCanvas that puts
 * colored circles at the upper-left corner of certain Swing controls.
 * See method markComponents for details.
 * <p/>
 * JButtons and JSliders in the control panel have a hand cursor,
 * so that you can verify that the cursor is being set correctly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestGlassPaneCanvas extends PhetApplication {

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    // Cursors
    private static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );

    /* Test harness */
    public static void main( final String[] args ) {
        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    return new TestGlassPaneCanvas( config );
                }
                catch( Exception e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        PhetApplicationConfig applicationConfig = new PhetApplicationConfig( args, "piccolo-phet" );
        new PhetApplicationLauncher().launchSim( applicationConfig, applicationConstructor );
    }

    /* Application */
    public TestGlassPaneCanvas( PhetApplicationConfig c ) throws InterruptedException {
        super( c);

        Module module1 = new TestModule( "Module 1", new Color( 255, 208, 252 ) /* canvasColor */ );
        addModule( module1 );

        Module module2 = new TestModule( "Module 2", new Color( 208, 255, 252 ) /* canvasColor */ );
        addModule( module2 );

        // Menu
        EventListener listener = new EventListener();
        JMenu menu = new JMenu( "Test" );
        getPhetFrame().addMenu( menu );
        for ( int i = 0; i < 20; i++ ) {
            JMenuItem item = new JMenuItem( "item" + i );
            item.setName( "item" + i );
            item.addActionListener( listener );
            menu.add( item );
        }
    }

    /* Clock */
    private static class TestClock extends SwingClock {

        public TestClock() {
            super( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        }
    }

    /* Module */
    private static class TestModule extends PiccoloModule {

        public TestModule( String title, Color canvasColor ) {
            super( title, new TestClock(), true /* startsPaused */ );

            EventListener listener = new EventListener();

            // Simulation panel (aka, play area) -----------------------------------

            PhetPCanvas canvas = new PhetPCanvas();
            setSimulationPanel( canvas );
            canvas.setBackground( canvasColor );

            // PSwing button
            JButton button0 = new JButton( "button0" );
            button0.setName( "button0" );
            button0.setOpaque( false );
            button0.addActionListener( listener );
            PSwing pswing = new PSwing( button0 );
            pswing.addInputEventListener( new CursorHandler() );
            pswing.setOffset( 100, 200 );
            canvas.getLayer().addChild( pswing );

            // PPath
            PPath pathNode = new PPath();
            pathNode.setPathToRectangle( 0, 0, 50, 50 );
            pathNode.setPaint( Color.RED );
            pathNode.setOffset( 400, 300 );
            pathNode.addInputEventListener( new CursorHandler() );
            pathNode.addInputEventListener( new PDragEventHandler() );
            canvas.getLayer().addChild( pathNode );

            // Pop-up menu attached to right mouse button
            HTMLNode html = new HTMLNode( "To access a pop-up menu,<br>click anywhere on the canvas<br>with the right mouse button</html>" );
            html.setFont( new PhetFont( Font.PLAIN, 18 ) );
            html.setOffset( 300, 100 );
            canvas.getLayer().addChild( html );
            final JPopupMenu popupMenu = new JPopupMenu();
            for ( int i = 0; i < 5; i++ ) {
                JMenuItem item = new JMenuItem( "popup" + i );
                item.setName( "popup" + i );
                item.addActionListener( listener );
                popupMenu.add( item );
            }
            canvas.addMouseListener( new MouseInputAdapter() {
                public void mousePressed( MouseEvent event ) {
                    if ( event.getButton() == MouseEvent.BUTTON3 ) {
                        popupMenu.show( event.getComponent(), event.getX(), event.getY() );
                    }
                }
            } );

            // Control panel -----------------------------------

            ControlPanel controlPanel = new ControlPanel();
            setControlPanel( controlPanel );

            JButton button1 = new JButton( "button1" );
            button1.setName( "button1" );
            button1.setCursor( HAND_CURSOR );
            button1.addActionListener( listener );

            JCheckBox checkBox1 = new JCheckBox( "checkBox1" );
            checkBox1.setName( "checkBox1" );
            checkBox1.addActionListener( listener );

            JSlider slider1 = new JSlider();
            slider1.setName( "slider1" );
            slider1.addChangeListener( listener );

            Object[] choices = {"choice1", "choice2", "choice3", "choice4"};
            JComboBox comboBox = new JComboBox( choices );
            comboBox.setName( "comboBox" );
            comboBox.addItemListener( listener );

            // controls embedded in a JPanel
            JPanel panel = new JPanel();
            {
                panel.setBorder( new TitledBorder( "title" ) );

                BoxLayout layout = new BoxLayout( panel, BoxLayout.Y_AXIS );
                panel.setLayout( layout );

                JButton button2 = new JButton( "button2" );
                button2.setName( "button2" );
                button2.setCursor( HAND_CURSOR );
                button2.addActionListener( listener );

                JCheckBox checkBox2 = new JCheckBox( "checkBox2" );
                checkBox2.setName( "checkBox2" );
                checkBox2.addActionListener( listener );

                JSlider slider2 = new JSlider();
                slider2.setName( "slider2" );
                slider2.addChangeListener( listener );

                panel.add( button2 );
                panel.add( checkBox2 );
                panel.add( slider2 );
            }

            controlPanel.addControl( button1 );
            controlPanel.addControl( checkBox1 );
            controlPanel.addControlFullWidth( slider1 );
            controlPanel.addControl( comboBox );
            controlPanel.addControlFullWidth( panel );

            // Help (glass pane)  -----------------------------------

            JFrame frame = PhetApplication.instance().getPhetFrame();
            JComponent glassPane = new MyGlassPane( frame );
            setHelpPane( glassPane );
        }

        public boolean hasHelp() {
            return true;
        }

        /**
         * Extension of GlassPaneCanvas that draws circles at the upper-left corner
         * of various JComponents.  See markComponents for details.
         * <p/>
         * MyGlassPane
         *
         * @author Chris Malley (cmalley@pixelzoom.com)
         * @version $Revision$
         */
        private class MyGlassPane extends GlassPaneCanvas {

            public MyGlassPane( JFrame parentFrame ) {
                super( parentFrame );

                // Periodically mark certain components with colored circles...
                Timer timer = new Timer( 500, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        getLayer().removeAllChildren();
                        markComponents( getParentFrame().getLayeredPane() );
                    }
                } );
                timer.start();
            }

            /*
             * Recursively navigate through the Swing component hierachy.
             * For certain component types, draw a circle at their upper-left corner
             * using these colors:
             *
             * RED   = AbstractButton
             * BLUE  = JCheckBox
             * GREEN = JSlider
             *
             * @param container
             */
            private void markComponents( Container container ) {
                for ( int i = 0; i < container.getComponentCount(); i++ ) {
                    Component c = container.getComponent( i );
                    if ( c.isVisible() ) {
                        if ( c instanceof AbstractButton ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.RED );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof JCheckBox ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.BLUE );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof JSlider ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.GREEN );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof Container ) {
                            markComponents( (Container) c );
                        }
                    }
                }
            }
        }
    }

    /* Handles various types of events */
    public static class EventListener implements ActionListener, ChangeListener, ItemListener {

        public void actionPerformed( ActionEvent e ) {
            showMessage( getName( e ) + " actionPerformed" );
        }

        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() instanceof JSlider && ( (JSlider) e.getSource() ).getValueIsAdjusting() ) {
                // don't do anything while a JSlider is being dragged
                return;
            }
            showMessage( getName( e ) + " stateChanged" );
        }

        public void itemStateChanged( ItemEvent e ) {
            if ( e.getStateChange() == ItemEvent.SELECTED ) {
                showMessage( getName( e ) + " " + e.getItem() + " selected" );
            }
        }

        /* for this test program, we've given every JComponent a name using setName */
        private String getName( EventObject event ) {
            String name = "?";
            if ( event.getSource() instanceof JComponent ) {
                name = ( (JComponent) event.getSource() ).getName();
            }
            return name;
        }

        private void showMessage( String message ) {
            JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(), message );
        }
    }
}
