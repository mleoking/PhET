package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 8:49:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhetJComponentContainerTest {
    private JFrame frame;
    private ApparatusPanel2 ap;
    private SwingTimerClock swingTimerClock;

    public PhetJComponentContainerTest() {
        /*Set up the application frame and apparatusPanel.*/
        frame = new JFrame( "Frame" );
        swingTimerClock = new SwingTimerClock( 1, 30 );
        ap = new ApparatusPanel2( swingTimerClock );
        ap.addGraphicsSetup( new BasicGraphicsSetup() );

        frame.setContentPane( ap );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        swingTimerClock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                ap.handleUserInput();
                ap.paint();
            }
        } );

        /**Create a JButton as you normally would. This can include fonts, foreground, action listeners, whatever.
         * (intrinsic data).
         * However, locations (and other extrinsic data like that) will be ignored.
         */
        JButton jb = new JButton( "JButton" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "You clicked it at time=" + System.currentTimeMillis() );
            }
        } );

        /**That's all.*/

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new FlowLayout() );
//        jPanel.setLayout( new BoxLayout( jPanel, BoxLayout.Y_AXIS ) );
        jPanel.setBorder( BorderFactory.createTitledBorder( "hello" ) );
        jPanel.add( new JTextField( 8 ) );
        jPanel.add( new JButton( "mybutton" ) );
//        jPanel.add( new JLabel( "label2" ) );
        PhetGraphic panelComponent = PhetJComponent.newInstance( ap, jPanel );
        ap.addGraphic( panelComponent );

        JSpinner spinner = new JSpinner( new SpinnerNumberModel( 5, 0, 10, 1 ) );
        spinner.setBorder( BorderFactory.createTitledBorder( "Spinner" ) );
        PhetGraphic pj = PhetJComponent.newInstance( ap, spinner );
        ap.addGraphic( pj );
        pj.setLocation( 100, 100 );
    }

    public static void main( String[] args ) {
//        PhetJComponent.newInstance().start();
        new PhetJComponentContainerTest().start();
    }

    private void start() {
        swingTimerClock.start();
        frame.setVisible( true );
    }
}
