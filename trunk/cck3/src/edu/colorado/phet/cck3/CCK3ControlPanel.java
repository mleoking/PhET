/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.cck3.common.PhetSlider;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import net.n3.nanoxml.*;
import org.srr.localjnlp.ServiceSource;
import org.srr.localjnlp.local.InputStreamFileContents;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class CCK3ControlPanel extends JPanel {
    private CCK3Module module;
    private JCheckBox seriesAmmeter;
    private AdvancedControlPanel advancedControlPanel;

    public CCK3ControlPanel( final CCK3Module module ) {
        advancedControlPanel = new AdvancedControlPanel( module );
        JLabel titleLabel = new JLabel( new ImageIcon( getClass().getClassLoader().getResource( "images/phet-cck-small.gif" ) ) );
        titleLabel.setToolTipText( "<html>Click to visit<br>the PhET webpage.</html>" );
        titleLabel.setBorder( BorderFactory.createRaisedBevelBorder() );
        titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 2 ) );
        titleLabel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                showPhetPage();
            }
        } );
        this.module = module;
        JPanel filePanel = makeFilePanel();
        JPanel circuitPanel = makeCircuitPanel();
        JPanel visualPanel = makeVisualPanel();
        JPanel toolPanel = makeToolPanel();
        JPanel sizePanel = makeSizePanel();

        JButton jb = new JButton( "Local Help" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpImage();
            }
        } );
        JButton browserGIF = new JButton( "GIF Help" );
        browserGIF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpGIF();
            }
        } );

        HelpPanel hp = new HelpPanel( module );

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
                                                CCK3Module.BATTERY_DIMENSION.getLength(), CCK3Module.BATTERY_DIMENSION.getHeight(), module.getKirkhoffListener(), 0 );
                    module.getCircuit().addBranch( batt );
                    module.getCircuitGraphic().addGraphic( batt );
                    System.out.println( "i = " + i );
                }
                module.relayout( module.getCircuit().getBranches() );
            }
        } );
//                add( manyComp );

        this.setLayout( new BorderLayout() );
        JPanel titlePanel = new JPanel();
        titlePanel.add( titleLabel );
        add( titlePanel, BorderLayout.NORTH );
        JPanel controlPanel = new JPanel( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        controlPanel.add( filePanel, constraints );
        constraints.gridy++;
        controlPanel.add( circuitPanel, constraints );
        constraints.gridy++;
        controlPanel.add( visualPanel, constraints );
        constraints.gridy++;
        controlPanel.add( toolPanel, constraints );
        constraints.gridy++;
        controlPanel.add( sizePanel, constraints );
        constraints.gridy++;

        add( controlPanel, BorderLayout.CENTER );
        JPanel helpPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints helpPanelConstraints = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        helpPanel.add( hp, helpPanelConstraints );

        this.add( helpPanel, BorderLayout.SOUTH );
    }


    private JPanel makeSizePanel() {

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
//        zoomPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Size" ) );
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
//        return placeInPanel( "Size", zoomPanel, new Insets( 0, 10, 0, 10 ) );
        return placeInPanel( "Size", zoomPanel, BASIC_INSETS, GridBagConstraints.WEST );
    }

    private JPanel makeToolPanel() {

        //        add( virtualAmmeter );
        JPanel toolPanel = new JPanel();
        //        toolPanel.setLayout( new BoxLayout( toolPanel, BoxLayout.Y_AXIS ) );
        toolPanel.setLayout( new GridBagLayout() );
        GridBagConstraints lhs = new GridBagConstraints( 1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints rhs = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        ImageIcon voltIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/dvm-thumb.gif" ) );
        ImageIcon nonContactAmmIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/va-thumb.gif" ) );
        ImageIcon ammIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/ammeter60.gif" ) );

        final JCheckBox voltmeter = new JCheckBox( "Voltmeter", false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );

        final JCheckBox virtualAmmeter = new JCheckBox( "<html>Non-Contact<br>Ammeter</html>", false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );
        seriesAmmeter = new JCheckBox( "Ammeter(s)", false );
        seriesAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSeriesAmmeterVisible( seriesAmmeter.isSelected() );
            }
        } );

        toolPanel.add( voltmeter, rhs );
        rhs.gridy++;

        toolPanel.add( seriesAmmeter, rhs );
        rhs.gridy++;

        if( !module.isVirtualLabMode() ) {
            toolPanel.add( virtualAmmeter, rhs );
        }
        lhs.gridy = 0;
        toolPanel.add( new JLabel( voltIcon ), lhs );
        lhs.gridy++;
        toolPanel.add( new JLabel( ammIcon ), lhs );
        lhs.gridy++;
        if( !module.isVirtualLabMode() ) {
            toolPanel.add( new JLabel( nonContactAmmIcon ), lhs );
        }

