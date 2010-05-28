/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.greenhouse.model.GreenhouseEffectModel;
import edu.colorado.phet.greenhouse.util.ModelViewTx1D;

// TODO: Copied from GreenhouseControlPanel, needs to be cleaned and commented.

/**
 * Control panel for the Greenhouse Effect module.
 */
public class GreenhouseEffectControlPanel extends ControlPanel implements Resettable {

    private static Color adjustableGGColor = Color.black;
    private static Color iceAgeColor = new Color( 0, 28, 229 );
    private static Color preIndRevColor = new Color( 176, 0, 219 );
    private static Color todayColor = new Color( 11, 142, 0 );
    private static Color panelForeground = Color.black;

    private GreenhouseEffectModel model;
    private ModelSlider greenhouseGasConcentrationControl;
    String[] iceAgeConcentrations = new String[]{" ?",
            "200 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            " ?",
            " ?"};

    String[] preIndRevConcentrations = new String[]{
            "70% " + GreenhouseResources.getString( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "280 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "730 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPBAbreviation" ),
            "270 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPBAbreviation" )};

    String[] todayConcentrations = new String[]{
            "70% " + GreenhouseResources.getString( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "370 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "1843 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPBAbreviation" ),
            "317 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPBAbreviation" )};

    String[] venusConcentrations = new String[]{
            "",
            "96.5% ",
            "",
            ""};
    private GreenhouseCompositionPane iceAgeCompositionPane = new GreenhouseCompositionPane( iceAgeConcentrations );
    private GreenhouseCompositionPane seventeenFiftyCompositionPane = new GreenhouseCompositionPane( preIndRevConcentrations );
    private GreenhouseCompositionPane todayCompositionPane = new GreenhouseCompositionPane( todayConcentrations );
    private GreenhouseCompositionPane venusCompositionPane = new GreenhouseCompositionPane( venusConcentrations );
    private JPanel adjustableCompositionPane = new JPanel();
    private GreenhouseEffectModule module;
    private JSpinner cloudsSpinner;
    private AtmosphereSelectionPane atmosphereSelectionPane;
    private JCheckBox allPhotonsCB;
    private JCheckBox thermometerCB;
    private JRadioButton fahrenheitRB;
    private JRadioButton celsiusRB;

