import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TestParametricCoil is a program that was used for developing the Faraday pickup coil.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestParametricCoil extends JComponent {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int CONTROL_POINT_SIZE = 4;
    private static final Color CONTROL_POINT_COLOR = Color.YELLOW;
    private static final Color END_POINT_COLOR = Color.BLUE;
    private static final Color LOOP_LIGHTEST_COLOR = new Color( 153, 102, 51 );
    private static final Color LOOP_MIDDLE_COLOR = new Color( 92, 52, 12 );
    private static final Color LOOP_DARKEST_COLOR = new Color( 40, 23, 3 );
    private static final int LOOP_RADIUS = 100;
    private static final int LOOP_WIDTH = 16;
    private static final int LOOP_SPACING = 25;
    
    private static final boolean DRAW_POINTS = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfLoops;
    private int _radius;
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
        Dimension sliderSize = new Dimension( 150, 20 );

        // Create a coil.
        final TestParametricCoil coil = new TestParametricCoil( numberOfLoops, LOOP_RADIUS, coilLocation );

        // Number of Loops control
        Box loopsPanel = new Box( BoxLayout.X_AXIS );
        {
            JLabel label = new JLabel( "Number of Loops:" );
            
            final JLabel value = new JLabel( String.valueOf( coil.getNumberOfLoops() ) );
            
            final JSlider slider = new JSlider();
            slider.setMinimumSize( sliderSize );
            slider.setMaximumSize( sliderSize );
            slider.setPreferredSize( sliderSize );
            slider.setMinimum( 1 );
            slider.setMaximum( 10 );
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
            slider.setMinimumSize( sliderSize );
            slider.setMaximumSize( sliderSize );
            slider.setPreferredSize( sliderSize );
            slider.setMinimum( 75 );
            slider.setMaximum( 150 );
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
    public TestParametricCoil( int numberOfLoops, int loopRadius, Point location ) {
        super();
        assert( numberOfLoops > 0 );
        _numberOfLoops = numberOfLoops;
        _radius = loopRadius;
        _location = location;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setNumberOfLoops( int numberOfLoops ) {
        assert( numberOfLoops > 0 );
        _numberOfLoops = numberOfLoops;
        repaint();
    }
    
    public int getNumberOfLoops() {
        return _numberOfLoops;
    }
    
    public void setRadius( int radius ) {
        assert( radius > 0 );
        _radius = radius;
        repaint();
    }
    
    public int getRadius() {
        return _radius;
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------
    
    public void paintComponent( Graphics g ) {
       
        assert( g instanceof Graphics2D );

        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( LOOP_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        g2.translate( _location.x, _location.y );
 
        // Center of all loops should remain fixed.
        int firstLoopCenter = -( LOOP_SPACING * (_numberOfLoops - 1) / 2 );
        
        // Back of loops
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * LOOP_SPACING );
            
            g2.setPaint( new GradientPaint( 0, (int)(_radius * .40), LOOP_DARKEST_COLOR, 0, (int)(_radius * .90), LOOP_MIDDLE_COLOR ) );
            
            // Back top
            {
                Point e1 = new Point( (int)(_radius * .25) + offset, 0 );
                Point e2 = new Point( -LOOP_SPACING + (int)(_radius * .15) + offset, -_radius );
                Point c = new Point( (int)(_radius * .15) + offset, (int)(-_radius * .70));
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Back bottom
            {
                Point e1 = new Point( (int)(_radius * .25) + offset, 0 );
                Point e2 = new Point( (int)(_radius * .15) + offset, (int)(_radius * .98) );
                Point c = new Point( (int)(_radius * .30) + offset, (int)(_radius * .70) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Left connection wire
            if ( i == 0 ) {
                g2.setPaint( LOOP_DARKEST_COLOR );
                Point e1 = new Point( -LOOP_SPACING + (int)(_radius * .15) + offset, -_radius );
                Point e2 = new Point( e1.x - 15, e1.y - 40 );
                Point c = new Point( e1.x - 20, e1.y - 20 );
                g2.setPaint( new GradientPaint( e2.x, 0, LOOP_MIDDLE_COLOR, e1.x, 0, LOOP_DARKEST_COLOR ) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
        }
        
        // Front of loops
        for ( int i = 0; i < _numberOfLoops; i++ ) {
            
            int offset = firstLoopCenter + ( i * LOOP_SPACING );;
            
            g2.setPaint( new GradientPaint( (int)(-_radius * .25) + offset, 0, LOOP_LIGHTEST_COLOR, (int)(-_radius * .15) + offset, 0, LOOP_MIDDLE_COLOR ) );
            
            // Front top
            {
                Point e1 = new Point( (int)(-_radius * .25) + offset, 0 );
                Point e2 = new Point( (int)(_radius * .15) + offset, -_radius );
                Point c = new Point( (int)(-_radius * .20) + offset, (int)(-_radius * 1.30) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }

            // Front bottom
            {
                Point e1 = new Point( (int)(-_radius * .25) + offset, 0 );
                Point e2 = new Point( (int)(_radius * .13) + offset, _radius );
                Point c = new Point( (int)(-_radius * .20) + offset, (int)(_radius * 1.30) );
                drawQuadCurve( g2, e1, c, e2, DRAW_POINTS );
            }
            
            // Right connection wire
            if ( i == (_numberOfLoops - 1) ) {
                g2.setPaint( LOOP_MIDDLE_COLOR );
                Point e1 = new Point( (int)(_radius * .15) + offset, -_radius );
                Point e2 = new Point( e1.x + 10, e1.y - 40 );
                Point c = new Point( e1.x + 20, e1.y + 20 );
                drawQuadCurve( g2, e1, c, e2, true );
            }
        }
        
        // Draw the origin
        g2.setPaint( Color.CYAN );
        g2.fill( new Ellipse2D.Double( -2, -2, 4, 4 ) );
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
