/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import org.srr.localjnlp.ServiceSource;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class CCK3ControlPanel extends JPanel {
    private CCK3Module module;

    public CCK3ControlPanel( final CCK3Module module ) {
        this.module = module;
        JPanel filePanel = new JPanel();
        filePanel.setLayout( new BoxLayout( filePanel, BoxLayout.Y_AXIS ) );

        JButton save = new JButton( "Save" );
        JButton load = new JButton( "Load" );
        filePanel.add( save );
        filePanel.add( load );
        filePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "File" ) );
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clear();
            }
        } );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( filePanel );

        JButton printKirkhoffsLaws = new JButton( "Show Equations" );
        printKirkhoffsLaws.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                printEm();
            }
        } );
        final JCheckBox showReadouts = new JCheckBox( "Show Values" );
        showReadouts.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean r = showReadouts.isSelected();
                module.getCircuitGraphic().setReadoutMapVisible( r );
                module.getApparatusPanel().repaint();
            }
        } );
        JPanel circuitPanel = new JPanel();
        circuitPanel.setLayout( new BoxLayout( circuitPanel, BoxLayout.Y_AXIS ) );
        circuitPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Circuit" ) );
        circuitPanel.add( printKirkhoffsLaws );
        circuitPanel.add( clear );
        circuitPanel.add( showReadouts );
        add( circuitPanel );

        JRadioButton lifelike = new JRadioButton( "Lifelike", true );
        JRadioButton schematic = new JRadioButton( "Schematic", false );
        ButtonGroup bg = new ButtonGroup();
        bg.add( lifelike );
        bg.add( schematic );
        lifelike.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( true );
            }
        } );
        schematic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( false );
            }
        } );

        JPanel visualizationPanel = new JPanel();
        visualizationPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Visual" ) );
        visualizationPanel.setLayout( new BoxLayout( visualizationPanel, BoxLayout.Y_AXIS ) );
        visualizationPanel.add( lifelike );
        visualizationPanel.add( schematic );

        add( visualizationPanel );
        final JCheckBox virtualAmmeter = new JCheckBox( "Virtual Ammeter", false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );
//        add( virtualAmmeter );
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout( new BoxLayout( toolPanel, BoxLayout.Y_AXIS ) );

        final JCheckBox voltmeter = new JCheckBox( "Voltmeter", false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );

        toolPanel.add( voltmeter );
        toolPanel.add( virtualAmmeter );
        toolPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Tools" ) );
        add( toolPanel );


        final JSpinner zoom = new JSpinner( new SpinnerNumberModel( 1, .1, 10, .1 ) );
        zoom.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number value = (Number)zoom.getValue();
                double v = value.doubleValue();
                zoom( v );
            }
        } );
        add( zoom );
        JButton jb = new JButton( "Show Megahelp Image" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showImage();
            }
        } );
        add( jb );
        JButton browserGIF = new JButton( "Show MegaHelp GIF in browser" );
        browserGIF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpGIF();
            }
        } );
        add( browserGIF );
        HelpPanel hp = new HelpPanel( module );
        add( hp );
    }

    void showHelpGIF() {
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
//        URL url=getClass().getClassLoader().getResource( "cck.pdf");
        URL url = null;
        try {
//            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck.pdf" );
            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/cck-help.gif" );
            System.out.println( "url = " + url );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    private void download() {
        DownloadService ds = null;
        try {
            ds = (DownloadService)ServiceManager.lookup( "javax.jnlp.DownloadService" );
        }
        catch( UnavailableServiceException e ) {
            ds = null;
        }

        if( ds != null ) {

            try {

                // determine if a particular resource is cached
                final URL url =
                        new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/help.jar" );
//                        new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/help.jar" );
                boolean cached = ds.isResourceCached( url, "1.0" );
                System.out.println( "cached = " + cached );
                // remove the resource from the cache
                if( cached ) {
                    System.out.println( "Removing." );
                    ds.removeResource( url, "1.0" );
                }
                // reload the resource into the cache

                final DownloadServiceListener dsl = ds.getDefaultProgressWindow();
                DownloadServiceListener mydsl = new DownloadServiceListener() {
                    public void downloadFailed( URL url, String s ) {
                        dsl.downloadFailed( url, s );
                        System.out.println( "Failed." );
                    }

                    public void progress( URL url, String s, long l, long l1, int i ) {
                        dsl.progress( url, s, l, l1, i );
                        System.out.println( "progress" );
                    }

                    public void upgradingArchive( URL url, String s, int i, int i1 ) {
                        dsl.upgradingArchive( url, s, i, i1 );
                        System.out.println( "upgrading" );
                    }

                    public void validating( URL url, String s, long l, long l1, int i ) {
                        dsl.validating( url, s, l, l1, i );
                        System.out.println( "validating." );
                    }
                };
                System.out.println( "Calling loadResource" );
                ds.loadResource( url, "1.0", mydsl );
                System.out.println( "Finished calling loadresource." );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
//        }
    }

    private void showImage() {

        /*
        Tests for loading files.*/
//        URL url = getClass().getClassLoader().getResource( "cck1.gif" );
//        if( url == null ) {
//            System.out.println( "Null url for cck1.gif" );
//            download();
//            System.out.println( "Finished call to download." );
////            return;
//            URL url2 = getClass().getClassLoader().getResource( "cck1.gif" );
//            if( url2 == null ) {
//                System.out.println( "URL is STILL NULL." );
//                return;
//            }
//        }

        final JFrame imageFrame = new JFrame();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/cck-help.gif" );
            JLabel label = new JLabel( new ImageIcon( image ) );
            imageFrame.setContentPane( label );
            imageFrame.pack();
            GraphicsUtil.centerWindowOnScreen( imageFrame );
            imageFrame.setVisible( true );
            imageFrame.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    imageFrame.dispose();
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void zoom( double scale ) {
        module.setZoom( scale );
    }

    private void printEm() {
        KirkhoffSolver ks = new KirkhoffSolver();
        Circuit circuit = module.getCircuit();
        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable( circuit );
        System.out.println( "mt = " + mt );

        KirkhoffSolver.Equation[] junctionEquations = ks.getJunctionEquations( circuit, mt );
        KirkhoffSolver.Equation[] loopEquations = ks.getLoopEquations( circuit, mt );
        KirkhoffSolver.Equation[] ohmsLaws = ks.getOhmsLaw( circuit, mt );

        String je = mt.describe( junctionEquations, "Junction Equations" );
        String le = mt.describe( loopEquations, "Loop Equations" );
        String oh = mt.describe( ohmsLaws, "Ohms Law Equations" );
        System.out.println( je );
        System.out.println( le );
        System.out.println( oh );

        JFrame readoutFrame = new JFrame();
        JTextArea jta = new JTextArea( je + "\n" + le + "\n" + oh + "\n" ) {
            protected void paintComponent( Graphics g ) {
                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
        jta.setEditable( false );
        jta.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        readoutFrame.setContentPane( jta );
        readoutFrame.pack();
        GraphicsUtil.centerWindowOnScreen( readoutFrame );
        readoutFrame.setVisible( true );
    }
}
