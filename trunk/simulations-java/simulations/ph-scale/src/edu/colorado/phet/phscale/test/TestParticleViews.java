// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This is a prototype for a feature of the ph-scale sim.
 * A "beaker view" of a liquid is desired, showing the relative numbers of acids and bases.
 * The amount of liquid in the beaker is variable.
 * Since the scale is log and can vary by 14 orders of magnitude, we're concerned 
 * about the feasibility of this idea.  This prototype explores ways to "fake" the 
 * log relationship.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestParticleViews extends JFrame {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String FRAME_TITLE = "ph Scale: test particle views";

    private static final Color DEFAULT_CANVAS_COLOR = new Color( 220, 220, 220 );
    private static final Color DEFAULT_H2O_COLOR= Color.WHITE;
    private static final Color DEFAULT_H3O_COLOR = Color.RED;
    private static final Color DEFAULT_OH_COLOR = Color.BLUE;

    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private static final int MIN_CONTROL_PANEL_WIDTH = 275;
    
    private static final DoubleRange PH_RANGE = new DoubleRange( 0, 14, 7 );
    private static final double PH_DELTA = 0.1;
    private static final String PH_PATTERN = "0.0";
    
    private static final DoubleRange MICROSCOPE_DIAMETER_RANGE = new DoubleRange( 50, 500, 250 );
    private static final double MICROSCOPE_DIAMETER_DELTA = 1;
    private static final String MICROSCOPE_DIAMETER_PATTERN = "##0";
    
    private static final DoubleRange BEAKER_WIDTH_RANGE = new DoubleRange( 50, 500, 250 );
    private static final double BEAKER_WIDTH_DELTA = 1;
    private static final String BEAKER_WIDTH_PATTERN = "##0";
    
    private static final DoubleRange BEAKER_HEIGHT_RANGE = new DoubleRange( 50, 500, 300 );
    private static final double BEAKER_HEIGHT_DELTA = 1;
    private static final String BEAKER_HEIGHT_PATTERN = "##0";
     
    private static final DoubleRange LIQUID_FILL_RANGE = new DoubleRange( 0, 1, 0.5 );
    private static final double LIQUID_FILL_DELTA = 0.01;
    private static final String LIQUID_FILL_PATTERN = "0.00";
    
    private static final DoubleRange MAX_PARTICLES_RANGE = new DoubleRange( 1000, 10000, 5000 );
    private static final double MAX_PARTICLES_DELTA = 1;
    private static final String MAX_PARTICLES_PATTERN = "####0";
    
    private static final DoubleRange PARTICLE_DIAMETER_RANGE = new DoubleRange( 1, 25, 4 );
    private static final double PARTICLE_DIAMETER_DELTA = 0.1;
    private static final String PARTICLE_DIAMETER_PATTERN = "#0.0";
    
    private static final DoubleRange TRANSPARENCY_RANGE = new DoubleRange( 0, 1, 0.5 );
    private static final double TRANSPARENCY_DELTA = 0.01;
    private static final String TRANSPARENCY_PATTERN = "0.00";
    
    private static final String H2O_HTML = "H<sub>2</sub>O";
    private static final String H3O_HTML = "H<sub>3</sub>O<sup>+</sup>";
    private static final String OH_HTML = "OH<sup>-</sup>";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final JLabel _countH30Label, _countOHLabel;
    private final JRadioButton _beakerRadioButton, _microscopeRadioButton;
    private final LinearValueControl _phControl, 
            _microscopeDiameterControl,
            _beakerWidthControl, _beakerHeightControl, _liquidFillControl,
            _particleTransparencyControl, _particleDiameterControl,
            _maxParticlesControl;
    private final ColorControl _colorCanvasControl, _colorH2OControl, _colorH3OControl, _colorOHControl;
    private final JPanel _countPanel, _microscopePanel, _beakerPanel, _particlePanel;

    private final PCanvas _canvas;
    private final BeakerNode _beakerNode;
    private final MicroscopeNode _microscopeNode;
    private final Random _random;
    private Color _colorH3O, _colorOH;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TestParticleViews() {
        super( FRAME_TITLE );

        _random = new Random();
        _colorH3O = DEFAULT_H3O_COLOR;
        _colorOH = DEFAULT_OH_COLOR;

        // beaker or microscope radio buttons
        JPanel viewPanel = new JPanel();
        {
            JLabel label = new JLabel( "view:" );
            
            _beakerRadioButton = new JRadioButton( "beaker" );
            _beakerRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateView();
                }
            });
            
            _microscopeRadioButton = new JRadioButton( "microscope" );
            _microscopeRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateView();
                }
            });
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _beakerRadioButton );
            buttonGroup.add( _microscopeRadioButton );
            _beakerRadioButton.setSelected( true );
            
            EasyGridBagLayout viewLayout = new EasyGridBagLayout( viewPanel );
            viewPanel.setLayout( viewLayout );
            int row = 0;
            int column = 0;
            viewLayout.addComponent( label, row, column++ );
            viewLayout.addComponent( _beakerRadioButton, row, column++ );
            viewLayout.addComponent( _microscopeRadioButton, row, column++ );
        }
        
        // count displays
        _countPanel = new JPanel();
        {
            _countH30Label = new JLabel();
            _countH30Label.setFont( _countH30Label.getFont().deriveFont( Font.BOLD, 18 ) );
            _countH30Label.setForeground( _colorH3O );
            _countOHLabel = new JLabel();
            _countOHLabel.setFont( _countOHLabel.getFont().deriveFont( Font.BOLD, 18 ) );
            _countOHLabel.setForeground( _colorOH );

            _countPanel.setBackground( DEFAULT_H2O_COLOR );
            _countPanel.setBorder( new TitledBorder( "particle counts" ) );
            EasyGridBagLayout countPanelLayout = new EasyGridBagLayout( _countPanel );
            _countPanel.setLayout( countPanelLayout );
            int row = 0;
            int column = 0;
            countPanelLayout.addComponent( _countH30Label, row, column++ );
            countPanelLayout.addComponent( Box.createHorizontalStrut( 15 ), row, column++ );
            countPanelLayout.addComponent( _countOHLabel, row, column++ );
        }

        // pH
        _phControl = new LinearValueControl( PH_RANGE.getMin(), PH_RANGE.getMax(), "pH:", PH_PATTERN, "", new TestLayoutStrategy() );
        _phControl.setValue( PH_RANGE.getDefault() );
        _phControl.setUpDownArrowDelta( PH_DELTA );
        _phControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                generateParticles();
            }
        } );
        
        // microscope controls
        _microscopePanel = new JPanel();
        {
            _microscopeDiameterControl = new LinearValueControl( MICROSCOPE_DIAMETER_RANGE.getMin(), MICROSCOPE_DIAMETER_RANGE.getMax(), "microscope diameter:", MICROSCOPE_DIAMETER_PATTERN, "", new TestLayoutStrategy() );
            _microscopeDiameterControl.setValue( MICROSCOPE_DIAMETER_RANGE.getDefault() );
            _microscopeDiameterControl.setUpDownArrowDelta( MICROSCOPE_DIAMETER_DELTA );
            _microscopeDiameterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateMicroscope();
                }
            });
            
            _microscopePanel.setBorder( new TitledBorder( "microscope controls" ) );
            EasyGridBagLayout microscopePanelLayout = new EasyGridBagLayout( _microscopePanel );
            _microscopePanel.setLayout( microscopePanelLayout );
            int row = 0;
            int column = 0;
            microscopePanelLayout.addComponent( _microscopeDiameterControl, row, column++ );
        }

        _beakerPanel = new JPanel();
        {
            // beaker width
            _beakerWidthControl = new LinearValueControl( BEAKER_WIDTH_RANGE.getMin(), BEAKER_WIDTH_RANGE.getMax(), "beaker width:", BEAKER_WIDTH_PATTERN, "", new TestLayoutStrategy() );
            _beakerWidthControl.setValue( BEAKER_WIDTH_RANGE.getDefault() );
            _beakerWidthControl.setUpDownArrowDelta( BEAKER_WIDTH_DELTA );
            _beakerWidthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateBeaker();
                }
            });

            // beaker height
            _beakerHeightControl = new LinearValueControl( BEAKER_HEIGHT_RANGE.getMin(), BEAKER_HEIGHT_RANGE.getMax(), "beaker height:", BEAKER_HEIGHT_PATTERN, "", new TestLayoutStrategy() );
            _beakerHeightControl.setValue( BEAKER_HEIGHT_RANGE.getDefault() );
            _beakerHeightControl.setUpDownArrowDelta( BEAKER_HEIGHT_DELTA );
            _beakerHeightControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateBeaker();
                }
            });

            // beaker fill
            _liquidFillControl = new LinearValueControl( LIQUID_FILL_RANGE.getMin(), LIQUID_FILL_RANGE.getMax(), "fill beaker:", LIQUID_FILL_PATTERN, "", new TestLayoutStrategy() );
            _liquidFillControl.setValue( LIQUID_FILL_RANGE.getDefault() );
            _liquidFillControl.setUpDownArrowDelta( LIQUID_FILL_DELTA );
            _liquidFillControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    _beakerNode.setPercentFilled( _liquidFillControl.getValue() );
                }
            } );
            Hashtable<Double,JLabel> beakerFillLabelTable = new Hashtable<Double,JLabel>();
            beakerFillLabelTable.put( new Double( _liquidFillControl.getMinimum() ), new JLabel( "empty" ) );
            beakerFillLabelTable.put( new Double( _liquidFillControl.getMaximum() ), new JLabel( "full" ) );
            _liquidFillControl.setTickLabels( beakerFillLabelTable );
            
            _beakerPanel.setBorder( new TitledBorder( "beaker controls" ) );
            EasyGridBagLayout beakerPanelLayout = new EasyGridBagLayout( _beakerPanel );
            _beakerPanel.setLayout( beakerPanelLayout );
            int row = 0;
            int column = 0;
            beakerPanelLayout.addComponent( _beakerWidthControl, row++, column );
            beakerPanelLayout.addComponent( _beakerHeightControl, row++, column );
            beakerPanelLayout.addComponent( _liquidFillControl, row++, column );
        }
        
        _particlePanel = new JPanel();
        {
            // max particles
            _maxParticlesControl = new LinearValueControl( MAX_PARTICLES_RANGE.getMin(), MAX_PARTICLES_RANGE.getMax(), "max # particles:", MAX_PARTICLES_PATTERN, "", new TestLayoutStrategy() );
            _maxParticlesControl.setValue( MAX_PARTICLES_RANGE.getDefault() );
            _maxParticlesControl.setUpDownArrowDelta( MAX_PARTICLES_DELTA );
            _maxParticlesControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    generateParticles();
                }
            });

            // particle size
            _particleDiameterControl = new LinearValueControl( PARTICLE_DIAMETER_RANGE.getMin(), PARTICLE_DIAMETER_RANGE.getMax(), "particle diameter:", PARTICLE_DIAMETER_PATTERN, "", new TestLayoutStrategy() );
            _particleDiameterControl.setValue( PARTICLE_DIAMETER_RANGE.getDefault() );
            _particleDiameterControl.setUpDownArrowDelta( PARTICLE_DIAMETER_DELTA );
            _particleDiameterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateParticles();
                }
            });

            // transparency
            _particleTransparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "particle transparency:", TRANSPARENCY_PATTERN, "", new TestLayoutStrategy() );
            _particleTransparencyControl.setValue( TRANSPARENCY_RANGE.getDefault() );
            _particleTransparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
            _particleTransparencyControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateParticles();
                }
            });
            Hashtable<Double,JLabel> particleTransparencyLabelTable = new Hashtable<Double,JLabel>();
            particleTransparencyLabelTable.put( new Double( _particleTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
            particleTransparencyLabelTable.put( new Double( _particleTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
            _particleTransparencyControl.setTickLabels( particleTransparencyLabelTable );
            
            _particlePanel.setBorder( new TitledBorder( "particle controls" ) );
            EasyGridBagLayout particlePanelLayout = new EasyGridBagLayout( _particlePanel );
            _particlePanel.setLayout( particlePanelLayout );
            int row = 0;
            int column = 0;
            particlePanelLayout.addComponent( _maxParticlesControl, row++, column );
            particlePanelLayout.addComponent( _particleDiameterControl, row++, column );
            particlePanelLayout.addComponent( _particleTransparencyControl, row++, column );
        }

        // colors
        JPanel colorsPanel = new JPanel();
        {
            _colorCanvasControl = new ColorControl( this, "play area:", DEFAULT_CANVAS_COLOR );
            _colorCanvasControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    _canvas.setBackground( _colorCanvasControl.getColor() );
                }
            } );

            _colorH2OControl = new ColorControl( this, "<html>" + H2O_HTML + ":</html>", DEFAULT_H2O_COLOR );
            _colorH2OControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Color colorH2O = _colorH2OControl.getColor();
                    _countPanel.setBackground( colorH2O );
                    _beakerNode.setLiquidColor( colorH2O );
                    _microscopeNode.setLiquidColor( colorH2O );
                }
            } );

            _colorH3OControl = new ColorControl( this, "<html>" + H3O_HTML + ":</html>", _colorH3O );
            _colorH3OControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    _colorH3O = _colorH3OControl.getColor();
                    _countH30Label.setForeground( _colorH3O );
                    updateParticles();
                }
            } );

            _colorOHControl = new ColorControl( this, "<html>" + OH_HTML + ":</html>", _colorOH );
            _colorOHControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    _colorOH = _colorOHControl.getColor();
                    _countOHLabel.setForeground( _colorOH );
                    updateParticles();
                }
            } );
            
            colorsPanel.setBorder( new TitledBorder( "colors" ) );
            EasyGridBagLayout colorsPanelLayout = new EasyGridBagLayout( colorsPanel );
            colorsPanel.setLayout( colorsPanelLayout );
            int row = 0;
            int column = 0;
            colorsPanelLayout.addComponent( _colorH3OControl, row, column++ );
            colorsPanelLayout.addComponent( Box.createHorizontalStrut( 15 ), row, column++ );
            colorsPanelLayout.addComponent( _colorOHControl, row, column++ );
            row++;
            column = 0;
            colorsPanelLayout.addComponent( _colorH2OControl, row, column++ );
            colorsPanelLayout.addComponent( Box.createHorizontalStrut( 15 ), row, column++ );
            colorsPanelLayout.addComponent( _colorCanvasControl, row, column++ );
        }


        // Reset All button
        JButton resetAllButton = new JButton( "Reset All" );
        resetAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetAll();
            }
        } );

        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        {
            JPanel innerPanel = new JPanel();
            controlPanel.add( innerPanel ); // so that controls stay anchored to the top of the control panel
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( Box.createHorizontalStrut( MIN_CONTROL_PANEL_WIDTH ), row++, column );
            layout.addFilledComponent( viewPanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( _countPanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( _phControl, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( _microscopePanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( _beakerPanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( _particlePanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( colorsPanel, row++, column, GridBagConstraints.HORIZONTAL );
            layout.addAnchoredComponent( resetAllButton, row++, column, GridBagConstraints.CENTER );
        }
        
        // scroll bars
        JPanel scrollPanel = new JPanel( new BorderLayout() );
        JScrollPane scrollPane = new JScrollPane( controlPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scrollPanel.add( scrollPane, BorderLayout.CENTER );
        
        // beaker
        _beakerNode = new BeakerNode( BEAKER_WIDTH_RANGE.getDefault(), BEAKER_HEIGHT_RANGE.getDefault(), LIQUID_FILL_RANGE.getDefault(), DEFAULT_H2O_COLOR );
        _beakerNode.setOffset( _beakerWidthControl.getMaximum() / 2 + 50, _beakerWidthControl.getMaximum() / 2 + 50 );

        // microscope
        _microscopeNode = new MicroscopeNode( MICROSCOPE_DIAMETER_RANGE.getDefault(), DEFAULT_H2O_COLOR );
        _microscopeNode.setOffset( _beakerNode.getOffset() );
        
        _canvas = new PCanvas();
        _canvas.getLayer().addChild( _beakerNode );
        _canvas.getLayer().addChild( _microscopeNode );
        _canvas.setBackground( DEFAULT_CANVAS_COLOR );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( _canvas, BorderLayout.CENTER );
        mainPanel.add( scrollPanel, BorderLayout.EAST );
        getContentPane().add( mainPanel );

        updateView();
        updateBeaker();
        updateMicroscope();
        updateParticles();
        generateParticles();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    private void resetAll() {
        
        // colors
        _beakerNode.setLiquidColor( DEFAULT_H2O_COLOR );
        _microscopeNode.setLiquidColor( DEFAULT_H2O_COLOR );
        _colorCanvasControl.setColor( DEFAULT_CANVAS_COLOR );
        _canvas.setBackground( DEFAULT_CANVAS_COLOR );
        _colorH2OControl.setColor( DEFAULT_H2O_COLOR );
        _colorH3OControl.setColor( DEFAULT_H3O_COLOR );
        _colorH3O = DEFAULT_H3O_COLOR;
        _colorOHControl.setColor( DEFAULT_OH_COLOR );
        _colorOH = DEFAULT_OH_COLOR;
        
        // controls
        _beakerRadioButton.setSelected( true );
        updateView();
        _phControl.setValue( PH_RANGE.getDefault() );
        _microscopeDiameterControl.setValue( MICROSCOPE_DIAMETER_RANGE.getDefault() );
        _beakerWidthControl.setValue( BEAKER_WIDTH_RANGE.getDefault() );
        _beakerHeightControl.setValue( BEAKER_HEIGHT_RANGE.getDefault() );
        _liquidFillControl.setValue( LIQUID_FILL_RANGE.getDefault() );
        _maxParticlesControl.setValue( MAX_PARTICLES_RANGE.getDefault() );
        _particleDiameterControl.setValue( PARTICLE_DIAMETER_RANGE.getDefault() );
        _particleTransparencyControl.setValue( TRANSPARENCY_RANGE.getDefault() );
    }
    
    /*
     * Updates when the view is switched between beaker and microscope.
     * Visibility of related nodes and controls is mutually exclusive.
     */
    private void updateView() {
        
        boolean b = _beakerRadioButton.isSelected();
        
        _beakerNode.setVisible( b );
        _beakerPanel.setVisible( b );
        
        _microscopeNode.setVisible( !b );
        _microscopePanel.setVisible( !b );
    }
    
    private void updateBeaker() {
        _beakerNode.setSize( _beakerWidthControl.getValue(), _beakerHeightControl.getValue() );
        _beakerNode.setPercentFilled( _liquidFillControl.getValue() );
        generateParticles();
    }
    
    private void updateMicroscope() {
        _microscopeNode.setDiameter( _microscopeDiameterControl.getValue() );
        generateParticles();
    }
    
    /*
     * Generates particles in the beaker and microscope.
     */
    private void generateParticles() {

        // clear particles
        _beakerNode.removeAllParticles();
        _microscopeNode.removeAllParticles();

        // calculate the ratio of H30 to OH
        final double pH = _phControl.getValue();
        final double ratio = ratio_H30_to_OH( pH );

        // calculate the number of H30 and OH particles
        final double multiplier = (int) _maxParticlesControl.getValue() / ratio_H30_to_OH( 0 );
        int numH30, numOH;
        if ( ratio == 1 ) {
            numH30 = numOH = (int) Math.max( 1, multiplier );
        }
        else if ( ratio > 1 ) {
            numH30 = (int) ( multiplier * ratio );
            numOH = (int) Math.max( 1, multiplier );
        }
        else {
            numH30 = (int) Math.max( 1, multiplier );
            numOH = (int) ( multiplier / ratio );
        }
        _countH30Label.setText( "<html>" + H3O_HTML + "=" + numH30 );
        _countOHLabel.setText( "<html>" + OH_HTML + "=" + numOH );

        // create particles
        if ( numH30 > numOH ) {
            createH3ONodes( numH30 );
            createOHNodes( numOH );
        }
        else {
            createOHNodes( numOH );
            createH3ONodes( numH30 );
        }
        
//        System.out.println( "pH=" + pH + " H30:OH=" + ratio + " OH:H3O=" + 1 / ratio + " #H30=" + numH30 + " #OH=" + numOH );//XXX
    }
    
    /*
     * Updates the diameter and color of all particles.
     */
    private void updateParticles() {

        final double diameter = _particleDiameterControl.getValue();
        Color colorH3O = getColorH3O();
        Color colorOH = getColorOH();

        _beakerNode.setParticleDiameter( diameter );
        _beakerNode.setH3OColor( colorH3O );
        _beakerNode.setOHColor( colorOH );

        _microscopeNode.setParticleDiameter( diameter );
        _microscopeNode.setH3OColor( colorH3O );
        _microscopeNode.setOHColor( colorOH );
    }

    /*
     * Gets the color for H3O, with alpha channel.
     */
    private Color getColorH3O() {
        final int alpha = (int) ( 255 * _particleTransparencyControl.getValue() );
        return ColorUtils.createColor( _colorH3O, alpha );
    }

    /*
     * Gets the color for OH, with alpha channel.
     */
    private Color getColorOH() {
        final int alpha = (int) ( 255 * _particleTransparencyControl.getValue() );
        return ColorUtils.createColor( _colorOH, alpha );
    }

    /*
     * Creates H3O nodes at random locations in the beaker and microscope.
     */
    private void createH3ONodes( int count ) {
        final double particleDiameter = _particleDiameterControl.getValue();
        Point2D pOffset = new Point2D.Double();
        Color color = getColorH3O();
        for ( int i = 0; i < count; i++ ) {
            
            // add to beaker
            getRandomPointInBeaker( pOffset );
            ParticleNode p1 = new H3ONode( particleDiameter, color );
            p1.setOffset( pOffset );
            _beakerNode.addParticle( p1 );
            
            // add to microscope
            getRandomPointInMicroscope( pOffset );
            ParticleNode p2 = new H3ONode( particleDiameter, color );
            p2.setOffset( pOffset );
            _microscopeNode.addParticle( p2 );
        }
    }

    /*
     * Creates OH nodes at random locations in the beaker and microscope.
     */
    private void createOHNodes( int count ) {
        final double particleDiameter = _particleDiameterControl.getValue();
        Point2D pOffset = new Point2D.Double();
        Color color = getColorOH();
        for ( int i = 0; i < count; i++ ) {
            
            // add to beaker
            getRandomPointInBeaker( pOffset );
            ParticleNode p1 = new OHNode( particleDiameter, color );
            p1.setOffset( pOffset );
            _beakerNode.addParticle( p1 );
            
            // add to microscope
            getRandomPointInMicroscope( pOffset );
            ParticleNode p2 = new OHNode( particleDiameter, color );
            p2.setOffset( pOffset );
            _microscopeNode.addParticle( p2 );
        }
    }

    /*
     * Gets a random point inside the circle.
     */
    private void getRandomPointInBeaker( Point2D pOutput ) {
        double beakerWidth = _beakerWidthControl.getValue();
        double beakerHeight = _beakerHeightControl.getValue();
        double x = ( -beakerWidth/2 ) + ( _random.nextDouble() * beakerWidth );
        double y = ( -beakerHeight/2 ) + ( _random.nextDouble() * beakerHeight );
        pOutput.setLocation( x, y );
    }
    
    /*
     * Gets a random point inside the microscope.
     */
    private void getRandomPointInMicroscope( Point2D pOutput ) {
        double circleRadius = _microscopeDiameterControl.getValue() / 2;
        double distance = Math.sqrt( _random.nextDouble() ) * circleRadius;
        double angle = _random.nextDouble() * ( 2 * Math.PI );
        double x = PolarCartesianConverter.getX( distance, angle );
        double y = PolarCartesianConverter.getY( distance, angle );
        pOutput.setLocation( x, y );
    }

    //----------------------------------------------------------------------------
    // Static utilities
    //----------------------------------------------------------------------------
    
    /* 
     * Computes the ratio of H30 to OH.
     * Between pH of 6 and 8, we use the actual log scale.
     * Below 6 and above 8, use a linear scale for "Hollywood" visualization.
     */
    private static double ratio_H30_to_OH( double pH ) {
        double ratio;
        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
            ratio = concentrationH30( pH ) / concentrationOH( pH );
        }
        else if ( pH < ACID_PH_THRESHOLD ) {
            double multiplier = ACID_PH_THRESHOLD - pH + 1;
            ratio = multiplier * concentrationH30( ACID_PH_THRESHOLD ) / concentrationOH( ACID_PH_THRESHOLD );
        }
        else {
            double multiplier = 1 / ( pH - BASE_PH_THRESHOLD + 1 );
            ratio = multiplier * concentrationH30( BASE_PH_THRESHOLD ) / concentrationOH( BASE_PH_THRESHOLD );
        }
        return ratio;
    }

    private static double concentrationH30( double pH ) {
        return Math.pow( 10, -pH );
    }

    private static double concentrationOH( double pH ) {
        return Math.pow( 10, -pOH( pH ) );
    }

    private static double pOH( double pH ) {
        return 14 - pH;
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* base class for anything node that contains particles */
    private static abstract class ParticleContainerNode extends PComposite {
        
        private final PNode _particlesParent;
        
        public ParticleContainerNode() {
            super();
            _particlesParent = new PComposite();
        }
        
        protected PNode getParticlesParent() {
            return _particlesParent;
        }
        
        public void addParticle( ParticleNode particleNode ) {
            _particlesParent.addChild( particleNode );
        }
        
        public void removeAllParticles() {
            _particlesParent.removeAllChildren();
        }
        
        public void setParticleDiameter( double diameter ) {
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                ((ParticleNode)_particlesParent.getChild(i)).setDiameter( diameter );
            }
        }
        
        public void setH3OColor( Color color ) {
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                PNode node = _particlesParent.getChild( i );
                if ( node instanceof H3ONode ) {
                    node.setPaint( color );
                }
            }
        }
        
        public void setOHColor( Color color ) {
            int count = _particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                PNode node = _particlesParent.getChild( i );
                if ( node instanceof OHNode ) {
                    node.setPaint( color );
                }
            }
        }
    }
    
    /* microscope view of a small area of some liquid */
    private static class MicroscopeNode extends ParticleContainerNode {
        
        private final PPath _circleNode;
        private final Ellipse2D _circlePath;
        
        private double _diameter;
        
        public MicroscopeNode( double diameter, Color liquidColor ) {
            super();
            
            _circlePath = new Ellipse2D.Double();
            _circleNode = new PClip();
            _circleNode.setPaint( liquidColor );
            _circleNode.setStroke( new BasicStroke( 2f ) );
            addChild( _circleNode );
            
            _circleNode.addChild( getParticlesParent() ); // clip particles to circle
            
            setDiameter( diameter );
        }
        
        public void setLiquidColor( Color color ) {
            _circleNode.setPaint( color );
        }
        
        public void setDiameter( double diameter ) {
            if ( diameter != _diameter ) {
                _diameter = diameter;
                update();
            }
        }
        
        private void update() {
            _circlePath.setFrame( -_diameter / 2, -_diameter / 2, _diameter, _diameter );
            _circleNode.setPathTo( _circlePath );
        }
        
    }
    
    /* a beaker that contains a liquid */
    private static class BeakerNode extends ParticleContainerNode {
        
        private final PPath _beakerNode, _liquidNode;
        private final GeneralPath _beakerPath; 
        private final Rectangle2D _liquidPath;
        
        private double _width, _height;
        private double _percentFilled;
        
        public BeakerNode( double width, double height, double percentFilled, Color liquidColor ) {
            super();
            
            _percentFilled = percentFilled;
            
            _liquidPath = new Rectangle2D.Double();
            _liquidNode = new PClip();
            _liquidNode.setPaint( liquidColor );
            _liquidNode.setStroke( null );
            addChild( _liquidNode );
            
            _beakerPath = new GeneralPath();
            _beakerNode = new PPath();
            _beakerNode.setPaint( null );
            _beakerNode.setStroke( new BasicStroke( 2f ) );
            addChild( _beakerNode );
            
            _liquidNode.addChild( getParticlesParent() ); // clip particles to liquid
            
            setSize( width, height );
        }
        
        public void setLiquidColor( Color color ) {
            _liquidNode.setPaint( color );
        }
        
        private void setSize( double width, double height ) {
            if ( width != _width || height != _height ) {
                _width = width;
                _height = height;
                updateBeakerSize();
            }
        }
        
        public void setPercentFilled( double percentFilled ) {
            if ( percentFilled != _percentFilled ) {
            _percentFilled = percentFilled;
            updateLiquid();
            }
        }
      
        private void updateBeakerSize() {
            _beakerPath.reset();
            _beakerPath.moveTo( (float) -_width/2, (float) -_height/2 );
            _beakerPath.lineTo( (float) -_width/2, (float) +_height/2 );
            _beakerPath.lineTo( (float) +_width/2, (float) +_height/2 );
            _beakerPath.lineTo( (float) +_width/2, (float) -_height/2 );
            _beakerNode.setPathTo( _beakerPath );
            updateLiquid();
        }
        
        private void updateLiquid() {
            double liquidHeight = _percentFilled * _height;
            _liquidPath.setRect( -_width/2, -_height/2 + (_height - liquidHeight ), _width, liquidHeight );
            _liquidNode.setPathTo( _liquidPath );
        }
    }

    /* Base class for all particle nodes */
    private static abstract class ParticleNode extends PPath {

        private Ellipse2D _ellipse;

        public ParticleNode( double diameter, Color color ) {
            super();
            _ellipse = new Ellipse2D.Double();
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            _ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
            setPathTo( _ellipse );
        }
    }

    /* H30 particle node */
    private static class H3ONode extends ParticleNode {
        public H3ONode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH particle node */
    private static class OHNode extends ParticleNode {
        public OHNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* layout all parts of value controls in a horizontal line */
    public class TestLayoutStrategy implements ILayoutStrategy {

        public TestLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {

            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();
            JComponent textField = valueControl.getTextField();
            JComponent valueLabel = valueControl.getValueLabel();
            JComponent unitsLabel = valueControl.getUnitsLabel();

            // Label - slider - textfield - units.
            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addComponent( valueLabel, 0, 0 );
            layout.addFilledComponent( slider, 0, 1, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( textField, 0, 2, GridBagConstraints.HORIZONTAL );
            layout.addComponent( unitsLabel, 0, 3 );
        }
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( String args[] ) {
        TestParticleViews frame = new TestParticleViews();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
