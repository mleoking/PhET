/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
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
    
    private static final boolean SHOW_OUTLINE = true;

    private static final Color OUTLINE_COLOR = Color.GRAY;
    public static final Stroke OUTLINE_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
    
    private static final int MAX_ALPHA_CHANNEL = 255; // 0-255
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private GradientNode _gradientNode;
    private ScaleAlphaImageOp _imageOp;
    private BufferedImage _maxPowerGradientImage; // gradient for the max power, this image is not modified
    private BufferedImage _actualPowerGradientImage; // gradient for the actual power, created by scaling the alpha channel of _maxPowerGradientImage

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser
     * @param height height in view coordinates
     */
    public LaserBeamNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _imageOp = new ScaleAlphaImageOp();
        
        _maxPowerGradientImage = createMaxPowerGradientImage();
        _actualPowerGradientImage = _imageOp.createCompatibleDestImage( _maxPowerGradientImage, _maxPowerGradientImage.getColorModel() );
        
        _gradientNode = new GradientNode( _maxPowerGradientImage, 0.05 /* overlap */ );
        _gradientNode.setOffset( 0, -_modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToControlPanel() ) );
        addChild( _gradientNode );

        // Outline of beam shape
        if ( SHOW_OUTLINE ) {
            PNode outlineNode = createOutlineNode();
            addChild( outlineNode );
        }
        
        updateVisible();
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
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                updateVisible();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateVisible() {
        setVisible( _laser.isRunning() );
    }
    
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
        final double waistRadius = _laser.getBeamRadiusAt( 0 );
        final double maxPower = _laser.getPowerRange().getMax();
        final double maxIntensity = Laser.getBeamIntensityAt( 0, waistRadius, maxPower );
        
        // Create the gradient pixel data
        int[][] dataBuffer = new int[gradientWidth][gradientHeight];
        for ( int y = 0; y < gradientHeight; y++ ) {
            
            double r = 0;
            if ( y < outHeight ) {
                // part of the beam coming out of the objective
                r = _laser.getBeamRadiusAt( y );
            }
            else {
                // part of the beam going into the objective
                r = _laser.getBeamRadiusAt( _laser.getDistanceFromObjectiveToWaist() );
            }
            
            for ( int x = 0; x < gradientWidth; x++ ) {
                int argb = 0x00000000; // 4 bytes, in order ARGB
                if ( x <= r ) {
                    final double intensity = Laser.getBeamIntensityAt( x, r, maxPower );
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

    /*
     * Create the shape of the beam's outline.
     * The shape is calculated in model coordinates, then transformed to view coordinates.
     * The node returned has it's origin at its geometrical center.
     */
    private PNode createOutlineNode() {
        
        // Shape is symmetric, calculate points for one quarter of the outline, 1 point for each 1 nm
        final int numberOfPoints = (int) _laser.getDistanceFromObjectiveToWaist();
        Point2D[] points = new Point2D.Double[numberOfPoints];
        for ( int y = 0; y < points.length; y++ ) {
            double r = _laser.getBeamRadiusAt( y );
            points[y] = new Point2D.Double( r, y );
        }

        int iFirst = 0;
        int iLast = points.length - 1;
        
        // Right half
        GeneralPath rightPath = new GeneralPath();
        // upper-right quadrant
        rightPath.moveTo( (float) points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 1; i > iFirst; i-- ) {
            rightPath.lineTo( (float) points[i].getX(), -(float) points[i].getY() );
        }
        // lower-right quadrant
        for ( int i = iFirst; i < iLast; i++ ) {
            rightPath.lineTo( (float) points[i].getX(), (float) points[i].getY() );
        }
        // portion coming into objective
        rightPath.lineTo( (float) points[iLast].getX(),  (float)( points[iLast].getY() + _laser.getDistanceFromObjectiveToControlPanel() ) );
        // transform to view coordinates
        Shape rightShape = _modelViewTransform.createTransformedShapeModelToView( rightPath );
        // node
        PPath nRight = new PPath( rightShape );
        nRight.setStroke( OUTLINE_STROKE );
        nRight.setStrokePaint( OUTLINE_COLOR );
        nRight.setPaint( null );
        
        // Left path
        GeneralPath leftPath = new GeneralPath();
        // upper-left quadrant
        leftPath.moveTo( (float) -points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 1; i > iFirst; i-- ) {
            leftPath.lineTo( (float) -points[i].getX(), -(float) points[i].getY() );
        }
        // lower-left quadrant
        for ( int i = iFirst; i < iLast; i++ ) {
            leftPath.lineTo( (float) -points[i].getX(), (float) points[i].getY() );
        }
        // portion coming into objective
        leftPath.lineTo( (float) -points[iLast].getX(),  (float)( points[iLast].getY() + _laser.getDistanceFromObjectiveToControlPanel() ) );
        // transform to view coordinates
        Shape leftShape = _modelViewTransform.createTransformedShapeModelToView( leftPath );
        // node
        PPath nLeft = new PPath( leftShape );
        nLeft.setStroke( OUTLINE_STROKE );
        nLeft.setStrokePaint( OUTLINE_COLOR );
        nLeft.setPaint( null );
        
        PNode parentNode = new PComposite();
        parentNode.addChild( nLeft );
        parentNode.addChild( nRight );
        
        // move origin to center of trap
        final double d = _modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToControlPanel() );
        parentNode.setOffset( parentNode.getFullBounds().getWidth() / 2, ( parentNode.getFullBounds().getHeight() / 2 ) - ( d / 2 ) );
        
        return parentNode;
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
        
        public GradientNode( Image image, double overlap ) {
            
            _upperLeftNode = new PImage( image );
            _upperRightNode = new PImage( image );
            _lowerLeftNode = new PImage( image );
            _lowerRightNode = new PImage( image );
            
            addChild( _upperLeftNode );
            addChild( _upperRightNode );
            addChild( _lowerLeftNode );
            addChild( _lowerRightNode );
            
            AffineTransform upperLeftTransform = new AffineTransform();
            upperLeftTransform.translate( _upperLeftNode.getFullBounds().getWidth() + overlap, _upperLeftNode.getFullBounds().getHeight() + overlap );
            upperLeftTransform.scale( -1, -1 ); // reflection about both axis
            _upperLeftNode.setTransform( upperLeftTransform );
            
            AffineTransform upperRightTransform = new AffineTransform();
            upperRightTransform.translate( _upperRightNode.getFullBounds().getWidth() - overlap, _upperRightNode.getFullBounds().getHeight() + overlap );
            upperRightTransform.scale( 1, -1 ); // reflection about the x axis
            _upperRightNode.setTransform( upperRightTransform );

            AffineTransform lowerLeftTransform = new AffineTransform();
            lowerLeftTransform.translate( _lowerLeftNode.getFullBounds().getWidth() + overlap, _lowerLeftNode.getFullBounds().getHeight() - overlap );
            lowerLeftTransform.scale( -1, 1 ); // reflection about the y axis
            _lowerLeftNode.setTransform( lowerLeftTransform );
            
            AffineTransform lowerRightTransform = new AffineTransform();
            lowerRightTransform.translate( _lowerRightNode.getFullBounds().getWidth() - overlap, _lowerRightNode.getFullBounds().getHeight() - overlap );
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
