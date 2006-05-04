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

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.coreadditions.ModelViewTx1D;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;


public class GreenhouseControlPanel extends JPanel {

    private static Color iceAgeColor = new Color( 255, 180, 20 );
    private static Color preIndRevColor = Color.green;
    private static Color todayColor = new Color( 60, 200, 255 );
//    private static Color todayColor = Color.white;
    private static Color venusColor = Color.cyan;
    private static Color panelBackground = new Color( 110, 110, 110 );
    private static Color panelForeground = Color.white;


    private ModelSlider greenhouseGasConcentrationControl;
    String[] iceAgeConcentrations = new String[]{"",
            "200 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
            "",
            ""};

    String[] preIndRevConcentrations = new String[]{
            "70% " + SimStrings.get( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "280 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
            "730 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
            "270 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" )};

    String[] todayConcentrations = new String[]{
            "70% " + SimStrings.get( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
            "370 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
            "1843 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
            "317 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" )};

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


    /**
     * @param module
     */
    public GreenhouseControlPanel( final GreenhouseModule module ) {

        this.module = module;
        final GreenhouseModel model = module.getGreenhouseModel();

        // PhET logo
        JLabel logo = new JLabel( ( new ImageIcon( new ImageLoader().loadImage( "images/Phet-Flatirons-logo-3-small.gif" ) ) ) );

        // Incident photon's from the sun
        final SliderWithReadout sunRateControl = new SliderWithReadout( SimStrings.get( "GreenhouseControlPanel.SunRateSlider" ),
                                                                        "", 0,
                                                                        GreenhouseConfig.maxIncomingRate,
                                                                        GreenhouseConfig.defaultSunPhotonProductionRate );
        sunRateControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setSunPhotonProductionRate( sunRateControl.getModelValue() );
            }
        } );

        // Earth emissivity
        final SliderWithReadout earthEmissivityControl = new SliderWithReadout( SimStrings.get( "GreenhouseControlPanel.EarthEmissivitySlider" ),
                                                                                "", 0,
                                                                                GreenhouseConfig.maxEarthEmissivity,
                                                                                GreenhouseConfig.defaultEarthEmissivity );
        earthEmissivityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setEarthEmissivity( earthEmissivityControl.getModelValue() );
            }
        } );

        // Greenhouse gas concentration
        greenhouseGasConcentrationControl = new ModelSlider( MessageFormatter.format( SimStrings.get( "GreenhouseControlPanel.GasConcentrationSlider" ) ),
                                                             0.0,
                                                             GreenhouseConfig.maxGreenhouseGasConcentration,
                                                             GreenhouseConfig.defaultGreenhouseGasConcentration );
        greenhouseGasConcentrationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setGreenhouseGasConcentration( greenhouseGasConcentrationControl.getModelValue() );
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
                JSpinner spinner = (JSpinner)e.getSource();
                int i = ( (Integer)spinner.getValue() ).intValue();
                module.numCloudsEnabled( i );
            }
        } );
        cloudPanel.add( cloudsSpinner );
        cloudPanel.add( new JLabel( SimStrings.get( "GreenhouseControlPanel.NumberOfCloudsLabel" ) ) );

        // Show/hide thermometer
        thermometerCB = new JCheckBox( SimStrings.get( "GreenhouseControlPanel.ThermometerCheckbox" ) );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );
//        thermometerCB.setSelected( module.isThermometerEnabled() );
//
//        // todo: HACK!!! make the control panel listen to the module for this?
//        thermometerCB.setSelected( true );

        // Ratio of photons to see
        allPhotonsCB = new JCheckBox( SimStrings.get( "GreenhouseControlPanel.ViewPhotonsCheckbox" ) );
        allPhotonsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( allPhotonsCB.isSelected() ) {
                    module.setVisiblePhotonRatio( 1.0 );
                }
                else {
                    module.setVisiblePhotonRatio( 0.1 );
                }
            }
        } );
