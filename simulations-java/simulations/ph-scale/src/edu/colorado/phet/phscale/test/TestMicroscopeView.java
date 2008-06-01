/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
 * Since the scale is logarithmic and can vary by 14 orders of magnitude, we're concerned 
 * about the feasibility of this idea.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestMicroscopeView extends JFrame {
    
    private static final Color H3O_COLOR = Color.RED;
    private static final Color OH_COLOR = Color.BLUE;
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private final LinearValueControl _phControl, _circleDiameterControl, _particleTransparencyControl, _particleDiameterControl, _maxParticlesControl;
    private final PPath _circleNode;
    private final Ellipse2D _circlePath;
    private final PComposite _particlesParent;
    private final Random _randomDistance, _randomAngle;
    
    public TestMicroscopeView() {
        super( "ph-scale: Microscope View test" );
        
        _randomDistance = new Random();
        _randomAngle = new Random();
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        
        // pH control
        _phControl = new LinearValueControl( 0, 14, "pH:", "#0.0", "", new HorizontalLayoutStrategy() );
        _phControl.setValue( 7 );
        _phControl.setUpDownArrowDelta( 1 );
        _phControl.addChangeListener( changeListener );
        
        // circle diameter control
        _circleDiameterControl = new LinearValueControl( 50, 500, "circle diameter:", "###0", "", new HorizontalLayoutStrategy() );
        _circleDiameterControl.setValue( 250 );
        _circleDiameterControl.setUpDownArrowDelta( 1 );
        _circleDiameterControl.addChangeListener( changeListener );
        
        // max particles
        _maxParticlesControl = new LinearValueControl( 1000, 10000,  "max # particles:", "####0", "", new HorizontalLayoutStrategy() );
        _maxParticlesControl.setValue( 5000 );
        _maxParticlesControl.setUpDownArrowDelta( 1 );
        _maxParticlesControl.addChangeListener( changeListener );
        
        // particle size
        _particleDiameterControl = new LinearValueControl( 1, 25, "particle diameter:", "#0.0", "", new HorizontalLayoutStrategy() );
        _particleDiameterControl.setValue( 6 );
        _particleDiameterControl.setUpDownArrowDelta( 0.1 );
        _particleDiameterControl.addChangeListener( changeListener );
        
        // transparency
        _particleTransparencyControl = new LinearValueControl( 0, 1, "particle transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        _particleTransparencyControl.setValue( 0.75 );
        _particleTransparencyControl.setUpDownArrowDelta( 0.01 );
        _particleTransparencyControl.addChangeListener( changeListener );
        
        // control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _phControl, row++, column );
        layout.addComponent( _circleDiameterControl, row++, column );
        layout.addComponent( _maxParticlesControl, row++, column );
        layout.addComponent( _particleDiameterControl, row++, column );
        layout.addComponent( _particleTransparencyControl, row++, column );
        
        // circle
        _circlePath = new Ellipse2D.Double();
        _circleNode = new PClip();
        _circleNode.setStroke( new BasicStroke( 2f ) );
        _circleNode.setOffset( _circleDiameterControl.getMaximum()/2 + 50, _circleDiameterControl.getMaximum()/2 + 50 );
        
        // parent for particles, clipped to circle
        _particlesParent = new PComposite();
        _circleNode.addChild( _particlesParent );
        
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( _circleNode );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( canvas, BorderLayout.CENTER );
        mainPanel.add( controlPanel, BorderLayout.EAST );
        getContentPane().add( mainPanel );
        
        update();
    }
    
    private void update() {
        
        // clear particles
        _particlesParent.removeAllChildren();
        
        // adjust circle diameter
        double circleDiameter = _circleDiameterControl.getValue();
        _circlePath.setFrame( -circleDiameter/2, -circleDiameter/2, circleDiameter, circleDiameter );
        _circleNode.setPathTo( _circlePath );
        
        // calculate the ratio of H30 to OH
        final double pH = _phControl.getValue();
        final double ratio = ratio_H30_to_OH( pH );
        System.out.println( "pH=" + pH + " H30:OH = " + ratio + " OH:H3O = " + 1/ratio );//XXX
        
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
        System.out.println( "pH=" + pH + " #H30= " + numH30 + " #OH= " + numOH );//XXX
        
        // create particles
        _particlesParent.removeAllChildren();
        if ( numH30 > numOH ) {
            createH30( numH30 );
            createOH( numOH );
        }
        else {
            createOH( numOH );
            createH30( numH30 );
        }
    }
    
    private void createH30( int count ) {
        final int alpha = (int)( 255 * _particleTransparencyControl.getValue() );
        Color color = ColorUtils.createColor( H3O_COLOR, alpha );
        createParticles( count, color );
    }
    
    private void createOH( int count ) {
        final int alpha = (int)( 255 * _particleTransparencyControl.getValue() );
        Color color = ColorUtils.createColor( OH_COLOR, alpha );
        createParticles( count, color );
    }
    
    private void createParticles( int count, Color color ) {
        final double circleRadius = _circleDiameterControl.getValue() / 2;
        final double particleDiameter = _particleDiameterControl.getValue();
        for ( int i = 0; i < count; i++ ) {
            double distance = _randomDistance.nextDouble() * circleRadius;
            double angle = _randomAngle.nextDouble() * ( 2 * Math.PI );
            double x = PolarCartesianConverter.getX( distance, angle );
            double y = PolarCartesianConverter.getY( distance, angle );
            PNode node = createParticle( particleDiameter, color );
            node.setOffset( x, y );
            _particlesParent.addChild( node );
        }
    }
    
    /* creates a particle node given a diameter and color */
    private static PNode createParticle( double diameter, Color color ) {
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath pathNode = new PPath( shape );
        pathNode.setPaint( color );
        pathNode.setStroke( null );
        return pathNode;
    }
    
    // Compute the ratio of H30 to OH.
    // Between pH of 6 and 8, we use the actual log scale.
    // Below 6 and above 8, use a linear scale.
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

    public static void main( String args[] ) {
        TestMicroscopeView frame = new TestMicroscopeView();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
