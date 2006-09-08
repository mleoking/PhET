package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 8:49:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PhetJComponentContainerTest2 {
    private JFrame frame;
    private ApparatusPanel2 ap;
    private SwingTimerClock swingTimerClock;

    public PhetJComponentContainerTest2() {
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
//        jPanel.setLocation( 200,200);
        jPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
//        jPanel.setLayout( new BoxLayout( jPanel, BoxLayout.Y_AXIS ) );
        jPanel.setBorder( BorderFactory.createTitledBorder( "hello" ) );
        jPanel.add( new JTextField( 8 ) );
        JButton comp = new JButton( "mybutton" );
        comp.addComponentListener( new ComponentAdapter() {
            public void componentHidden( ComponentEvent e ) {
                System.out.println( "e = " + e );
            }

            public void componentMoved( ComponentEvent e ) {
                System.out.println( "e = " + e );
            }

            public void componentResized( ComponentEvent e ) {
                System.out.println( "e = " + e );
            }

            public void componentShown( ComponentEvent e ) {
                System.out.println( "e = " + e );
            }
        } );
        jPanel.add( comp );

        jPanel.invalidate();
        jPanel.validate();
        jPanel.doLayout();

        JWindow frame = new JWindow() {
            public void invalidate() {
//                super.invalidate();
            }

            public void paint( Graphics g ) {
                super.paint( g );
            }
        };
        frame.getContentPane().setLayout( null );
        frame.getContentPane().add( jPanel );
        System.out.println( "1comp = " + comp.getX() + ", " + comp.getY() );
        frame.setVisible( true );
//        frame.setSize(400,400 );

//        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        System.out.println( "2comp = " + comp.getX() + ", " + comp.getY() );
        jPanel.reshape( 0, 0, 100, 100 );
        System.out.println( "c3omp = " + comp.getX() + ", " + comp.getY() );
        frame.getContentPane().invalidate();
        frame.getContentPane().validate();
        frame.getContentPane().doLayout();
        System.out.println( "4comp = " + comp.getX() + ", " + comp.getY() );
//        frame.reshape( 0,0,800,800);
//        jPanel.add( new JLabel( "label2" ) );
//        jPanel.reshape( 0,0,100,100);
//        PhetGraphic panelComponent = PhetJComponent.newInstance( ap, jPanel );
//        ap.addGraphic( panelComponent );

//        JSpinner spinner = new JSpinner( new SpinnerNumberModel( 5, 0, 10, 1 ) );
//        spinner.setBorder( BorderFactory.createTitledBorder( "Spinner" ) );
//        PhetGraphic pj = PhetJComponent.newInstance( ap, spinner );
//        ap.addGraphic( pj );
//        pj.setLocation( 100, 100 );
        System.out.println( "c5omp = " + comp.getX() + ", " + comp.getY() );
    }

    public static void main( String[] args ) {
//        PhetJComponent.newInstance().start();
        new PhetJComponentContainerTest2().start();
    }

    private void start() {
        swingTimerClock.start();
//        frame.setVisible( true );
    }
}
