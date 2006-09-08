/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.analysis.KirkhoffSolver;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.CCKStrings;
import edu.colorado.phet.cck3.grabbag.GrabBagButton;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.view.components.PhetSlider;
import edu.colorado.phet.common_cck.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.view.util.ImageLoader;
import edu.colorado.phet.common_cck.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.util.SwingUtils;
import net.n3.nanoxml.*;
import org.srr.localjnlp.ServiceSource;
import org.srr.localjnlp.local.InputStreamFileContents;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
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
public class CCK3ControlPanel extends ControlPanel {
    private CCKModule module;
    private JCheckBox seriesAmmeter;
    private AdvancedControlPanel advancedControlPanel;
    private GrabBagButton grabBagButton;
    private JPanel advancedPanel;

    public CCK3ControlPanel( final CCKModule module ) {
        super( module );
        setTitleVisible( false );
        advancedControlPanel = new AdvancedControlPanel( module );
        advancedControlPanel.setBorder( null );
        JLabel titleLabel = new JLabel( new ImageIcon( getClass().getClassLoader().getResource( "images/phet-cck-small.gif" ) ) );
        titleLabel.setToolTipText( SimStrings.get( "CCK3ControlPanel.PhETToolTip" ) );
        titleLabel.setBorder( BorderFactory.createRaisedBevelBorder() );
        titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 2 ) );
        titleLabel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                showPhetPage();
            }
        } );
        this.module = module;
        JPanel filePanel = makeFilePanel();
        if( useAdvanced() ) {
            advancedPanel = makeAdvancedPanel();
        }

        JPanel visualPanel = makeVisualPanel();
        JPanel toolPanel = makeToolPanel();
        JPanel sizePanel = makeSizePanel();

        JButton jb = new JButton( SimStrings.get( "CCK3ControlPanel.LocalHelpButton" ) );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpImage();
            }
        } );
        JButton browserGIF = new JButton( SimStrings.get( "CCK3ControlPanel.GIFHelpButton" ) );
        browserGIF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpGIF();
            }
        } );

        JButton xml = new JButton( SimStrings.get( "CCK3ControlPanel.ShowXMLButton" ) );
        xml.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().toXML();
            }
        } );

        JButton changeBunch = new JButton( SimStrings.get( "CCK3ControlPanel.ChangeViewButton" ) );
        changeBunch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int IMAX = 200;
                for( int i = 0; i < IMAX; i++ ) {
                    System.out.println( "i = " + i + "/" + IMAX );
                    module.setLifelike( !module.getCircuitGraphic().isLifelike() );
                }
            }
        } );
        JButton manyComp = new JButton( SimStrings.get( "CCK3ControlPanel.AddBatteriesButton" ) );
        final Random rand = new Random( 0 );
        manyComp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < 100; i++ ) {
                    double x1 = rand.nextDouble() * 10;
                    double y1 = rand.nextDouble() * 10;
                    Battery batt = new Battery( new Point2D.Double( x1, y1 ), new ImmutableVector2D.Double( 1, 0 ),
                                                CCKModule.BATTERY_DIMENSION.getLength(), CCKModule.BATTERY_DIMENSION.getHeight(), module.getKirkhoffListener(), 0, false );
                    module.getCircuit().addBranch( batt );
                    module.getCircuitGraphic().addGraphic( batt );
                    System.out.println( "i = " + i );
                }
                module.relayout( module.getCircuit().getBranches() );
            }
        } );

        JPanel titlePanel = new JPanel();
        titlePanel.add( titleLabel );
        add( titlePanel );
        add( filePanel );
        if( module.getParameters().isUseVisualControlPanel() ) {
            add( visualPanel );
        }
        add( toolPanel );
        add( sizePanel );

        if( useAdvanced() ) {
            add( advancedPanel );
        }

        if( module.getParameters().showGrabBag() ) {
            addGrabBag();
        }

        if( module.getParameters().getAllowDynamics() ) {
            addControl( Box.createVerticalStrut( 7 ) );
            addControl( new ResetDynamicsButton( module ) );
        }
        addControl( Box.createVerticalStrut( 7 ) );
