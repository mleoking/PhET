/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleApplication;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LiquidNode represents the liquid in the beaker.
 * It plays a secondary role, used as a clipping path for the particles shown
 * in the "H3O/OH ratio" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PDimension _maxSize;
    private final PPath _waterNode; // put water behind liquid so that water will appear as liquid fades
    private final Rectangle2D _liquidPath;
    private final PPath _liquidNode;
    private final ParticlesNode _particlesNode;
    private final PText _rgbaNode; // displays RGBA color of liquid, for debugging
    private final VolumeValueNode _volumeValueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LiquidNode( Liquid liquid, PDimension maxSize ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        WATER.addLiquidDescriptorListener( new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                _waterNode.setPaint( WATER.getColor() );
            }
        } );
        
        _maxSize = new PDimension( maxSize );

        _waterNode = new PPath();
        _waterNode.setPaint( LiquidDescriptor.getWater().getColor() );
        _waterNode.setStroke( null );
        addChild( _waterNode );
        
        _liquidPath = new Rectangle2D.Double();
        _liquidNode = new PClip(); // clip particles to liquid
        _liquidNode.setStroke( null );
        addChild( _liquidNode );
        
        _volumeValueNode = new VolumeValueNode();
        _volumeValueNode.setOffset( maxSize.getWidth() + 3, 0 ); // y offset will be set by update
        addChild( _volumeValueNode );
        
        PBounds containerBounds = new PBounds( 0, 0, maxSize.getWidth(), maxSize.getHeight() );
        _particlesNode = new ParticlesNode( _liquid, containerBounds );
        _liquidNode.addChild( _particlesNode ); // clip particles to liquid
        
        // developer only, display color component values at lower right
        _rgbaNode = new PText( "Color=[255,255,255,255]" );
        _rgbaNode.setFont( new PhetFont() );
        _rgbaNode.setOffset( maxSize.getWidth() - _rgbaNode.getWidth() - 15, maxSize.getHeight() - _rgbaNode.getHeight() - 5 );
        if ( PHScaleApplication.instance().isDeveloperControlsEnabled() ) {
            addChild( _rgbaNode );
        }
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setParticlesVisible( boolean visible ) {
        _particlesNode.setVisible( visible );
    }
    
    public boolean isParticlesVisible() {
        return _particlesNode.getVisible();
    }
    
    // for attaching developer control panel
    public ParticlesNode getParticlesNode() {
        return _particlesNode;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        // color
        Color c = _liquid.getColor();
        _liquidNode.setPaint( c );
        if ( c != null ) {
            _rgbaNode.setText( "Color=[" + c.getRed() + "," + c.getBlue() + "," + c.getGreen() + "," + c.getAlpha() + "]" );
        }
        else {
            _rgbaNode.setText( "" );
        }

        // volume
        double percentFilled = _liquid.getVolume() / _liquid.getMaxVolume();
        double liquidHeight = percentFilled * _maxSize.getHeight();
        _liquidPath.setRect( 0, _maxSize.getHeight() - liquidHeight, _maxSize.getWidth(), liquidHeight );
        _liquidNode.setPathTo( _liquidPath );
        _waterNode.setPathTo( _liquidPath );
        
        // value display, at right-top of volume
        _volumeValueNode.setValue( _liquid.getVolume() );
        _volumeValueNode.setOffset( _volumeValueNode.getXOffset(), _maxSize.getHeight() - liquidHeight );
    }
}
