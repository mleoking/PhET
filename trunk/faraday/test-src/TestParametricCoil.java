/* Copyright 2005, University of Colorado */

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TestParametricCoil is a program that was used for developing the Faraday pickup coil.
 * The coil is composed entirely of quadratic curves.  The control points for the curves
 * were set via "trial and error". IF YOU MODIFY ANYTHING, PROCEED WITH CAUTION!
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestParametricCoil extends JComponent {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
   
    // Loop parameters
    private static final Color LOOP_LIGHTEST_COLOR = new Color( 153, 102, 51 );
    private static final Color LOOP_MIDDLE_COLOR = new Color( 92, 52, 12 );
    private static final Color LOOP_DARKEST_COLOR = new Color( 40, 23, 3 );
    private static final int WIRE_WIDTH = 14;
    private static final double LOOP_SPACING_FACTOR = 0.3; // ratio of loop spacing to loop radius
    
    // End-point and control-point parameters
    private static final int CONTROL_POINT_SIZE = 4;
    private static final Color CONTROL_POINT_COLOR = Color.YELLOW;
    private static final Color CONTROL_LINE_COLOR = Color.WHITE;
    private static final int CONTROL_LINE_WIDTH = 1;
    private static final Color END_POINT_COLOR = Color.BLUE;
    
    // Debugging parameters
    private static final boolean DRAW_POINTS = false;
    private static final boolean DRAW_ORIGIN = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfLoops;  // number of loops in the coil
    private int _radius; // radius (in pixels) used for all loops
    private Point _location; // location of the coil's approximate center of mass
    
    //----------------------------------------------------------------------------
    // Test harness
    //----------------------------------------------------------------------------
    
    /**
     * Test harness. 
     * Creates a user interface for testing the coil.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args )
    {
        // Test harness parameters
        final int LOOPS = 1;
        final int MAX_LOOPS = 10;
        final int MIN_LOOPS = 1;
        final int RADIUS = 100;
        final int MIN_RADIUS = 75;
        final int MAX_RADIUS = 150;
        final Point COIL_LOCATION = new Point( 250, 250 );
        final Dimension FRAME_SIZE = new Dimension( 500, 500 );
        final Color FRAME_BACKGROUND = Color.BLACK;
        final Dimension SLIDER_SIZE = new Dimension( 150, 20 );

        // Create a coil.
        final TestParametricCoil coil = new TestParametricCoil( LOOPS, RADIUS, COIL_LOCATION );

        // Number of Loops control
        Box loopsPanel = new Box( BoxLayout.X_AXIS );
        {
            JLabel label = new JLabel( "Number of Loops:" );
            
            final JLabel value = new JLabel( String.valueOf( coil.getNumberOfLoops() ) );
            
            final JSlider slider = new JSlider();
            slider.setMinimumSize( SLIDER_SIZE );
            slider.setMaximumSize( SLIDER_SIZE );
            slider.setPreferredSize( SLIDER_SIZE );
            slider.setMinimum( MIN_LOOPS );
            slider.setMaximum( MAX_LOOPS );
            slider.setMajorTickSpacing( 1 );
            slider.setSnapToTicks( true );
            slider.setPaintTicks( true );
            slider.setValue( coil.getNumberOfLoops() );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int numberOfLoops = slider.getValue();
                    coil.setNumberOfLoops( numberOfLoops );
                    value.setText( String.valueOf( numberOfLoops ) );
                }
            } );
            
            loopsPanel.add( label );
            loopsPanel.add( slider );
            loopsPanel.add( value );
        }
        
        // Loop Radius control
        Box radiusPanel = new Box( BoxLayout.X_AXIS );
        {
            JLabel label = new JLabel( "Loop Radius:" );
            
            final JLabel value = new JLabel( String.valueOf( coil.getRadius() ) );
            
            final JSlider slider = new JSlider();
            slider.setMinimumSize( SLIDER_SIZE );
            slider.setMaximumSize( SLIDER_SIZE );
            slider.setPreferredSize( SLIDER_SIZE );
            slider.setMinimum( MIN_RADIUS );
            slider.setMaximum( MAX_RADIUS );
            slider.setValue( coil.getRadius() );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int radius = slider.getValue();
                    coil.setRadius( radius );
                    value.setText( String.valueOf( radius) + " pixels" );
                }
            } );
            
            radiusPanel.add( label );
            radiusPanel.add( slider );
            radiusPanel.add( value );
        }
        
        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground( Color.WHITE );
        controlPanel.setLayout( new BorderLayout() );
        controlPanel.add( loopsPanel, BorderLayout.NORTH );
        controlPanel.add( radiusPanel, BorderLayout.CENTER );
        
        // Put stuff in a panel.
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( coil, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );
        
        // Put the panel in a frame.
        final JFrame f = new JFrame( "Parametric Coil" );
        f.getContentPane().add( panel );
        
        // Set up the frame
        f.setBackground( FRAME_BACKGROUND);
        f.setSize( FRAME_SIZE );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                f.dispose();
                System.exit( 0 );
            }
        } );
        
        // Center on the display
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ( screenSize.width - f.getSize().width ) / 2;
        int y = ( screenSize.height - f.getSize().height ) / 2;
        f.setLocation( x, y );
        
        f.setVisible( true );
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /** 
     * Sole constructor
     * 
     * @param numberOfLoops the number of loops in the coil
     * @param radius the radius of each loop
     * @param location the location of the coil's center of mass
     */
    public TestParametricCoil( int numberOfLoops, int radius, Point location ) {
        super();
        assert( numberOfLoops > 0 );
        _numberOfLoops = numberOfLoops;
        _radius = radius;
        _location = location;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the number of loops.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfLoops( int numberOfLoops ) {
        assert( numberOfLoops > 0 );
        _numberOfLoops = numberOfLoops;
        repaint();
    }
    
    /**
     * Gets the number of loops.
     * 
     * @return the number of loops
     */
    public int getNumberOfLoops() {
        return _numberOfLoops;
    }
    
    /**
     * Set the radius used for all loops.
     * 
     * @param radius the radius, in pixels
     */
    public void setRadius( int radius ) {
        assert( radius > 0 );
        _radius = radius;
        repaint();
    }
    
    /**
     * Gets the radius used for all loops.
     * 
     * @return the radius, in pixels
     */
    public int getRadius() {
        return _radius;
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------
    
    /**
     * Draws the coil.
     * 
     * @param g graphics context
     */
    public void paintComponent( Graphics g ) {
       
        assert( g instanceof Graphics2D );

        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( WIRE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        g2.translate( _location.x, _location.y );
 
        // Loop spacing
        int loopSpacing = (int)(_radius * LOOP_SPACING_FACTOR );
        
        // Center of all loops should remain fixed.
        int firstLoopCenter = -( loopSpacing * (_numberOfLoops - 1) / 2 );
        
        // Back of loops
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * loopSpacing );
            
            g2.setPaint( new GradientPaint( 0, (int)(_radius * .40), LOOP_DARKEST_COLOR, 0, (int)(_radius * .90), LOOP_MIDDLE_COLOR ) );
            
            // Back bottom
            {
                Point e1 = new Point( (int)(_radius * .25) + offset, 0 );
                Point e2 = new Point( (int)(_radius * .15) + offset, (int)(_radius * .98) );
                Point c = new Point( (int)(_radius * .30) + offset, (int)(_radius * .80) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Back top
            {
                Point e1 = new Point( (int)(_radius * .25) + offset, 0 );
                Point e2 = new Point( -loopSpacing + (int)(_radius * .15) + offset, -_radius );
                Point c = new Point( (int)(_radius * .15) + offset, (int)(-_radius * .70));
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }

            // Left connection wire
            if ( i == 0 ) {
                Point e1 = new Point( -loopSpacing + (int)(_radius * .15) + offset, -_radius );
                Point e2 = new Point( e1.x - 15, e1.y - 40 );
                Point c = new Point( e1.x - 20, e1.y - 20 );
                g2.setPaint( new GradientPaint( e2.x, 0, LOOP_MIDDLE_COLOR, e1.x, 0, LOOP_DARKEST_COLOR ) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
        }
        
        // Front of loops
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * loopSpacing );;
            
            g2.setPaint( new GradientPaint( (int)(-_radius * .25) + offset, 0, LOOP_LIGHTEST_COLOR, (int)(-_radius * .15) + offset, 0, LOOP_MIDDLE_COLOR ) );
            
            // Front bottom
            {
                Point e1 = new Point( (int)(-_radius * .25) + offset, 0 );
                Point e2 = new Point( (int)(_radius * .13) + offset, _radius );
                Point c = new Point( (int)(-_radius * .25) + offset, (int)(_radius * 1.30) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Front top
            if ( i < _numberOfLoops - 1 )
            {
                Point e1 = new Point( (int) ( -_radius * .25 ) + offset, 0 );
                Point e2 = new Point( (int) ( _radius * .15 ) + offset, -_radius );
                Point c = new Point( (int) ( -_radius * .20 ) + offset, (int) ( -_radius * 1.30 ) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            else {
                // Front top of right-most loop (shorter than the others for joining with connection wire)
                {
                    Point e1 = new Point( (int) ( -_radius * .25 ) + offset, 0 );
                    Point e2 = new Point( -loopSpacing + (int) ( _radius * .25 ) + offset, -_radius );
                    Point c = new Point( (int) ( -_radius * .25 ) + offset, (int) ( -_radius * .8 ) );
                    drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
                }

                // Right connection wire
                {
                    g2.setPaint( LOOP_MIDDLE_COLOR );
                    Point e1 = new Point( -loopSpacing + (int) ( _radius * .25 ) + offset, -_radius );
                    Point e2 = new Point( e1.x + 15, e1.y - 40 );
                    Point c = new Point( e1.x + 20, e1.y - 20 );
                    drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
                }
            }
        }
        
        // Draw the origin
        if ( DRAW_ORIGIN ) {
            g2.setPaint( Color.CYAN );
            int size = 8;
            g2.fill( new Ellipse2D.Double( -size/2, -size/2, size, size ) );
        }
    }
    
    //----------------------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------------------
    
    /**
     * Draws a quadratic curve.
     * Optionally renders the end-points and control point.
     * 
     * @param g2 graphics context
     * @param end1 end point #1
     * @param control control point
     * @param end2 end point #2
     * @param drawPoints true to render points
     */
    private static void drawQuadCurve( Graphics2D g2, Point2D end1, Point2D control, Point2D end2, boolean drawPoints ) {
        
        QuadCurve2D.Double curve = new QuadCurve2D.Double();
        curve.setCurve( end1, control, end2 );
        
        // Draw the curve.
        g2.draw( curve );

        // Draw the control points.
        if ( drawPoints ) {
            // Save state
            Paint oldPaint = g2.getPaint();
            Stroke oldStroke = g2.getStroke();
            
            g2.setPaint( Color.WHITE ); //
            g2.setStroke( new BasicStroke( 1f ) );
            g2.drawLine( (int)end1.getX(), (int)end1.getY(), (int)control.getX(), (int)control.getY() );
            g2.drawLine( (int)end2.getX(), (int)end2.getY(), (int)control.getX(), (int)control.getY() );
            
            g2.setPaint( END_POINT_COLOR );
            g2.fill( getPointShape( end1 ) );
            g2.fill( getPointShape( end2 ) );
            g2.setPaint( CONTROL_POINT_COLOR );
            g2.fill( getPointShape( control ) );

            // Restore state
            g2.setPaint( oldPaint );
            g2.setStroke( oldStroke );
        }
    }

    /**
     * Creates the shape used to represent a point.
     * 
     * @param p the point
     * @return a Shape
     */
    private static Shape getPointShape( Point2D p ) {
        int size = CONTROL_POINT_SIZE;
        return new Ellipse2D.Double( p.getX() - size / 2, p.getY() - size / 2, size, size );
    }
}
