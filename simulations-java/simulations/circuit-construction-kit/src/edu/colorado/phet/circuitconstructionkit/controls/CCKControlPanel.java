// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.controls;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.NonValidator;
import net.n3.nanoxml.StdXMLBuilder;
import net.n3.nanoxml.StdXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLWriter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.CircuitConstructionKitDCApplication;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.persistence.CircuitXML;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.BranchNodeFactory;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.DelayedRunner;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.servicemanager.InputStreamFileContents;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBox;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HelpPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 */
public class CCKControlPanel extends ControlPanel {
    private CCKModule module;
    private SimSharingJCheckBox seriesAmmeter;
    private JPanel advancedPanel;
    private boolean debugging = false;
    private static DelayedRunner runner = new DelayedRunner();

    public CCKControlPanel( final CCKModule module, Module m ) {
        this.module = module;
        JPanel filePanel = getFilePanel();
        if ( useAdvanced() ) {
            advancedPanel = new AdvancedControlPanel( module );
        }

        add( filePanel );
        if ( module.getParameters().isUseVisualControlPanel() && !CircuitConstructionKitDCApplication.isStanfordStudy() ) {
            add( makeVisualPanel() );
        }
        if ( !CircuitConstructionKitDCApplication.isStanfordStudy() ) {
            add( makeToolPanel() );
        }
        add( new SizeControlPanel( module ) );

        if ( debugging ) {
            JButton testLifelikeSchematic = new JButton( "Test Lifelike/Schematic" );
            testLifelikeSchematic.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( int i = 0; i < 100; i++ ) {
                        module.setLifelike( !module.isLifelike() );
                    }
                }
            } );
            add( testLifelikeSchematic );
        }

        if ( useAdvanced() && !CircuitConstructionKitDCApplication.isStanfordStudy() ) {
            add( advancedPanel );
        }

        if ( module.getParameters().showGrabBag() ) {
            addGrabBag();
        }

        if ( module.getParameters().getAllowDynamics() ) {
            addControl( new ResetDynamicsButton( module ) );
        }
        addResetAllButton( new Resettable() {
            public void reset() {
                module.resetAll();
            }
        } );
        super.addControlFullWidth( new HelpPanel( m ) );
    }

    private void add( JComponent component ) {
        addControlFullWidth( component );
    }

    private boolean useAdvanced() {
        return module.getParameters().getUseAdvancedControlPanel();
    }

    private void addGrabBag() {
        module.addGrabBag();
    }

    private JPanel makeToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout( new GridBagLayout() );
        GridBagConstraints lhs = new GridBagConstraints( 1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints rhs = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        try {
            ImageIcon voltIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/dvm-thumb.gif" ) );
            ImageIcon nonContactAmmIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/va-thumb.gif" ) );
            ImageIcon ammIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/ammeter60.gif" ) );

            final SimSharingJCheckBox voltmeter = new SimSharingJCheckBox( CCKSimSharing.UserComponents.voltmeterCheckBox, CCKResources.getString( "CCK3ControlPanel.VoltmeterCheckBox" ), false );
            voltmeter.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setVoltmeterVisible( voltmeter.isSelected() );

                }
            } );

            final SimSharingJCheckBox virtualAmmeter = new SimSharingJCheckBox( CCKSimSharing.UserComponents.nonContactAmmeterCheckBox, CCKResources.getString( "CCK3ControlPanel.NonContactAmmeterCheckBox" ), false );
            virtualAmmeter.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
                }
            } );

            seriesAmmeter = new SimSharingJCheckBox( CCKSimSharing.UserComponents.seriesAmmeterCheckBox, CCKResources.getString( "CCK3ControlPanel.AmmeterCheckBox" ), false );
            seriesAmmeter.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setSeriesAmmeterVisible( seriesAmmeter.isSelected() );
                }
            } );

            toolPanel.add( voltmeter, rhs );
            rhs.gridy++;

            toolPanel.add( seriesAmmeter, rhs );
            rhs.gridy++;

            if ( module.getParameters().useNonContactAmmeter() ) {
                toolPanel.add( virtualAmmeter, rhs );
                rhs.gridy++;
            }
            lhs.gridy = 0;
            toolPanel.add( new JLabel( voltIcon ), lhs );
            lhs.gridy++;
            toolPanel.add( new JLabel( ammIcon ), lhs );
            lhs.gridy++;
            if ( module.getParameters().useNonContactAmmeter() ) {
                toolPanel.add( new JLabel( nonContactAmmIcon ), lhs );
                lhs.gridy++;
            }

            if ( module.getParameters().getAllowDynamics() ) {
                try {
                    ImageIcon timerIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/stopwatch-thumb.png" ) );
                    final JCheckBox timerButton = new JCheckBox( CCKStrings.getString( "stopwatch" ), module.isStopwatchVisible() );
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

                ImageIcon chartIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/detector-thumb.gif" ) );
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

                ImageIcon voltageIcon = new ImageIcon( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/detector-thumb.gif" ) );
                toolPanel.add( new JLabel( chartIcon ), lhs );
                JButton voltageChartButton = new JButton( CCKStrings.getString( "add.voltage" ) );
                voltageChartButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        module.addVoltageChart();
                    }
                } );
                toolPanel.add( voltageChartButton, rhs );
            }
            toolPanel.setBorder( new CCKTitledBorder( CCKResources.getString( "CCK3ControlPanel.ToolsPanelBorder" ) ) );
            return toolPanel;
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private JPanel makeVisualPanel() {
        final JRadioButton lifelike = new JRadioButton( CCKResources.getString( "CCK3ControlPanel.LIfelikeRadioButton" ), module.isLifelike() );
        final JRadioButton schematic = new JRadioButton( CCKResources.getString( "CCK3ControlPanel.SchematicRadioButton" ), !module.isLifelike() );

        module.addBranchNodeFactoryListener( new BranchNodeFactory.Listener() {
            public void displayStyleChanged() {
                lifelike.setSelected( module.isLifelike() );
                schematic.setSelected( !module.isLifelike() );
            }
        } );

        lifelike.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingManager.sendButtonPressed( CCKSimSharing.UserComponents.lifelikeRadioButton );
                module.setLifelike( true );
            }
        } );
        schematic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingManager.sendButtonPressed( CCKSimSharing.UserComponents.schematicRadioButton );
                module.setLifelike( false );
            }
        } );

        JPanel visualizationPanel = new VerticalLayoutPanel();

        JPanel radioButtons = new JPanel();

        radioButtons.add( lifelike );
        if ( module.getParameters().allowSchematicMode() ) {
            radioButtons.add( schematic );
        }
        visualizationPanel.add( radioButtons );
        if ( module.getParameters().allowShowReadouts() ) {
            visualizationPanel.add( new ShowReadoutPanel( module ) );
        }
        visualizationPanel.setBorder( new CCKTitledBorder( CCKResources.getString( "CCK3ControlPanel.VisualPanelBorder" ) ) );
        return visualizationPanel;
    }

    private void load() throws IOException, XMLException {
        FileOpenService fos = null;
        try {
            fos = PhetServiceManager.getFileOpenService( module.getSimulationPanel() );
        }
        catch( UnavailableServiceException e ) {
            e.printStackTrace();
        }
        FileContents open = fos.openFileDialog( null, null );
        if ( open == null ) {
            return;
        }
        InputStreamReader isr = new InputStreamReader( open.getInputStream() );
        BufferedReader br = new BufferedReader( isr );
        String str = "";
        while ( br.ready() ) {
            String read = br.readLine();
            System.out.println( "read = " + read );
            str += read;
        }
        IXMLParser parser = new StdXMLParser();
        parser.setReader( new StdXMLReader( new StringReader( patchString( str ) ) ) );
        parser.setBuilder( new StdXMLBuilder() );
        parser.setValidator( new NonValidator() );

        IXMLElement parsed = (IXMLElement) parser.parse();
        Circuit circuit = CircuitXML.parseXML( parsed, module.getCircuitChangeListener(), module );
        if ( isOldVersionCCK( str ) ) {
            flipY( circuit );
            PhetOptionPane.showMessageDialog( this, "<html>The file you loaded is from an earlier version of this program, <br>and some " +
                                                    "parts of the circuit may be oriented incorrectly.  <br><br>Manually correct any problems, and be sure to save the new circuit.</html>" );
        }
        module.setCircuit( circuit );
    }

    private boolean isOldVersionCCK( String str ) {
        return str.indexOf( "edu.colorado.phet.cck3.circuit.Branch" ) >= 0 || str.indexOf( "edu.colorado.phet.cck3.circuit.components." ) >= 0;
    }

    private void flipY( Circuit circuit ) {
        for ( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            double y = j.getY();
            double offsetFrom5 = y - 5;
            double flipped = -offsetFrom5;
            double newY = 5 + flipped;
            j.setPosition( j.getX(), newY );
        }
    }

    private String patchString( String str ) {
        str = str.replaceAll( "edu.colorado.phet.cck3.circuit.Branch", "edu.colorado.phet.cck.model.components.Wire" );
        str = str.replaceAll( "edu.colorado.phet.cck3.circuit.components.", "edu.colorado.phet.cck.model.components." );
        str = str.replaceAll( "edu.colorado.phet.cck3.", "edu.colorado.phet.cck." );//grab bag
        return str;
    }

    private void save() throws IOException {
        FileSaveService fos = null;
        try {
            fos = PhetServiceManager.getFileSaveService( module.getSimulationPanel() );
        }
        catch( UnavailableServiceException e ) {
            e.printStackTrace();
        }

        XMLElement xml = CircuitXML.toXML( module.getCircuit() );
        StringWriter sw = new StringWriter();
        XMLWriter writer = new XMLWriter( sw );
        writer.write( xml );
        String circuitxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
        InputStream stream = new ByteArrayInputStream( circuitxml.getBytes() );
        FileContents data = new InputStreamFileContents( "circuitxml", stream );
        FileContents out = fos.saveFileDialog( null, null, data.getInputStream(), null );
        if ( out != null ) {
            System.out.println( "Saved to " + out.getName() + " as: " + out );
        }
    }

    private JPanel getFilePanel() {
        final JButton save = new JButton( CCKResources.getString( "CCK3ControlPanel.SaveButton" ) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingManager.sendButtonPressed( CCKSimSharing.UserComponents.saveButton );
                try {
                    save();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        JButton load = new JButton( CCKResources.getString( "CCK3ControlPanel.LoadButton" ) );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingManager.sendButtonPressed( CCKSimSharing.UserComponents.loadButton );
                try {
                    load();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JPanel filePanel = new PhetTitledPanel( CCKResources.getString( "CCK3ControlPanel.FilePanelBorder" ) );

        filePanel.add( save );
        filePanel.add( load );
        return filePanel;
    }

    public static class CCKTitledBorder extends PhetTitledBorder {
        public CCKTitledBorder( String title ) {
            super( title );
        }
    }

    static class AdvancedControlPanel extends AdvancedPanel {
        private CCKModule module;
        private JDialog dialog;
        private ResistivitySlider resistivitySlider;
        private JCheckBox hideElectrons;

        public AdvancedControlPanel( final CCKModule module ) {
            super( CCKResources.getString( "CCK3ControlPanel.Enable" ), CCKResources.getString( "CCK3ControlPanel.Disable" ) );
            setBorder( new PhetTitledBorder( CCKResources.getString( "CCK3ControlPanel.AdvancedPanelBorder" ) ) );
            this.module = module;
            resistivitySlider = new ResistivitySlider();

            addControl( resistivitySlider );
            resistivitySlider.getSlider().addMouseListener( new MouseAdapter() {
                @Override public void mousePressed( MouseEvent e ) {
                    SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.resistivitySlider, UserComponentTypes.slider, UserActions.startDrag, ParameterSet.parameterSet( ParameterKeys.value, resistivitySlider.getValue() ) );
                }

                @Override public void mouseReleased( MouseEvent e ) {
                    SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.resistivitySlider, UserComponentTypes.slider, UserActions.endDrag, ParameterSet.parameterSet( ParameterKeys.value, resistivitySlider.getValue() ) );
                    runner.terminate();
                }
            } );
            resistivitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    final double value = resistivitySlider.getValue();
                    if ( value <= 0 ) {
                        new RuntimeException( "Illegal resistivity: " + value ).printStackTrace();
                    }
                    runner.set( new Runnable() {
                        public void run() {
                            SimSharingManager.sendUserMessage( CCKSimSharing.UserComponents.resistivitySlider, UserComponentTypes.slider, UserActions.drag, ParameterSet.parameterSet( ParameterKeys.value, value ) );
                        }
                    } );
                    module.getResistivityManager().setResistivity( value );
                }
            } );
            module.getResistivityManager().resistivity.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    resistivitySlider.setValue( value );
                }
            } );

            hideElectrons = new SimSharingJCheckBox( CCKSimSharing.UserComponents.hideElectronsCheckBox, CCKResources.getString( "CCK3ControlPanel.HideElectronsCheckBox" ),
                                                     !module.isElectronsVisible() );
            hideElectrons.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setElectronsVisible( !hideElectrons.isSelected() );
                }
            } );
            addControl( hideElectrons );
            JButton close = new JButton( CCKResources.getString( "CCK3ControlPanel.CloseButton" ) );
            close.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.setVisible( false );
                }
            } );

            //When the simulation is reset, minimize the advanced control panel
            module.addResetListener( new VoidFunction0() {
                public void apply() {
                    hideAdvanced();
                }
            } );
        }

        /* Shows the advanced controls in a dialog. */

        public void showDialog() {
            if ( dialog == null ) {
                Window parent = SwingUtilities.getWindowAncestor( module.getSimulationPanel() );
                dialog = new JDialog( (Frame) parent, CCKResources.getString( "CCK3ControlPanel.AdvancedControlsDialog" ) );
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

        try {
            BasicService bs = PhetServiceManager.getBasicService();
            URL url = new URL( CCKResources.getString( "CCK3ControlPanel.PhETURL" ) );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( UnavailableServiceException e ) {
            e.printStackTrace();
        }
    }

}