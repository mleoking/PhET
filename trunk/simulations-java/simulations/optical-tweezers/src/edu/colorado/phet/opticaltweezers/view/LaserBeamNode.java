/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.colorado.phet.opticaltweezers.util.ScaleAlphaImageOpARGB;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * LaserBeamNode is the visual representation of the laser beam.
 * It comes out of the control panel with a constant radius,
 * and is shaped as it passes through the objective.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserBeamNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int MAX_ALPHA_CHANNEL = 255; // 0-255
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private GradientNode _gradientNode;
    private ScaleAlphaImageOpARGB _imageOp;
    private BufferedImage _maxPowerGradientImage; // gradient for the max power, this image is not modified
    private BufferedImage _actualPowerGradientImage; // gradient for the actual power, created by scaling the alpha channel of _maxPowerGradientImage

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser
     * @param modelViewTransform
     */
    public LaserBeamNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _imageOp = new ScaleAlphaImageOpARGB();
        
        _maxPowerGradientImage = createMaxPowerGradientImage();
        _actualPowerGradientImage = _imageOp.createCompatibleDestImage( _maxPowerGradientImage, _maxPowerGradientImage.getColorModel() );
        
        _gradientNode = new GradientNode( _maxPowerGradientImage, 0 /* horizontalOverlap */, 0.1 /* verticalOverlap */ );
        _gradientNode.setOffset( 0, -_modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToControlPanel() ) );
        addChild( _gradientNode );
        
        updateAlpha();
    }
    
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
   
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POWER ) {
                updateAlpha();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Adjusts the alpha channel of the gradient image so that it corresponds
     * to the current value of the laser power.
     */
    private void updateAlpha() {
        double scale = Math.sqrt( _laser.getPower() / _laser.getPowerRange().getMax() ); // use sqrt to compensate for eye's alpha sensitivity
        _imageOp.setScale( scale );
        _imageOp.filter( _maxPowerGradientImage, _actualPowerGradientImage );
        _gradientNode.setImage( _actualPowerGradientImage );
    }
    
    //----------------------------------------------------------------------------
    // Creators
    //----------------------------------------------------------------------------
    
    /*
     * Creates a buffered image for the lower right quadrant of the maximum power gradient.
     */
    private BufferedImage createMaxPowerGradientImage() {
        
        final int gradientWidth = (int) ( _laser.getDiameterAtObjective() / 2 );
        final int inHeight = (int)( _laser.getDistanceFromObjectiveToControlPanel() );
        final int outHeight = (int) ( _laser.getDistanceFromObjectiveToWaist() );
        final int gradientHeight = inHeight + outHeight;
        BufferedImage gradientImage = new BufferedImage( gradientWidth, gradientHeight, BufferedImage.TYPE_INT_ARGB );
        
        // Color for the laser beam
        Color c = VisibleColor.wavelengthToColor( _laser.getVisibleWavelength() );
        final int red = c.getRed();
        final int green = c.getGreen();
        final int blue = c.getBlue();
        
        // Max power intensity, at center of trap
        final double maxPower = _laser.getPowerRange().getMax();
        final double maxIntensity = _laser.getMaxIntensity();
        
        // Create the gradient pixel data
        int[][] dataBuffer = new int[gradientWidth][gradientHeight];
        for ( int y = 0; y < gradientHeight; y++ ) {
            
            double r = 0;
            if ( y < outHeight ) {
                // part of the beam coming out of the objective
                r = _laser.getRadius( y );
            }
            else {
                // part of the beam going into the objective
                r = _laser.getRadius( _laser.getDistanceFromObjectiveToWaist() );
            }
            
            for ( int x = 0; x < gradientWidth; x++ ) {
                int argb = 0x00000000; // 4 bytes, in order ARGB
                if ( x <= r ) {
                    final double intensity = Laser.getIntensityOnRadius( x, r, maxPower );
                    final int alpha = (int) ( MAX_ALPHA_CHANNEL * intensity / maxIntensity );
                    argb = ( alpha << 24 ) | ( red << 16 ) | ( green << 8 ) | ( blue );
                }
                dataBuffer[x][y] = argb;
            }
        }
        
        // Copy the gradient data to the image's raster buffer.
        // This is many times faster than calling BufferedImage.setRGB !!
        WritableRaster raster = gradientImage.getRaster();
        int[] rasterBuffer = ( (DataBufferInt) raster.getDataBuffer() ).getData();
        int ri = 0;
        for ( int row = 0; row < gradientHeight; row++ ) {
            for ( int col = 0; col < gradientWidth; col++ ) {
                rasterBuffer[ri] = dataBuffer[col][row];
                ri++;
            }
        }
        
        double scale = _modelViewTransform.getScaleModelToView();
        BufferedImage scaledImage = BufferedImageUtils.rescaleFractional( gradientImage, scale, scale );

        return scaledImage;
    }


    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * GradientNode is the node that represents the power gradient.
     * The power gradient is symmetric about the center of the trap.
     * So we use the same image to draw each of the quadrants.
     * The image is assumed to have been generated for the lower-right quadrant.
     * <p>
     * NOTE! Since the image also contains the portion of the beam coming into
     * the objective, we must be careful when displaying this node.  We must
     * position the node so that the incoming-portion of the beam doesn't show
     * up above the laser beam.
     */
    private static class GradientNode extends PComposite {
        
        // one node for each quadrant
        private PImage _upperLeftNode, _upperRightNode, _lowerLeftNode, _lowerRightNode;
        
        public GradientNode( Image image, double horizontalOverlap, double verticalOverlap ) {
            
            _upperLeftNode = new PImage( image );
            _upperRightNode = new PImage( image );
            _lowerLeftNode = new PImage( image );
            _lowerRightNode = new PImage( image );
            
            addChild( _lowerLeftNode );
            addChild( _lowerRightNode );
            addChild( _upperLeftNode );
            addChild( _upperRightNode );
            
            PBounds upperLeftBounds = _upperLeftNode.getFullBoundsReference();
            AffineTransform upperLeftTransform = new AffineTransform();
            upperLeftTransform.translate( upperLeftBounds.getWidth() + horizontalOverlap, upperLeftBounds.getHeight() + verticalOverlap );
            upperLeftTransform.scale( -1, -1 ); // reflection about both axis
            _upperLeftNode.setTransform( upperLeftTransform );
            
            PBounds upperRightBounds = _upperRightNode.getFullBoundsReference();
            AffineTransform upperRightTransform = new AffineTransform();
            upperRightTransform.translate( upperRightBounds.getWidth() - horizontalOverlap, upperRightBounds.getHeight() + verticalOverlap );
            upperRightTransform.scale( 1, -1 ); // reflection about the x axis
            _upperRightNode.setTransform( upperRightTransform );

            PBounds lowerLeftBounds = _lowerLeftNode.getFullBoundsReference();
            AffineTransform lowerLeftTransform = new AffineTransform();
            lowerLeftTransform.translate( lowerLeftBounds.getWidth() + horizontalOverlap, lowerLeftBounds.getHeight() - verticalOverlap );
            lowerLeftTransform.scale( -1, 1 ); // reflection about the y axis
            _lowerLeftNode.setTransform( lowerLeftTransform );
            
            PBounds lowerRightBounds = _lowerRightNode.getFullBoundsReference();
            AffineTransform lowerRightTransform = new AffineTransform();
            lowerRightTransform.translate( lowerRightBounds.getWidth() - horizontalOverlap, lowerRightBounds.getHeight() - verticalOverlap );
            lowerRightTransform.scale( 1, 1 ); // no reflection
            _lowerRightNode.setTransform( lowerRightTransform );
        }
        
        public void setImage( Image image ) {
            _upperLeftNode.setImage( image );
            _upperRightNode.setImage( image );
            _lowerLeftNode.setImage( image );
            _lowerRightNode.setImage( image );
        }
    }
}
