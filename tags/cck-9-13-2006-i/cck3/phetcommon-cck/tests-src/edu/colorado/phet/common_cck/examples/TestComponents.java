/** Sam Reid*/
package edu.colorado.phet.common_cck.examples;

//import edu.colorado.phet.common.tests.uitest.AnimFactoryLookAndFeel;

import edu.colorado.phet.common_cck.view.plaf.PlafUtil;
import edu.colorado.phet.common_cck.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 2:16:22 PM
 * Copyright (c) Apr 2, 2004 by Sam Reid
 */
public class TestComponents {
    public static void main( String[] args ) throws IllegalAccessException, UnsupportedLookAndFeelException, InstantiationException, ClassNotFoundException {
        JFrame frame = new JFrame( "Test Slider UI" );
        JMenuBar jmb = new JMenuBar() {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        JMenu plafs = new JMenu( "Look and Feel" );
        JMenuItem[] it = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < it.length; i++ ) {
            JMenuItem jMenuItem = it[i];
            plafs.add( jMenuItem );
            jMenuItem.setSelected( false );

        }
        it[it.length - 1].setSelected( true );
        jmb.add( plafs );
        frame.setJMenuBar( jmb );
        JSlider slider = new JSlider( 0, 100 ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        slider.setMajorTickSpacing( 10 );
        slider.setMinorTickSpacing( 2 );
        slider.setPaintTicks( true );
        slider.setPaintLabels( true );
        slider.setSnapToTicks( true );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        contentPane.add( slider );

        frame.setContentPane( contentPane );
        slider.setBorder( BorderFactory.createTitledBorder( "Slider Test" ) );

        JButton button = new JButton( "Fire Photon" );
        contentPane.add( button );

        JCheckBox jcb = new JCheckBox( "Power" );
        contentPane.add( jcb );

        //        UIManager.installLookAndFeel( "Test Look And Feel", new PlayfulLookAndFeel().getClass().getName() );
        //        UIManager.setLookAndFeel( PlayfulLookAndFeel.class.getName());
        SwingUtilities.updateComponentTreeUI( frame );

        SpinnerNumberModel snm = new SpinnerNumberModel( 0, 0, 10, 1 );
        final JSpinner spinner = new JSpinner( snm );
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "Spinner value changed to " + spinner.getValue() );
            }
        } );
        //        spinner.setSize( 200,200);

        JPanel spinnerPanel = new JPanel();
        spinnerPanel.add( spinner );
        contentPane.add( spinnerPanel );

        frame.setSize( 600, 600 );
        GraphicsUtil.centerWindowOnScreen( frame );
        //        PlafUtil.applyPlayful();

        //        UIManager.setLookAndFeel( AnimFactoryLookAndFeel.class.getName() );
        PlafUtil.updateFrames();


        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );


    }
}
