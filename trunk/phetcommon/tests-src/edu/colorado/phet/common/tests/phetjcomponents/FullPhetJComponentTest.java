package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 8, 2005
 * Time: 8:49:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FullPhetJComponentTest {
    private JFrame frame;
    private ApparatusPanel2 ap;
    private SwingTimerClock swingTimerClock;

    public FullPhetJComponentTest() throws IOException {

        frame = new JFrame( "Frame" );

        SwingTimerClock swingTimerClock = new SwingTimerClock( 1, 30 );
        ap = new ApparatusPanel2( swingTimerClock );
        ap.addGraphicsSetup( new BasicGraphicsSetup() );
        JButton jb = new JButton( "JButton" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "e = " + e );
            }
        } );
        PhetJComponent buttonPhetJ = new PhetJComponent( ap, jb );
        ap.addGraphic( buttonPhetJ );
        frame.setContentPane( ap );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        buttonPhetJ.setCursorHand();
        buttonPhetJ.scale( 2 );

        JTextField text = new JTextField( 10 );
        text.setBorder( BorderFactory.createTitledBorder( "TextField" ) );
        PhetJComponent textFieldPhetJ = new PhetJComponent( ap, text );
        ap.addGraphic( textFieldPhetJ );
        textFieldPhetJ.setLocation( 100, 300 );

        PhetJComponent checkBoxPhetJ = new PhetJComponent( ap, new JCheckBox( "Checkbox" ) );
        ap.addGraphic( checkBoxPhetJ );
        checkBoxPhetJ.setLocation( 300, 50 );

        JRadioButton jRadioButton = new JRadioButton( "Option A" );
        JRadioButton jRadioButton2 = new JRadioButton( "The other Option" );
        jRadioButton.setSelected( true );

        PhetJComponent radioButton1 = new PhetJComponent( ap, jRadioButton );
        PhetJComponent radioButton2 = new PhetJComponent( ap, jRadioButton2 );
        ap.addGraphic( radioButton1 );
        ap.addGraphic( radioButton2 );
        radioButton1.scale( 1.3 );
        radioButton2.scale( 1.4 );
        radioButton1.setLocation( 300, 200 );
        radioButton2.setLocation( 300, 200 + radioButton2.getHeight() );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( jRadioButton );
        buttonGroup.add( jRadioButton2 );

        buttonPhetJ.setLocation( 100, 100 );
        this.swingTimerClock = swingTimerClock;
        this.swingTimerClock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                ap.handleUserInput();
//                ap.paintImmediately( new Rectangle( 0, 0, ap.getWidth(), ap.getHeight() ) );
                ap.paint();
            }
        } );

        final JButton pressIt = new JButton( "Play",
                                             new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/java/media/Play24.gif" ) ) );
        pressIt.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        pressIt.setForeground( Color.blue );
        pressIt.setBackground( Color.green );

        final JButton pauseIt = new JButton( "Pause", new ImageIcon( ImageLoader.loadBufferedImage( "images/icons/java/media/Pause24.gif" ) ) );
        pauseIt.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        pauseIt.setForeground( Color.red );
        pauseIt.setBackground( Color.green );

        pauseIt.setEnabled( false );
        pressIt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pauseIt.setEnabled( true );
                pressIt.setEnabled( false );
            }
        } );
        pauseIt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pauseIt.setEnabled( false );
                pressIt.setEnabled( true );
            }
        } );

        PhetJComponent phetJComponent = new PhetJComponent( ap, pressIt );
        ap.addGraphic( phetJComponent );
        phetJComponent.setLocation( 300, 100 );


        PhetJComponent pauseComponent = new PhetJComponent( ap, pauseIt );
        ap.addGraphic( pauseComponent );
        pauseComponent.setLocation( phetJComponent.getX() + phetJComponent.getWidth() + 5, phetJComponent.getY() );