    /**
     * Constructor
     * @param model TODO
     *
     */
    public GreenhouseEffectControlPanel(final GreenhouseEffectModel model) {
    	
    	this.model = model;

        //--------------------------------------------------------------------------------------------------
        // Create the controls
        //--------------------------------------------------------------------------------------------------

        // Incident photons from the sun
        final SliderWithReadout sunRateControl = new SliderWithReadout( GreenhouseResources.getString( "GreenhouseControlPanel.SunRateSlider" ),
                                                                        "", 0,
                                                                        GreenhouseConfig.maxIncomingRate,
                                                                        GreenhouseConfig.defaultSunPhotonProductionRate );
        sunRateControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                model.setSunPhotonProductionRate( sunRateControl.getModelValue() );
            }
        } );

        // Earth emissivity
        final SliderWithReadout earthEmissivityControl = new SliderWithReadout( GreenhouseResources.getString( "GreenhouseControlPanel.EarthEmissivitySlider" ),
                                                                                "", 0,
                                                                                GreenhouseConfig.maxEarthEmissivity,
                                                                                GreenhouseConfig.defaultEarthEmissivity );
        earthEmissivityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                model.setEarthEmissivity( earthEmissivityControl.getModelValue() );
            }
        } );

        // Greenhouse gas concentration
        greenhouseGasConcentrationControl = new ModelSlider( "",
                                                             0.0,
                                                             GreenhouseConfig.maxGreenhouseGasConcentration,
                                                             GreenhouseConfig.defaultGreenhouseGasConcentration );
        greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
        greenhouseGasConcentrationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                model.setGreenhouseGasConcentration( greenhouseGasConcentrationControl.getModelValue() );
            }
        } );
        greenhouseGasConcentrationControl.addSliderListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                atmosphereSelectionPane.setAdjustableAtmosphere();
            }
        } );

        // Add/remove clouds
        JPanel cloudPanel = new JPanel();
        int min = 0;
        int max = GreenhouseEffectModel.getMaxNumClouds();
        int step = 1;
        int initValue = 0;
        SpinnerModel cloudSpinnerModel = new SpinnerNumberModel( initValue, min, max, step );
        cloudsSpinner = new JSpinner( cloudSpinnerModel );
        cloudsSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSpinner spinner = (JSpinner) e.getSource();
                int i = ( (Integer) spinner.getValue() ).intValue();
                if (i > model.getNumClouds()){
                	model.addCloud();
                }
                else if (i < model.getNumClouds()){
                	model.removeCloud();
                }
            }
        } );
        JFormattedTextField tf = ( (JSpinner.DefaultEditor) cloudsSpinner.getEditor() ).getTextField();
        tf.setEditable( false );
        tf.setBackground( Color.white );

        cloudPanel.add( cloudsSpinner );
        cloudPanel.add( new JLabel( GreenhouseResources.getString( "GreenhouseControlPanel.NumberOfCloudsLabel" ) ) );

        // Show/hide thermometer
        thermometerCB = new JCheckBox( GreenhouseResources.getString( "GreenhouseControlPanel.ThermometerCheckbox" ) );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                module.thermometerEnabled( thermometerCB.isSelected() );
                fahrenheitRB.setEnabled( thermometerCB.isSelected() );
                celsiusRB.setEnabled( thermometerCB.isSelected() );
            }
        } );

        // Set temperature readout to fahreheit or celsius
        ButtonGroup tempUnitsBG = new ButtonGroup();
        fahrenheitRB = new JRadioButton( GreenhouseResources.getString( "GreenhouseControlPanel.Faherenheit" ) );
        celsiusRB = new JRadioButton( GreenhouseResources.getString( "GreenhouseControlPanel.Celsius" ) );
        tempUnitsBG.add( fahrenheitRB );
        tempUnitsBG.add( celsiusRB );
        TemperatureUnitsSetter temperatureUnitsSetter = new TemperatureUnitsSetter();
        fahrenheitRB.addActionListener( temperatureUnitsSetter );
        celsiusRB.addActionListener( temperatureUnitsSetter );
        fahrenheitRB.setSelected( true );

        // Ratio of photons to see
        allPhotonsCB = new JCheckBox( GreenhouseResources.getString( "GreenhouseControlPanel.ViewPhotonsCheckbox" ) );
        allPhotonsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( allPhotonsCB.isSelected() ) {
//                    module.setVisiblePhotonRatio( 1.0 );
                }
                else {
//                    module.setVisiblePhotonRatio( 0.1 );
                }
            }
        } );

        // Atmosphere selection
        atmosphereSelectionPane = new AtmosphereSelectionPane();

        // Reset button
        JButton resetBtn = new ResetAllButton( this, PhetApplication.getInstance().getPhetFrame() );

        setDefaultConditions();

        //--------------------------------------------------------------------------------------------------
        // Lay out the controls
        //--------------------------------------------------------------------------------------------------

//        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
//                                                         GridBagConstraints.CENTER,
//                                                         GridBagConstraints.HORIZONTAL,
//                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth( new GreenhouseLegend() );

        // Greenhouse gas concentrations
        {
            JPanel panel = new JPanel();
            panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GreenhouseResources.getString( "GreenhouseControlPanel.GasConcentrationSlider" ) ) );
            panel.add( greenhouseGasConcentrationControl );
            addControlFullWidth( panel );
        }

        // Options panel
        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        optionsPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GreenhouseResources.getString( "GreenhouseControlPanel.Options" ) ) );
        {
            Insets insetsA = new Insets( 0, 15, 0, 15 );
            Insets insetsB = new Insets( 0, 2, 0, 2 );
            GridBagConstraints optsGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                 GridBagConstraints.CENTER,
                                                                 GridBagConstraints.NONE,
                                                                 insetsA, 0, 0 );
            optsGbc.gridwidth = 2;
            optionsPanel.add( cloudPanel, optsGbc );

            optsGbc.anchor = GridBagConstraints.WEST;
            optsGbc.gridy++;
            optionsPanel.add( thermometerCB, optsGbc );

            optsGbc.gridwidth = 1;
            optsGbc.gridheight = 1;
            optsGbc.gridy++;
            optsGbc.anchor = GridBagConstraints.EAST;
            optsGbc.insets = insetsB;
            optionsPanel.add( fahrenheitRB, optsGbc );
            optsGbc.gridx = 1;
            optsGbc.anchor = GridBagConstraints.WEST;
            optionsPanel.add( celsiusRB, optsGbc );

            optsGbc.gridwidth = 2;
            optsGbc.gridheight = 1;
            optsGbc.gridx = 0;
            optsGbc.gridy++;
            optsGbc.anchor = GridBagConstraints.WEST;
            optsGbc.insets = insetsA;
            optionsPanel.add( allPhotonsCB, optsGbc );
        }

