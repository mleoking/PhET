/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * GunControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunControlPanel extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double X_MARGIN = 40;
    private static final double Y_MARGIN = 10;
    private static final double Y_SPACING = 5;
    
    private static final Dimension INTENSITY_CONTROL_SIZE = new Dimension( 175, 20 );
    private static final Color LABEL_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GunTypeControl _gunTypeControl;
    
    private PhetPNode _lightControls;
    private LightTypeControl _lightTypeControl;
    private IntensityControl _lightIntensityControl;
    private WavelengthControl _wavelengthControl;
    
    private PhetPNode _alphaParticleControls;
    private IntensityControl _alphaParticlesIntensityControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public GunControlPanel( PSwingCanvas canvas ) {
        super();
        
        PImage panel = PImageFactory.create( HAConstants.IMAGE_GUN_PANEL );
        
        _gunTypeControl = new GunTypeControl( canvas );
        
        _lightControls = new PhetPNode();
        _lightTypeControl = new LightTypeControl();
        _lightIntensityControl = new IntensityControl( INTENSITY_CONTROL_SIZE );
        _wavelengthControl = new WavelengthControl( canvas,
                HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH,
                HAConstants.UV_COLOR, HAConstants.IR_COLOR );
        
        _alphaParticleControls = new PhetPNode();
        _alphaParticlesIntensityControl = new IntensityControl( INTENSITY_CONTROL_SIZE );
        _alphaParticlesIntensityControl.setColor( HAConstants.ALPHA_PARTICLES_COLOR );

        // Wrappers for Swing components
        PSwing lightTypeControlWrapper = new PSwing( canvas, _lightTypeControl );
        PSwing lightIntensityControlWrapper = new PSwing( canvas, _lightIntensityControl );
        PSwing alphaParticlesIntensityControlWrapper = new PSwing( canvas, _alphaParticlesIntensityControl );
        
        // Layering, back to front
        {
            _lightControls.addChild( lightTypeControlWrapper );
            _lightControls.addChild( lightIntensityControlWrapper );
            _lightControls.addChild( _wavelengthControl );

            _alphaParticleControls.addChild( alphaParticlesIntensityControlWrapper );

            addChild( panel );
            addChild( _gunTypeControl );
            addChild( _lightControls );
            addChild( _alphaParticleControls );
        }

        // Positioning
        {
           _gunTypeControl.setOffset( X_MARGIN, Y_MARGIN );
           PBounds gtb = _gunTypeControl.getFullBounds();
               
           _lightControls.setOffset( gtb.getX(), gtb.getY() + gtb.getHeight() + Y_SPACING );
           lightTypeControlWrapper.setOffset( 0, 0 );
           PBounds ltb = lightTypeControlWrapper.getFullBounds();
           lightIntensityControlWrapper.setOffset( ltb.getX(), ltb.getY() + ltb.getHeight() + Y_SPACING );
           PBounds lib = lightIntensityControlWrapper.getFullBounds();
           double yFudge = 20; // fudge factor for text field above wavelength slider
           _wavelengthControl.setOffset( lib.getX(), lib.getY() + lib.getHeight() + Y_SPACING + yFudge );
           
           _alphaParticleControls.setOffset( gtb.getX(), gtb.getY() + gtb.getHeight() + Y_SPACING );
           alphaParticlesIntensityControlWrapper.setOffset( 0, 0 );
        }
        
        // Scale the panel background image
        {
            _wavelengthControl.setWavelength( HAConstants.MAX_WAVELENGTH );
            
            PBounds pb = panel.getFullBounds();
            PBounds gtb = _gunTypeControl.getFullBounds();
            PBounds lb = _lightControls.getFullBounds();
            PBounds ab = _alphaParticleControls.getFullBounds();
            
            double xFudge = 30; // fudge factor for text field above wavelength slider
            
            double width = Math.max( gtb.getWidth(), Math.max( lb.getWidth() - xFudge, ab.getWidth() ) ) + ( 2 * X_MARGIN );
            double height = gtb.getHeight() + Y_SPACING + Math.max( lb.getHeight(), ab.getHeight() ) + ( 2 * Y_MARGIN );
           
            double scaleX = width / pb.getWidth();
            double scaleY = height / pb.getHeight();
            AffineTransform xform = new AffineTransform();
            xform.scale( scaleX, scaleY );
            panel.setTransform( xform );
        }
        
        // Colors
        _gunTypeControl.setLabelsForeground( LABEL_COLOR );
        _lightTypeControl.setLabelsForeground( LABEL_COLOR );
        _lightIntensityControl.setUnitsForeground( LABEL_COLOR );
        _wavelengthControl.setUnitsForeground( LABEL_COLOR );
        _alphaParticlesIntensityControl.setUnitsForeground( LABEL_COLOR );
        
        // Event handlers
        {
            // Gun type
            _gunTypeControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleGunTypeChange();
                }
            } );
            
            // Light type control handler
            _lightTypeControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleLightTypeChange();
                }
            } );

            // Wavelength control handler
            _wavelengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleWavelengthChange();
                }
            } );
        }
        
        // Default state
        _gunTypeControl.setLightSelected();
        _lightControls.setVisible( _gunTypeControl.isLightSelected() );
        _alphaParticleControls.setVisible( _gunTypeControl.isAlphaParticlesSelected() );
        _lightTypeControl.setMonochromaticSelected();
        _lightIntensityControl.setValue( 100 );
        _wavelengthControl.setWavelength( VisibleColor.MIN_WAVELENGTH );
        _alphaParticlesIntensityControl.setValue( 100 );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public GunTypeControl getGunTypeControl() {
        return _gunTypeControl;
    }
    
    public LightTypeControl getLightTypeControl() {
        return _lightTypeControl;
    }
    
    public IntensityControl getLightIntensityControl() {
        return _lightIntensityControl;
    }
    
    public WavelengthControl getWavelengthControl() {
        return _wavelengthControl;
    }
    
    public IntensityControl getAlphaParticlesIntensityControl() {
        return _alphaParticlesIntensityControl;
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Changes the controls based on the type of gun selected.
     */
    private void handleGunTypeChange() {
        _lightControls.setVisible( _gunTypeControl.isLightSelected() );
        _alphaParticleControls.setVisible( _gunTypeControl.isAlphaParticlesSelected() );
    }
    
    /*
     * Changes visibility and color of controls to match the type of light.
     */
    private void handleLightTypeChange() {
        if ( _lightTypeControl.isMonochromaticSelected() ) {
            _lightIntensityControl.setColor( _wavelengthControl.getColor() );
            _wavelengthControl.setVisible( true );
        }
        else {
            _lightIntensityControl.setColor( Color.WHITE );
            _wavelengthControl.setVisible( false );
        }
    }
    
    /*
     * Changes the intensity control's color to match the wavelength.
     */
    private void handleWavelengthChange() {
        _lightIntensityControl.setColor( _wavelengthControl.getColor() );
    }
}
