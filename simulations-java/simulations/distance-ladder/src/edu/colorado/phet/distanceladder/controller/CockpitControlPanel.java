/**
 * Class: CockpitControlPanel
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:52:27 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.StringResourceReader;
import edu.colorado.phet.distanceladder.exercise.HelpDialog;
import edu.colorado.phet.distanceladder.model.*;
import edu.colorado.phet.distanceladder.view.StarMapGraphic;
import edu.colorado.phet.distanceladder.view.StarshipCoordsGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

public class CockpitControlPanel extends JPanel {
    private CockpitModule module;
    boolean parallaxInstrumentEnabled = false;
    boolean photometerEnabled = false;
    private ParallaxPanel parallaxPanel;
    private PhotometerPanel photometerPanel;
    private UniverseModel model;
    private StarMapPanel starMapPanel;
    private GridBagLayout layout;

    public CockpitControlPanel( CockpitModule module ) {

        this.module = module;
        this.model = (UniverseModel)module.getModel();
        parallaxPanel = new ParallaxPanel();
        photometerPanel = new PhotometerPanel();

        layout = new GridBagLayout();
        this.setLayout( layout );
//        this.setLayout( new GridBagLayout() );
        JPanel pilotingPanel = new JPanel( new GridBagLayout() );
        JPanel instrumentPanel = new JPanel( new GridBagLayout() );
        instrumentPanel.setMinimumSize( pilotingPanel.getMinimumSize() );
        this.add( pilotingPanel );
        this.add( instrumentPanel );
        int rowIdx = 0;
        try {
            starMapPanel = new StarMapPanel();
//            GraphicsUtil.addGridBagComponent( this, pilotingPanel,
//                                              0, 0,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.CENTER );
//            GraphicsUtil.addGridBagComponent( this, instrumentPanel,
//                                              0, 1,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.CENTER );
//
//            GraphicsUtil.addGridBagComponent( this, starMapPanel,
//                                              0, 0,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.NORTH );
//            GraphicsUtil.addGridBagComponent( this, new ControlPanel( module ),
//                                              0, 1,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.NORTH );
//
//            GraphicsUtil.addGridBagComponent( this, parallaxPanel,
//                                              0, 0,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.NORTH );
//            GraphicsUtil.addGridBagComponent( this, photometerPanel,
//                                              0, 1,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.NORTH );


//            GraphicsUtil.addGridBagComponent( this, pilotingPanel,
//                                              0, 0,
//                                              1, 1,
//                                              GridBagConstraints.HORIZONTAL,
//                                              GridBagConstraints.CENTER );
//            GraphicsUtil.addGridBagComponent( this, instrumentPanel,
//                                              0, 1,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.CENTER );


            GraphicsUtil.addGridBagComponent( this, starMapPanel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.NORTH );
            GraphicsUtil.addGridBagComponent( this, new ControlPanel( module ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.NORTH );
            GraphicsUtil.addGridBagComponent( this, parallaxPanel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, photometerPanel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
//        ((GridBagLayout)pilotingPanel.getLayout()).layoutContainer( pilotingPanel );
//        ((GridBagLayout)instrumentPanel.getLayout()).layoutContainer( instrumentPanel );
//        layout.layoutContainer( this );
        this.module = module;

        setParallaxEnabled( false );
        setPhotometerEnabled( false );
    }

    public void setParallaxEnabled( boolean isEnabled ) {
        parallaxPanel.setVisible( isEnabled );
        parallaxInstrumentEnabled = isEnabled;
        parallaxPanel.update();
    }

    public void setPhotometerEnabled( boolean isEnabled ) {
        photometerPanel.setVisible( isEnabled );
        photometerEnabled = isEnabled;
        photometerPanel.update();
    }

    public void setStarshipCordinateGraphicEnabled( boolean isEnabled ) {
        this.starMapPanel.setStarshipCordinateGraphicEnabled( isEnabled );
    }


    //
    // Inner Classes
    //

    private class ControlPanel extends JPanel {

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
                    double theta = Math.toRadians( orientationSlider.getValue() );
                    PointOfView pov = module.getCockpitPov();
                    pov.setTheta( theta );
                    module.setPov( pov );
                }
            } );

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
        private String parallaxHelp;
        private double leftRightSliderFactor = 1;
        private JTextField alphaTF;
        private JTextField betaTF;
//        private JSlider leftRightSlider;
        private JTextField leftRightTF;
        private JButton computeBtn;
        private JButton markBtn;
        private JButton helpBtn;
        private JLabel reticleLabel1;
        private JLabel reticleLabel2;
        JLabel distanceLabel = new JLabel( "Distance" );


        public ParallaxPanel() {
            super( new GridBagLayout() );

            // Load messages
            StringResourceReader srr = new StringResourceReader();
            parallaxHelp = srr.read( "messages/parallax-help.html" );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Parallax Instrument" );
            this.setBorder( titledBorder );

            parallaxReticleBtn = new JButton( new AbstractAction( enableString ) {
                public void actionPerformed( ActionEvent e ) {
                    parallaxInstrumentEnabled = !parallaxInstrumentEnabled;
                    parallaxReticleBtn.setText( parallaxInstrumentEnabled ? disableString : enableString );
                    ParallaxPanel.this.update();
                }
            } );

            leftRightTF = new JTextField( "0", 6 );
            leftRightTF.setEditable( false );
            leftRightTF.setBackground( Color.white );
            leftRightTF.setHorizontalAlignment( JTextField.RIGHT );

            // Add an observer to the star ship that puts its relative position in the leftRightTF
            final PointOfView markPov = new PointOfView();
            model.getStarShip().addObserver( new SimpleObserver() {
                private DecimalFormat leftRightFormat = new DecimalFormat( "##.0" );

                public void update() {
                    if( leftRightTF.isEnabled() ) {
                        double x = model.getStarShip().getPov().distance( markPov );
                        leftRightTF.setText( leftRightFormat.format( new Double( x ) ) );
                    }
                }
            } );

//            leftRightSlider = new JSlider( -500, 500, 0 );
//            leftRightSlider.setMajorTickSpacing( 100 );
//            leftRightSlider.setMinorTickSpacing( 25 );
//            leftRightSlider.setPaintTicks( true );
//            leftRightSlider.setPaintTrack( true );
//            leftRightSlider.addChangeListener( new ChangeListener() {
//                private int parallaxRef = 0;
//                private int parallaxCurr = 0;
//
//                public void stateChanged( ChangeEvent e ) {
//                    parallaxCurr = leftRightSlider.getValue();
//
//                    // Set the point of view of the cockpit
//                    double d = (double)( parallaxCurr - parallaxRef ) / leftRightSliderFactor;
//                    parallaxRef = parallaxCurr;
//                    PointOfView pov = module.getCockpitPov();
//                    pov.setLocation( pov.getX() - d * Math.sin( pov.getTheta() ),
//                                     pov.getY() + d * Math.cos( pov.getTheta() ) );
//                    module.setPov( pov );
//
//                    // Display the lateral offset
//                    leftRightTF.setText( Integer.toString( parallaxRef - markRef ) );
//                }
//            } );

            markBtn = new JButton( new AbstractAction( "Mark" ) {
                public void actionPerformed( ActionEvent e ) {
//                    markRef = leftRightSlider.getValue();
                    leftRightTF.setText( "0" );
                    markPov.setPointOfView( model.getStarShip().getPov() );
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
                    double b = Math.abs( Double.parseDouble( leftRightTF.getText() ) / leftRightSliderFactor );
                    double alpha = Math.toRadians( Double.parseDouble( alphaTF.getText() ) );
//                    double beta = Math.toRadians( Double.parseDouble( betaTF.getText() ) );
                    double d = b / Math.tan( alpha );
//                    double d = b / ( Math.tan( beta ) - Math.tan( alpha ) );
                    resultTF.setText( distFormatter.format( new Double( d ) ) );
                }
            } );
            computeBtn.setEnabled( false );

            alphaTF.getDocument().addDocumentListener( new DocumentListener() {
                public void changedUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) /*&& !betaTF.getText().equals( "" ) */ );
                }

                public void insertUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) /*&& !betaTF.getText().equals( "" ) */ );
                }

                public void removeUpdate( DocumentEvent e ) {
                    computeBtn.setEnabled( !alphaTF.getText().equals( "" ) /* && !betaTF.getText().equals( "" ) */ );
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
                    JDialog dlg = new HelpDialog( (JFrame)SwingUtilities.getRoot( helpBtn ),
                                                  new JEditorPane( "text/html; charset=iso-8859-1", parallaxHelp ) );
                    dlg.setLocation( 500, 50 );
                    dlg.setVisible( true );
                }
            } );

            int rowIdx = 0;
            try {
//                GraphicsUtil.addGridBagComponent( this, parallaxReticleBtn,
//                                                  0, rowIdx++, 2, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, leftRightSlider,
//                                                  0, rowIdx++, 2, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
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
                reticleLabel1 = new JLabel( "Reticle offset:" );
                GraphicsUtil.addGridBagComponent( this, reticleLabel1,
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, alphaTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
//                reticleLabel2 = new JLabel( "Reticle offset B:" );
//                GraphicsUtil.addGridBagComponent( this, reticleLabel2,
//                                                  0, rowIdx, 1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, betaTF,
//                                                  1, rowIdx++, 1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, computeBtn,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, distanceLabel,
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, resultTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, helpBtn,
                                                  0, rowIdx++, 2, 1,
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
            leftRightTF.setEnabled( parallaxInstrumentEnabled );
            markBtn.setEnabled( parallaxInstrumentEnabled );
            computeBtn.setEnabled( parallaxInstrumentEnabled );
            helpBtn.setEnabled( parallaxInstrumentEnabled );
            reticleLabel1.setEnabled( parallaxInstrumentEnabled );
            distanceLabel.setEnabled( parallaxInstrumentEnabled );
//            reticleLabel2.setEnabled( parallaxInstrumentEnabled );
//            leftRightSlider.setEnabled( parallaxInstrumentEnabled );
//            module.setParallaxReticleOn( parallaxInstrumentEnabled );
        }
    }

    private class PhotometerPanel extends JPanel {
        private JButton brightnessReticleBtn;
        private JTextField brightnessTF;
        private String enableString = "Enable";
        private String disableString = "Disable";
        private JLabel textFieldLabel;
        private SimpleObserver photometerObserver;

        public PhotometerPanel() {
            super( new GridBagLayout() );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Photometer" );
            this.setBorder( titledBorder );

            // Create controls
            brightnessTF = null;
            brightnessTF = new JTextField( 6 );
            brightnessTF.setHorizontalAlignment( JTextField.RIGHT );
            final Format brightnessFormatter = new DecimalFormat( "###E0" );

            photometerObserver = new SimpleObserver() {
                public void update() {
                    StarView starView = model.getStarShip().getStarView();
                    List visibleStars = starView.getVisibleStars();
                    double brightness = 0;
                    for( int i = 0; i < visibleStars.size(); i++ ) {
                        Star star = (Star)visibleStars.get( i );
                        if( module.getPhotometerReticle().contains( starView.getApparentLocation( star ) ) ) {
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
                    PhotometerPanel.this.update();
                }
            } );

            // Lay out panel
            int rowIdx = 0;
            try {
//                GraphicsUtil.addGridBagComponent( this, brightnessReticleBtn,
//                                                  0, rowIdx++, 2, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
                textFieldLabel = new JLabel( "Brightness:" );
                GraphicsUtil.addGridBagComponent( this, textFieldLabel,
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

            update();
        }

        private void update() {
            brightnessTF.setEnabled( photometerEnabled );
            textFieldLabel.setEnabled( photometerEnabled );
            if( photometerEnabled ) {
                module.getPhotometerReticle().getPhotometer().addObserver( photometerObserver );
                photometerObserver.update();
            }
            else {
                module.getPhotometerReticle().getPhotometer().removeObserver( photometerObserver );
                brightnessTF.setText( "" );
            }
        }
    }

    private class HyperjumpPanel extends JPanel {
        private String hyperjumpHelp;
        private JButton helpBtn;

        public HyperjumpPanel() {
            super( new GridBagLayout() );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Hyperjump" );
            this.setBorder( titledBorder );

            // Load messages
            StringResourceReader srr = new StringResourceReader();
            hyperjumpHelp = srr.read( "messages/hyperjump-help.html" );


            // Create controls
            final JTextField distanceTF = new JTextField( 6 );
            JButton jumpBtn = new JButton( new AbstractAction( "Jump" ) {
                public void actionPerformed( ActionEvent e ) {
                    double jumpDistance = Double.parseDouble( distanceTF.getText() );
                    module.getStarship().move( jumpDistance );
                    module.getStarView().update();
                    CockpitControlPanel.this.repaint();
                }
            } );

            helpBtn = new JButton( new AbstractAction( "Help" ) {
                public void actionPerformed( ActionEvent e ) {
                    JDialog dlg = new HelpDialog( (JFrame)SwingUtilities.getRoot( helpBtn ),
                                                  new JEditorPane( "text/html; charset=iso-8859-1", hyperjumpHelp ) );
                    dlg.setLocation( 500, 50 );
                    dlg.setVisible( true );
                }
            } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Distance" ),
                                                  0, rowIdx, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, distanceTF,
                                                  1, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, new JLabel( "Jump direction" ),
//                                                  0, rowIdx++, 1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, directionTF,
//                                                  0, rowIdx++, 1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, jumpBtn,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, helpBtn,
                                                  0, rowIdx++, 2, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

    private class StarMapPanel extends ApparatusPanel implements SimpleObserver {
        private AffineTransform atx = new AffineTransform();
        private Line2D.Double orientationLine = new Line2D.Double();
        private AffineTransform lineTx = new AffineTransform();
        private Starship starship;
        private Stroke lineStroke = new BasicStroke( 1f );
        private StarshipCoordsGraphic starshipCoordsGraphic;
        private StarMapGraphic starMapGraphic;

        public StarMapPanel() {
            this.setLayout( new FlowLayout() );

            starship = model.getStarShip();
            starship.addObserver( this );
            this.setPreferredSize( new Dimension( 200, 200 ) );
            starMapGraphic = new StarMapGraphic( this, model.getStarField() );
            starMapGraphic.setStarGraphicRadius( 20 );

            // Make the coordinate graphic and thicken the lines
            starshipCoordsGraphic = new StarshipCoordsGraphic( starship, this );
            starshipCoordsGraphic.setRingStroke( new BasicStroke( 14f ) );
            setStarshipCordinateGraphicEnabled( true );

            this.addGraphic( starMapGraphic );
//            starMapGraphic.addGraphic( starshipCoordsGraphic );
            orientationLine.setLine( 0, 0, this.getPreferredSize().getWidth(), 0 );
        }


        public void update() {
            this.repaint();
        }

        public void setStarshipCordinateGraphicEnabled( boolean isEnabled ) {
            if( isEnabled ) {
                starMapGraphic.addGraphic( starshipCoordsGraphic );
                CockpitControlPanel.this.repaint();
            }
            else {
                starMapGraphic.remove( starshipCoordsGraphic );
                CockpitControlPanel.this.repaint();
            }

        }
    }
}
