/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import net.n3.nanoxml.*;
import org.srr.localjnlp.ServiceSource;
import org.srr.localjnlp.local.InputStreamFileContents;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

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

        JLabel label = ( new JLabel( new ImageIcon( getClass().getClassLoader().getResource( "images/phet-cck-small.gif" ) ) ) );
        label.setBorder( BorderFactory.createRaisedBevelBorder() );
        label.setBorder( BorderFactory.createLineBorder( Color.black, 2 ) );
        add( label );

        JButton save = new JButton( "Save" );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    save();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        JButton load = new JButton( "Load" );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    load();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
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

//        add( virtualAmmeter );
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout( new BoxLayout( toolPanel, BoxLayout.Y_AXIS ) );

        ImageIcon voltIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/dvm-thumb.gif" ) );
        ImageIcon ammIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/va-thumb.gif" ) );
        final JCheckBox voltmeter = new JCheckBox( "Voltmeter", false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );
//        voltmeter.add( new JLabel(voltIcon));
//        JPanel vm = new JPanel();
//        vm.setLayout( new GridBagLayout() );
//        GridBagConstraints gbc=new GridBagConstraints( );
//        gbc.gridwidth=2;
//        gbc.gridheight=1;
//        gbc.gridx=0;
//        gbc.gridy=1;
//        vm.add
//        vm.add( voltmeter );
//        vm.add( new JLabel( voltIcon ) );

        final JCheckBox virtualAmmeter = new JCheckBox( "Virtual Ammeter", false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );

//        JPanel am = new JPanel();
//        am.setLayout( new BoxLayout( am, BoxLayout.X_AXIS ) );
//        am.add( virtualAmmeter );
//        am.add( new JLabel( ammIcon ) );

//        toolPanel.add( vm );
//        toolPanel.add( new JLabel( ammIcon ) );
        toolPanel.add( virtualAmmeter );
//        toolPanel.add( am );
//        toolPanel.add( new JLabel( voltIcon ) );
        toolPanel.add( voltmeter );
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
        zoom.setSize( 50, zoom.getPreferredSize().height );
        zoom.setPreferredSize( new Dimension( 50, zoom.getPreferredSize().height ) );
//        add( zoom );

        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout( new BoxLayout( zoomPanel, BoxLayout.Y_AXIS ) );
        zoomPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Size" ) );
        ButtonGroup zoomGroup = new ButtonGroup();
        JRadioButton small = new JRadioButton( "Small" );
        small.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 2 );
            }
        } );
        JRadioButton medium = new JRadioButton( "Medium" );
        medium.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 1 );
            }
        } );
        JRadioButton large = new JRadioButton( "Large" );
        large.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( .5 );
            }
        } );
        medium.setSelected( true );
        zoomGroup.add( large );
        zoomGroup.add( medium );
        zoomGroup.add( small );
        zoomPanel.add( large );
        zoomPanel.add( medium );
        zoomPanel.add( small );
        add( zoomPanel );
        JButton jb = new JButton( "Local Help" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpImage();
            }
        } );
//        add( jb );
        JButton browserGIF = new JButton( "GIF Help" );
        browserGIF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpGIF();
            }
        } );
//        add( browserGIF );
        add( new JSeparator() );
        HelpPanel hp = new HelpPanel( module );
//        hp.setBorder( BorderFactory.createRaisedBevelBorder() );
        add( hp );

        JButton xml = new JButton( "Show XML" );
        xml.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().toXML();
            }
        } );
//        add( xml );

        JButton changeBunch = new JButton( "Change view 200x" );
        changeBunch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int IMAX = 200;
                for( int i = 0; i < IMAX; i++ ) {
                    System.out.println( "i = " + i + "/" + IMAX );
                    module.setLifelike( !module.getCircuitGraphic().isLifelike() );
                }
            }
        } );
//        add( changeBunch );
        JButton manyComp = new JButton( "add 100 batteries" );
        final Random rand = new Random( 0 );
        manyComp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < 100; i++ ) {
                    double x1 = rand.nextDouble() * 10;
                    double y1 = rand.nextDouble() * 10;
                    Battery batt = new Battery( new Point2D.Double( x1, y1 ), new ImmutableVector2D.Double( 1, 0 ),
                                                CCK3Module.BATTERY_DIMENSION.getLength(), CCK3Module.BATTERY_DIMENSION.getHeight(), module.getKirkhoffListener() );
                    module.getCircuit().addBranch( batt );
                    module.getCircuitGraphic().addGraphic( batt );
                    System.out.println( "i = " + i );
                }
                module.relayout( module.getCircuit().getBranches() );
            }
        } );
//        add( manyComp );
    }

    private void load() throws IOException, XMLException {
        ServiceSource ss = new ServiceSource();
        FileOpenService fos = ss.getFileOpenService( module.getApparatusPanel() );
        FileContents open = fos.openFileDialog( "Open Which CCK File?", new String[]{"cck"} );
        if( open == null ) {
            return;
        }
        InputStreamReader isr = new InputStreamReader( open.getInputStream() );
        BufferedReader br = new BufferedReader( isr );
        String str = "";
        while( br.ready() ) {
            String read = br.readLine();
            System.out.println( "read = " + read );
            str += read;
        }
        IXMLParser parser = new StdXMLParser();
        parser.setReader( new StdXMLReader( new StringReader( str ) ) );
        parser.setBuilder( new StdXMLBuilder() );
        parser.setValidator( new NonValidator() );

        IXMLElement parsed = (IXMLElement)parser.parse();
        Circuit circuit = Circuit.parseXML( parsed, module.getKirkhoffListener(), module );
        module.setCircuit( circuit );
    }

    private void save() throws IOException {
        ServiceSource ss = new ServiceSource();
        FileSaveService fos = ss.getFileSaveService( module.getApparatusPanel() );

        XMLElement xml = module.getCircuit().toXML();
        StringWriter sw = new StringWriter();
        XMLWriter writer = new XMLWriter( sw );
        writer.write( xml );
        String circuitxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
        InputStream stream = new ByteArrayInputStream( circuitxml.getBytes() );
        FileContents data = new InputStreamFileContents( "circuitxml", stream );
        FileContents out = fos.saveAsFileDialog( "circuit.cck", new String[]{"cck"}, data );
        System.out.println( "out = " + out );
    }

    public void showHelpGIF() {
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

    public void showHelpImage() {

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
            imageFrame.setResizable( false );
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

//        super.showMegaHelp();
//        ServiceSource ss = new ServiceSource();
//        BasicService bs = ss.getBasicService();
////        URL url=getClass().getClassLoader().getResource( "cck.pdf");
//        URL url = null;
//        try {
//            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck.pdf" );
////            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck-help.gif" );
//            System.out.println( "url = " + url );
//            bs.showDocument( url );
//        }
//        catch( MalformedURLException e ) {
//            e.printStackTrace();
//        }