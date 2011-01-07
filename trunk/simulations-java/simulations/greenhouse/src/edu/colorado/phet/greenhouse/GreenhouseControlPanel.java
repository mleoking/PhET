// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.event.*;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.greenhouse.model.GreenhouseModel;
import edu.colorado.phet.greenhouse.util.ModelViewTx1D;


public class GreenhouseControlPanel extends JPanel implements Resettable {

    private static Color adjustableGGColor = Color.black;
    private static Color iceAgeColor = new Color( 0, 28, 229 );
    private static Color preIndRevColor = new Color( 176, 0, 219 );
    private static Color todayColor = new Color( 11, 142, 0 );

    // This font is used for the titled borders on this control panel.  It
    // is needed because the titles are long, so the default font made the
    // control panel too wide.
    private static Font BORDER_TITLE_FONT = new PhetFont(12, true);

    private final ModelSlider greenhouseGasConcentrationControl;
    String[] iceAgeConcentrations = new String[]{" ?",
            "180 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "0.380 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "0.215 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" )};

    String[] preIndRevConcentrations = new String[]{
            "70% " + GreenhouseResources.getString( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "280 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "0.730 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "0.270 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" )};

    String[] todayConcentrations = new String[]{
            "70% " + GreenhouseResources.getString( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "388 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "1.843 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" ),
            "0.317 " + GreenhouseResources.getString( "GreenhouseControlPanel.PPMAbreviation" )};

    String[] venusConcentrations = new String[]{
            "",
            "96.5% ",
            "",
            ""};
    private final GreenhouseCompositionPane iceAgeCompositionPane = new GreenhouseCompositionPane( iceAgeConcentrations, iceAgeColor );
    private final GreenhouseCompositionPane seventeenFiftyCompositionPane = new GreenhouseCompositionPane( preIndRevConcentrations, preIndRevColor );
    private final GreenhouseCompositionPane todayCompositionPane = new GreenhouseCompositionPane( todayConcentrations, todayColor );
    private final GreenhouseCompositionPane venusCompositionPane = new GreenhouseCompositionPane( venusConcentrations, Color.black );
    private final JPanel adjustableCompositionPane = new JPanel();
    private final GreenhouseModule module;
    private final JSpinner cloudsSpinner;
    private final AtmosphereSelectionPanel atmosphereSelectionPane;
    private final JCheckBox allPhotonsCB;
    private final JCheckBox thermometerCB;
    private final JRadioButton fahrenheitRB;
    private final JRadioButton celsiusRB;


    /**
     * Constructor
     *
     */
    public GreenhouseControlPanel( final GreenhouseModule module ) {

        this.module = module;
        final GreenhouseModel model = module.getGreenhouseModel();

        //--------------------------------------------------------------------------------------------------
        // Create the controls
        //--------------------------------------------------------------------------------------------------

        // Incident photon's from the sun
        final SliderWithReadout sunRateControl = new SliderWithReadout( GreenhouseResources.getString( "GreenhouseControlPanel.SunRateSlider" ),
                                                                        "", 0,
                                                                        GreenhouseConfig.maxIncomingRate,
                                                                        GreenhouseConfig.defaultSunPhotonProductionRate );
        sunRateControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setSunPhotonProductionRate( sunRateControl.getModelValue() );
            }
        } );

        // Earth emissivity
        final SliderWithReadout earthEmissivityControl = new SliderWithReadout( GreenhouseResources.getString( "GreenhouseControlPanel.EarthEmissivitySlider" ),
                                                                                "", 0,
                                                                                GreenhouseConfig.maxEarthEmissivity,
                                                                                GreenhouseConfig.defaultEarthEmissivity );
        earthEmissivityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setEarthEmissivity( earthEmissivityControl.getModelValue() );
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
                model.setGreenhouseGasConcentration( greenhouseGasConcentrationControl.getModelValue() );
            }
        } );
        greenhouseGasConcentrationControl.addSliderListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                atmosphereSelectionPane.setAdjustableAtmosphere();
            }
        } );

        // Add/remove clouds
        JPanel cloudPanel = new JPanel();
        int min = 0;
        int max = 3;
        int step = 1;
        int initValue = 0;
        SpinnerModel cloudSpinnerModel = new SpinnerNumberModel( initValue, min, max, step );
        cloudsSpinner = new JSpinner( cloudSpinnerModel );
        cloudsSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSpinner spinner = (JSpinner) e.getSource();
                int i = ( (Integer) spinner.getValue() ).intValue();
                module.numCloudsEnabled( i );
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
                module.thermometerEnabled( thermometerCB.isSelected() );
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
                    module.setVisiblePhotonRatio( 1.0 );
                }
                else {
                    module.setVisiblePhotonRatio( 0.1 );
                }
            }
        } );

        // Atmosphere selection
        atmosphereSelectionPane = new AtmosphereSelectionPanel();

        // Reset button
        JButton resetBtn = new ResetAllButton( this, PhetApplication.getInstance().getPhetFrame() );

        setDefaultConditions();

        //--------------------------------------------------------------------------------------------------
        // Lay out the controls
        //--------------------------------------------------------------------------------------------------

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( new GreenhouseLegend(BORDER_TITLE_FONT), gbc );

        // Greenhouse gas concentrations
        {
            JPanel panel = new PhetTitledPanel(
                    GreenhouseResources.getString( "GreenhouseControlPanel.GasConcentrationSlider" ),
                    BORDER_TITLE_FONT );
            panel.add( greenhouseGasConcentrationControl );
            add( panel, gbc );
        }

        // Options panel
        JPanel optionsPanel = new PhetTitledPanel( GreenhouseResources.getString( "GreenhouseControlPanel.Options" ),
                BORDER_TITLE_FONT );
        optionsPanel.setLayout( new GridBagLayout() );
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

        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( atmosphereSelectionPane, gbc );
        gbc.anchor = GridBagConstraints.WEST;
        add( optionsPanel, gbc );

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets( 15, 15, 0, 15 );
        add( resetBtn, gbc );
    }

    private void setDefaultConditions() {
        cloudsSpinner.setValue( new Integer( 0 ) );

        // todo: HACK!!! make the control panel listen to the module for this?
        allPhotonsCB.setSelected( false );
        module.setVisiblePhotonRatio( 0.1 );

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
        module.thermometerEnabled( thermometerCB.isSelected() );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class AtmosphereSelectionPanel extends JPanel {
        private final JRadioButton todayGGRB;
        private final JRadioButton adjustableGGRB;

        AtmosphereSelectionPanel() {
            setBorder( new PhetTitledBorder( GreenhouseResources.getString( "GreenhouseControlPanel.TimePeriodBorderLabel" ), BORDER_TITLE_FONT ) );

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
            ggBG.add( todayGGRB );
            ggBG.add( preIndRevGGRB );
            ggBG.add( iceAgeGGRB );
            ggBG.add( adjustableGGRB );
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
            this.add( todayGGRB, gbc );
            this.add( preIndRevGGRB, gbc );
            this.add( iceAgeGGRB, gbc );
            this.add( adjustableGGRB, gbc );
            this.add( adjustableCompositionPane, gbc );
            this.add( seventeenFiftyCompositionPane, gbc );
            this.add( iceAgeCompositionPane, gbc );
            this.add( todayCompositionPane, gbc );
//            this.add( venusGGRB, gbc );
//            this.add( venusCompositionPane, gbc );

            setDefaultConditions();
        }

        private void setAdjustableAtmosphere() {
            GreenhouseControlPanel.this.module.setVirginEarth();
//            greenhouseGasConcentrationControl.setEnabled( true );
            module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
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
            module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
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

        private final AbstractAction pickIceAgeGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
//                greenhouseGasConcentrationControl.setEnabled( false );
                GreenhouseControlPanel.this.module.setIceAge();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                iceAgeCompositionPane.setVisible( true );
            }
        };

        private final AbstractAction pickPreIndRevGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentration1750 );