//        toolPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Tools" ) );
//        return placeInPanel( "Tools", toolPanel, new Insets( 0, 10, 0, 10 ) );
        return placeInPanel( "Tools", toolPanel, BASIC_INSETS, GridBagConstraints.WEST );
    }

    private JPanel makeVisualPanel() {

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
//        visualizationPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Visual" ) );
        visualizationPanel.setLayout( new BoxLayout( visualizationPanel, BoxLayout.Y_AXIS ) );
        visualizationPanel.add( lifelike );
        visualizationPanel.add( schematic );
//        return placeInPanel( "Visual", visualizationPanel, new Insets( 0, 10, 0, 10 ) );
        return placeInPanel( "Visual", visualizationPanel, BASIC_INSETS, GridBagConstraints.WEST );
    }

    public class GridBagLayoutHelper extends GridBagConstraints {
        public GridBagLayoutHelper() {
        }
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
        String oh = mt.describe( ohmsLaws, "Ohm's Law Equations" );
        System.out.println( je );
        System.out.println( le );
        System.out.println( oh );

        JFrame readoutFrame = new JFrame( "Circuit Equations" );
        String plainText = je + "\n" + le + "\n" + oh + "\n";
        JTextArea jta = new JTextArea( plainText ) {
            protected void paintComponent( Graphics g ) {
                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };

//        String html = toHTML( plainText );
//        JEditorPane jta = new JEditorPane( "text/html", html ) {
//            protected void paintComponent( Graphics g ) {
//                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
//                super.paintComponent( g );
//            }
//        };
        jta.setEditable( false );
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if( Arrays.asList( names ).contains( "Courier New" ) ) {
            jta.setFont( new Font( "Courier New", Font.BOLD, 18 ) );
        }
        else {
            System.out.println( "Courier New font not supported." );
            jta.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        }

        readoutFrame.setContentPane( new JScrollPane( jta ) );
        readoutFrame.pack();
        GraphicsUtil.centerWindowOnScreen( readoutFrame );
        readoutFrame.setVisible( true );
    }


    private JPanel makeFilePanel() {
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
        //        filePanel.add( save );
        //        filePanel.add( load );

        JPanel filePanelContents = new JPanel();
        filePanelContents.add( save );
        filePanelContents.add( load );

        //        JPanel filePanel = createJPanel( "File", filePanelContents );
        //        filePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "File" ) );
        //        filePanel.setLayout( new GridBagLayout() );
        //                GraphicsUtil.addGridBagComponent( filePanel, Box.createRigidArea( new Dimension( ) ), );
        //        Insets insets = new Insets( 0, 10, 0, 10 );

        //        GraphicsUtil.addGridBagComponent( filePanel, filePanelContents, 0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER, insets );
        return placeInPanel( "File", filePanelContents, new Insets( 0, 10, 0, 10 ), GridBagConstraints.CENTER );
//        return placeInPanel( "File", filePanelContents, BASIC_INSETS, GridBagConstraints.WEST );
    }

    private static JPanel placeInPanel( String title, JPanel contents, Insets insets, int anchor ) {
        GridBagConstraints constraints = new GridBagConstraints( 0, 0, 1, 1, 0, 0, anchor, GridBagConstraints.NONE, insets, 10, 0 );
        JPanel panel = new JPanel( new GridBagLayout() );
//        panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), title ) );
        panel.setBorder( new TitledBorder( BorderFactory.createRaisedBevelBorder(), title ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        } );
        panel.add( Box.createRigidArea( new Dimension( 10, 0 ) ), constraints );
        constraints.gridx++;
        panel.add( contents, constraints );
        constraints.weightx = 1;
        constraints.gridx++;
        panel.add( Box.createRigidArea( new Dimension( 1, 0 ) ), constraints );
        return panel;
    }

    private JPanel makeCircuitPanel() {
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int answer = JOptionPane.showConfirmDialog( module.getApparatusPanel(), "Delete the entire circuit and start over?" );
                if( answer == JOptionPane.OK_OPTION ) {
                    module.clear();
                }
            }
        } );

        final JCheckBox showReadouts = new JCheckBox( "Show Values" );
        showReadouts.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean r = showReadouts.isSelected();
                module.getCircuitGraphic().setAllReadoutsVisible( r );
                module.getApparatusPanel().repaint();
            }
        } );
        JButton advanced = new JButton( "Advanced" );
        advanced.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                advancedControlPanel.showDialog();
            }
        } );
        JPanel circuitPanel = new JPanel();
        circuitPanel.setLayout( new BoxLayout( circuitPanel, BoxLayout.Y_AXIS ) );
