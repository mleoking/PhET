/**
 * Class: GreenhouseControlPanel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
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

    public GreenhouseControlPanel( final GreenhouseModule module ) {

        this.module = module;
        final GreenhouseModel model = module.getGreenhouseModel();

        //
        // Create the controls
        //

        // Incident photon's from the sun
        final SliderWithReadout sunRateControl = new SliderWithReadout( "Incident Photon Rate", "",
                                                                        0,
                                                                        GreenhouseConfig.maxIncomingRate,
                                                                        GreenhouseConfig.defaultSunPhotonProductionRate );
        sunRateControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setSunPhotonProductionRate( sunRateControl.getModelValue() );
            }
        } );

        // Earth emissivity
        final SliderWithReadout earthEmissivityControl = new SliderWithReadout( "Earth Emissivity", "",
                                                                                0,
                                                                                GreenhouseConfig.maxEarthEmissivity,
                                                                                GreenhouseConfig.defaultEarthEmissivity );
        earthEmissivityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setEarthEmissivity( earthEmissivityControl.getModelValue() );
            }
        } );

        // Greenhouse gas concentration
        greenhouseGasConcentrationControl = new ModelSlider( MessageFormatter.format( "Greenhouse Gas\nConcentration" ),
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
        cloudPanel.add( new JLabel( "Number of Clouds" ) );


        // Show/hide thermometer
        final JCheckBox thermometerCB = new JCheckBox( "Thermometer" );
        thermometerCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.thermometerEnabled( thermometerCB.isSelected() );
            }
        } );
        thermometerCB.setSelected( module.isThermometerEnabled() );

        // todo: HACK!!! make the control panel listen to thye module for this?
        thermometerCB.setSelected( true );

        // Ratio of photons to see
        final JCheckBox allPhotonsCB = new JCheckBox( "View all photons" );
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
    }

    private class AtmosphereSelectionPane extends JPanel {

        private AbstractAction pickNoGG = new AbstractAction() {
            String[] concentrations = new String[]{ "", "", "", ""};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( 0 );
                GreenhouseControlPanel.this.module.setVirginEarth();
            }
        };

        private AbstractAction pickIceAgeGG = new AbstractAction() {
            String[] concentrations = new String[]{ "", "200 ppm", "", ""};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationIceAge );
                GreenhouseControlPanel.this.module.setIceAge();
            }
        };

        private AbstractAction pickPreIndRevGG = new AbstractAction() {
            String[] concentrations = new String[]{ "70% rel. humidity", "280 ppm", "730 ppm", "270 ppm"};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentration1750 );
                GreenhouseControlPanel.this.module.setPreIndRev();
            }
        };

        private AbstractAction pickTodayGG = new AbstractAction() {
            String[] concentrations = new String[]{ "70% rel. humidity", "370 ppm", "1843 ppm", "317 ppm"};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.greenhouseGasConcentrationToday );
                GreenhouseControlPanel.this.module.setToday();
            }
        };

        private AbstractAction pickTomorrowGG = new AbstractAction() {
            String[] concentrations = new String[]{ "?", "?", "?", "?"};

            public void actionPerformed( ActionEvent e ) {
                greenhouseGasCompositionPane.setConcentrations( concentrations );
                greenhouseGasConcentrationControl.setModelValue( GreenhouseConfig.maxGreenhouseGasConcentration * 0.9 );
                GreenhouseControlPanel.this.module.setTomorrow();
            }
        };

        AtmosphereSelectionPane() {
            JRadioButton noGGRB = new JRadioButton();
            noGGRB.setAction( pickNoGG );
            noGGRB.setText( "No greenhouse gases" );

            JRadioButton iceAgeGGRB = new JRadioButton();
            iceAgeGGRB.setAction( pickIceAgeGG );
            iceAgeGGRB.setText( "Ice age" );

            JRadioButton preIndRevGGRB = new JRadioButton();
            preIndRevGGRB.setAction( pickPreIndRevGG );
            preIndRevGGRB.setText( "1750" );

            JRadioButton todayGGRB = new JRadioButton();
            todayGGRB.setAction( pickTodayGG );
            todayGGRB.setText( "Today" );

            JRadioButton tomorrowGGRB = new JRadioButton();
            tomorrowGGRB.setAction( pickTomorrowGG );
            tomorrowGGRB.setText( "Tomorrow?" );
            ButtonGroup ggBG = new ButtonGroup();

            ggBG.add( noGGRB );
            ggBG.add( iceAgeGGRB );
            ggBG.add( preIndRevGGRB );
            ggBG.add( todayGGRB );
            ggBG.add( tomorrowGGRB );
            noGGRB.setSelected( true );

            this.setLayout( new GridLayout( 5, 1 ) );
            this.add( noGGRB );
            this.add( iceAgeGGRB );
            this.add( preIndRevGGRB );
            this.add( todayGGRB );
            this.add( tomorrowGGRB );

            TitledBorder titledBorder = BorderFactory.createTitledBorder( "Atmosphere during..." );
            titledBorder.setTitleJustification( TitledBorder.LEFT );
            this.setBorder( titledBorder );
        }
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
                "H2O",
                "CO2",
                "CH4",
                "N2O"
            };
            JTextField[] concentrations = new JTextField[]{
                h2oTF,
                co2TF,
                ch4TF,
                n2oTF
            };

            this.setLayout( new GridBagLayout() );
            TitledBorder titledBorder = BorderFactory.createTitledBorder( "Greenhouse Gas Composition" );
            Font font = this.getFont();
            FontMetrics fontMetrics = getFontMetrics( font );
            int width = fontMetrics.stringWidth( "Greenhouse Gas Composition   " );
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
    }
}
