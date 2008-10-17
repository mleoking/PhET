/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.event.*;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.greenhouse.coreadditions.ModelViewTx1D;


public class GreenhouseControlPanel extends JPanel {

    private static Color adjustableGGColor = Color.black;
    //    private static Color adjustableGGColor = Color.cyan;
    //    private static Color adjustableGGColor = new Color( 255, 160, 180 );
    private static Color iceAgeColor = new Color( 0, 28, 229 );
    private static Color preIndRevColor = new Color( 176, 0, 219 );
    //    private static Color preIndRevColor = Color.green;
    //    private static Color todayColor = new Color( 60, 200, 255 );
    private static Color todayColor = new Color( 11, 142, 0 );
    //    private static Color todayColor = Color.yellow;
    private static Color panelForeground = Color.black;
//    private static Color panelForeground = Color.white;


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
    private GreenhouseModule module;
    private JSpinner cloudsSpinner;
    private AtmosphereSelectionPane atmosphereSelectionPane;
    private JCheckBox allPhotonsCB;
    private JCheckBox thermometerCB;
    private JRadioButton fahrenheitRB;
    private JRadioButton celsiusRB;


    /**
     * Constructor
     *
     * @param module
     */
    public GreenhouseControlPanel( final GreenhouseModule module ) {

        this.module = module;
        final GreenhouseModel model = module.getGreenhouseModel();

        //--------------------------------------------------------------------------------------------------
        // Create the controls
        //--------------------------------------------------------------------------------------------------

        // PhET logo
        JLabel logo = new JLabel( ( new ImageIcon( GreenhouseResources.getImage( "Phet-Flatirons-logo-3-small.gif" ) ) ) );

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
        atmosphereSelectionPane = new AtmosphereSelectionPane();

        // Reset button
        JButton resetBtn = new JButton( GreenhouseResources.getString( "GreenhouseControlPanel.Reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );

        setDefaultConditions();

        //--------------------------------------------------------------------------------------------------
        // Lay out the controls
        //--------------------------------------------------------------------------------------------------

        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( logo, gbc );
        add( new GreenhouseLegend(), gbc );

        // Greenhouse gas concentrations
        {
            JPanel panel = new JPanel();
            panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GreenhouseResources.getString( "GreenhouseControlPanel.GasConcentrationSlider" ) ) );
            panel.add( greenhouseGasConcentrationControl );
            add( panel, gbc );
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
        allPhotonsCB.setSelected( true );
        module.setVisiblePhotonRatio( 1.0 );

        // todo: HACK!!! make the control panel listen to the module for this?
        thermometerCB.setSelected( true );
    }

    private void reset() {
        module.reset();
        atmosphereSelectionPane.reset();
        setDefaultConditions();
        module.thermometerEnabled( thermometerCB.isSelected() );
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

        private AbstractAction pickIceAgeGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
//                greenhouseGasConcentrationControl.setEnabled( false );
                GreenhouseControlPanel.this.module.setIceAge();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                iceAgeCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickPreIndRevGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentration1750 );
//                greenhouseGasConcentrationControl.setEnabled( false );
                GreenhouseControlPanel.this.module.setPreIndRev();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                seventeenFiftyCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickTodayGG = new AbstractAction() {
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

            this.setLayout( new GridBagLayout() );
            TitledBorder titledBorder = BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), GreenhouseResources.getString( "GreenhouseControlPanel.GreenhouseGasBorderLabel" ) );
            Font font = this.getFont();
            FontMetrics fontMetrics = getFontMetrics( font );
            int width = fontMetrics.stringWidth( GreenhouseResources.getString( "GreenhouseControlPanel.GreenhouseGasBorderLabel" ) + "   " );
            Dimension concentrationsPanelDim = new Dimension( width, 120 );
            this.setPreferredSize( concentrationsPanelDim );

            titledBorder.setTitleColor( panelForeground );
            this.setBorder( titledBorder );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 5, 0, 5 ), 0, 0 );
            for ( int rowIdx = 0; rowIdx < concentrations.length; rowIdx++ ) {
                gbc.gridy = rowIdx;
                gbc.gridx = 0;
                gbc.anchor = GridBagConstraints.EAST;
                add( new JLabel( labels[rowIdx] ), gbc );
                gbc.anchor = GridBagConstraints.WEST;
                gbc.gridx = 1;
                add( concentrations[rowIdx], gbc );
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
            if ( fahrenheitRB.isSelected() ) {
                GreenhouseConfig.TEMPERATURE_UNITS = GreenhouseConfig.FAHRENHEIT;
            }
            else if ( celsiusRB.isSelected() ) {
                GreenhouseConfig.TEMPERATURE_UNITS = GreenhouseConfig.CELSIUS;
            }
        }
    }
}