//        module.setVisiblePhotonRatio( 1.0 );
//        // todo: HACK!!! make the control panel listen to the module for this?
//        allPhotonsCB.setSelected( true );

        // Atmosphere selection
        atmosphereSelectionPane = new AtmosphereSelectionPane();

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "GreenhouseControlPanel.Reset" ) );
//        resetBtn.setBackground( Color.lightGray );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );

        setDefaultConditions();

        //
        // Lay out the controls
        //
        this.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( logo, gbc );
        add( new GreenhouseLegend(), gbc );
        add( greenhouseGasConcentrationControl, gbc );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( atmosphereSelectionPane, gbc );
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets( 0, 15, 0, 15 );
        add( cloudPanel, gbc );
        add( thermometerCB, gbc );
        add( allPhotonsCB, gbc );
        gbc.anchor = GridBagConstraints.CENTER;
        add( resetBtn, gbc );

        setBackground( this );
    }

    private void setDefaultConditions() {
        cloudsSpinner.setValue( new Integer( 0 ) );

        // todo: HACK!!! make the control panel listen to the module for this?
        allPhotonsCB.setSelected( true );
        module.setVisiblePhotonRatio( 1.0 );

        // todo: HACK!!! make the control panel listen to the module for this?
        thermometerCB.setSelected( true );
//        module.thermometerEnabled( thermometerCB.isSelected() );
    }

    private void setBackground( Container container ) {
//        container.setBackground( panelBackground );
//        Component[] components = container.getComponents();
//        for( int i = 0; i < components.length; i++ ) {
//            Component component = components[i];
//            component.setBackground( panelBackground );
//            if( component.getForeground().equals( Color.black ) ) {
//                component.setForeground( panelForeground );
//            }
//            if( component instanceof Container ) {
//                setBackground( (Container)component );
//            }
//        }
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

        AtmosphereSelectionPane() {

            final JRadioButton adjustableGGRB = new JRadioButton();
            adjustableGGRB.setAction( pickAdjustableGG );
            adjustableGGRB.setText( SimStrings.get( "GreenhouseControlPanel.Adjustable" ) );

            JRadioButton iceAgeGGRB = new JRadioButton();
            iceAgeGGRB.setAction( pickIceAgeGG );
            iceAgeGGRB.setText( SimStrings.get( "GreenhouseControlPanel.IceAgeLabel" ) );
            iceAgeGGRB.setForeground( iceAgeColor );

            JRadioButton preIndRevGGRB = new JRadioButton();
            preIndRevGGRB.setAction( pickPreIndRevGG );
            preIndRevGGRB.setText( SimStrings.get( "GreenhouseControlPanel.PreIndustrialLabel" ) );
            preIndRevGGRB.setForeground( preIndRevColor );

            todayGGRB = new JRadioButton();
            todayGGRB.setAction( pickTodayGG );
            todayGGRB.setText( SimStrings.get( "GreenhouseControlPanel.TodayLabel" ) );
            todayGGRB.setForeground( todayColor );

//            JRadioButton venusGGRB = new JRadioButton();
//            venusGGRB.setAction( pickVenusGG );
//            venusGGRB.setText( SimStrings.get( "GreenhouseControlPanel.Venus" ) );
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

            TitledBorder titledBorder = BorderFactory.createTitledBorder( SimStrings.get( "GreenhouseControlPanel.TimePeriodBorderLabel" ) );
            titledBorder.setTitleColor( panelForeground );
            titledBorder.setTitleJustification( TitledBorder.LEFT );
            this.setBorder( titledBorder );

            setDefaultConditions();
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
            greenhouseGasConcentrationControl.setEnabled( false );
            greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
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

        private AbstractAction pickAdjustableGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                GreenhouseControlPanel.this.module.setVirginEarth();
                greenhouseGasConcentrationControl.setEnabled( true );
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
                adjustableCompositionPane.setPreferredSize( iceAgeCompositionPane.getPreferredSize() );
                adjustableCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickIceAgeGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
                greenhouseGasConcentrationControl.setEnabled( false );
                greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
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
                greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
                GreenhouseControlPanel.this.module.setPreIndRev();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                seventeenFiftyCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickTodayGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
                greenhouseGasConcentrationControl.setEnabled( false );
                greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration );
                GreenhouseControlPanel.this.module.setToday();
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
                hideConcentrations();
                todayCompositionPane.setVisible( true );
            }
        };

        private AbstractAction pickVenusGG = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
                greenhouseGasConcentrationControl.setEnabled( false );
                greenhouseGasConcentrationControl.setMaxValue( GreenhouseConfig.maxGreenhouseGasConcentration * 4 );
                GreenhouseControlPanel.this.module.setVenus();
                module.getEarth().setBaseTemperature( GreenhouseConfig.venusBaseTemperature );
                hideConcentrations();
                venusCompositionPane.setVisible( true );
            }
        };
