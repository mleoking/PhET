// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * TestBeamShapeModel test the model used to create the beam shape.
 * It shows the beam shape in the context of the simulation, so that we can see scale issues.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBeamShapeModel extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 1024, 768 ); // screen coordinates
    private static final Dimension2D PLAY_AREA_SIZE = new PDimension( 2*950, 2*750 );
    private static final Dimension2D FLUID_SIZE = new PDimension( PLAY_AREA_SIZE.getWidth(), 1000 );
    private static final double FLUID_Y_OFFSET = 100;
    private static final double BEAM_RADIUS_AT_OBJECTIVE = 500;
    private static final double BEAM_RADIUS_AT_WAIST = 250;
    private static final double BEAM_HEIGHT = ( 2 * FLUID_Y_OFFSET ) + FLUID_SIZE.getHeight();
    private static final double BEAM_WAVELENGTH = 1064;
    private static final double VISIBLE_WAVELENGTH = 632; // nm
    private static final double BEAD_DIAMETER = 200;
    private static final double OBJECTIVE_HEIGHT = 40;
    private static final double MAX_POWER = 1000; // mW
    private static final double POWER = 800; // mW
    private static final double MAX_ALPHA = 230; // 0..255
    
    public static void main( String[] args ) {
        TestBeamShapeModel frame = new TestBeamShapeModel();
        frame.show();
    }
    
    public TestBeamShapeModel() {
        super();
        
        /*--------------------------- Canvas ---------------------------*/
        
        PCanvas canvas = new PhetPCanvas();
        PComposite rootNode = new PComposite();
        canvas.getLayer().addChild( rootNode );
        
        PPath playAreaNode = new PPath( new Rectangle2D.Double( 0, 0, PLAY_AREA_SIZE.getWidth(), PLAY_AREA_SIZE.getHeight() ) );
        playAreaNode.setOffset( 0, 0 );
        playAreaNode.setStroke( new BasicStroke( 3f ) );
        playAreaNode.setStrokePaint( Color.RED );
        rootNode.addChild( playAreaNode );
        
        PPath fluidNode = new PPath( new Rectangle2D.Double( 0, 0, FLUID_SIZE.getWidth(), FLUID_SIZE.getHeight() ) );
        fluidNode.setOffset( 0, FLUID_Y_OFFSET );
        fluidNode.setStroke( new BasicStroke( 1f ) );
        fluidNode.setStrokePaint( Color.BLUE );
        fluidNode.setPaint( new Color( 0, 0, 255, 20 ) );
        rootNode.addChild( fluidNode );
        
        BeamNode beamNode = new BeamNode( BEAM_RADIUS_AT_OBJECTIVE, BEAM_RADIUS_AT_WAIST, BEAM_HEIGHT / 2, BEAM_WAVELENGTH );
        beamNode.setOffset( PLAY_AREA_SIZE.getWidth()/2, FLUID_Y_OFFSET + FLUID_SIZE.getHeight()/2 );
        rootNode.addChild( beamNode );
        
        System.out.println( "beam size = " + beamNode.getFullBounds().getWidth() + "x" + BEAM_HEIGHT );
        
        PPath objectiveNode = new PPath( new Ellipse2D.Double( 0, 0, beamNode.getFullBounds().getWidth() / 0.95, OBJECTIVE_HEIGHT ) );
        objectiveNode.setOffset( 
                beamNode.getFullBounds().getX() - ( ( objectiveNode.getFullBounds().getWidth() - beamNode.getFullBounds().getWidth() ) / 2 ), 
                BEAM_HEIGHT - objectiveNode.getFullBounds().getHeight() / 2 );
        objectiveNode.setStroke( new BasicStroke( 1f ) );
        objectiveNode.setStrokePaint( Color.BLACK );
        objectiveNode.setPaint( new Color( 0, 0, 255, 10 ) );
        rootNode.addChild( objectiveNode );
        
        PPath laserControlsNode = new PPath( new Rectangle2D.Double( 0, 0, objectiveNode.getFullBounds().getWidth(), 130 ) );
        laserControlsNode.setOffset( objectiveNode.getFullBounds().getX(), BEAM_HEIGHT + 100 );
        laserControlsNode.setPaint( Color.DARK_GRAY );
        rootNode.addChild( laserControlsNode );
        
        PNode beadNode = new SphericalNode( BEAD_DIAMETER, Color.yellow, new BasicStroke(1f), Color.BLACK, true );
        beadNode.setOffset( beamNode.getOffset().getX() - 400, beamNode.getOffset().getY() );
        rootNode.addChild( beadNode );
        
        rootNode.scale( 0.3 );
        rootNode.setOffset( 50, 50 );

        /*--------------------------- Control Panel ---------------------------*/
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );
        controlPanel.add( new JLabel( "Controls go here" ) );
        
        /*--------------------------- Frame ---------------------------*/
        
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.X_AXIS ) );
        panel.add( canvas );
        panel.add( controlPanel );
        
        setContentPane( panel );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    private class BeamNode extends PComposite {
        
        public BeamNode( double radiusAtObjective, double radiusAtWaist, double distanceFromWaistToObjective, double wavelength ) {
            super();

            // Shape is symmetric, calculate points on one quarter of outline
            final int numberOfPoints = (int) distanceFromWaistToObjective;
            Point2D[] points = new Point2D.Double[ numberOfPoints ];
            for ( int z = 0; z < points.length; z++ ) {
                double r = getBeamRadiusAt( z, distanceFromWaistToObjective, radiusAtWaist, radiusAtObjective, wavelength );
                points[z] = new Point2D.Double( r, z );
            }
            System.out.println( "min radius = " + points[0].getX() );
            System.out.println( "max radius = " + points[points.length-1].getX() );
            
            // Create path for entire outline.
            GeneralPath p = new GeneralPath();
            int first = 0;
            int last = points.length - 1;
            // start at right center
            p.moveTo( (float)points[first].getX(), (float)points[first].getY() );
            // lower-right quadrant
            for ( int i = first+1; i < last-1; i++ ) {
                p.lineTo( (float)points[i].getX(), (float)points[i].getY() );
            }
            // lower-left quadrant
            p.lineTo( -(float)points[last].getX(), (float)points[last].getY() );
            for ( int i = last-2; i >= first ; i-- ) {
                p.lineTo( -(float)points[i].getX(), (float)points[i].getY() );
            }
            // upper-left quadrant
            for ( int i = first+1; i < last-1; i++ ) {
                p.lineTo( -(float)points[i].getX(), -(float)points[i].getY() );
            }
            // upper-right quadrant
            p.lineTo( (float)points[last].getX(), -(float)points[last].getY() );
            for ( int i = last-2; i >= first ; i-- ) {
                p.lineTo( (float)points[i].getX(), -(float)points[i].getY() );
            }
            p.closePath();
            
            // Node to draw the outline
            PPath outlineNode = new PPath( p );
            outlineNode.setStroke( new BasicStroke( 1f ) );
            outlineNode.setStrokePaint( Color.BLACK );
            outlineNode.setPaint( null );
            addChild( outlineNode );
            
            // Max power intensity, at center of trap
            double minRadius = getBeamRadiusAt( 0, distanceFromWaistToObjective, radiusAtWaist, radiusAtObjective, wavelength );
            final double maxIntensity = getBeamIntensityAt( 0, minRadius, MAX_POWER );
            
            // Color for the laser beam
            Color c = VisibleColor.wavelengthToColor( VISIBLE_WAVELENGTH );
            int red = c.getRed();
            int green = c.getGreen();
            int blue = c.getBlue();
            
            // Image to hold gradient, odd number of pixels in each dimension, image will be symmetrical about center
            int bufWidth = (int)( 2 * radiusAtObjective );
            if ( bufWidth % 2 == 0 ) {
                bufWidth++;
            }
            int bufHeight = (int)( 2 * distanceFromWaistToObjective );
            if ( bufHeight % 2 == 0 ) {
                bufHeight++;
            }
            BufferedImage bi = new BufferedImage( bufWidth, bufHeight, BufferedImage.TYPE_INT_ARGB );

            // Create the gradient, working from the center out
            int iy1 = bufHeight / 2;
            int iy2 = iy1;
            for ( int y = 0; y < ( bufHeight / 2 ) + 1; y++ ) {
                double r = getBeamRadiusAt( y, distanceFromWaistToObjective, radiusAtWaist, radiusAtObjective, wavelength );
                int ix1 = bufWidth / 2;
                int ix2 = ix1;
                for ( int x = 0; x < ( bufWidth / 2 ) + 1; x++ ) {
                    int argb = 0x00000000;  // ARGB
                    if ( x <= r ) {
                        double intensity = getBeamIntensityAt( x, r, POWER );
                        int alpha = (int) ( MAX_ALPHA * intensity / maxIntensity );
                        argb = ( alpha << 24 ) | ( red << 16 ) | ( green << 8 ) | blue;
                    }
                    bi.setRGB( ix1, iy1, argb ); // lower right quadrant
                    bi.setRGB( ix1, iy2, argb ); // upper right
                    bi.setRGB( ix2, iy1, argb ); // lower left
                    bi.setRGB( ix2, iy2, argb ); // upper left
                    ix1++;
                    ix2--;
                }
                iy1++;
                iy2--;
            }
            
            PImage gradientNode = new PImage( bi );
            gradientNode.setOffset( -gradientNode.getFullBounds().getWidth()/2, -gradientNode.getFullBounds().getHeight()/2 );
            addChild( gradientNode );
        }
        
        private double getBeamRadiusAt( double z, double zmax, double r0, double rmax, double wavelength ) {
            double zAbs = Math.abs( z );
            double zr = ( Math.PI * r0 * r0 ) / wavelength;
            double A = ( zmax / zr ) / Math.sqrt( ( ( rmax / r0 ) * ( rmax / r0 ) ) - 1 );
            double t1 = zAbs / ( A * zr );
            double rz = r0 * Math.sqrt( 1 + ( t1 * t1 ) );
            return rz;
        }
        
        private double getBeamIntensityAt( double x, double rz, double power ) {
            double t1 = power / ( Math.PI * ( ( rz * rz ) / 2 ) );
            double t2 = Math.exp( ( -2 * x * x ) / ( rz * rz ) );
            return t1 * t2;
        }
    }

}