//        circuitPanel.add( printKirkhoffsLaws );
        circuitPanel.add( advanced );
        circuitPanel.add( clear );
        if( !module.isVirtualLabMode() ) {
            circuitPanel.add( showReadouts );
        }
        return placeInPanel( "Circuit", circuitPanel, BASIC_INSETS, GridBagConstraints.WEST );
    }

    public boolean isSeriesAmmeterSelected() {
        return seriesAmmeter.isSelected();
    }

    static Insets BASIC_INSETS = new Insets( 0, 0, 0, 0 );

    class AdvancedControlPanel extends JPanel {
        private CCK3Module module;
        JDialog dialog;
        private PhetSlider resistivitySlider;
//        private PhetSlider batteryInternalResistance;

        public AdvancedControlPanel( final CCK3Module module ) {
            this.module = module;
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            final JCheckBox resistivityEnabled = new JCheckBox( "Resistivity", module.getResistivityManager().isEnabled() );
            resistivityEnabled.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setResistivityEnabled( resistivityEnabled.isSelected() );
                    resistivitySlider.setEnabled( resistivityEnabled.isSelected() );
                    if( resistivityEnabled.isSelected() ) {
                        resistivitySlider.requestSliderFocus();
                    }
                }
            } );
            add( resistivityEnabled );
            resistivitySlider = new PhetSlider( "Wire Resistivity", "resistance/length", 0, 1, module.getResistivityManager().getResistivity(), new DecimalFormat( "0.00" ) );
            resistivitySlider.setNumMajorTicks( 5 );
            resistivitySlider.setNumMinorTicksPerMajorTick( 5 );

            Font labelFont = new Font( "Lucida Sans", Font.PLAIN, 10 );
            JLabel lowLabel = new JLabel( "None" );
            lowLabel.setFont( labelFont );
            JLabel highLabel = new JLabel( "Lots" );
            highLabel.setFont( labelFont );

            resistivitySlider.setExtremumLabels( lowLabel, highLabel );
            add( resistivitySlider );
            resistivitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = resistivitySlider.getValue();
                    module.getResistivityManager().setResistivity( value );
                }
            } );
            resistivitySlider.setEnabled( resistivityEnabled.isSelected() );

            final JCheckBox internalResistanceEnabled = new JCheckBox( "Battery Internal Resistance Editable", module.isInternalResistanceOn() );
            internalResistanceEnabled.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setInternalResistanceOn( internalResistanceEnabled.isSelected() );
                }
            } );
            add( internalResistanceEnabled );

            JButton printKirkhoffsLaws = new JButton( "Show Equations" );
            printKirkhoffsLaws.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    printEm();
                }
            } );
            add( printKirkhoffsLaws );

            JButton close = new JButton( "Close" );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.setVisible( false );
                }
            } );
            add( Box.createRigidArea( new Dimension( 0, 30 ) ) );
            add( close );
        }

        public void showDialog() {
            if( dialog == null ) {
                Window parent = SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
                dialog = new JDialog( (Frame)parent, "Advanced Controls" );
                dialog.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
                dialog.setModal( false );
//                dialog.setUndecorated( true );
                dialog.setContentPane( this );
                SwingUtilities.updateComponentTreeUI( dialog );
                dialog.pack();
                GraphicsUtil.centerDialogInParent( dialog );
            }
            dialog.setVisible( true );
        }
    }

    private void showPhetPage() {
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
        try {
            URL url = new URL( "http://phet.colorado.edu" );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
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