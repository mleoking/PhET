/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ModelViewTx1D;
import edu.colorado.phet.coreadditions.MessageFormatter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GreenhouseControlPanel extends JPanel {
    private ModelSlider greenhouseGasConcentrationControl;
    private GreenhouseCompositionPane greenhouseGasCompositionPane;
    private GreenhouseModule module;
    private Color panelBackground;
    private Color panelForeground;

    public GreenhouseControlPanel( final GreenhouseModule module ) {

        this.module = module;
        final GreenhouseModel model = module.getGreenhouseModel();

        panelBackground = Color.darkGray;
        panelForeground = Color.white;

        //
        // Create the controls
        //

        // PhET logo
        JLabel logo = new JLabel( (new ImageIcon( new ImageLoader().loadImage( "images/Phet-Flatirons-logo-3-small.gif" )))) ;

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
                System.out.println( greenhouseGasConcentrationControl.getModelValue() );
            }
        } );

        // Add/remove clouds
        JPanel cloudPanel = new JPanel();
        int min = 0;
        int max = 3;
        int step = 1;
        int initValue = 0;
        SpinnerModel cloudSpinnerModel = new SpinnerNumberModel( initValue, min, max, step );
        final JSpinner cloudsSpinner = new JSpinner( cloudSpinnerModel );
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
        final JCheckBox thermometerCB = new JCheckBox( SimStrings.get( "GreenhouseControlPanel.ThermometerCheckbox" ) );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );
        thermometerCB.setSelected( module.isThermometerEnabled() );

        // todo: HACK!!! make the control panel listen to the module for this?
        thermometerCB.setSelected( true );

        // Ratio of photons to see
        final JCheckBox allPhotonsCB = new JCheckBox( SimStrings.get( "GreenhouseControlPanel.ViewPhotonsCheckbox" ) );
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
        // todo: HACK!!! make the control panel listen to the module for this?
        allPhotonsCB.setSelected( true );

        // Atmosphere selection
        JPanel atmosphereSelectionPane = new AtmosphereSelectionPane();

        // Greenhouse gas composition
        greenhouseGasCompositionPane = new GreenhouseCompositionPane();

        //
        // Lay out the controls
        //
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, logo, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.NORTH );
//            GraphicsUtil.addGridBagComponent( this, sunRateControl, 0, rowIdx++, 1, 1,
//                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
//            GraphicsUtil.addGridBagComponent( this, earthEmissivityControl, 0, rowIdx++, 1, 1,
//                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new GreenhouseLegend(), 0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, greenhouseGasConcentrationControl, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, atmosphereSelectionPane, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, greenhouseGasCompositionPane, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, cloudPanel, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this, thermometerCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this, allPhotonsCB, 0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
        }
        catch( AWTException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        setBackground( this );
    }

    private void setBackground( Container container ) {
        container.setBackground( panelBackground);
        Component[] components = container.getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            component.setBackground( panelBackground);
            component.setForeground( panelForeground );
            if( component instanceof Container) {
                setBackground( (Container)component );
            }
        }

    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class AtmosphereSelectionPane extends JPanel {

        AtmosphereSelectionPane() {
            JRadioButton adjustableGGRB = new JRadioButton();
            adjustableGGRB.setAction( pickAdjustableGG );
            adjustableGGRB.setText( SimStrings.get( "GreenhouseControlPanel.Adjustable" ) );

//            JRadioButton noGGRB = new JRadioButton();
//            noGGRB.setAction( pickNoGG );
//            noGGRB.setText( SimStrings.get( "GreenhouseControlPanel.NoGasesLabel" ) );
//
            JRadioButton iceAgeGGRB = new JRadioButton();
            iceAgeGGRB.setAction( pickIceAgeGG );
            iceAgeGGRB.setText( SimStrings.get( "GreenhouseControlPanel.IceAgeLabel" ) );

            JRadioButton preIndRevGGRB = new JRadioButton();
            preIndRevGGRB.setAction( pickPreIndRevGG );
            preIndRevGGRB.setText( SimStrings.get( "GreenhouseControlPanel.PreIndustrialLabel" ) );

            JRadioButton todayGGRB = new JRadioButton();
            todayGGRB.setAction( pickTodayGG );
            todayGGRB.setText( SimStrings.get( "GreenhouseControlPanel.TodayLabel" ) );

            JRadioButton venusGGRB = new JRadioButton();
            venusGGRB.setAction( pickVenusGG );
            venusGGRB.setText( SimStrings.get( "GreenhouseControlPanel.Venus" ) );

//            JRadioButton tomorrowGGRB = new JRadioButton();
//            tomorrowGGRB.setAction( pickTomorrowGG );
//            tomorrowGGRB.setText( SimStrings.get( "GreenhouseControlPanel.TomorrowLabel" ) );
//            ButtonGroup ggBG = new ButtonGroup();
//
            ButtonGroup ggBG = new ButtonGroup();
            ggBG.add( adjustableGGRB );
//            ggBG.add( noGGRB );
            ggBG.add( iceAgeGGRB );
            ggBG.add( preIndRevGGRB );
            ggBG.add( todayGGRB );
            ggBG.add( venusGGRB );
//            ggBG.add( tomorrowGGRB );
            todayGGRB.setSelected( true );
//            noGGRB.setSelected( true );

            this.setLayout( new GridLayout( 5, 1 ) );
            this.add( adjustableGGRB );
//            this.add( noGGRB );
            this.add( iceAgeGGRB );
            this.add( preIndRevGGRB );
            this.add( todayGGRB );
            this.add( venusGGRB );
//            this.add( tomorrowGGRB );

            TitledBorder titledBorder = BorderFactory.createTitledBorder( SimStrings.get( "GreenhouseControlPanel.TimePeriodBorderLabel" ) );
            titledBorder.setTitleJustification( TitledBorder.LEFT );
            this.setBorder( titledBorder );
        }

        private AbstractAction pickAdjustableGG = new AbstractAction() {
            String[] concentrations = new String[]{"", "", "", ""};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( 0 );
                GreenhouseControlPanel.this.module.setVirginEarth();
                greenhouseGasConcentrationControl.setEnabled( true );
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            }
        };

