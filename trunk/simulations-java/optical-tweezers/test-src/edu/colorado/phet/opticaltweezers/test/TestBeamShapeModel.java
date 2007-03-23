/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.*;

import javax.swing.*;

import edu.colorado.phet.opticaltweezers.view.SphericalNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
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
    private static final double BEAM_WAIST_RADIUS = 250;
    private static final double BEAM_HEIGHT = ( 2 * FLUID_Y_OFFSET ) + FLUID_SIZE.getHeight();
    private static final double BEAM_WAVELENGTH = 1064;
    private static final double BEAD_DIAMETER = 200;
    private static double OBJECTIVE_HEIGHT = 40;
    
    public static void main( String[] args ) {
        TestBeamShapeModel frame = new TestBeamShapeModel();
        frame.show();
    }
    
    public TestBeamShapeModel() {
        super();
        
        /*--------------------------- Canvas ---------------------------*/
        
        PPath dragBoundsNode = new PPath(); // shape will be determined by HorizontalBarNode
        dragBoundsNode.setStrokePaint( Color.RED ); // so we can see the bounds
        
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
        
        BeamNode beamNode = new BeamNode( BEAM_WAIST_RADIUS, BEAM_HEIGHT, BEAM_WAVELENGTH );
        beamNode.setOffset( PLAY_AREA_SIZE.getWidth()/2, FLUID_Y_OFFSET + FLUID_SIZE.getHeight()/2 );
        rootNode.addChild( beamNode );
        
        System.out.println( "beam width = " + beamNode.getFullBounds().getWidth() );
        
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
        beadNode.setOffset( beamNode.getOffset() );
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
    
    private class BeamNode extends PPath {
        
        private double _waistRadius;
        private double _wavelength;
        
        public BeamNode( double waistRadius, double height, double wavelength ) {
            super();
            _waistRadius = waistRadius;
            _wavelength = wavelength;
            
            int numberOfPoints = (int)height/2;
            Point2D[] points = new Point2D.Double[ numberOfPoints ];
            for ( int z = 0; z < points.length; z++ ) {
                double x = getBeamRadiusAt( z, _waistRadius, _wavelength );
                points[z] = new Point2D.Double( x, z );
            }
            
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
            
            setPathTo( p );
            setStroke( new BasicStroke( 1f ) );
            setStrokePaint( Color.BLACK );
            setPaint( null );
        }
        
        private double getBeamRadiusAt( double z, double w0, double wavelength ) {
            double zr = Math.PI * w0 * w0 / _wavelength;
            double wz = w0 * Math.sqrt(  1 + ( ( z / zr ) * ( z / zr )  ) );
            return wz;
        }
    }

}
