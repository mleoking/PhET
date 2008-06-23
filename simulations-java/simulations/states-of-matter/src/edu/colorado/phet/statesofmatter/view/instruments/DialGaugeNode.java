/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view.instruments;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PLine;


public class DialGaugeNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants the control the appearance of the dial gauge.
    private static final Color  BACKGROUND_COLOR = new Color( 245, 255, 250 );
    private static final Color  BORDER_COLOR = Color.DARK_GRAY;
    private static final Color  NEEDLE_COLOR = Color.RED;
    private static final double BORDER_SCALE_FACTOR = 0.005;           // Size of border wrt overal diameter.
    private static final double TICK_MARK_LENGTH_SCALE_FACTOR = 0.03; // Length of tick marks wrt overall diameter. 
    private static final double TICK_MARK_WIDTH_SCALE_FACTOR = 0.008;  // Width of tick marks wrt overall diameter. 
    private static final double NEEDLE_LENGTH_SCALE_FACTOR = 0.55;  // Length of needle wrt overall diameter. 
    private static final double NEEDLE_WIDTH_SCALE_FACTOR = 0.015;  // Width of needle wrt overall diameter. 
    private static final double PIN_DIAMETER_SCALE_FACTOR = 0.020;  // Diameter of attachment pin wrt overall diameter. 
    private static final int    NUM_TICKMARKS = 19;
    private static double       GAUGE_START_ANGLE = -Math.PI * 5 / 4; // In radians.
    private static double       GAUGE_END_ANGLE = Math.PI / 4;        // In radians.
    private static double       GAUGE_ANGLE_RANGE = GAUGE_END_ANGLE - GAUGE_START_ANGLE;
    private static double       NEEDLE_SHIFT_PROPORTION = 0.75;      // Proportion of needle used as pointer.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PPath m_needle;
    private double m_needleAngle;
    private double m_needleLength;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    
    public DialGaugeNode(double diameter) {
        
        // Scale other aspects of the size of this dial based on the overall
        // diameter.
        double borderWidth = diameter * BORDER_SCALE_FACTOR;
        
        // Create the node that will represent the face of the dial.
        PPath dialFace = new PPath(new Ellipse2D.Double(0, 0, diameter, diameter));
        dialFace.setPaint( BACKGROUND_COLOR );
        dialFace.setStrokePaint( BORDER_COLOR );
        dialFace.setStroke( new BasicStroke( (float)borderWidth) );
        addChild( dialFace );
        
        // Add the tick marks.
        double tickSpace = ( Math.PI * 6 / 4 ) / ( NUM_TICKMARKS );
        Stroke tickMarkStroke = new BasicStroke((float)(diameter * TICK_MARK_WIDTH_SCALE_FACTOR));
        for( double theta = GAUGE_START_ANGLE; theta <= GAUGE_END_ANGLE; theta += tickSpace ) {
            PPath tickMark = new PPath( new Line2D.Double(0, 0, diameter * TICK_MARK_LENGTH_SCALE_FACTOR, 0));
            tickMark.setStroke( tickMarkStroke );
            tickMark.rotate( theta );
            tickMark.setOffset( (diameter / 2) + (diameter * 0.44) * Math.cos(theta), 
                    (diameter / 2) + (diameter * 0.44) * Math.sin(theta) );
            addChild(tickMark);
        }
        
        // Add the needle.
        m_needleLength = diameter * NEEDLE_LENGTH_SCALE_FACTOR;
        m_needle = new PPath(new Line2D.Double(0, 0, m_needleLength, 0));
        m_needle.setStroke( new BasicStroke((float)(diameter * NEEDLE_WIDTH_SCALE_FACTOR)) );
        m_needle.setStrokePaint( NEEDLE_COLOR );
        m_needle.setOffset( (diameter / 2) - (m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION)),
                diameter/2 );
        m_needleAngle = 0;
//        m_needleAngle = GAUGE_START_ANGLE;
//        m_needle.rotateAboutPoint( m_needleAngle, (m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION)), 0 );
        addChild( m_needle );
        
        // Add a little pin in the center where the needle attaches to the face.
        double pinDiameter = PIN_DIAMETER_SCALE_FACTOR * diameter;
        PPath knob = new PPath(new Ellipse2D.Double( 0, 0, pinDiameter, pinDiameter ) ); 
        knob.setPaint( Color.BLACK );
        knob.setOffset( diameter/2 -  pinDiameter/2, diameter/2 - pinDiameter/2);
        addChild( knob );
        
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the value of the pressure gauge to a normalized value, meaning that
     * the value is between 0 and 1.
     */
    public void setValue(double value){
        assert ((value >= 0.0) && (value <= 1.0));

        double targetNeedleAngle = GAUGE_START_ANGLE + (GAUGE_ANGLE_RANGE * value);
        m_needle.rotateAboutPoint( targetNeedleAngle - m_needleAngle, 
                m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION), 0 );
        m_needleAngle = targetNeedleAngle;
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Dial Gauge Test");
                DialGaugeNode dialGaugeNode = new DialGaugeNode(200);
                dialGaugeNode.setOffset(50, 50);
                testFrame.addNode(dialGaugeNode);
                testFrame.setVisible(true);
                dialGaugeNode.setValue( 0.75 );
            }
        });
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
}