//        private AbstractAction pickNoGG = new AbstractAction() {
//            String[] concentrations = new String[]{"", "", "", ""};
//
//            public void actionPerformed( ActionEvent e ) {
//                greenhouseGasCompositionPane.setConcentrations( concentrations );
//                greenhouseGasConcentrationControl.setModelValue( 0 );
//                GreenhouseControlPanel.this.module.setVirginEarth();
//            }
//        };
//
        private AbstractAction pickIceAgeGG = new AbstractAction() {
            String[] concentrations = new String[]{"", "200 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ), "", ""};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
                GreenhouseControlPanel.this.module.setIceAge();
                greenhouseGasConcentrationControl.setEnabled( false );
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            }
        };

        private AbstractAction pickPreIndRevGG = new AbstractAction() {
            String[] concentrations = new String[]{
                "70% " + SimStrings.get( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
                "280 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
                "730 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
                "270 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" )};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentration1750 );
                GreenhouseControlPanel.this.module.setPreIndRev();
                greenhouseGasConcentrationControl.setEnabled( false );
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            }
        };

        private AbstractAction pickTodayGG = new AbstractAction() {
            String[] concentrations = new String[]{
                "70% " + SimStrings.get( "GreenhouseControlPanel.RelativeHumidityAbbreviation" ),
                "370 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
                "1843 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" ),
                "317 " + SimStrings.get( "GreenhouseControlPanel.PPMAbreviation" )};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
                GreenhouseControlPanel.this.module.setToday();
                greenhouseGasConcentrationControl.setEnabled( false );
                module.getEarth().setBaseTemperature( GreenhouseConfig.earthBaseTemperature );
            }
        };

        private AbstractAction pickVenusGG = new AbstractAction() {
            String[] concentrations = new String[]{
                "",
                "96.5% ",
                "",
                ""};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
                GreenhouseControlPanel.this.module.setVenus();
                greenhouseGasConcentrationControl.setEnabled( false );

                module.getEarth().setBaseTemperature( GreenhouseConfig.venusBaseTemperature );
            }
        };
//
//        private AbstractAction pickTomorrowGG = new AbstractAction() {
//            String[] concentrations = new String[]{"?", "?", "?", "?"};
//
//            public void actionPerformed( ActionEvent e ) {
//                greenhouseGasCompositionPane.setConcentrations( concentrations );
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
            this.setPreferredSize( new Dimension( width, 120 ) );

            titledBorder.setTitleJustification( TitledBorder.LEFT );
            this.setBorder( titledBorder );
            try {
                for( int rowIdx = 0; rowIdx < concentrations.length; rowIdx++ ) {
                    GraphicsUtil.addGridBagComponent( this, new JLabel( labels[rowIdx] ), 0, rowIdx, 1, 1,
                                                      GridBagConstraints.NONE, GridBagConstraints.CENTER );
                    GraphicsUtil.addGridBagComponent( this, concentrations[rowIdx], 1, rowIdx, 1, 1,
                                                      GridBagConstraints.NONE, GridBagConstraints.CENTER );
                }
            }
            catch( AWTException e ) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }

//        public void setCO2( double concentration ) {
//            co2TF.setText( Double.toString( concentration ) + " ppm" );
//        }
//
//        public void setH2O( double concentration ) {
//            h2oTF.setText( Double.toString( concentration ) + "%" );
//        }
//
//        public void setCH4( double concentration ) {
//            ch4TF.setText( Double.toString( concentration ) + " ppm" );
//        }
//
//        public void setN2O( double concentration ) {
//            n2oTF.setText( Double.toString( concentration ) + " ppm" );
//        }

        public void setConcentrations( String[] concentrations ) {
            h2oTF.setText( concentrations[0] );
            co2TF.setText( concentrations[1] );
            ch4TF.setText( concentrations[2] );
            n2oTF.setText( concentrations[3] );
        }
    }

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
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
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