//        addControl( Box.createVerticalBox( ));
//        addControl( Box.createVerticalGlue( ));
        super.addControlFullWidth( getHelpPanel() );
    }

    private void add( JComponent component ) {
        addControlFullWidth( component );
    }

    private boolean useAdvanced() {
        return module.getParameters().getUseAdvancedControlPanel();
    }

    private void addGrabBag() {
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/bag.gif" );
            grabBagButton = new GrabBagButton( module );
            grabBagButton.setIcon( new ImageIcon( image ) );
            module.getApparatusPanel().setLayout( null );
            module.getApparatusPanel().add( grabBagButton );

            module.getApparatusPanel().addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateButton();
                }

                public void componentShown( ComponentEvent e ) {
                    updateButton();
                }
            } );
            updateButton();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void updateButton() {
        int w = grabBagButton.getPreferredSize().width;
        int h = grabBagButton.getPreferredSize().height;

        int insetX = 15;
        int insetY = 15;
        int x = module.getApparatusPanel().getWidth() - insetX - w;
        int y = insetY;
        grabBagButton.reshape( x, y, w, h );
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

        JPanel zoomPanel = new VerticalLayoutPanel();
        ButtonGroup zoomGroup = new ButtonGroup();
        JRadioButton small = new JRadioButton( SimStrings.get( "CCK3ControlPanel.SmallRadioButton" ) );
        small.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 2 );
            }
        } );
        JRadioButton medium = new JRadioButton( SimStrings.get( "CCK3ControlPanel.MediumRadioButton" ) );
        medium.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 1 );
            }
        } );
        JRadioButton large = new JRadioButton( SimStrings.get( "CCK3ControlPanel.LargeRadioButton" ) );
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
        return addBorder( SimStrings.get( "CCK3ControlPanel.SizePanelBorder" ), zoomPanel );
    }

    private JPanel makeToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout( new GridBagLayout() );
        GridBagConstraints lhs = new GridBagConstraints( 1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints rhs = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        ImageIcon voltIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/dvm-thumb.gif" ) );
        ImageIcon nonContactAmmIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/va-thumb.gif" ) );
        ImageIcon ammIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/ammeter60.gif" ) );

        final JCheckBox voltmeter = new JCheckBox( SimStrings.get( "CCK3ControlPanel.VoltmeterCheckBox" ), false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );

        final JCheckBox virtualAmmeter = new JCheckBox( SimStrings.get( "CCK3ControlPanel.NonContactAmmeterCheckBox" ), false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );
        seriesAmmeter = new JCheckBox( SimStrings.get( "CCK3ControlPanel.AmmeterCheckBox" ), false );
        seriesAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSeriesAmmeterVisible( seriesAmmeter.isSelected() );
            }
        } );

        toolPanel.add( voltmeter, rhs );
        rhs.gridy++;

        toolPanel.add( seriesAmmeter, rhs );
        rhs.gridy++;

        if( module.getParameters().useNonContactAmmeter() ) {
            toolPanel.add( virtualAmmeter, rhs );
            rhs.gridy++;
        }
        lhs.gridy = 0;
        toolPanel.add( new JLabel( voltIcon ), lhs );
        lhs.gridy++;
        toolPanel.add( new JLabel( ammIcon ), lhs );
        lhs.gridy++;
        if( module.getParameters().useNonContactAmmeter() ) {
            toolPanel.add( new JLabel( nonContactAmmIcon ), lhs );
            lhs.gridy++;
        }

        if( module.getParameters().getAllowDynamics() ) {
            try {
                ImageIcon timerIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/stopwatch-thumb.png" ) );
                final JCheckBox timerButton = new JCheckBox( "Stopwatch", module.isStopwatchVisible() );
                timerButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        module.setStopwatchVisible( timerButton.isSelected() );
                    }
                } );
                toolPanel.add( new JLabel( timerIcon ), lhs );
                toolPanel.add( timerButton, rhs );
                lhs.gridy++;
                rhs.gridy++;
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            ImageIcon chartIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/detector-thumb.gif" ) );
            toolPanel.add( new JLabel( chartIcon ), lhs );
            JButton floatingChartButton = new JButton( CCKStrings.getString( "add.current.chart" ) );
            floatingChartButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.addCurrentChart();
                }
            } );
            toolPanel.add( floatingChartButton, rhs );
            rhs.gridy++;
            lhs.gridy++;

            ImageIcon voltageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/detector-thumb.gif" ) );
            toolPanel.add( new JLabel( chartIcon ), lhs );
            JButton voltageChartButton = new JButton( CCKStrings.getString( "add.voltage" ) );
            voltageChartButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.addVoltageChart();
                }
            } );
            toolPanel.add( voltageChartButton, rhs );
        }

        return addBorder( SimStrings.get( "CCK3ControlPanel.ToolsPanelBorder" ), toolPanel );
    }

    private JPanel makeVisualPanel() {
        final JCheckBox showReadouts = new JCheckBox( SimStrings.get( "CCK3ControlPanel.ShowValuesCheckBox" ) );
        showReadouts.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean r = showReadouts.isSelected();
                module.getCircuitGraphic().setAllReadoutsVisible( r );
                module.getApparatusPanel().repaint();
            }
        } );
        JRadioButton lifelike = new JRadioButton( SimStrings.get( "CCK3ControlPanel.LIfelikeRadioButton" ), true );
        JRadioButton schematic = new JRadioButton( SimStrings.get( "CCK3ControlPanel.SchematicRadioButton" ), false );
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

        JPanel visualizationPanel = new VerticalLayoutPanel();
        visualizationPanel.add( lifelike );
        if( module.getParameters().allowSchematicMode() ) {
            visualizationPanel.add( schematic );
        }
        if( module.getParameters().allowShowReadouts() ) {
            visualizationPanel.add( showReadouts );
        }
        return addBorder( SimStrings.get( "CCK3ControlPanel.VisualPanelBorder" ), visualizationPanel );
    }

    private void load() throws IOException, XMLException {
        ServiceSource ss = new ServiceSource();
        FileOpenService fos = ss.getFileOpenService( module.getApparatusPanel() );
        FileContents open = fos.openFileDialog( SimStrings.get( "CCK3ControlPanel.OpenFileDialog" ),
                                                new String[]{SimStrings.get( "CCK3ControlPanel.FileExtension" )} );
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
        FileContents out = fos.saveAsFileDialog( SimStrings.get( "CCK3ControlPanel.DefaultFileName" ),
                                                 new String[]{SimStrings.get( "CCK3ControlPanel.FileExtension" )}, data );
        System.out.println( "out = " + out );
    }

    public void showHelpGIF() {
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
        URL url = null;
        try {
            url = new URL( SimStrings.get( "CCK3ControlPanel.CCKHelpGifURL" ) );
            System.out.println( "url = " + url );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    public void showHelpImage() {
        final JFrame imageFrame = new JFrame();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/cck-help.gif" );

            JLabel label = new JLabel( new ImageIcon( image ) );
            imageFrame.setContentPane( label );
            imageFrame.pack();
            SwingUtils.centerWindowOnScreen( imageFrame );
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
            StringWriter sw = new StringWriter();
            e.printStackTrace( new PrintWriter( sw ) );
            JOptionPane.showMessageDialog( module.getApparatusPanel(), sw.getBuffer().toString(),
                                           SimStrings.get( "CCK3ControlPanel.ErrorLoadingHelpDialog" ), JOptionPane.ERROR_MESSAGE );
        }
    }

    private void zoom( double scale ) {
        module.setZoom( scale );
    }

    private static void printEm( CCKModule module ) {
        KirkhoffSolver ks = new KirkhoffSolver();
        Circuit circuit = module.getCircuit();
        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable( circuit );
        System.out.println( "mt = " + mt );

        KirkhoffSolver.Equation[] junctionEquations = ks.getJunctionEquations( circuit, mt );
        KirkhoffSolver.Equation[] loopEquations = ks.getLoopEquations( circuit, mt );
        KirkhoffSolver.Equation[] ohmsLaws = ks.getOhmsLaw( circuit, mt );

        String je = mt.describe( junctionEquations, SimStrings.get( "CCK3ControlPanel.JunctionEquations" ) );
        String le = mt.describe( loopEquations, SimStrings.get( "CCK3ControlPanel.LoopEquations" ) );
        String oh = mt.describe( ohmsLaws, SimStrings.get( "CCK3ControlPanel.OhmsLawEquations" ) );
        System.out.println( je );
        System.out.println( le );
        System.out.println( oh );

        JFrame readoutFrame = new JFrame( SimStrings.get( "CCK3ControlPanel.CircuitEquationsFrame" ) );
        String plainText = je + "\n" + le + "\n" + oh + "\n";
        JTextArea jta = new JTextArea( plainText ) {
            protected void paintComponent( Graphics g ) {
                SwingUtils.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
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
        SwingUtils.centerWindowOnScreen( readoutFrame );
        readoutFrame.setVisible( true );
    }


    private JPanel makeFilePanel() {
        JButton save = new JButton( SimStrings.get( "CCK3ControlPanel.SaveButton" ) );
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
        JButton load = new JButton( SimStrings.get( "CCK3ControlPanel.LoadButton" ) );
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

        JButton clear = new JButton( SimStrings.get( "CCK3ControlPanel.ClearButton" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean needsClearing = module.getCircuit().numBranches() != 0 || module.getCircuit().numJunctions() != 0;
                if( needsClearing ) {
                    int answer = JOptionPane.showConfirmDialog( module.getApparatusPanel(),
                                                                SimStrings.get( "CCK3ControlPanel.DeleteConfirm" ) );
                    if( answer == JOptionPane.OK_OPTION ) {
                        module.clear();
                    }
                }
            }
        } );
        JPanel filePanelContents = new JPanel();
        filePanelContents.add( clear );
        filePanelContents.add( save );
        filePanelContents.add( load );
        return addBorder( SimStrings.get( "CCK3ControlPanel.FilePanelBorder" ), filePanelContents );
    }

    private static JPanel addBorder( String title, JPanel contents ) {
        contents.setBorder( new TitledBorder( BorderFactory.createRaisedBevelBorder(), title ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        } );
        return contents;
    }

    private JPanel makeAdvancedPanel() {
        AdvancedControlPanel advancedControlPanel = new AdvancedControlPanel( module );
        return addBorder( SimStrings.get( "CCK3ControlPanel.AdvancedPanelBorder" ), advancedControlPanel );
    }

    public boolean isSeriesAmmeterSelected() {
        return seriesAmmeter.isSelected();
    }

    static class AdvancedControlPanel extends AdvancedPanel {
        private CCKModule module;
        private JDialog dialog;
        private PhetSlider resistivitySlider;
        private JCheckBox hideElectrons;

        public AdvancedControlPanel( final CCKModule module ) {
            super( SimStrings.get( "CCK3ControlPanel.Enable" ), SimStrings.get( "CCK3ControlPanel.Disable" ) );

            addListener( new Listener() {
                public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                    module.setAdvancedEnabled( false );
//                    System.out.println( "CCK3ControlPanel$AdvancedControlPanel.advancedPanelHidden" );
                }

                public void advancedPanelShown( AdvancedPanel advancedPanel ) {
                    module.setAdvancedEnabled( true );
//                    System.out.println( "CCK3ControlPanel$AdvancedControlPanel.advancedPanelShown" );
                }
            } );
            this.module = module;
//            setLayout( new GridBagLayout() );
            resistivitySlider = new PhetSlider( SimStrings.get( "CCK3ControlPanel.WireResistivitySlider" ),
                                                SimStrings.get( "CCK3ControlPanel.WireResistivitySliderMeasure" ),
                                                CCKModule.ResistivityManager.DEFAULT_RESISTIVITY, 1, module.getResistivityManager().getResistivity(),
                                                new DecimalFormat( "0.0000000" ) );
            resistivitySlider.setBorder( null );
            resistivitySlider.getTitleLabel().setFont( CCKLookAndFeel.getFont() );
            resistivitySlider.setNumMajorTicks( 5 );
            resistivitySlider.setNumMinorTicksPerMajorTick( 5 );

            Font labelFont = new Font( "Lucida Sans", Font.PLAIN, 10 );
            JLabel lowLabel = new JLabel( SimStrings.get( "CCK3ControlPanel.AlmostNoneLabel" ) );
            lowLabel.setFont( labelFont );
            JLabel highLabel = new JLabel( SimStrings.get( "CCK3ControlPanel.LotsLabel" ) );
            highLabel.setFont( labelFont );

            resistivitySlider.setExtremumLabels( lowLabel, highLabel );
            resistivitySlider.getTextField().setVisible( false );
            resistivitySlider.getUnitsReadout().setVisible( false );

            addControl( resistivitySlider );
            resistivitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = resistivitySlider.getValue();
                    if( value <= 0 ) {
                        new RuntimeException( "Illegal resistivity: " + value ).printStackTrace();
                    }
                    module.getResistivityManager().setResistivity( value );
                }
            } );

            JButton printKirkhoffsLaws = new JButton( SimStrings.get( "CCK3ControlPanel.ShowEquationsButton" ) );
            printKirkhoffsLaws.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    printEm( module );
                }
            } );
            addControl( printKirkhoffsLaws );

            hideElectrons = new JCheckBox( SimStrings.get( "CCK3ControlPanel.HideElectronsCheckBox" ),
                                           !module.isElectronsVisible() );
            hideElectrons.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setElectronsVisible( !hideElectrons.isSelected() );
                }
            } );
            addControl( hideElectrons );
            JButton close = new JButton( SimStrings.get( "CCK3ControlPanel.CloseButton" ) );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.setVisible( false );
                }
            } );
        }

        public void showDialog() {
            if( dialog == null ) {
                Window parent = SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
                dialog = new JDialog( (Frame)parent, SimStrings.get( "CCK3ControlPanel.AdvancedControlsDialog" ) );
                dialog.setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
                dialog.setModal( false );
                dialog.setContentPane( this );
                SwingUtilities.updateComponentTreeUI( dialog );
                dialog.pack();
                SwingUtils.centerDialogInParent( dialog );
            }
            dialog.setVisible( true );
        }
    }

    private void showPhetPage() {
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
        try {
            URL url = new URL( SimStrings.get( "CCK3ControlPanel.PhETURL" ) );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

}