//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        add( atmosphereSelectionPane, gbc );
//        gbc.anchor = GridBagConstraints.WEST;
        addControlFullWidth( atmosphereSelectionPane );
//        add( optionsPanel, gbc );
        addControlFullWidth( optionsPanel );

//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets( 15, 15, 0, 15 );
//        add( resetBtn, gbc );
        addControl( resetBtn );
    }

    private void setDefaultConditions() {
        cloudsSpinner.setValue( new Integer( 0 ) );

        // todo: HACK!!! make the control panel listen to the module for this?
        allPhotonsCB.setSelected( true );
//        module.setVisiblePhotonRatio( 1.0 );

        // todo: HACK!!! make the control panel listen to the module for this?
        thermometerCB.setSelected( true );
        fahrenheitRB.setEnabled( thermometerCB.isSelected() );
        celsiusRB.setEnabled( thermometerCB.isSelected() );
        fahrenheitRB.setSelected( true );
        updateTemperatureUnits();
    }

    public void reset() {
        module.reset();
        atmosphereSelectionPane.reset();
        setDefaultConditions();
//        module.thermometerEnabled( thermometerCB.isSelected() );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class AtmosphereSelectionPane extends JPanel {
        private JRadioButton todayGGRB;
        private JRadioButton adjustableGGRB;

        AtmosphereSelectionPane() {

            adjustableGGRB = new JRadioButton();
            adjustableGGRB.setAction( pickAdjustableGG );
            adjustableGGRB.setText( GreenhouseResources.getString( "GreenhouseControlPanel.Adjustable" ) );
            adjustableGGRB.setForeground( adjustableGGColor );

            JRadioButton iceAgeGGRB = new JRadioButton();
            iceAgeGGRB.setAction( pickIceAgeGG );
            iceAgeGGRB.setText( GreenhouseResources.getString( "GreenhouseControlPanel.IceAgeLabel" ) );
            iceAgeGGRB.setForeground( iceAgeColor );

            JRadioButton preIndRevGGRB = new JRadioButton();
            preIndRevGGRB.setAction( pickPreIndRevGG );
            preIndRevGGRB.setText( GreenhouseResources.getString( "GreenhouseControlPanel.PreIndustrialLabel" ) );
            preIndRevGGRB.setForeground( preIndRevColor );

            todayGGRB = new JRadioButton();
            todayGGRB.setAction( pickTodayGG );
            todayGGRB.setText( GreenhouseResources.getString( "GreenhouseControlPanel.TodayLabel" ) );
            todayGGRB.setForeground( todayColor );

//            JRadioButton venusGGRB = new JRadioButton();
//            venusGGRB.setAction( pickVenusGG );
//            venusGGRB.setText( GreenhouseResources.getString( "GreenhouseControlPanel.Venus" ) );
//            venusGGRB.setForeground( venusColor );

            ButtonGroup ggBG = new ButtonGroup();
            ggBG.add( adjustableGGRB );
            ggBG.add( iceAgeGGRB );
            ggBG.add( preIndRevGGRB );
            ggBG.add( todayGGRB );
//            ggBG.add( venusGGRB );

            iceAgeCompositionPane.setVisible( false );
            seventeenFiftyCompositionPane.setVisible( false );
            todayCompositionPane.setVisible( false );
            venusCompositionPane.setVisible( false );

            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 5, 0, 5 ), 0, 0 );
            this.add( adjustableGGRB, gbc );
            this.add( iceAgeGGRB, gbc );
            this.add( iceAgeCompositionPane, gbc );
            this.add( preIndRevGGRB, gbc );
            this.add( seventeenFiftyCompositionPane, gbc );
            this.add( todayGGRB, gbc );
            this.add( todayCompositionPane, gbc );
//            this.add( venusGGRB, gbc );
            this.add( venusCompositionPane, gbc );
            this.add( adjustableCompositionPane, gbc );

            TitledBorder titledBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GreenhouseResources.getString( "GreenhouseControlPanel.TimePeriodBorderLabel" ) );
            titledBorder.setTitleColor( panelForeground );
