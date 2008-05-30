/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class TestZoomedView extends JFrame {
    
    private static final Color ACID_COLOR = Color.RED;
    private static final Color BASE_COLOR = Color.BLUE;
    
    private final LinearValueControl _phControl, _circleDiameterControl, _particleTransparencyControl, _particleDiameterControl, _maxParticlesControl;
    private final PPath _circleNode;
    private final Ellipse2D _circlePath;
    private final PComposite _particlesParent;
    
    public TestZoomedView() {
        super( "TestZoomedView" );
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        };
        
        // pH control
        _phControl = new LinearValueControl( 0, 14, "pH:", "#0.0", "", new HorizontalLayoutStrategy() );
        _phControl.setValue( 7 );
        _phControl.addChangeListener( changeListener );
        
        // circle diameter control
        _circleDiameterControl = new LinearValueControl( 50, 500, "circle diameter:", "###0", "", new HorizontalLayoutStrategy() );
        _circleDiameterControl.setValue( 250 );
        _circleDiameterControl.addChangeListener( changeListener );
        
        // max particles
        _maxParticlesControl = new LinearValueControl( 100, 10000,  "max # particles:", "####0", "", new HorizontalLayoutStrategy() );
        _maxParticlesControl.setValue( 5000 );
        _maxParticlesControl.addChangeListener( changeListener );
        
        // particle size
        _particleDiameterControl = new LinearValueControl( 1, 25, "particle diameter:", "#0.0", "", new HorizontalLayoutStrategy() );
        _particleDiameterControl.setValue( 10 );
        _particleDiameterControl.addChangeListener( changeListener );
        
        // transparency
        _particleTransparencyControl = new LinearValueControl( 0, 1, "particle transparency:", "0.00", "", new HorizontalLayoutStrategy() );
        _particleTransparencyControl.setValue( 1 );
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
        
        // create particles
        final double particleDiameter = _particleDiameterControl.getValue();
        final int alpha = (int)( 255 * _particleTransparencyControl.getValue() );
        Color acidColor = ColorUtils.createColor( ACID_COLOR, alpha );
        Color baseColor = ColorUtils.createColor( BASE_COLOR, alpha );
        
        //XXX quick test of particle size & transparency
        PNode acidNode = createParticle( particleDiameter, acidColor );
        acidNode.setOffset( -( circleDiameter/4 - 10 ), 0 );
        _particlesParent.addChild( acidNode );
        PNode baseNode = createParticle( particleDiameter, baseColor );
        baseNode.setOffset( +( circleDiameter/4 - 10 ), 0 );
        _particlesParent.addChild( baseNode );
        
        //TODO create random distribution of particles based on pH and max # particles
    }
    
    /* creates a particle node given a diameter and color */
    private static PNode createParticle( double diameter, Color color ) {
        Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );
        PPath pathNode = new PPath( shape );
        pathNode.setPaint( color );
        pathNode.setStroke( null );
        return pathNode;
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
        TestZoomedView frame = new TestZoomedView();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