//                greenhouseGasConcentrationControl.setEnabled( false );
                GreenhouseControlPanel.this.module.setPreIndRev();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                seventeenFiftyCompositionPane.setVisible( true );
            }
        };

        private final AbstractAction pickTodayGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
//                greenhouseGasConcentrationControl.setEnabled( false );
                GreenhouseControlPanel.this.module.setToday();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                todayCompositionPane.setVisible( true );
            }
        };
    }


    /**
     * Pane that shows concentration of various greenhouse gas components
     */
    private class GreenhouseCompositionPane extends PhetTitledPanel {

        JTextField h2oTF = new GreenhouseTextField();
        JTextField co2TF = new GreenhouseTextField();
        JTextField ch4TF = new GreenhouseTextField();
        JTextField n2oTF = new GreenhouseTextField();

        GreenhouseCompositionPane( String[] concentrations, Color titleColor ) {
            this();
            setConcentrations( concentrations );
            setTitleColor( titleColor );
        }

        GreenhouseCompositionPane() {
            super( GreenhouseResources.getString( "GreenhouseControlPanel.GreenhouseGasBorderLabel" ),
                    BORDER_TITLE_FONT );
            setFont( new PhetFont(8) );

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

        @Override
        public void setTitleColor(Color color){
            if (getBorder() instanceof TitledBorder){
                ((TitledBorder)getBorder()).setTitleColor( color );
            }
            else{
                System.err.println( getClass().getName() + " - Error: Border is not a titled border, ignoring attempt to set color." );
            }
        }
    }

    /**
     *
     */
    class ModelSlider extends JPanel {
        private final JSlider slider;
        private final ModelViewTx1D tx;

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

        @Override
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
        private final JTextField modelValueTF;
        private final JSlider slider;
        private final ModelViewTx1D tx;
        private final String units;

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

        @Override
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

    /**
     *
     * @author John Blanco
     */
    public class GreenhouseTextField extends JTextField {

        public GreenhouseTextField(){
            super( 10 );
            // Make this transparent so that the background of the control
            // panel shows through.
            setBackground( new Color(0, 0, 0, 0) );
            setOpaque( false );
        }
    }
}