//        JSlider slider = new JSlider( 0, 100 );
//        JSlider slider = new JSlider( new DefaultBoundedRangeModel( 5, 0, 0, 100 ) );
        {
            final JSlider slider = new JSlider( new DefaultBoundedRangeModel() );
            slider.setBorder( BorderFactory.createTitledBorder( "Gravity (m/s^2)" ) );
            slider.setPaintTicks( true );
            slider.setPaintTrack( true );
            slider.setPaintLabels( true );

            slider.setMajorTickSpacing( 10 );
            slider.setMinorTickSpacing( 5 );
            Hashtable labels = new Hashtable();
            labels.put( new Integer( 9 ), new JLabel( "<html><italics>Earth</italics></html>" ) );
            labels.put( new Integer( 48 ), new JLabel( "phetland" ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    System.out.println( "value = " + value );
                }
            } );
            slider.setLabelTable( labels );
            PhetJComponent zslider = new PhetJComponent( ap, slider );
            ap.addGraphic( zslider );
            zslider.setLocation( 400, 300 );

        }
        {
//        zslider.rotate( Math.PI / 32, 400, 300 );
            final JSlider slider = new JSlider( new DefaultBoundedRangeModel() );
            slider.setBorder( BorderFactory.createTitledBorder( "PhetGraphics can be transformed" ) );
            slider.setPaintTicks( true );
            slider.setPaintTrack( true );
            slider.setPaintLabels( true );

            slider.setMajorTickSpacing( 10 );
            slider.setMinorTickSpacing( 5 );
            Hashtable labels = new Hashtable();
            labels.put( new Integer( 9 ), new JLabel( "Whoo" ) );
            labels.put( new Integer( 48 ), new JLabel( "lala" ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    System.out.println( "value = " + value );
                }
            } );
            slider.setLabelTable( labels );
            PhetJComponent zslider = new PhetJComponent( ap, slider );
            ap.addGraphic( zslider );
            zslider.setLocation( 100, 400 );
            zslider.rotate( Math.PI / 32 );
            zslider.scale( 1.3 );
        }

        JButton draggableButton = new JButton( "DraggableButton", new ImageIcon( ImageLoader.loadBufferedImage( "images/x-30.png" ) ) );
        final PhetJComponent phetJComponentDraggable = new PhetJComponent( ap, draggableButton );
        phetJComponentDraggable.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                phetJComponentDraggable.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        } );
        ap.addGraphic( phetJComponentDraggable );


        //composites seem like the way to approach JSpinners (and solve other problems.)
//        JSpinner jSpinner = new JSpinner( new SpinnerNumberModel( 5, 0, 10, 1 ) );
//        jSpinner.setBorder( BorderFactory.createTitledBorder( "Spin" ) );
//        BasicSpinnerUI basicSpinnerUI = new BasicSpinnerUI();
////        basicSpinnerUI.installUI( jSpinner );
//        jSpinner.setUI( basicSpinnerUI );
//        Component[] ch = jSpinner.getComponents();
//        for( int i = 0; i < ch.length; i++ ) {
//            JComponent component = (JComponent)ch[i];
//            PhetJComponent pjStar = new PhetJComponent( ap, component );
//            Point loc = component.getLocation();
//            ap.addGraphic( pjStar );
//            pjStar.setLocation( loc.x+i*10,loc.y );
//        }
//        PhetJComponent pj = new PhetJComponent( ap, jSpinner );
//        ap.addGraphic( pj );
//        pj.setLocation( 50, 350 );

//        ap.add( jSpinner );
//        jSpinner.reshape( 100, 100, jSpinner.getPreferredSize().width, jSpinner.getPreferredSize().height );

        RepaintDebugGraphic.enable( ap, swingTimerClock );
    }

    public static void main( String[] args ) throws IOException {
        new FullPhetJComponentTest().start();
    }

    private void start() {
        swingTimerClock.start();
        frame.setVisible( true );
    }
}
