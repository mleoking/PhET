/**
 * Class: CockpitControlPanel
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:52:27 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.distanceladder.model.*;

import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.DecimalFormat;

public class CockpitControlPanel extends JPanel {
    private CockpitModule module;
    private int markRef;

    public CockpitControlPanel( CockpitModule module ) {

        this.module = module;

        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new ControlPanel( module ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new ParallaxPanel(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new BrightnessPanel(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
//            GraphicsUtil.addGridBagComponent( this, new HyperjumpPanel(),
//                                              0, rowIdx++,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        this.module = module;
    }


    //
    // Inner Classes
    //

    private class ControlPanel extends JPanel {
        private JButton orientationReticleBtn;

        ControlPanel( final CockpitModule module ) {

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Starship Control" );
            this.setBorder( titledBorder );

            final JSlider orientationSlider = new JSlider( -180, 180, 0 );
            orientationSlider.setMajorTickSpacing( 90 );
            orientationSlider.setMinorTickSpacing( 45 );
            orientationSlider.setPaintLabels( true );
            orientationSlider.setPaintTicks( true );
            orientationSlider.setPaintTrack( true );
            orientationSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double theta = orientationSlider.getValue() * Math.PI / 180;
                    PointOfView pov = module.getCockpitPov();
                    pov.setTheta( theta );
                    module.setPov( pov );
                }
            } );

//            orientationReticleBtn = new JButton( new AbstractAction( "Show Reticle" ) {
//                boolean reticleOn = false;
//
//                public void actionPerformed( ActionEvent e ) {
//                    reticleOn = !reticleOn;
//                    module.setOrientationReticleOn( reticleOn );
//                    orientationReticleBtn.setText( reticleOn ? "Hide Reticle" : "Show Reticle" );
//                }
//            } );

            // Lay out the panel
            setLayout( new GridBagLayout() );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Turn (deg.)" ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, orientationSlider,
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new HyperjumpPanel(),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, orientationReticleBtn,
//                                                  0, rowIdx++,
//                                                  1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, new JLabel( "Move Left/Right" ),
//                                                  0, rowIdx++,
//                                                  1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

    private class ParallaxPanel extends JPanel {
        private String enableString = "Enable";
        private String disableString = "Disable";

        private JButton parallaxReticleBtn;
        private String parallaxHelp = "<html><style type=\"text/css\"><!--.style1 {font-family: Arial, Helvetica, sans-serif}--></style>"
                                      + "<h2 class=\"style1\">How to Measure Distances Using Parallax</h2>"
                                      + "<ol>"
                                      + "<li>Display the measuring reticle.</li><li>Using the slider, line up the star whose distance you would like to measure with one of the vertical reticle lines.</li>"
                                      + "<li>Press the &quot;Mark&quot; button to set the offset readout to 0.</li>"
                                      + "<li>Move the slider left or right until the star you are measuring lines up with another verticle reticle line.</li>"
                                      + "<li>Count the number of verticle reticle lines through which the star moved and enter this number in the &quot;Reticle Offset&quot; field.</li>"
                                      + "<li>Click on the &quot;Compute&quot; button.</li>"
                                      + "<li>The distance to the star will be displayed in the &quot;Distance&quot; field.   </li>"
                                      + "</ol>"
                                      + "</html> ";
        private double leftRightSliderFactor = 100;
        private JTextField alphaTF;
        private JTextField betaTF;
        private JSlider leftRightSlider;
        private JTextField leftRightTF;
        private JButton computeBtn;
        boolean parallaxInstrumentEnabled = false;
        private JButton markBtn;
        private JButton helpBtn;


        public ParallaxPanel() {
            super( new GridBagLayout() );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Parallax Instrument" );
            this.setBorder( titledBorder );

            parallaxReticleBtn = new JButton( new AbstractAction( enableString ) {
                public void actionPerformed( ActionEvent e ) {
                    parallaxInstrumentEnabled = !parallaxInstrumentEnabled;
//                    module.setParallaxReticleOn( parallaxInstrumentEnabled );
                    parallaxReticleBtn.setText( parallaxInstrumentEnabled ? disableString : enableString );
                    ParallaxPanel.this.update();
                }
            } );

            leftRightTF = new JTextField( "0", 6 );
            leftRightTF.setEditable( false );
            leftRightTF.setBackground( Color.white );
            leftRightTF.setHorizontalAlignment( JTextField.RIGHT );

            leftRightSlider = new JSlider( -100, 100, 0 );
            leftRightSlider.setMajorTickSpacing( 25 );
            leftRightSlider.setMinorTickSpacing( 5 );
            leftRightSlider.setPaintTicks( true );
            leftRightSlider.setPaintTrack( true );
            leftRightSlider.addChangeListener( new ChangeListener() {
                private int parallaxRef = 0;
                private int parallaxCurr = 0;

                public void stateChanged( ChangeEvent e ) {
                    parallaxCurr = leftRightSlider.getValue();

                    // Set the point of view of the cockpit
                    double d = (double)( parallaxCurr - parallaxRef ) / leftRightSliderFactor;
                    parallaxRef = parallaxCurr;
                    PointOfView pov = module.getCockpitPov();
                    pov.setLocation( pov.getX() - d * Math.sin( pov.getTheta() ),
                                     pov.getY() + d * Math.cos( pov.getTheta() ) );
                    module.setPov( pov );

                    // Display the lateral offset
                    leftRightTF.setText( Integer.toString( parallaxRef - markRef ) );
                }
            } );

            markBtn = new JButton( new AbstractAction( "Mark" ) {
                            public void actionPerformed( ActionEvent e ) {
                                markRef = leftRightSlider.getValue();
                                leftRightTF.setText( "0" );
                            }
                        } );


            alphaTF = new JTextField( 6 );
            alphaTF.setHorizontalAlignment( JTextField.RIGHT );
            betaTF = new JTextField( 6 );
            betaTF.setHorizontalAlignment( JTextField.RIGHT );
            final JTextField resultTF = new JTextField( 6 );
            resultTF.setHorizontalAlignment( JTextField.RIGHT );
            resultTF.setEditable( false );
            resultTF.setBackground( Color.white );
            final Format distFormatter = new DecimalFormat( "##.0" );

            computeBtn = new JButton( new AbstractAction( "Compute" ) {
                            public void actionPerformed( ActionEvent e ) {
                                double b = Double.parseDouble( leftRightTF.getText() ) / leftRightSliderFactor;
                                double alpha = Math.toRadians( Double.parseDouble( alphaTF.getText() ));
                                double beta = Math.toRadians( Double.parseDouble( betaTF.getText() ));
                                double d = b / ( Math.tan( beta ) - Math.tan( alpha ));
                                resultTF.setText( distFormatter.format( new Double( d )));
                            }
                        } );
            computeBtn.setEnabled( false );

            alphaTF.getDocument().addDocumentListener( new DocumentListener() {
                public void changedUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ));
                }

                public void insertUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ));
                }

                public void removeUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ) );
                }
            } );

            betaTF.getDocument().addDocumentListener( new DocumentListener() {
                public void changedUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ) );
                }

                public void insertUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ) );
                }

                public void removeUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) && !betaTF.getText().equals( "" ) );
                }
            } );

            helpBtn = new JButton( new AbstractAction( "Help" ) {
                            public void actionPerformed( ActionEvent e ) {
                                JOptionPane.showMessageDialog( ParallaxPanel.this.getRootPane(),
                                                               new JEditorPane( "text/html; charset=iso-8859-1", parallaxHelp ),
                                                               "Help",
                                                               JOptionPane.INFORMATION_MESSAGE );
                            }
                        } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, parallaxReticleBtn,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, leftRightSlider,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, leftRightTF,
//                                                  0, rowIdx++,
//                                                  1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, markBtn,
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, leftRightTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Enter reticle offset:" ),
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, alphaTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, betaTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, computeBtn,
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, resultTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, helpBtn,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            this.update();
        }

        private void update() {
            alphaTF.setEnabled( parallaxInstrumentEnabled );
            betaTF.setEnabled( parallaxInstrumentEnabled );
            leftRightSlider.setEnabled( parallaxInstrumentEnabled );
            leftRightTF.setEnabled( parallaxInstrumentEnabled );
            markBtn.setEnabled( parallaxInstrumentEnabled );
            computeBtn.setEnabled( parallaxInstrumentEnabled );
            helpBtn.setEnabled( parallaxInstrumentEnabled );
            module.setOrientationReticleOn( parallaxInstrumentEnabled );
        }
    }

    private class BrightnessPanel extends JPanel {
        private JButton brightnessReticleBtn;
        private boolean photometerEnabled = false;
        private JTextField brightnessTF;
        private String enableString = "Enable";
        private String disableString = "Disable";

        public BrightnessPanel() {
            super( new GridBagLayout() );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Photometer" );
            this.setBorder( titledBorder );

            // Create controls
            brightnessTF = null;
            brightnessTF = new JTextField( 6 );
            final Format brightnessFormatter = new DecimalFormat( "###E0" );

            final SimpleObserver photometerObserver = new SimpleObserver() {
                public void update() {
                    StarView starView = ( (UniverseModel)module.getModel() ).getStarShip().getStarView();
                    List visibleStars = starView.getVisibleStars();
                    double brightness = 0;
                    for( int i = 0; i < visibleStars.size(); i++ ) {
                        Star star = (Star)visibleStars.get( i );
                        if( module.getPhotometerReticle().contains( starView.getLocation( star ) ) ) {
                            brightness += star.getLuminance() * ( 1 / star.getLocation().distanceSq( starView.getPov() ) );
                        }
                    }
                    brightnessTF.setText( brightnessFormatter.format( new Double( brightness ) ) );
                }
            };

            brightnessReticleBtn = new JButton( new AbstractAction( enableString ) {
                public void actionPerformed( ActionEvent e ) {
                    photometerEnabled = !photometerEnabled;
                    module.setPhotometerReticle( photometerEnabled );
                    brightnessReticleBtn.setText( photometerEnabled ? disableString : enableString );
                    if( photometerEnabled ) {
                        module.getPhotometerReticle().getPhotometer().addObserver( photometerObserver );
                    }
                    else {
                        module.getPhotometerReticle().getPhotometer().removeObserver( photometerObserver );
                    }
                    BrightnessPanel.this.update();
                }
            } );

            // Lay out panel
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, brightnessReticleBtn,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Brightness:" ),
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, brightnessTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }

        private void update() {
            brightnessTF.setEnabled( photometerEnabled );
        }
    }

    private class HyperjumpPanel extends JPanel {
        public HyperjumpPanel() {
            super( new GridBagLayout() );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Hyperdrive" );
            this.setBorder( titledBorder );

            // Create controls
            final JTextField distanceTF = new JTextField( 6 );
            final JTextField directionTF = new JTextField( 6 );

            JButton jumpBtn = new JButton( new AbstractAction( "Jump" ) {
                public void actionPerformed( ActionEvent e ) {
                    double jumpDistance = Double.parseDouble( distanceTF.getText() );
                    double jumpDirection = Double.parseDouble( directionTF.getText() ) * Math.PI / 180;
                    module.getStarship().move( jumpDistance, jumpDirection );
                    module.getStarView().update();
                }
            });

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Jump distance"),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, distanceTF,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Jump direction"),
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, directionTF,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, jumpBtn,
                                                  0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
