/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * BeamOutNode is the visual representation of the portion of the 
 * laser beam that is coming out of the microscope objective.
 * This part of the beam is shaped by the objective and shows the 
 * gradient field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeamOutNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_OUTLINE = true;

    private static final Color OUTLINE_COLOR = Color.GRAY;
    public static final Stroke OUTLINE_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
    
    private static final int MAX_ALPHA_CHANNEL = 220; // 0-255
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private PImage _gradientNode;
    private BufferedImage _gradientImage;
    private int _gradientWidth, _gradientHeight;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser
     * @param height height in view coordinates
     */
    public BeamOutNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;

        // Gradient image
        _gradientWidth = (int) _laser.getDiameterAtObjective();
        if ( _gradientWidth % 2 == 0 ) {
            _gradientWidth++;
        }
        _gradientHeight = (int) ( 2 * _laser.getDistanceFromObjectiveToWaist() );
        if ( _gradientHeight % 2 == 0 ) {
            _gradientHeight++;
        }
        _gradientImage = new BufferedImage( _gradientWidth, _gradientHeight, BufferedImage.TYPE_INT_ARGB );
        _gradientNode = new PImage();
        _gradientNode.setScale( _modelViewTransform.getScaleModelToView() ); // image is in scale of model coordinates!
        addChild( _gradientNode );
        updateGradient();
        
        // Outline of beam shape
        if ( SHOW_OUTLINE ) {
            PNode outlineNode = createOutlineNode();
            addChild( outlineNode );
        }
        
        updateVisible();
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
                updateGradient();
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
    
    private void updateGradient() {
        
        assert( _gradientImage.getType() == BufferedImage.TYPE_INT_ARGB );
        
        if ( !isVisible() ) {
            return;
        }
        
        double tBegin = System.currentTimeMillis();
        
        // Color for the laser beam
        Color c = VisibleColor.wavelengthToColor( _laser.getVisibleWavelength() );
        final int red = c.getRed();
        final int green = c.getGreen();
        final int blue = c.getBlue();
        
        // Max power intensity, at center of trap
        final double waistRadius = _laser.getBeamRadiusAt( 0 );
        final double maxPower = _laser.getPowerRange().getMax();
        final double maxIntensity = Laser.getBeamIntensityAt( 0, waistRadius, maxPower );
        
        // Create the gradient pixel data, working from the center out
        int[][] dataBuffer = new int[ _gradientWidth ][ _gradientHeight ];
        int iy1 = _gradientHeight / 2;
        int iy2 = iy1;
        for ( int y = 0; y < ( _gradientHeight / 2 ) + 1; y++ ) {
            final double r = _laser.getBeamRadiusAt( y );
            int ix1 = _gradientWidth / 2;
            int ix2 = ix1;
            for ( int x = 0; x < ( _gradientWidth / 2 ) + 1; x++ ) {
                int argb = 0x00000000;  // 4 bytes, in order ARGB
                if ( x <= r ) {
                    final double intensity = _laser.getBeamIntensityAt( x, r );
                    final int alpha = (int) ( MAX_ALPHA_CHANNEL * intensity / maxIntensity );
                    argb = ( alpha << 24 ) | ( red << 16 ) | ( green << 8 ) | ( blue );
                }
                dataBuffer[ ix1 ][ iy1 ] = argb; // lower right quadrant
                dataBuffer[ ix1 ][ iy2 ] = argb; // upper right
                dataBuffer[ ix2 ][ iy1 ] = argb; // lower left
                dataBuffer[ ix2 ][ iy2 ] = argb; // upper left
                ix1++;
                ix2--;
            }
            iy1++;
            iy2--;
        }
        
        // Copy the gradient data to the image's raster buffer
        WritableRaster raster = _gradientImage.getRaster();
        int[] rasterBuffer = ( (DataBufferInt) raster.getDataBuffer() ).getData();
        int ri = 0;
        for ( int row = 0; row < _gradientHeight; row++ ) {
            for ( int col = 0; col < _gradientWidth; col++ ) {
                rasterBuffer[ri] = dataBuffer[col][row];
                ri++;
            }
        }
        
        _gradientNode.setImage( _gradientImage );
        
        double tEnd = System.currentTimeMillis();
        System.out.println( "BeamOutNode.updateGradient: " + ( tEnd - tBegin ) + " ms" );//XXX
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
        GeneralPath pRight = new GeneralPath();
        // upper-right quadrant
        pRight.moveTo( (float) points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 2; i >= iFirst; i-- ) {
            pRight.lineTo( (float) points[i].getX(), -(float) points[i].getY() );
        }
        // lower-right quadrant
        for ( int i = iFirst; i < iLast - 1; i++ ) {
            pRight.lineTo( (float) points[i].getX(), (float) points[i].getY() );
        }
        // transform to view coordinates
        Shape sRight = _modelViewTransform.createTransformedShapeModelToView( pRight );
        // node
        PPath nRight = new PPath( sRight );
        nRight.setStroke( OUTLINE_STROKE );
        nRight.setStrokePaint( OUTLINE_COLOR );
        nRight.setPaint( null );
        
        // Left path
        GeneralPath pLeft = new GeneralPath();
        // upper-left quadrant
        pLeft.moveTo( (float) -points[iLast].getX(), -(float) points[iLast].getY() );
        for ( int i = iLast - 2; i >= iFirst; i-- ) {
            pLeft.lineTo( (float) -points[i].getX(), -(float) points[i].getY() );
        }
        // lower-left quadrant
        for ( int i = iFirst; i < iLast - 1; i++ ) {
            pLeft.lineTo( (float) -points[i].getX(), (float) points[i].getY() );
        }
        // transform to view coordinates
        Shape sLeft = _modelViewTransform.createTransformedShapeModelToView( pLeft );
        // node
        PPath nLeft = new PPath( sLeft );
        nLeft.setStroke( OUTLINE_STROKE );
        nLeft.setStrokePaint( OUTLINE_COLOR );
        nLeft.setPaint( null );
        
        PNode parentNode = new PComposite();
        parentNode.addChild( nLeft );
        parentNode.addChild( nRight );
        
        // shape was drawn starting at right center, adjust so that origin is at center
        parentNode.setOffset( parentNode.getFullBounds().getWidth()/2, parentNode.getFullBounds().getHeight()/2 );
        
        return parentNode;
    }
}
