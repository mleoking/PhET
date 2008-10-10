/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view.instruments;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents a node that displays a dial gauge, which is a
 * circular instrument that can be used to portray measurements of temperature,
 * pressure, etc.
 *
 * @author John Blanco
 */
public class DialGaugeNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants the control the appearance of the dial gauge.
    private static final Color  BACKGROUND_COLOR = new Color( 245, 255, 250 );
    private static final Color  BORDER_COLOR = Color.LIGHT_GRAY;
    private static final Color  NEEDLE_COLOR = Color.RED;
    private static final double BORDER_SCALE_FACTOR = 0.015;           // Size of border wrt overal diameter.
    private static final double TICK_MARK_LENGTH_SCALE_FACTOR = 0.03; // Length of tick marks wrt overall diameter. 
    private static final double TICK_MARK_WIDTH_SCALE_FACTOR = 0.008;  // Width of tick marks wrt overall diameter. 
    private static final double NEEDLE_LENGTH_SCALE_FACTOR = 0.55;  // Length of needle wrt overall diameter. 
    private static final double NEEDLE_WIDTH_SCALE_FACTOR = 0.015;  // Width of needle wrt overall diameter. 
    private static final double PIN_DIAMETER_SCALE_FACTOR = 0.020;  // Diameter of attachment pin wrt overall diameter. 
    private static final double TEXTUAL_READOUT_HEIGHT_SCALE_FACTOR = 0.1;  // Height of textual readout box wrt overall diameter. 
    private static final double TEXTUAL_READOUT_WIDTH_SCALE_FACTOR = 0.6;     // Width of textual readout box wrt overall diameter. 
    private static final double TEXTUAL_READOUT_STROKE_SCALE_FACTOR = 0.010;  // Stroke width textual readout box wrt overall diameter. 
    private static final int    NUM_TICKMARKS = 19;
    private static double       GAUGE_START_ANGLE = -Math.PI * 1.15; // In radians.
    private static double       GAUGE_END_ANGLE = Math.PI * 0.15;    // In radians.
    private static double       GAUGE_ANGLE_RANGE = GAUGE_END_ANGLE - GAUGE_START_ANGLE;
    private static double       NEEDLE_SHIFT_PROPORTION = 0.75;      // Proportion of needle used as pointer.
    private static double       CONNECTOR_LENGTH_PROPORATION = 0.30; // Length of non-elbowed connector wrt overall diameter.
    private static double       CONNECTOR_WIDTH_PROPORATION = 0.15;  // Width of connector wrt overall diameter.
    private static DecimalFormat NUMBER_FORMATTER = new DecimalFormat( "##0.00" );
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private double m_diameter;
    private PPath m_needle;
    private double m_needleAngle;
    private PhetPPath m_connector;
    private double m_needleLength;
    private PText m_gaugeTitle;
    private PText m_textualReadout;
    private double m_minValue;
    private double m_maxValue;
    private String m_unitsLabel;
    private RoundRectangle2D m_textualReadoutBoxShape;
    private GeneralPath m_connectorPath;
    private boolean m_elbowEnabled;
    private double m_elbowHeight;
    private Paint m_connectorPaint;
	private PNode m_dialComponentsNode;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public DialGaugeNode(double diameter, String title, double minValue, double maxValue, String unitsLabel) {
        
        m_minValue = minValue;
        m_maxValue = maxValue;
        m_unitsLabel = new String(unitsLabel);
        m_elbowEnabled = false;
        m_diameter = diameter;
        
        // Scale various aspects of the size of this dial based on the overall
        // diameter.
        double borderWidth = diameter * BORDER_SCALE_FACTOR;
        
        m_dialComponentsNode = new PNode();
        m_dialComponentsNode.setOffset( 0, 0 );
        
        // Create the node that will represent the face of the dial.
        PPath dialFace = new PPath(new Ellipse2D.Double(0, 0, diameter, diameter));
        dialFace.setPaint( BACKGROUND_COLOR );
        dialFace.setStrokePaint( BORDER_COLOR );
        dialFace.setStroke( new BasicStroke( (float)borderWidth) );
        m_dialComponentsNode.addChild( dialFace );
        
        // Add the tick marks.
        double tickSpace = ( Math.PI * 6 / 4 ) / ( NUM_TICKMARKS );
        Stroke tickMarkStroke = new BasicStroke((float)(diameter * TICK_MARK_WIDTH_SCALE_FACTOR));
        for( double theta = GAUGE_START_ANGLE; theta <= GAUGE_END_ANGLE; theta += tickSpace ) {
            PPath tickMark = new PPath( new Line2D.Double(0, 0, diameter * TICK_MARK_LENGTH_SCALE_FACTOR, 0));
            tickMark.setStroke( tickMarkStroke );
            tickMark.rotate( theta );
            tickMark.setOffset( (diameter / 2) + (diameter * 0.44) * Math.cos(theta), 
                    (diameter / 2) + (diameter * 0.44) * Math.sin(theta) );
            m_dialComponentsNode.addChild(tickMark);
        }
        
        // Add the title.
        m_gaugeTitle = new PText();
        m_gaugeTitle.setText( title );
        m_gaugeTitle.setFont(new PhetFont(12));
        m_gaugeTitle.scale( (m_dialComponentsNode.getFullBoundsReference().width * 0.6)/m_gaugeTitle.getFullBoundsReference().width );
        m_gaugeTitle.setOffset(diameter / 2 - m_gaugeTitle.getFullBoundsReference().width / 2,
                diameter / 4);
        m_dialComponentsNode.addChild( m_gaugeTitle );
        
        // Add the textual readout display.
        m_textualReadoutBoxShape = new RoundRectangle2D.Double(0, 0, 
                TEXTUAL_READOUT_WIDTH_SCALE_FACTOR * diameter, 
                TEXTUAL_READOUT_HEIGHT_SCALE_FACTOR * diameter,
                4, 4);
        PPath textualReadoutHighlight = new PPath(m_textualReadoutBoxShape);
        float highlightStrokeWidth = (float)(diameter * TEXTUAL_READOUT_STROKE_SCALE_FACTOR * 3);
        textualReadoutHighlight.setStroke( new BasicStroke( highlightStrokeWidth ) );
        textualReadoutHighlight.setStrokePaint( Color.YELLOW );
        textualReadoutHighlight.setPaint( Color.WHITE );
        textualReadoutHighlight.setOffset( diameter / 2 - textualReadoutHighlight.getWidth() / 2 + highlightStrokeWidth / 2, 
                diameter * 0.70 );
        m_dialComponentsNode.addChild( textualReadoutHighlight );
        
        PPath textualReadoutBox = new PPath(m_textualReadoutBoxShape);
        float textBoxStrokeWidth = (float)(diameter * TEXTUAL_READOUT_STROKE_SCALE_FACTOR);
        textualReadoutBox.setStroke( new BasicStroke( textBoxStrokeWidth ) );
        textualReadoutBox.setStrokePaint( Color.DARK_GRAY );
        textualReadoutBox.setOffset( diameter / 2 - textualReadoutBox.getWidth() / 2 + textBoxStrokeWidth / 2,
                diameter * 0.70 );
        m_dialComponentsNode.addChild( textualReadoutBox );
        m_textualReadout = new PText(" ");
        m_textualReadout.setFont( new PhetFont(12) );
        m_textualReadout.scale( textualReadoutBox.getHeight() * 0.8 / m_textualReadout.getFullBoundsReference().height );
        textualReadoutBox.addChild( m_textualReadout );
        
        // Add the needle.
        m_needleLength = diameter * NEEDLE_LENGTH_SCALE_FACTOR;
        m_needle = new PPath(new Line2D.Double(0, 0, m_needleLength, 0));
        m_needle.setStroke( new BasicStroke((float)(diameter * NEEDLE_WIDTH_SCALE_FACTOR)) );
        m_needle.setStrokePaint( NEEDLE_COLOR );
        m_needle.setOffset( (diameter / 2) - (m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION)),
                diameter/2 );
        m_needleAngle = GAUGE_START_ANGLE;
        m_needle.rotateAboutPoint( m_needleAngle, (m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION)), 0 );
        m_dialComponentsNode.addChild( m_needle );
        
        // Add a little pin in the center where the needle attaches to the face.
        double pinDiameter = PIN_DIAMETER_SCALE_FACTOR * diameter;
        PPath pin = new PPath(new Ellipse2D.Double( 0, 0, pinDiameter, pinDiameter ) ); 
        pin.setPaint( Color.BLACK );
        pin.setOffset( diameter/2 -  pinDiameter/2, diameter/2 - pinDiameter/2);
        m_dialComponentsNode.addChild( pin );

        // Create the connector.  It is placed first because then it can be
        // behind everything else.  A PhetPPath is used because it allows us
        // to use a gradient without crashing any Macs.
        m_connector = new PhetPPath();
        m_connectorPaint = new GradientPaint((float)(diameter / 2), 0, Color.LIGHT_GRAY, (float)(diameter / 2), 
                    (float)(CONNECTOR_WIDTH_PROPORATION * diameter), Color.BLUE);
        m_connector.setPaint( m_connectorPaint );
        m_connectorPath = new GeneralPath();
        updateConnector();
        addChild(m_connector);
        
        // Now add the dial as a child of the main node.
        addChild(m_dialComponentsNode);
        
        // Set the initial value.
        setValue( m_minValue );
    }

    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the value of the pressure gauge.  The value must be within the
     * bounds set when the gauge was created.
     */
    public void setValue( double value ){

    	boolean overload = false;
    	
        // Limit the value.
        if (value < m_minValue){
        	value = m_minValue;
        }
        else if (value > m_maxValue){
        	value = m_maxValue;
        	overload = true;
        }

        // Set the needle position.
        double normalizedValue = value / (m_maxValue - m_minValue);
        double targetNeedleAngle = GAUGE_START_ANGLE + (GAUGE_ANGLE_RANGE * normalizedValue);
        m_needle.rotateAboutPoint( targetNeedleAngle - m_needleAngle, 
                m_needleLength * (1 - NEEDLE_SHIFT_PROPORTION), 0 );
        m_needleAngle = targetNeedleAngle;
        
        // Set the textual readout.
        if ( !overload ){
	        m_textualReadout.setText( new String (NUMBER_FORMATTER.format(value) + " " + m_unitsLabel ) );
	        m_textualReadout.setTextPaint( Color.BLACK );
	        m_textualReadout.setOffset( 
	                m_textualReadoutBoxShape.getWidth() / 2 - m_textualReadout.getFullBoundsReference().width / 2, 
	                m_textualReadoutBoxShape.getHeight() / 2 - m_textualReadout.getFullBoundsReference().height / 2  );
        }
        else{
	        m_textualReadout.setText( StatesOfMatterStrings.PRESSURE_GAUGE_OVERLOAD );
	        m_textualReadout.setTextPaint( Color.RED );
	        m_textualReadout.setOffset( 
	                m_textualReadoutBoxShape.getWidth() / 2 - m_textualReadout.getFullBoundsReference().width / 2, 
	                m_textualReadoutBoxShape.getHeight() / 2 - m_textualReadout.getFullBoundsReference().height / 2  );
        }
    }
    
    /**
     * This turns on/off the "elbow" portion of the connector, which allows
     * the pressure gauge to connect to something above or below it.
     * 
     * @param elbowEnabled
     */
    public void setElbowEnabled(boolean elbowEnabled){
    	m_elbowEnabled = elbowEnabled;
    	
    	// Create a different gradient if the elbow is enabled.
    	float width = (float)(CONNECTOR_WIDTH_PROPORATION * m_diameter);
    	float length = (float)(CONNECTOR_LENGTH_PROPORATION * m_diameter);
    	if (m_elbowEnabled){
            m_connectorPaint = new GradientPaint(length, width/2, Color.BLUE, length, length + width, Color.GRAY);
    	}
    	else{
            m_connectorPaint = new GradientPaint((float)(m_diameter / 2), 0, Color.LIGHT_GRAY, (float)(m_diameter / 2), 
                    (float)(CONNECTOR_WIDTH_PROPORATION * m_diameter), Color.BLUE);
    	}
        m_connector.setPaint(m_connectorPaint);
        
    	updateConnector();
    }
    
    /**
     * Set the height of the elbow.  Height is specified with respect to the
     * vertical center of the node.
     */
    public void setElbowHeight(double height){
    	m_elbowHeight = height;
    	updateConnector();
    }
    
    /**
     * Test harness.
     * 
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Dial Gauge Test");

                DialGaugeNode dialGaugeNode1 = new DialGaugeNode(200, "Pressure", 0, 1, "Atm");
                dialGaugeNode1.setOffset(50, 20);
                dialGaugeNode1.setValue( 0.75 );
                testFrame.addNode(dialGaugeNode1);
                
                DialGaugeNode dialGaugeNode2 = new DialGaugeNode(200, "Temperature", 0, 1, "Deg");
                dialGaugeNode2.setOffset(300, 220);
                dialGaugeNode2.setValue( 0.0 );
                dialGaugeNode2.setElbowEnabled(true);
                dialGaugeNode2.setElbowHeight(-200);
                testFrame.addNode(dialGaugeNode2);

                testFrame.setVisible(true);
            }
        });
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void updateConnector(){
    	
    	double width = CONNECTOR_WIDTH_PROPORATION * m_diameter;
    	double length = CONNECTOR_LENGTH_PROPORATION * m_diameter;
    	
    	if (!m_elbowEnabled){
    		Shape connectorShape = new Rectangle2D.Double(0, 0, length, width );
            m_connector.setPathTo(connectorShape);
        	m_connector.setOffset( m_dialComponentsNode.getFullBoundsReference().width * 0.9, 
                    m_diameter / 2 - width / 2 );
    	}
    	else{
    		m_connectorPath.reset();
    		m_connectorPath.moveTo(0f, 0f);
    		if (Math.abs(m_elbowHeight) < width / 2){
    			// The height of the elbow is less than the width of the
    			// connector, so just draw the connector at its basic
    			// width.
        		m_connectorPath.lineTo( (float)(length + width * 2), 0);
        		m_connectorPath.lineTo( (float)(length + width * 2), (float)width);
        		m_connectorPath.lineTo( 0, (float)width);
        		m_connectorPath.closePath();
                m_connector.setPathTo(m_connectorPath);
            	m_connector.setOffset( m_dialComponentsNode.getFullBoundsReference().width * 0.9, 
                        m_diameter / 2 - width / 2 );
    		}
    		else if (m_elbowHeight < 0){
    			// Connector is pointing upwards.
        		m_connectorPath.lineTo( (float)(length + width), 0);
        		m_connectorPath.lineTo( (float)(length + width), (float)(m_elbowHeight + width / 2));
        		m_connectorPath.lineTo( (float)(length + width * 2), (float)(m_elbowHeight + width / 2));
        		m_connectorPath.lineTo( (float)(length + width * 2), (float)width);
        		m_connectorPath.lineTo( 0, (float)width);
        		m_connectorPath.closePath();
                m_connector.setPathTo(m_connectorPath);
    		}
    		else{
    			// Connector is pointing downwards.
        		m_connectorPath.lineTo( (float)(length + width * 2), 0);
        		m_connectorPath.lineTo( (float)(length + width * 2), (float)(m_elbowHeight + width / 2));
        		m_connectorPath.lineTo( (float)(length + width), (float)(m_elbowHeight + width / 2));
        		m_connectorPath.lineTo( (float)(length + width), (float)width);
        		m_connectorPath.lineTo( 0, (float)width);
        		m_connectorPath.closePath();
                m_connector.setPathTo(m_connectorPath);
    		}
    	}

    }
}
