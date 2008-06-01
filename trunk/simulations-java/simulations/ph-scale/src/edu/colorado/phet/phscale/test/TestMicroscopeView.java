/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
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
 * A "microscope view" of a liquid is desired, showing the relative numbers of acids and bases.
 * Since the scale is log and can vary by 14 orders of magnitude, we're concerned 
 * about the feasibility of this idea.  This prototype explores ways to "fake" the 
 * log relationship.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestMicroscopeView extends JFrame {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Color H3O_COLOR = Color.RED;
    private static final Color OH_COLOR = Color.BLUE;

    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private static final int MIN_CONTROL_PANEL_WIDTH = 275;
    
    private static final DoubleRange PH_RANGE = new DoubleRange( 0, 14, 7 );
    private static final double PH_DELTA = 1;
    private static final String PH_PATTERN = "0.0";
    
    private static final DoubleRange CIRCLE_DIAMETER_RANGE = new DoubleRange( 50, 500, 250 );
    private static final double CIRCLE_DIAMETER_DELTA = 1;
    private static final String CIRCLE_DIAMETER_PATTERN = "##0";
        
    private static final DoubleRange MAX_PARTICLES_RANGE = new DoubleRange( 1000, 10000, 5000 );
    private static final double MAX_PARTICLES_DELTA = 1;
    private static final String MAX_PARTICLES_PATTERN = "####0";
    
    private static final DoubleRange PARTICLE_DIAMETER_RANGE = new DoubleRange( 1, 25, 6 );
    private static final double PARTICLE_DIAMETER_DELTA = 0.1;
    private static final String PARTICLE_DIAMETER_PATTERN = "#0.0";
    
    private static final DoubleRange TRANSPARENCY_RANGE = new DoubleRange( 0, 1, 0.75 );
    private static final double TRANSPARENCY_DELTA = 0.01;
    private static final String TRANSPARENCY_PATTERN = "0.00";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final JLabel _countH30Label, _countOHLabel;
    private final LinearValueControl _phControl, _circleDiameterControl,
            _particleTransparencyControl, _particleDiameterControl,
            _maxParticlesControl;

    private final PPath _circleNode;
    private final Ellipse2D _circlePath;
    private final PComposite _particlesParent;
    private final Random _randomDistance, _randomAngle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TestMicroscopeView() {
        super( "ph-scale: Microscope View test" );

        _randomDistance = new Random();
        _randomAngle = new Random();

        ChangeListener globalUpdateListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateAll();
            }
        };

        ChangeListener particleUpdateListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateParticles();
            }
        };
        
        // count displays
        _countH30Label = new JLabel();
        _countH30Label.setFont( _countH30Label.getFont().deriveFont( Font.BOLD, 18 ) );
        _countH30Label.setForeground( H3O_COLOR );
        _countOHLabel = new JLabel();
        _countOHLabel.setFont( _countOHLabel.getFont().deriveFont( Font.BOLD, 18 ) );
        _countOHLabel.setForeground( OH_COLOR );
        JPanel countPanel = new JPanel();
        countPanel.setBorder( new TitledBorder( "particle counts" ) );
        EasyGridBagLayout countPanelLayout = new EasyGridBagLayout( countPanel );
        countPanel.setLayout( countPanelLayout );
        int row = 0;
        int column = 0;
        countPanelLayout.addComponent( _countH30Label, row, column++ );
        countPanelLayout.addComponent( Box.createHorizontalStrut( 15 ), row, column++ );
        countPanelLayout.addComponent( _countOHLabel, row, column++ );

        // pH control
        _phControl = new LinearValueControl( PH_RANGE.getMin(), PH_RANGE.getMax(), "pH:", PH_PATTERN, "" );
        _phControl.setValue( PH_RANGE.getDefault() );
        _phControl.setUpDownArrowDelta( PH_DELTA );
        _phControl.addChangeListener( globalUpdateListener );

        // circle diameter control
        _circleDiameterControl = new LinearValueControl( CIRCLE_DIAMETER_RANGE.getMin(), CIRCLE_DIAMETER_RANGE.getMax(), "circle diameter:", CIRCLE_DIAMETER_PATTERN, "" );
        _circleDiameterControl.setValue( CIRCLE_DIAMETER_RANGE.getDefault() );
        _circleDiameterControl.setUpDownArrowDelta( CIRCLE_DIAMETER_DELTA );
        _circleDiameterControl.addChangeListener( globalUpdateListener );

        // max particles
        _maxParticlesControl = new LinearValueControl( MAX_PARTICLES_RANGE.getMin(), MAX_PARTICLES_RANGE.getMax(), "max # particles:", MAX_PARTICLES_PATTERN, "" );
        _maxParticlesControl.setValue( MAX_PARTICLES_RANGE.getDefault() );
        _maxParticlesControl.setUpDownArrowDelta( MAX_PARTICLES_DELTA );
        _maxParticlesControl.addChangeListener( globalUpdateListener );

        // particle size
        _particleDiameterControl = new LinearValueControl( PARTICLE_DIAMETER_RANGE.getMin(), PARTICLE_DIAMETER_RANGE.getMax(), "particle diameter:", PARTICLE_DIAMETER_PATTERN, "" );
        _particleDiameterControl.setValue( PARTICLE_DIAMETER_RANGE.getDefault() );
        _particleDiameterControl.setUpDownArrowDelta( PARTICLE_DIAMETER_DELTA );
        _particleDiameterControl.addChangeListener( particleUpdateListener );

        // transparency
        _particleTransparencyControl = new LinearValueControl( TRANSPARENCY_RANGE.getMin(), TRANSPARENCY_RANGE.getMax(), "particle transparency:", TRANSPARENCY_PATTERN, "" );
        _particleTransparencyControl.setValue( TRANSPARENCY_RANGE.getDefault() );
        _particleTransparencyControl.setUpDownArrowDelta( TRANSPARENCY_DELTA );
        _particleTransparencyControl.addChangeListener( particleUpdateListener );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Double( _particleTransparencyControl.getMinimum() ), new JLabel( "invisible" ) );
        labelTable.put( new Double( _particleTransparencyControl.getMaximum() ), new JLabel( "opaque" ) );
        _particleTransparencyControl.setTickLabels( labelTable );

        // Reset All button
        JButton resetAllButton = new JButton( "Reset All" );
        resetAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetAll();
            }
        });
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        row = 0;
        column = 0;
        controlPanelLayout.addComponent( Box.createHorizontalStrut( MIN_CONTROL_PANEL_WIDTH ), row++, column );
        controlPanelLayout.addFilledComponent( countPanel, row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addComponent( _phControl, row++, column );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addComponent( _circleDiameterControl, row++, column );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addComponent( _maxParticlesControl, row++, column );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addComponent( _particleDiameterControl, row++, column );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addComponent( _particleTransparencyControl, row++, column );
        controlPanelLayout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        controlPanelLayout.addAnchoredComponent( resetAllButton, row++, column, GridBagConstraints.CENTER );
        
        // circle
        _circlePath = new Ellipse2D.Double();
        _circleNode = new PClip();
        _circleNode.setStroke( new BasicStroke( 2f ) );
        _circleNode.setOffset( _circleDiameterControl.getMaximum() / 2 + 50, _circleDiameterControl.getMaximum() / 2 + 50 );

        // parent for particles, clipped to circle
        _particlesParent = new PComposite();
        _circleNode.addChild( _particlesParent );

        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( _circleNode );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        getContentPane().add( mainPanel );

        updateAll();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------

    private void resetAll() {
        _phControl.setValue( PH_RANGE.getDefault() );
        _circleDiameterControl.setValue( CIRCLE_DIAMETER_RANGE.getDefault() );
        _maxParticlesControl.setValue( MAX_PARTICLES_RANGE.getDefault() );
        _particleDiameterControl.setValue( PARTICLE_DIAMETER_RANGE.getDefault() );
        _particleTransparencyControl.setValue( TRANSPARENCY_RANGE.getDefault() );
    }
    
    /*
     * Updates everything.
     */
    private void updateAll() {

        // clear particles
        _particlesParent.removeAllChildren();

        // adjust circle diameter
        double circleDiameter = _circleDiameterControl.getValue();
        _circlePath.setFrame( -circleDiameter / 2, -circleDiameter / 2, circleDiameter, circleDiameter );
        _circleNode.setPathTo( _circlePath );

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
        _countH30Label.setText( "<html>H<sub>3</sub>O<sup>+</sup>=" + numH30 );
        _countOHLabel.setText( "<html>OH<sup>-</sup>=" + numOH );

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

        Color colorH3O = getColorH3O();
        Color colorOH = getColorOH();
        final double diameter = _particleDiameterControl.getValue();

        Iterator i = _particlesParent.getChildrenIterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof H3ONode ) {
                H3ONode node = (H3ONode) o;
                node.setPaint( colorH3O );
                node.setDiameter( diameter );
            }
            else if ( o instanceof OHNode ) {
                OHNode node = (OHNode) o;
                node.setPaint( colorOH );
                node.setDiameter( diameter );
            }
        }
    }

    /*
     * Gets the color for H3O, with alpha channel.
     */
    private Color getColorH3O() {
        final int alpha = (int) ( 255 * _particleTransparencyControl.getValue() );
        return ColorUtils.createColor( H3O_COLOR, alpha );
    }

    /*
     * Gets the color for OH, with alpha channel.
     */
    private Color getColorOH() {
        final int alpha = (int) ( 255 * _particleTransparencyControl.getValue() );
        return ColorUtils.createColor( OH_COLOR, alpha );
    }

    /*
     * Creates H3O nodes at random locations in the circle.
     */
    private void createH3ONodes( int count ) {
        final double particleDiameter = _particleDiameterControl.getValue();
        Point2D pOffset = new Point2D.Double();
        Color color = getColorH3O();
        for ( int i = 0; i < count; i++ ) {
            getRandomPointInCircle( pOffset );
            PNode node = new H3ONode( particleDiameter, color );
            node.setOffset( pOffset );
            _particlesParent.addChild( node );
        }
    }

    /*
     * Creates OH nodes at random locations in the circle.
     */
    private void createOHNodes( int count ) {
        final double particleDiameter = _particleDiameterControl.getValue();
        Point2D pOffset = new Point2D.Double();
        Color color = getColorOH();
        for ( int i = 0; i < count; i++ ) {
            getRandomPointInCircle( pOffset );
            PNode node = new OHNode( particleDiameter, color );
            node.setOffset( pOffset );
            _particlesParent.addChild( node );
        }
    }

    /*
     * Gets a random point inside the circle.
     */
    private void getRandomPointInCircle( Point2D pOutput ) {
        double circleRadius = _circleDiameterControl.getValue() / 2;
        double distance = _randomDistance.nextDouble() * circleRadius;
        double angle = _randomAngle.nextDouble() * ( 2 * Math.PI );
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

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( String args[] ) {
        TestMicroscopeView frame = new TestMicroscopeView();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
