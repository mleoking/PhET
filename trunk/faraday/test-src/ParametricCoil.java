import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * ParametricCoil is a program that was used for developing the Faraday pickup coil.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ParametricCoil extends JComponent {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int CONTROL_POINT_SIZE = 4;
    private static final Color CONTROL_POINT_COLOR = Color.YELLOW;
    private static final Color END_POINT_COLOR = Color.BLUE;
    private static final Color LOOP_LIGHTEST_COLOR = new Color( 153, 102, 51 );
    private static final Color LOOP_MIDDLE_COLOR = new Color( 92, 52, 12 );
    private static final Color LOOP_DARKEST_COLOR = new Color( 40, 23, 3 );
    private static final int LOOP_WIDTH = 16;
    private static final int LOOP_SPACING = 25;
    
    private static final boolean DRAW_POINTS = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfLoops;
    private Point _location;
    
    //----------------------------------------------------------------------------
    // Test harness
    //----------------------------------------------------------------------------
    
    public static void main( String[] args )
    {
        // Test parameters
        int numberOfLoops = 4;
        Point coilLocation = new Point( 250, 250 );
        Dimension frameSize = new Dimension( 500, 500 );
        Color frameBackground = Color.BLACK;

        // Create a coil.
        ParametricCoil coil = new ParametricCoil( numberOfLoops, coilLocation );

        // Put it in a frame.
        final Frame f = new Frame( "Parametric Coil" );
        f.add( coil );
        f.setBackground( frameBackground);
        f.setSize( frameSize );
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
     */
    public ParametricCoil( int numberOfLoops, Point location ) {
        super();
        assert( numberOfLoops > 0 );
        _numberOfLoops = numberOfLoops;
        _location = location;
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------
    
    public void paintComponent( Graphics g ) {
        System.out.println( "ParametricCoil.paintComponent" );

        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( LOOP_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        g2.translate( _location.x, _location.y );
        
        // Draw the origin
        g2.setPaint( Color.CYAN );
        g2.fill( getPointShape( new Point(0,0) ) );
 
        // Back of loops
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = i * LOOP_SPACING;
            
            g2.setPaint( new GradientPaint( 0, 40, LOOP_DARKEST_COLOR, 0, 90, LOOP_MIDDLE_COLOR ) );
            
            // Back top
            {
                Point e1 = new Point( 25 + offset, 0 );
                Point e2 = new Point( -LOOP_SPACING + 15 + offset, -100 );
                Point c = new Point( 15 + offset, -70);
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Back bottom
            {
                Point e1 = new Point( 25 + offset, 0 );
                Point e2 = new Point( 15 + offset, 98 );
                Point c = new Point( 30 + offset, 70);
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
        }
        
        // Front of loops
        g2.setPaint( new GradientPaint( -25, 0, LOOP_LIGHTEST_COLOR, 25, 130, LOOP_MIDDLE_COLOR ) );
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = i * LOOP_SPACING;
            
            g2.setPaint( new GradientPaint( -25 + offset, 0, LOOP_LIGHTEST_COLOR, -15 + offset, 0, LOOP_MIDDLE_COLOR ) );
            
            // Front top
            {
                Point e1 = new Point( -25 + offset, 0 );
                Point e2 = new Point( 15 + offset, -100 );
                Point c = new Point( -20 + offset, -130 );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }

            // Front bottom
            {
                Point e1 = new Point( -25 + offset, 0 );
                Point e2 = new Point( 13 + offset, 100 );
                Point c = new Point( -20 + offset, 130 );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------------------
    
    /**
     * Draws a cubic curve.
     */
    private static void drawCubicCurve( Graphics2D g2, Point2D end1, Point2D control1, Point2D control2, Point2D end2, boolean drawPoints ) {
        
        CubicCurve2D.Double curve = new CubicCurve2D.Double();
        curve.setCurve( end1, control1, control2, end2 );
        
        // Draw the curve.
        g2.draw( curve );

        // Draw the control points.
        if ( drawPoints ) {
            Paint oldPaint = g2.getPaint();
            g2.setPaint( END_POINT_COLOR );
            g2.fill( getPointShape( end1 ) );
            g2.fill( getPointShape( end2 ) );
            g2.setPaint( CONTROL_POINT_COLOR );
            g2.fill( getPointShape( control1 ) );
            g2.fill( getPointShape( control2 ) );
            g2.setPaint( oldPaint );
        }
    }
    
    /**
     * Draws a quadratic curve, and renders the control points.
     */
    private static void drawQuadCurve( Graphics2D g2, Point2D end1, Point2D control, Point2D end2, boolean drawPoints ) {
        
        QuadCurve2D.Double curve = new QuadCurve2D.Double();
        curve.setCurve( end1, control, end2 );
        
        // Draw the curve.
        g2.draw( curve );

        // Draw the control points.
        if ( drawPoints ) {
            Paint oldPaint = g2.getPaint();
            g2.setPaint( END_POINT_COLOR );
            g2.fill( getPointShape( end1 ) );
            g2.fill( getPointShape( end2 ) );
            g2.setPaint( CONTROL_POINT_COLOR );
            g2.fill( getPointShape( control ) );
            g2.setPaint( oldPaint );
        }
    }

    /**
     * Creates the shape used to represent a point.
     * 
     * @param p the point
     * @return a Shape
     */
    private static Shape getPointShape( Point2D p ) {
        int side = CONTROL_POINT_SIZE;
        return new Rectangle2D.Double( p.getX() - side / 2, p.getY() - side / 2, side, side );
    }
}