//            titledBorder.setTitleJustification( TitledBorder.LEFT );
            this.setBorder( titledBorder );

            setDefaultConditions();
        }

        private void setAdjustableAtmosphere() {
//            GreenhouseEffectControlPanel.this.module.setVirginEarth();
//            greenhouseGasConcentrationControl.setEnabled( true );
//            module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            hideConcentrations();
            adjustableCompositionPane.setPreferredSize( iceAgeCompositionPane.getPreferredSize() );
            adjustableCompositionPane.setVisible( true );
            adjustableGGRB.setSelected( true );
        }

        public void reset() {
            setDefaultConditions();
        }

        private void setDefaultConditions() {
            // Default conditions. Note that this very messy way of setting the startup condition isn't good, but it
            // works. This is a duplicate of the code in pickTodayGG.actionPerformed(), without the line telling
            // the module to setToday(). The other key line of code in setting initial conditions is at the
            // end of Zoomer.run() in the BaseGreenhouseModule class.
            todayGGRB.setSelected( true );
            greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
//            greenhouseGasConcentrationControl.setEnabled( false );
//            module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            hideConcentrations();
            todayCompositionPane.setVisible( true );
        }

        private void hideConcentrations() {
            iceAgeCompositionPane.setVisible( false );
            seventeenFiftyCompositionPane.setVisible( false );
            todayCompositionPane.setVisible( false );
            venusCompositionPane.setVisible( false );
            adjustableCompositionPane.setVisible( false );
        }

        //----------------------------------------------------------------
        // Actions that set the simulation for different times and atmospheric scenarios
        //----------------------------------------------------------------

        AbstractAction pickAdjustableGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                setAdjustableAtmosphere();
            }
        };

        private AbstractAction pickIceAgeGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