//
//        private AbstractAction pickTomorrowGG = new AbstractAction() {
//            String[] preIndRevConcentrations = new String[]{"?", "?", "?", "?"};
//
//            public void actionPerformed( ActionEvent e ) {
//                greenhouseGasCompositionPane.setConcentrations( preIndRevConcentrations );
//                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.maxGreenhouseGasConcentration * 0.9 );
//                GreenhouseControlPanel.this.module.setTomorrow();
//                greenhouseGasConcentrationControl.setEnabled( false );
//            }
//        };

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
                    SimStrings.get( "GreenhouseControlPanel.H2OLabel" ),
                    SimStrings.get( "GreenhouseControlPanel.CO2Label" ),
                    SimStrings.get( "GreenhouseControlPanel.CH4Label" ),
                    SimStrings.get( "GreenhouseControlPanel.N2OLabel" )
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
            TitledBorder titledBorder = BorderFactory.createTitledBorder( SimStrings.get( "GreenhouseControlPanel.GreenhouseGasBorderLabel" ) );
            Font font = this.getFont();
            FontMetrics fontMetrics = getFontMetrics( font );
            int width = fontMetrics.stringWidth( SimStrings.get( "GreenhouseControlPanel.GreenhouseGasBorderLabel" ) + "   " );
            Dimension concentrationsPanelDim = new Dimension( width, 120 );
            this.setPreferredSize( concentrationsPanelDim );

            titledBorder.setTitleJustification( TitledBorder.LEFT );
            titledBorder.setTitleColor( panelForeground );
            this.setBorder( titledBorder );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 5, 0, 5 ), 0, 0 );
            for( int rowIdx = 0; rowIdx < concentrations.length; rowIdx++ ) {
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
            tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                    0, 10000 );
            slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  10000,
                                  (int)tx.modelToView( defaultModelValue ) );
            slider.setPaintLabels( true );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double rate = tx.viewToModel( slider.getValue() );
                }
            } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Make the colored tick marks on the slider
            Font defaultFont = new JLabel().getFont();
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
            labelTable.put( new Integer( (int)tx.modelToView( GreenhouseConfig.greenhouseGasConcentrationIceAge ) ),
                            iceAgeTick );
            labelTable.put( new Integer( (int)tx.modelToView( GreenhouseConfig.greenhouseGasConcentrationToday ) ),
                            todayTick );
            labelTable.put( new Integer( (int)tx.modelToView( GreenhouseConfig.greenhouseGasConcentration1750 ) ),
                            preIndRevTick );
            slider.setLabelTable( labelTable );
        }

        void setMaxValue( double value ) {
            int max = (int)tx.modelToView( value );
            slider.setMaximum( max );
        }

        double getModelValue() {
            return tx.viewToModel( slider.getValue() );
        }

        void addChangeListener( ChangeListener changeListener ) {
            slider.addChangeListener( changeListener );
        }

        void setModelValue( double value ) {
            slider.setValue( (int)tx.modelToView( value ) );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            slider.setEnabled( enabled );
        }

        void addTick( double value, Color color ) {
            JLabel label = new JLabel( "|" );
            label.setForeground( color );
            Integer loc = new Integer( (int)tx.modelToView( value ) );
            Dictionary labelTable = slider.getLabelTable();
            if( labelTable == null ) {
                labelTable = new Hashtable();
                labelTable.put( loc, label );
                slider.setLabelTable( labelTable );
            }
            else {
                labelTable.put( loc, label );
            }
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
                                  (int)tx.modelToView( defaultModelValue ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double rate = tx.viewToModel( slider.getValue() );
                    setModelValueTF( rate );
                }
            } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, modelValueTF, 0, rowIdx++, 1, 1,
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
}
