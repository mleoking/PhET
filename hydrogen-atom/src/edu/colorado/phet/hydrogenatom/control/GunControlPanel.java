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
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.enums.GunMode;
import edu.colorado.phet.hydrogenatom.enums.LightType;
import edu.colorado.phet.hydrogenatom.model.Gun;
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
public class GunControlPanel extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String FONT_NAME = HAConstants.DEFAULT_FONT_NAME;
    private static final int FONT_STYLE = HAConstants.DEFAULT_FONT_STYLE;
    private static final int DEFAULT_FONT_SIZE = HAConstants.DEFAULT_FONT_SIZE;
    private static final String FONT_SIZE_RESOURCE = "gunControls.font.size";
    
    private static final double X_MARGIN = 40;
    private static final double Y_MARGIN = 10;
    private static final double Y_SPACING = 5;
    
    private static final Dimension INTENSITY_CONTROL_SIZE = new Dimension( 175, 20 );
    private static final Color LABEL_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Gun _gun;
    
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
    public GunControlPanel( PSwingCanvas canvas, Gun gun ) {
        super();
        
        _gun = gun;
        
        // Font
        int fontSize = SimStrings.getInt( FONT_SIZE_RESOURCE, DEFAULT_FONT_SIZE );
        Font font = new Font( FONT_NAME, FONT_STYLE, fontSize );
        
        PImage panel = PImageFactory.create( HAConstants.IMAGE_GUN_PANEL );
        
        _gunTypeControl = new GunTypeControl( canvas, font );
        
        _lightControls = new PhetPNode();
        _lightTypeControl = new LightTypeControl( font );
        _lightIntensityControl = new IntensityControl( INTENSITY_CONTROL_SIZE, font );
        _wavelengthControl = new WavelengthControl( canvas,
                _gun.getMinWavelength(), _gun.getMaxWavelength(),
                HAConstants.UV_TRACK_COLOR, HAConstants.UV_LABEL_COLOR,
                HAConstants.IR_TRACK_COLOR, HAConstants.IR_LABEL_COLOR );
        
        _alphaParticleControls = new PhetPNode();
        _alphaParticlesIntensityControl = new IntensityControl( INTENSITY_CONTROL_SIZE, font );
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
           double yFudge = 25; // fudge factor for text field above wavelength slider
           _wavelengthControl.setOffset( lib.getX(), lib.getY() + lib.getHeight() + Y_SPACING + yFudge );
           
           _alphaParticleControls.setOffset( gtb.getX(), gtb.getY() + gtb.getHeight() + Y_SPACING );
           alphaParticlesIntensityControlWrapper.setOffset( 0, 0 );
        }
        
        // Scale the panel background image
        {
            _wavelengthControl.setWavelength( HAConstants.MAX_WAVELENGTH );
            _wavelengthControl.setTextFieldFont( font );
            _wavelengthControl.setUnitsFont( font );
            
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
        
        // Event handling
        {
            panel.setPickable( false );
            
            GunChangeListener listener = new GunChangeListener();
            _gunTypeControl.addChangeListener( listener );
            _lightTypeControl.addChangeListener( listener );
            _lightIntensityControl.addChangeListener( listener );
            _wavelengthControl.addChangeListener( listener );
            _alphaParticlesIntensityControl.addChangeListener( listener );
        }
        
        // Sync with model
        updateAll();
        _gun.addObserver( this );
    }
    
    public void updateWavelengthControlDragBounds() {
        _wavelengthControl.updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class GunChangeListener implements ChangeListener {

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _gunTypeControl ) {
                handleGunTypeChange();
            }
            else if ( source == _lightTypeControl ) {
                handleLightTypeChange();
            }
            else if ( source == _lightIntensityControl ) {
                handleLightIntensityChange();
            }
            else if ( source == _wavelengthControl ) {
                handleWavelengthChange();
            }
            else if ( source == _alphaParticlesIntensityControl ) {
                handleAlphaParticlesIntensityChange();
            }
        }   
    }
    
    private void handleGunTypeChange() {
        _lightControls.setVisible( _gun.isPhotonsMode() );
        _alphaParticleControls.setVisible( _gun.isAlphaParticlesMode() );
        GunMode mode = ( _gunTypeControl.isPhotonsSelected() ? GunMode.PHOTONS : GunMode.ALPHA_PARTICLES );
        _gun.setMode( mode );
    }
    
    private void handleLightTypeChange() {
        LightType lightType = null;
        if ( _lightTypeControl.isMonochromaticSelected() ) {
            _lightIntensityControl.setColor( _wavelengthControl.getWavelengthColor() );
            _wavelengthControl.setVisible( true );
            lightType = LightType.MONOCHROMATIC;
        }
        else {
            _lightIntensityControl.setColor( Color.WHITE );
            _wavelengthControl.setVisible( false );
            lightType = LightType.WHITE;
        }
        _gun.setLightType( lightType );
    }
    
    private void handleLightIntensityChange() {
        double intensity = _lightIntensityControl.getValue() / 100d;
        _gun.setLightIntensity( intensity );
    }
    
    private void handleWavelengthChange() {
        _lightIntensityControl.setColor( _wavelengthControl.getWavelengthColor() );
        _gun.setWavelength( _wavelengthControl.getWavelength() );
    }
    
    private void handleAlphaParticlesIntensityChange() {
        double intensity = _alphaParticlesIntensityControl.getValue() / 100d;
        _gun.setAlphaParticlesIntensity( intensity );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        if ( o == _gun ) {
            if ( arg == Gun.PROPERTY_MODE ) {
                _gunTypeControl.setPhotonsSelected( _gun.isPhotonsMode() );
            }
            else if ( arg == Gun.PROPERTY_LIGHT_TYPE ) {
                _lightTypeControl.setMonochromaticSelected( _gun.isMonochromaticLightType() );
            }
            else if ( arg == Gun.PROPERTY_WAVELENGTH ) {
                _wavelengthControl.setWavelength( _gun.getWavelength() );
            }
            else if ( arg == Gun.PROPERTY_LIGHT_INTENSITY ) {
                int i = (int)( 100 * _gun.getLightIntensity() );
                _lightIntensityControl.setValue( i );
            }
            else if ( arg == Gun.PROPERTY_ALPHA_PARTICLES_INTENSITY ) {
                int i = (int)( 100 * _gun.getAlphaParticlesIntensity() );
                _alphaParticlesIntensityControl.setValue( i );
            }
        }
    }
    
    private void updateAll() {
        _gunTypeControl.setPhotonsSelected( _gun.isPhotonsMode() );
        _lightControls.setVisible( _gun.isPhotonsMode() );
        _alphaParticleControls.setVisible( _gun.isAlphaParticlesMode() );
        _lightTypeControl.setMonochromaticSelected( _gun.isMonochromaticLightType() );
        _wavelengthControl.setWavelength( _gun.getWavelength() );
        _lightIntensityControl.setValue( (int)( 100 * _gun.getLightIntensity() ) );
        _alphaParticlesIntensityControl.setValue( (int)( 100 * _gun.getAlphaParticlesIntensity() ) );
    }
}
