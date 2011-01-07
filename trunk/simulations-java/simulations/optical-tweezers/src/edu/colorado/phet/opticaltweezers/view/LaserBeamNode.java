// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ScaleAlphaImageOpARGB;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.nodes.PImage;

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
    private OTModelViewTransform _modelViewTransform;
    private PImage _gradientNode;
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
    public LaserBeamNode( Laser laser, OTModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _imageOp = new ScaleAlphaImageOpARGB();
        
        _maxPowerGradientImage = createMaxPowerGradientImage();
        _actualPowerGradientImage = _imageOp.createCompatibleDestImage( _maxPowerGradientImage, _maxPowerGradientImage.getColorModel() );
        
        _gradientNode = new PImage( _actualPowerGradientImage  );
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
     * Creates a buffered image for the beam's maximum power gradient.
     * We create the data for 1 quadrant of the beam, then copy it to the
     * other 3 quadrants.
     * 
     * NOTE! Since the image also contains the portion of the beam coming into
     * the objective, we must be careful when displaying this node.  We must
     * position the node so that the incoming-portion of the beam doesn't show
     * up above the laser beam.
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
        
        // Scale the data
        double scale = _modelViewTransform.getScaleModelToView();
        BufferedImage scaledImage = BufferedImageUtils.rescaleFractional( gradientImage, scale, scale );

        // Assemble the complete beam image by duplicating the data for each quadrant
        BufferedImage beamImage = null;
        {
            // lower right is the image we've created so far
            BufferedImage lowerRightImage = scaledImage;

            // lower left, flip horizontally
            AffineTransform tx = AffineTransform.getScaleInstance( -1, 1 );
            tx.translate( -lowerRightImage.getWidth( null ), 0 );
            AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
            BufferedImage lowerLeftImage = op.filter( lowerRightImage, null );

            // upper left, flip vertically and horizontally
            tx = AffineTransform.getScaleInstance( -1, -1 );
            tx.translate( -lowerRightImage.getWidth( null ), -lowerRightImage.getHeight( null ) );
            op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
            BufferedImage upperLeftImage = op.filter( lowerRightImage, null );

            // upper right, flip vertically
            tx = AffineTransform.getScaleInstance( 1, -1 );
            tx.translate( 0, -lowerRightImage.getHeight( null ) );
            op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
            BufferedImage upperRightImage = op.filter( lowerRightImage, null );

            // create a buffer large enough to hold all quadrants
            Graphics2D g2 = (Graphics2D) lowerRightImage.getGraphics();
            final int w = 2 * lowerRightImage.getWidth();
            final int h = 2 * lowerRightImage.getHeight();
            beamImage = g2.getDeviceConfiguration().createCompatibleImage( w, h );

            // draw the image
            g2 = (Graphics2D) beamImage.getGraphics();
            g2.drawImage( upperLeftImage, null, 0, 0 );
            g2.drawImage( upperRightImage, null, w / 2, 0 );
            g2.drawImage( lowerLeftImage, null, 0, h / 2 );
            g2.drawImage( lowerRightImage, null, w / 2, h / 2 );
        }

        return beamImage;
    }
}
