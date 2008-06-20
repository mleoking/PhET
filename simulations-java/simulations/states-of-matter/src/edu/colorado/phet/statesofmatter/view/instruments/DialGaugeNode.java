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
    private static final Color BACKGROUND_COLOR = new Color( 245, 255, 250 );
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private static final Color NEEDLE_COLOR = Color.RED;
    private static final int NUM_TICKMARKS = 19;
    private static final double BORDER_SCALE_FACTOR = 0.005;           // Size of border wrt overal diameter.
    private static final double TICK_MARK_LENGTH_SCALE_FACTOR = 0.03; // Length of tick marks wrt overall diameter. 
    private static final double TICK_MARK_WIDTH_SCALE_FACTOR = 0.008;  // Width of tick marks wrt overall diameter. 
    private static final double NEEDLE_LENGTH_SCALE_FACTOR = 0.60;  // Length of needle wrt overall diameter. 
    private static final double NEEDLE_WIDTH_SCALE_FACTOR = 0.010;  // Width of needle wrt overall diameter. 
    private static final double PIN_DIAMETER_SCALE_FACTOR = 0.020;  // Diameter of attachment pin wrt overall diameter. 
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    PPath m_needle;

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
        double tickSpace = ( Math.PI * 6 / 4 ) / ( NUM_TICKMARKS - 1 );
        Stroke tickMarkStroke = new BasicStroke((float)(diameter * TICK_MARK_WIDTH_SCALE_FACTOR));
        for( double theta = Math.PI * 3 / 4; theta <= Math.PI * 9 / 4 + tickSpace / 2; theta += tickSpace ) {
            PPath tickMark = new PPath( new Line2D.Double(0, 0, diameter * TICK_MARK_LENGTH_SCALE_FACTOR, 0));
            tickMark.setStroke( tickMarkStroke );
            tickMark.rotate( theta );
            tickMark.setOffset( (diameter / 2) + (diameter * 0.44) * Math.cos(theta), 
                    (diameter / 2) + (diameter * 0.44) * Math.sin(theta) );
            addChild(tickMark);
        }
        
        // Add the needle.
        double needleLength = diameter * NEEDLE_LENGTH_SCALE_FACTOR;
        m_needle = new PPath(new Line2D.Double(0, 0, needleLength, 0));
        m_needle.setStroke( new BasicStroke((float)(diameter * NEEDLE_WIDTH_SCALE_FACTOR)) );
        m_needle.setStrokePaint( NEEDLE_COLOR );
        m_needle.setOffset( (diameter / 2) - (needleLength * 0.75), diameter/2 );
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Dial Gauge Test");
                DialGaugeNode dialGaugeNode = new DialGaugeNode(200);
                dialGaugeNode.setOffset(50, 50);
                testFrame.addNode(dialGaugeNode);
                testFrame.setVisible(true);
            }
        });
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
}
