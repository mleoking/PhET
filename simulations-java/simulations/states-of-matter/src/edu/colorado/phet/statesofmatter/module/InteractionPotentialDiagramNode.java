/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.LjPotentialCalculator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    protected static final double MAX_INTER_ATOM_DISTANCE = 1200;   // In picometers.
    
    // Constants that control the appearance of the diagram.
    private static final double NARROW_VERSION_WIDTH = 200;
    private static final double WIDE_VERSION_WIDTH = 300;
    private static final float AXIS_LINE_WIDTH = 1f;
    private static final Stroke AXIS_LINE_STROKE = new BasicStroke(AXIS_LINE_WIDTH);
    private static final Color AXIS_LINE_COLOR = Color.BLACK;
    private static final double AXES_ARROW_HEAD_WIDTH = 5 * AXIS_LINE_WIDTH;
    private static final double AXES_ARROW_HEAD_HEIGHT = 8 * AXIS_LINE_WIDTH;
    private static final double ARROW_LINE_WIDTH = 0.50;
    private static final double PARAM_ARROW_HEAD_WIDTH = 8 * ARROW_LINE_WIDTH;
    private static final double PARAM_ARROW_HEAD_HEIGHT = 8 * ARROW_LINE_WIDTH;
    private static final float POTENTIAL_ENERGY_LINE_WIDTH = 1.5f;
    private static final Stroke POTENTIAL_ENERGY_LINE_STROKE = new BasicStroke(POTENTIAL_ENERGY_LINE_WIDTH);
    private static final Color POTENTIAL_ENERGY_LINE_COLOR = Color.red;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final Color POSITION_MARKER_COLOR = Color.CYAN;
    private static final double POSITION_MARKER_DIAMETER_PROPORTION = 0.03; // Size of pos marker wrt overall width.
    private static final float POSITION_MARKER_STROKE_WIDTH = 0.75f;
    private static final Stroke POSITION_MARKER_STROKE = new BasicStroke(POSITION_MARKER_STROKE_WIDTH);
    private static final Color CENTER_AXIS_LINE_COLOR = Color.LIGHT_GRAY;
    private static final double CLOSE_BUTTON_PROPORTION = 0.11;  // Size of button as fraction of diagram height.
    
    // Constants that control the location and size of the graph.
    private static final double VERT_AXIS_SIZE_PROPORTION = 0.85;
    
    // Font for the labels used on the axes and within the graph.
    private static final int AXIS_LABEL_FONT_SIZE = 14;
    private static final Font AXIS_LABEL_FONT = new PhetFont(AXIS_LABEL_FONT_SIZE);
    private static final int GREEK_LETTER_FONT_SIZE = 16;
    private static final Font GREEK_LETTER_FONT = new PhetFont(GREEK_LETTER_FONT_SIZE);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    protected final double m_width;
    protected final double m_height;
    private double m_sigma;
    private double m_epsilon;
    private double m_graphXOrigin;
    private double m_graphYOrigin;
    private double m_graphWidth;
    private double m_graphHeight;
    private PPath m_background;
    private PPath m_potentialEnergyLine;
    private DoubleArrowNode m_epsilonArrow;
    private PText m_epsilonLabel;
    private PText m_sigmaLabel;
    private DoubleArrowNode m_sigmaArrow;
    private double m_verticalScalingFactor;
    private Point2D m_graphMin;
    private Point2D m_zeroCrossingPoint;
    private double m_markerDistance;
    private PText m_horizontalAxisLabel;
    private LjPotentialCalculator m_LjPotentialCalculator;
    private ArrayList _listeners = new ArrayList();
    private JButton m_closeButton;
	private PSwing m_closePSwing;

    // Variables for controlling the appearance, visibility, and location of
    // the position marker.
    protected final PNode m_markerLayer;
    protected final PPath m_positionMarker;
    private boolean m_positionMarkerEnabled;

    // Layer where the graph elements are added.
    protected final PPath m_ljPotentialGraph;

    /**
     * Constructor.
     * @param sigma - Initial value of sigma, a.k.a. the atom diameter
     * @param epsilon - Initial value of epsilon, a.k.a. the interaction strength
     * @param wide - True if the widescreen version of the graph is needed,
     * false if not.
     */
    public InteractionPotentialDiagramNode(double sigma, double epsilon, boolean wide, boolean closable){

        m_sigma = sigma;
        m_epsilon = epsilon;
        m_positionMarkerEnabled = false;
        m_graphMin = new Point2D.Double(0, 0);
        m_zeroCrossingPoint = new Point2D.Double(0, 0);
        m_markerDistance = 0;
        m_LjPotentialCalculator = new LjPotentialCalculator(m_sigma, m_epsilon);
        
        // Set up for the normal or wide version of the graph.
        if (wide){
            m_width = WIDE_VERSION_WIDTH;
            m_height = m_width * 0.6;
        }
        else{
            m_width = NARROW_VERSION_WIDTH;
            m_height = m_width * 0.8;
        }
        m_graphXOrigin = 0.10 * (double)m_width;
        m_graphYOrigin = 0.85 * (double)m_height;
        m_graphWidth = m_width - m_graphXOrigin - AXES_ARROW_HEAD_HEIGHT;
        if (closable){
            m_graphHeight = m_height * (VERT_AXIS_SIZE_PROPORTION - CLOSE_BUTTON_PROPORTION);
        }
        else{
            m_graphHeight = m_height * VERT_AXIS_SIZE_PROPORTION - AXES_ARROW_HEAD_HEIGHT;
        }
        m_verticalScalingFactor = m_graphHeight / 2 / (StatesOfMatterConstants.MAX_EPSILON * StatesOfMatterConstants.K_BOLTZMANN);
        
        // Create the background that will sit behind everything.
        m_background = new PPath(new Rectangle2D.Double( 0, 0, m_width, m_height ));
        m_background.setPaint( DEFAULT_BACKGROUND_COLOR );
        addChild( m_background );

        // Create and add the portion that depicts the Lennard-Jones potential curve.
        m_ljPotentialGraph = new PPath(new Rectangle2D.Double(0, 0, m_graphWidth, m_graphHeight));
        m_ljPotentialGraph.setOffset( m_graphXOrigin, m_graphYOrigin - m_graphHeight );
        m_ljPotentialGraph.setPaint( Color.WHITE );
        m_ljPotentialGraph.setStrokePaint( Color.WHITE );
        addChild( m_ljPotentialGraph );

        // Create and add the center axis line for the graph.
        PPath centerAxis = new PPath(new Line2D.Double(new Point2D.Double(0, 0),new Point2D.Double(m_graphWidth, 0)));
        centerAxis.setStroke( AXIS_LINE_STROKE );
        centerAxis.setStrokePaint( CENTER_AXIS_LINE_COLOR );
        m_ljPotentialGraph.addChild( centerAxis );
        centerAxis.setOffset( 0, m_graphHeight / 2 );
        
        // Create and add the potential energy line.
        m_potentialEnergyLine = new PPath(){
            // Override the rendering hints so that the segmented line can be
            // drawn smoothly.
            public void paint(PPaintContext paintContext){
                Graphics2D g2 = paintContext.getGraphics();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                super.paint( paintContext );
                g2.setRenderingHints( oldHints );
            }
        };
        m_potentialEnergyLine.setStroke( POTENTIAL_ENERGY_LINE_STROKE );
        m_potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        m_ljPotentialGraph.addChild( m_potentialEnergyLine );
        
        // Add the arrows and labels that will depict sigma and epsilon.
        m_epsilonArrow = new DoubleArrowNode( new Point2D.Double( 0, 0 ), 
                new Point2D.Double( 0, m_graphHeight / 2 ), PARAM_ARROW_HEAD_HEIGHT, PARAM_ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        m_epsilonArrow.setPaint( Color.BLACK );
        m_ljPotentialGraph.addChild( m_epsilonArrow );
        
        m_epsilonLabel = new PText("\u03B5");
        m_epsilonLabel.setFont( GREEK_LETTER_FONT );
        m_ljPotentialGraph.addChild( m_epsilonLabel );

        m_sigmaLabel = new PText("\u03C3");
        m_sigmaLabel.setFont( GREEK_LETTER_FONT );
        m_ljPotentialGraph.addChild( m_sigmaLabel );

        m_sigmaArrow = new DoubleArrowNode(new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), 
                PARAM_ARROW_HEAD_HEIGHT, PARAM_ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        m_sigmaArrow.setPaint( Color.BLACK );
        m_ljPotentialGraph.addChild( m_sigmaArrow );

        // Add the position marker.
        m_markerLayer = new PNode();
        m_markerLayer.setOffset( m_graphXOrigin, m_graphYOrigin - m_graphHeight );
        addChild( m_markerLayer );
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
        m_markerLayer.addChild( m_positionMarker );

        if (closable){
            // Add the button that will allow the user to close the diagram.
            m_closeButton = new JButton( 
            		new ImageIcon( PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON)));
            m_closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                	notifyCloseRequestReceived();
                }
            } );
            
            m_closePSwing = new PSwing( m_closeButton );
            m_closePSwing.setScale(getFullBoundsReference().height * CLOSE_BUTTON_PROPORTION / 
            		m_closePSwing.getFullBoundsReference().height);
            m_closePSwing.setOffset(m_width - m_closePSwing.getFullBoundsReference().width, 0);
            addChild(m_closePSwing);
        }
        
        // Create and add the horizontal axis line for the graph.
        ArrowNode horizontalAxis = new ArrowNode( new Point2D.Double(0, 0), 
        		new Point2D.Double(m_graphWidth + AXES_ARROW_HEAD_HEIGHT, 0), AXES_ARROW_HEAD_HEIGHT,
        		AXES_ARROW_HEAD_WIDTH, AXIS_LINE_WIDTH );
        horizontalAxis.setStroke( AXIS_LINE_STROKE );
        horizontalAxis.setPaint( AXIS_LINE_COLOR );
        horizontalAxis.setStrokePaint( AXIS_LINE_COLOR );
        horizontalAxis.setOffset( m_graphXOrigin, m_graphYOrigin );
        addChild( horizontalAxis );
        
        m_horizontalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL_ATOMS);
        m_horizontalAxisLabel.setFont( AXIS_LABEL_FONT );
        addChild( m_horizontalAxisLabel );
        setMolecular( false );
        
        // Create and add the vertical axis line for the graph.
        ArrowNode verticalAxis = new ArrowNode( new Point2D.Double(0, 0),  
        		new Point2D.Double(0, -m_graphHeight - AXES_ARROW_HEAD_HEIGHT),
        		AXES_ARROW_HEAD_HEIGHT, AXES_ARROW_HEAD_WIDTH, AXIS_LINE_WIDTH );
        verticalAxis.setStroke( AXIS_LINE_STROKE );
        verticalAxis.setPaint( AXIS_LINE_COLOR );
        verticalAxis.setStrokePaint( AXIS_LINE_COLOR );
        verticalAxis.setOffset( m_graphXOrigin, m_graphYOrigin );
        addChild( verticalAxis );
        
        PText verticalAxisLabel = new PText(StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_Y_AXIS_LABEL);
        verticalAxisLabel.setFont( AXIS_LABEL_FONT );
        verticalAxisLabel.setOffset( m_graphXOrigin / 2 - (verticalAxisLabel.getFullBoundsReference().height / 2), 
                m_graphYOrigin - (m_graphHeight / 2) + (verticalAxisLabel.getFullBoundsReference().width / 2) );
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addChild( verticalAxisLabel );
        
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
        
        // Update the Lennard-Jones force calculator.
        m_LjPotentialCalculator.setEpsilon( m_epsilon );
        m_LjPotentialCalculator.setSigma( m_sigma );
        
        // Redraw the graph to reflect the new parameters.
        drawPotentialCurve();
    }
    
    public void addListener(CloseRequestListener listener)
    {
        if ( !_listeners.contains( listener )){
            _listeners.add( listener );
        }
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
    
    protected Point2D getZeroCrossingPoint(){
        return m_zeroCrossingPoint;
    }

    protected Point2D getGraphMin(){
        return m_graphMin;
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

        m_markerDistance = distance;
        double xPos = m_markerDistance * ( m_graphWidth / MAX_INTER_ATOM_DISTANCE );
        double potential = calculateLennardJonesPotential( m_markerDistance );
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
     * Set whether the graph is showing the potential between individual atoms
     * or multi-atom molecules.
     * 
     * @param molecular - true if graph is portraying molecules, false for
     * individual atoms.
     */
    public void setMolecular( boolean molecular ) {
        if (molecular) {
            m_horizontalAxisLabel.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL_MOLECULES );
        }
        else {
            m_horizontalAxisLabel.setText( StatesOfMatterStrings.INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL_ATOMS );
        }
        m_horizontalAxisLabel.setOffset( m_graphXOrigin + (m_graphWidth / 2) - 
                (m_horizontalAxisLabel.getFullBoundsReference().width / 2), 
                m_graphYOrigin + (m_horizontalAxisLabel.getFullBoundsReference().height * 0.3));

    }
    
    public void setBackgroundColor(Color newColor){
    	m_background.setPaint(newColor);
    	m_background.setStrokePaint(newColor);
    }
    
    /**
     * Calculate the Lennard-Jones potential for the given distance.
     * 
     * @param radius
     * @return
     */
    private double calculateLennardJonesPotential(double radius){
        return ( m_LjPotentialCalculator.calculateLjPotential( radius ) );
    }

    /**
     * Draw the curve that reflects the Lennard-Jones potential based upon the
     * current values for sigma and epsilon.
     */
    protected void drawPotentialCurve(){
        GeneralPath potentialEnergyLineShape = new GeneralPath();
        potentialEnergyLineShape.moveTo( 0, 0);
        m_graphMin.setLocation( 0, 0 );
        m_zeroCrossingPoint.setLocation( 0, 0 );
        double horizontalIndexMultiplier = MAX_INTER_ATOM_DISTANCE / m_graphWidth;
        for (int i = 1; i < (int)m_graphWidth; i++){
            double potential = calculateLennardJonesPotential( i * horizontalIndexMultiplier );
            double yPos = ((m_graphHeight / 2) - (potential * m_verticalScalingFactor));
            if ((yPos > 0) && (yPos < m_graphHeight)){
                potentialEnergyLineShape.lineTo( (float)i, (float)(yPos) );
                if (yPos > m_graphMin.getY()){
                    // A new minimum has been found.  If you're wondering why
                    // the test is for greater than rather than less than, it
                    // is because positive Y is down rather than up within a
                    // PNode.
                    m_graphMin.setLocation( i, yPos );
                }
                if ((potential > 0) || (m_zeroCrossingPoint.getX() == 0)){
                    // The potential hasn't become negative yet, so update the
                    // zero crossing point.
                    m_zeroCrossingPoint.setLocation( i, m_graphHeight / 2 );
                }
            }
            else{
                // Move to a good location from which to start graphing.
                potentialEnergyLineShape.moveTo( i + 1, 0);
            }
        }
        m_potentialEnergyLine.setPathTo( potentialEnergyLineShape );
        
        // Position the arrow that depicts epsilon along with its label.
      
        Point2D epsilonArrowStartPt = new Point2D.Double( m_graphMin.getX(), m_graphHeight / 2 );
        if (epsilonArrowStartPt.distance(m_graphMin) > m_epsilonArrow.getHeadHeight() * 2){
        	m_epsilonArrow.setVisible(true);
            try{
                m_epsilonArrow.setTipAndTailLocations( m_graphMin, epsilonArrowStartPt );
            }
            catch(RuntimeException r){
                System.err.println("Error: Caught exception while positioning epsilon arrow - " + r);
            }
        }
        else{
        	// Don't show the arrow if there isn't enough space.
        	m_epsilonArrow.setVisible(false);
        }
        
        m_epsilonLabel.setOffset( m_graphMin.getX() + m_epsilonLabel.getFullBoundsReference().width * 0.5, 
                ((m_graphMin.getY() - (m_graphHeight / 2)) / 3) - (m_epsilonLabel.getFullBoundsReference().height / 2) +
                m_graphHeight / 2 );

        // Position the arrow that depicts sigma along with its label.
        m_sigmaLabel.setOffset( m_zeroCrossingPoint.getX() / 2 - m_sigmaLabel.getFullBoundsReference().width / 2, 
                m_graphHeight / 2 );

        try{
            m_sigmaArrow.setTipAndTailLocations( new Point2D.Double(0, m_graphHeight / 2), m_zeroCrossingPoint );
        }
        catch(RuntimeException r){
            System.err.println("Error: Caught exception while positioning sigma arrow - " + r);
        }
        
        // Update the position of the marker in case the curve has moved.
        setMarkerPosition( m_markerDistance );
    }
    
    /**
     * Notify listeners about a request to close this diagram.
     */
    private void notifyCloseRequestReceived(){
        for (int i = 0; i < _listeners.size(); i++){
            ((CloseRequestListener)_listeners.get(i)).closeRequestReceived();
        }
    }
}
