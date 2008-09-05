/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class displays a phase diagram suitable for inclusion on the control
 * panel of a PhET simulation.
 *
 * @author John Blanco
 */
public class InteractionPotentialDiagramNode extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Constants that control the range of data that is graphed.
    private static final double MAX_INTER_ATOM_DISTANCE = 1200;   // In picometers.
    
    // Constants that control the appearance of the diagram.
    private static final double NARROW_VERSION_WIDTH = 200;
    private static final double WIDE_VERSION_WIDTH = 300;
    private static final float AXIS_LINE_WIDTH = 1;
    private static final Stroke AXIS_LINE_STROKE = new BasicStroke(AXIS_LINE_WIDTH);
    private static final Color AXIS_LINE_COLOR = Color.LIGHT_GRAY;
    private static final double ARROW_LINE_WIDTH = 0.50;
    private static final double ARROW_HEAD_WIDTH = 8 * ARROW_LINE_WIDTH;
    private static final double ARROW_HEAD_HEIGHT = 10 * ARROW_LINE_WIDTH;
    private static final float POTENTIAL_ENERGY_LINE_WIDTH = 1.5f;
    private static final Stroke POTENTIAL_ENERGY_LINE_STROKE = new BasicStroke(POTENTIAL_ENERGY_LINE_WIDTH);
    private static final Color POTENTIAL_ENERGY_LINE_COLOR = Color.red;
    private static final int NUM_HORIZ_TICK_MARKS = 4;
    private static final int NUM_VERT_TICK_MARKS = 3;
    private static final double TICK_MARK_LENGTH = 2;
    private static final float TICK_MARK_WIDTH = 1;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke(TICK_MARK_WIDTH);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color POSITION_MARKER_COLOR = Color.CYAN;
    private static final double POSITION_MARKER_DIAMETER_PROPORTION = 0.03; // Size of pos marker wrt overall width.
    private static final float POSITION_MARKER_STROKE_WIDTH = 0.75f;
    private static final Stroke POSITION_MARKER_STROKE = new BasicStroke(POSITION_MARKER_STROKE_WIDTH);
    
    // Constants that control the location and size of the graph.
    private static final double HORIZ_AXIS_SIZE_PROPORTION = 0.80;
    private static final double VERT_AXIS_SIZE_PROPORTION = 0.85;
    
    // Font for the labels used on the axes and within the graph.
    private static final int AXIS_LABEL_FONT_SIZE = 14;
    private static final Font AXIS_LABEL_FONT = new PhetFont(AXIS_LABEL_FONT_SIZE);
    private static final int GREEK_LETTER_FONT_SIZE = 16;
    private static final Font GREEK_LETTER_FONT = new PhetFont(GREEK_LETTER_FONT_SIZE);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private double m_sigma;
    private double m_epsilon;
    private double m_width;
    private double m_height;
    private double m_graphOffsetX;
    private double m_graphOffsetY;
    private double m_graphWidth;
    private double m_graphHeight;
    private PPath m_potentialEnergyLine;
    private DoubleArrowNode m_epsilonArrow;
    private PText m_epsilonLabel;
    private PText m_sigmaLabel;
    private DoubleArrowNode m_sigmaArrow;
    private double m_verticalScalingFactor;
    
    // Variables for controlling the appearance, visibility, and location of
    // the position marker.
    private PPath m_positionMarker;
    private boolean m_positionMarkerEnabled;
    private Function.LinearFunction linearFunction;

    /**
     * Constructor.
     * @param sigma TODO
     * @param epsilon TODO
     * @param wide - True if the widescreen version of the graph is needed,
     * false if not.
     */
    public InteractionPotentialDiagramNode(double sigma, double epsilon, boolean wide){

        m_sigma = sigma;
        m_epsilon = epsilon;
        m_positionMarkerEnabled = false;
        
        // Set up for the normal or wide version of the graph.
        if (wide){
            m_width = WIDE_VERSION_WIDTH;
            m_height = m_width * 0.6;
        }
        else{
            m_width = NARROW_VERSION_WIDTH;
            m_height = m_width * 0.8;
        }
        m_graphOffsetX = 0.10 * (double)m_width;
        m_graphOffsetY = 0;
        m_graphWidth = m_width - m_graphOffsetX;
        m_graphHeight = m_height * VERT_AXIS_SIZE_PROPORTION;
        m_verticalScalingFactor = m_graphHeight / 2 / StatesOfMatterConstants.MAX_EPSILON;
        
        // Create a background that will sit behind everything.
        PPath graphBackground = new PPath(new Rectangle2D.Double( 0, 0, m_width, m_height ));
        graphBackground.setPaint( BACKGROUND_COLOR );
        addChild( graphBackground );

        // Create and add the Piccolo node that will contain the graph.
        PPath ljPotentialGraph = new PPath(new Rectangle2D.Double(0, 0, m_graphWidth, m_graphHeight));
        ljPotentialGraph.setOffset( m_graphOffsetX, 0 );
        ljPotentialGraph.setPaint( Color.WHITE );
        addChild( ljPotentialGraph );
        
        // Create and add the axis line for the graph.
        PPath horizontalAxis = new PPath(new Line2D.Double(new Point2D.Double(0, 0), 
                new Point2D.Double(m_graphWidth, 0)));
        horizontalAxis.setStroke( AXIS_LINE_STROKE );
        horizontalAxis.setStrokePaint( AXIS_LINE_COLOR );
        ljPotentialGraph.addChild( horizontalAxis );
        horizontalAxis.setOffset( 0, m_graphHeight / 2 );
        
        // Create and add the tick marks for the graph.
        double horizTickMarkSpacing = m_graphWidth / (NUM_HORIZ_TICK_MARKS + 1);
        Line2D tickMarkShape = new Line2D.Double();
        Point2D endpoint1 = new Point2D.Double();
        Point2D endpoint2 = new Point2D.Double();
        for (int i = 0; i < NUM_HORIZ_TICK_MARKS; i++){
            // Top tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), 0 );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath topTickMark = new PPath(tickMarkShape);
            topTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( topTickMark );

            // Bottom tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), m_graphHeight );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), m_graphHeight - TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath bottomTickMark = new PPath(tickMarkShape);
            bottomTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( bottomTickMark );
        }
        double vertTickMarkSpacing = m_graphHeight / (NUM_VERT_TICK_MARKS + 1);
        for (int i = 0; i < NUM_VERT_TICK_MARKS; i++){
            // Left tick mark
            endpoint1.setLocation( 0, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath leftTickMark = new PPath(tickMarkShape);
            leftTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( leftTickMark );

            // Right tick mark
            endpoint1.setLocation( m_graphWidth, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( m_graphWidth - TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath rightTickMark = new PPath(tickMarkShape);
            rightTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( rightTickMark );
        }
        
        // Add the potential energy line.
        m_potentialEnergyLine = new PPath();
        m_potentialEnergyLine.setStroke( POTENTIAL_ENERGY_LINE_STROKE );
        m_potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        ljPotentialGraph.addChild( m_potentialEnergyLine );
        
        // Add the position marker.
        GeneralPath markerPath = new GeneralPath();
        double markerDiameter = POSITION_MARKER_DIAMETER_PROPORTION * m_graphWidth;
        markerPath.append( new Ellipse2D.Double(0, 0, markerDiameter, markerDiameter ), false );
        markerPath.moveTo( 0f, (float)markerDiameter / 2 );
        markerPath.lineTo( (float)markerDiameter, (float)markerDiameter / 2 );
        markerPath.moveTo( (float)markerDiameter / 2, 0f );
        markerPath.lineTo( (float)markerDiameter / 2, (float)markerDiameter );
        m_positionMarker = new PPath( markerPath );
        m_positionMarker.setStroke( POSITION_MARKER_STROKE );
        m_positionMarker.setPaint( POSITION_MARKER_COLOR );
        m_positionMarker.setVisible( m_positionMarkerEnabled );
        ljPotentialGraph.addChild( m_positionMarker );

        // Add the arrows and labels that will depict sigma and epsilon.
        m_epsilonArrow = new DoubleArrowNode( new Point2D.Double( 0, 0 ), 
                new Point2D.Double( 0, m_graphHeight / 2 ), ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        m_epsilonArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( m_epsilonArrow );
        m_epsilonArrow.addInputEventListener( new CursorHandler() );
        
        m_epsilonLabel = new PText("\u03B5");
        m_epsilonLabel.setFont( GREEK_LETTER_FONT );
        ljPotentialGraph.addChild( m_epsilonLabel );

        m_sigmaLabel = new PText("\u03C3");
        m_sigmaLabel.setFont( GREEK_LETTER_FONT );
        ljPotentialGraph.addChild( m_sigmaLabel );

        m_sigmaArrow = new DoubleArrowNode(new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        m_sigmaArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( m_sigmaArrow );

        // Create and add the labels for the axes.
        PText horizontalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL);
        horizontalAxisLabel.setFont( AXIS_LABEL_FONT );
        horizontalAxisLabel.setOffset( m_graphOffsetX + (m_graphWidth / 2) - 
                (horizontalAxisLabel.getFullBoundsReference().width / 2), 
                m_graphOffsetY + m_graphHeight + (horizontalAxisLabel.getFullBoundsReference().height * 0.3));
        addChild( horizontalAxisLabel );
        
        PText verticalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_Y_AXIS_LABEL);
        verticalAxisLabel.setFont( AXIS_LABEL_FONT );
        verticalAxisLabel.setOffset( m_graphOffsetX / 2 - (verticalAxisLabel.getFullBoundsReference().height / 2), 
                (m_graphOffsetY + m_graphHeight) / 2 + (verticalAxisLabel.getFullBoundsReference().width / 2) );
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addChild( verticalAxisLabel );
        
        // Set the overall background color.
        setPaint( Color.LIGHT_GRAY );
        
        // Draw the curve upon the graph.
        drawPotentialCurve();
    }
    
    /**
     * Set the parameters that define the shape of the Lennard-Jones
     * potential curve.
     * 
     * @param sigma
     * @param epsilon
     */
    public void setLjPotentialParameters( double sigma, double epsilon ){
        
        // Update the parameters.
        m_sigma = sigma;
        m_epsilon = epsilon;
        
        // Redraw the graph to reflect the new parameters.
        drawPotentialCurve();

        double de=0.001;
        linearFunction = new Function.LinearFunction(0,-de,0,getEpsilonView(m_epsilon)-getEpsilonView(m_epsilon+de));

    }

    public Function.LinearFunction getLinearFunction() {
        return linearFunction;
    }

    private double getEpsilonView(double tempEpsilon) {
        double saveValue=this.m_epsilon;
        this.m_epsilon=tempEpsilon;
        drawPotentialCurve();
        double v = m_epsilonArrow.getGlobalFullBounds().getMaxY();
        this.m_epsilon=saveValue;
        drawPotentialCurve();
        return v;
    }

    protected DoubleArrowNode getEpsilonArrow() {
        return m_epsilonArrow;
    }

    protected DoubleArrowNode getSigmaArrow() {
        return m_sigmaArrow;
    }
    
    protected double getGraphHeight(){
        return m_graphHeight;
    }

    protected double getGraphWidth(){
        return m_graphWidth;
    }

    public void setMarkerEnabled( boolean enabled ){
        m_positionMarkerEnabled = enabled;
    }
    
    /**
     * Set the position of the position marker.  Note that is is only possible
     * to set the x axis position, which is distance.  The y axis position is
     * always on the LJ potential curve.
     * 
     * @param distance - distance from the center of the interacting molecules.
     */
    public void setMarkerPosition(double distance){

        double xPos = distance * ( m_graphWidth / MAX_INTER_ATOM_DISTANCE );
        double potential = calculateLennardJonesPotential( distance );
        double yPos = ((m_graphHeight / 2) - (potential * m_verticalScalingFactor));

        if ( m_positionMarkerEnabled && (xPos > 0) && (xPos < m_graphWidth) &&
                (yPos > 0) && (yPos < m_graphHeight)){

            m_positionMarker.setVisible( true );
            m_positionMarker.setOffset( xPos - m_positionMarker.getFullBoundsReference().width / 2,
                    yPos - m_positionMarker.getFullBoundsReference().getHeight() / 2 );
        }
        else{
            m_positionMarker.setVisible( false );
        }
    }
    
    /**
     * Get the range of values over which the potential curve is graphed.  It
     * is assumed to go from 0 to the value returned by this function.
     */
    public double getXAxisRange(){
        return MAX_INTER_ATOM_DISTANCE;
    }
    
    /**
     * Returns a value between 0 and 1 representing the fraction of the
     * overall node that is actually used for graphing in the x direction.
     */
    public double getXAxisGraphProportion(){
        return m_graphWidth / m_width;
    }
    
    /**
     * Calculate the Lennard-Jones potential for the given distance.
     * 
     * @param radius
     * @return
     */
    private double calculateLennardJonesPotential(double radius){
        return (4 * m_epsilon * (Math.pow( m_sigma / radius, 12 ) - Math.pow( m_sigma / radius, 6 )));
        
    }

    /**
     * Draw the curve that reflects the Lennard-Jones potential based upon the
     * current values for sigma and epsilon.
     */
    private void drawPotentialCurve(){
        GeneralPath potentialEnergyLineShape = new GeneralPath();
        potentialEnergyLineShape.moveTo( 0, 0);
        Point2D graphMin = new Point2D.Double(0, 0);
        Point2D zeroCrossingPoint = new Point2D.Double(0, 0);
        double horizontalIndexMultiplier = MAX_INTER_ATOM_DISTANCE / m_graphWidth;
        for (int i = 1; i < (int)m_graphWidth; i++){
            double potential = calculateLennardJonesPotential( i * horizontalIndexMultiplier );
            double yPos = ((m_graphHeight / 2) - (potential * m_verticalScalingFactor));
            if ((yPos > 0) && (yPos < m_graphHeight)){
                potentialEnergyLineShape.lineTo( (float)i, (float)(yPos) );
                if (yPos > graphMin.getY()){
                    // A new minimum has been found.  If you're wondering why
                    // the test is for greater than rather than less than, it
                    // is because positive Y is down rather than up within a
                    // PNode.
                    graphMin.setLocation( i, yPos );
                }
                if (potential > 0){
                    // The potential hasn't become negative yet, so update the
                    // zero crossing point.
                    zeroCrossingPoint.setLocation( i, m_graphHeight / 2 );
                }
            }
            else{
                // Move to a good location from which to start graphing.
                potentialEnergyLineShape.moveTo( i, 0);
            }
        }
        m_potentialEnergyLine.setPathTo( potentialEnergyLineShape );
        
        // Position the arrow that depicts epsilon along with its label.
      
        try{
            m_epsilonArrow.setTipAndTailLocations( graphMin, new Point2D.Double( graphMin.getX(), m_graphHeight / 2 ) );
        }
        catch(RuntimeException r){
            System.err.println("Error: Caught exception while positioning epsilon arrow - " + r);
        }
        
        m_epsilonLabel.setOffset( graphMin.getX() + m_epsilonLabel.getFullBoundsReference().width * 0.5, 
                ((graphMin.getY() - (m_graphHeight / 2)) / 2) - (m_epsilonLabel.getFullBoundsReference().height / 2) +
                m_graphHeight / 2 );

        // Position the arrow that depicts sigma along with its label.
        m_sigmaLabel.setOffset( zeroCrossingPoint.getX() / 2 - m_sigmaLabel.getFullBoundsReference().width / 2, 
                m_graphHeight / 2 );

        try{
            m_sigmaArrow.setTipAndTailLocations( new Point2D.Double(0, m_graphHeight / 2), zeroCrossingPoint );
        }
        catch(RuntimeException r){
            System.err.println("Error: Caught exception while positioning sigma arrow - " + r);
        }
    }
}