//                greenhouseGasConcentrationControl.setEnabled( false );
//                GreenhouseEffectControlPanel.this.module.setIceAge();
//                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                iceAgeCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickPreIndRevGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentration1750 );
//                greenhouseGasConcentrationControl.setEnabled( false );
//                GreenhouseEffectControlPanel.this.module.setPreIndRev();
//                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                seventeenFiftyCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickTodayGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
//                greenhouseGasConcentrationControl.setEnabled( false );
//                GreenhouseEffectControlPanel.this.module.setToday();
//                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                todayCompositionPane.setVisible( true );
            }
        };
    }


    /**
     * Pane that shows concentration of various greenhouse gas components
     */
    private class GreenhouseCompositionPane extends JPanel {

        JTextField h2oTF = new JTextField( 10 );
        JTextField co2TF = new JTextField( 10 );
        JTextField ch4TF = new JTextField( 10 );
        JTextField n2oTF = new JTextField( 10 );

        GreenhouseCompositionPane( String[] concentrations ) {
            this();
            setConcentrations( concentrations );
        }

        GreenhouseCompositionPane() {
            String[] labels = new String[]{
                    GreenhouseResources.getString( "GreenhouseControlPanel.H2OLabel" ),
                    GreenhouseResources.getString( "GreenhouseControlPanel.CO2Label" ),
                    GreenhouseResources.getString( "GreenhouseControlPanel.CH4Label" ),
                    GreenhouseResources.getString( "GreenhouseControlPanel.N2OLabel" )
            };
            JTextField[] concentrations = new JTextField[]{
                    h2oTF,
                    co2TF,
                    ch4TF,
                    n2oTF
            };
            h2oTF.setEditable( false );
            co2TF.setEditable( false );
            ch4TF.setEditable( false );
            n2oTF.setEditable( false );

            // titled border
            String title = GreenhouseResources.getString( "GreenhouseControlPanel.GreenhouseGasBorderLabel" );
            TitledBorder titledBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title );
            titledBorder.setTitleColor( panelForeground );
            setBorder( titledBorder );

            // grid of labels and concentrations
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            setLayout( layout );
            for ( int row = 0; row < concentrations.length; row++ ) {
                int col = 0;
                layout.addAnchoredComponent( new JLabel( labels[row] ), row, col++, GridBagConstraints.EAST );
                layout.addAnchoredComponent( concentrations[row], row, col++, GridBagConstraints.WEST );
            }
        }

        public void setConcentrations( String[] concentrations ) {
            h2oTF.setText( concentrations[0] );
            co2TF.setText( concentrations[1] );
            ch4TF.setText( concentrations[2] );
            n2oTF.setText( concentrations[3] );
        }

    }

    /**
     *
     */
    class ModelSlider extends JPanel {
        private JSlider slider;
        private ModelViewTx1D tx;

        ModelSlider( String title, double minModelValue, double maxModelValue, double defaultModelValue ) {
            this.setLayout( new GridBagLayout() );
            putClientProperty( "JSlider.isFilled", Boolean.TRUE );
            tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                    0, 10000 );
            slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  10000,
                                  (int) tx.modelToView( defaultModelValue ) );
            slider.setPaintLabels( true );
            int rowIdx = 0;
            try {
                SwingUtils.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE, GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Make the colored tick marks on the slider
            Font defaultFont = new PhetFont();
            Font tickFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 6 );
            Hashtable labelTable = new Hashtable();
            JLabel iceAgeTick = new JLabel( "|" );
            iceAgeTick.setFont( tickFont );
            iceAgeTick.setForeground( iceAgeColor );
            JLabel preIndRevTick = new JLabel( "|" );
            preIndRevTick.setFont( tickFont );
            preIndRevTick.setForeground( preIndRevColor );
            JLabel todayTick = new JLabel( "|" );
            todayTick.setFont( tickFont );
            todayTick.setForeground( todayColor );
            labelTable.put( new Integer( (int) tx.modelToView( GreenhouseConfig.greenhouseGasConcentrationIceAge ) ),
                            iceAgeTick );
            labelTable.put( new Integer( (int) tx.modelToView( GreenhouseConfig.greenhouseGasConcentrationToday ) ),
                            todayTick );
            labelTable.put( new Integer( (int) tx.modelToView( GreenhouseConfig.greenhouseGasConcentration1750 ) ),
                            preIndRevTick );

            JLabel noneTick = new JLabel( GreenhouseResources.getString( "GreenhouseControlPanel.None" ) );
            JLabel lotsTick = new JLabel( GreenhouseResources.getString( "GreenhouseControlPanel.Lots" ) );
            labelTable.put( new Integer( (int) tx.modelToView( maxModelValue ) ),
                            lotsTick );
            labelTable.put( new Integer( (int) tx.modelToView( minModelValue ) ),
                            noneTick );

            slider.setLabelTable( labelTable );
            slider.setMajorTickSpacing( (int) ( tx.modelToView( maxModelValue ) - tx.modelToView( minModelValue ) ) );
            slider.setPaintTicks( true );
        }

        void setMaxValue( double value ) {
            int max = (int) tx.modelToView( value );
            slider.setMaximum( max );
        }

        double getModelValue() {
            return tx.viewToModel( slider.getValue() );
        }

        void addChangeListener( ChangeListener changeListener ) {
            slider.addChangeListener( changeListener );
        }

        void setModelValue( double value ) {
            slider.setValue( (int) tx.modelToView( value ) );
            putClientProperty( "JSlider.isFilled", Boolean.TRUE );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            slider.setEnabled( enabled );
        }

        void addTick( double value, Color color ) {
            JLabel label = new JLabel( "|" );
            label.setForeground( color );
            Integer loc = new Integer( (int) tx.modelToView( value ) );
            Dictionary labelTable = slider.getLabelTable();
            if ( labelTable == null ) {
                labelTable = new Hashtable();
                labelTable.put( loc, label );
                slider.setLabelTable( labelTable );
            }
            else {
                labelTable.put( loc, label );
            }
        }

        void addSliderListener( MouseListener listener ) {
            slider.addMouseListener( listener );
        }
    }


    class SliderWithReadout extends JPanel {
        private JTextField modelValueTF;
        private JSlider slider;
        private ModelViewTx1D tx;
        private String units;

        SliderWithReadout( String title, String units, double minModelValue, double maxModelValue, double defaultModelValue ) {
            this.units = units;
            this.setLayout( new GridBagLayout() );
            modelValueTF = new JTextField( 10 );
            setModelValueTF( defaultModelValue );
            tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                    0, 100 );
            slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  100,
                                  (int) tx.modelToView( defaultModelValue ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double rate = tx.viewToModel( slider.getValue() );
                    setModelValueTF( rate );
                }
            } );

            int rowIdx = 0;
            try {
                SwingUtils.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE, GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE, GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( this, modelValueTF, 0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }

        private void setModelValueTF( double value ) {
            modelValueTF.setText( "  " + Double.toString( value ) + " " + units );
        }

        double getModelValue() {
            return tx.viewToModel( slider.getValue() );
        }

        void addChangeListener( ChangeListener changeListener ) {
            slider.addChangeListener( changeListener );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            slider.setEnabled( enabled );
        }
    }

    private class TemperatureUnitsSetter implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            updateTemperatureUnits();
        }
    }

    private void updateTemperatureUnits() {
        if ( fahrenheitRB.isSelected() ) {
            GreenhouseConfig.TEMPERATURE_UNITS = GreenhouseConfig.FAHRENHEIT;
        }
        else if ( celsiusRB.isSelected() ) {
            GreenhouseConfig.TEMPERATURE_UNITS = GreenhouseConfig.CELSIUS;
        }
    }
}
