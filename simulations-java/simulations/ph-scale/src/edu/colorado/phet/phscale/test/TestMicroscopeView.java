/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
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
 * TestZoomedView is a design-phase prototype for the ph-scale sim.
 * A "zoomed" view of a liquid is desired, showing the relative numbers of acids and bases.
 * Since the scale is log and can vary by 14 orders of magnitude, we're concerned 
 * about the feasibility of this idea.
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final LinearValueControl _phControl, _circleDiameterControl,
            _particleTransparencyControl, _particleDiameterControl,
            _maxParticlesControl;
    private final JLabel _countH30Label, _countOHLabel;
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

        // pH control
        _phControl = new LinearValueControl( 0, 14, "pH:", "#0.0", "", new HorizontalLayoutStrategy() );
        _phControl.setValue( 7 );
        _phControl.setUpDownArrowDelta( 1 );
        _phControl.addChangeListener( globalUpdateListener );

        // circle diameter control
        _circleDiameterControl = new LinearValueControl( 50, 500, "circle diameter:", "###0", "", new HorizontalLayoutStrategy() );
        _circleDiameterControl.setValue( 250 );
        _circleDiameterControl.setUpDownArrowDelta( 1 );
        _circleDiameterControl.addChangeListener( globalUpdateListener );

        // max particles
        _maxParticlesControl = new LinearValueControl( 1000, 10000, "max # particles:", "####0", "", new HorizontalLayoutStrategy() );
        _maxParticlesControl.setValue( 5000 );
        _maxParticlesControl.setUpDownArrowDelta( 1 );
        _maxParticlesControl.addChangeListener( globalUpdateListener );

        // particle size
        _particleDiameterControl = new LinearValueControl( 1, 25, "particle diameter:", "#0.0", "", new HorizontalLayoutStrategy() );
        _particleDiameterControl.setValue( 6 );
        _particleDiameterControl.setUpDownArrowDelta( 0.1 );
        _particleDiameterControl.addChangeListener( particleUpdateListener );

        // transparency
        _particleTransparencyControl = new LinearValueControl( 0, 1, "particle transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        _particleTransparencyControl.setValue( 0.75 );
        _particleTransparencyControl.setUpDownArrowDelta( 0.01 );
        _particleTransparencyControl.addChangeListener( particleUpdateListener );
        
        // count displays
        _countH30Label = new JLabel();
        _countOHLabel = new JLabel();
        JPanel countPanel = new JPanel();
        countPanel.setBorder( new TitledBorder( "particle counts" ) );
        EasyGridBagLayout countPanelLayout = new EasyGridBagLayout( countPanel );
        countPanel.setLayout( countPanelLayout );
        int row = 0;
        int column = 0;
        countPanelLayout.addComponent( _countH30Label, row++, column );
        countPanelLayout.addComponent( _countOHLabel, row++, column );

        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        row = 0;
        column = 0;
        controlPanelLayout.addComponent( _phControl, row++, column );
        controlPanelLayout.addComponent( _circleDiameterControl, row++, column );
        controlPanelLayout.addComponent( _maxParticlesControl, row++, column );
        controlPanelLayout.addComponent( _particleDiameterControl, row++, column );
        controlPanelLayout.addComponent( _particleTransparencyControl, row++, column );
        controlPanelLayout.addFilledComponent( countPanel, row++, column, GridBagConstraints.HORIZONTAL );

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
        _countH30Label.setText( "<html>H<sub>3</sub>O<sup>+</sup> = " + numH30 );
        _countOHLabel.setText( "<html>OH<sup>-</sup> = " + numOH );

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

    /* Control layout strategy that puts all parts of the control in one row. */
    private static class HorizontalLayoutStrategy implements ILayoutStrategy {

        public HorizontalLayoutStrategy() {}

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
        TestMicroscopeView frame = new TestMicroscopeView();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
