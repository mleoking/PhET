/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ResizeArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;
import edu.colorado.phet.statesofmatter.model.AtomType;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.module.InteractionPotentialDiagramNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class extends the Interaction Potential diagram to allow the user to
 * change the curve through direct interaction with it.
 *
 * @author John Blanco
 */
public class InteractiveInteractionPotentialDiagram extends InteractionPotentialDiagramNode {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    private static final double RESIZE_HANDLE_SIZE_PROPORTION = 0.05;    // Size of handles as function of node width.
    private static final double EPSILON_HANDLE_OFFSET_PROPORTION = 0.08; // Position of handle as function of node width.
    private static final double SIGMA_HANDLE_OFFSET_PROPORTION = 0.08;   // Position of handle as function of node width.
    private static final Color RESIZE_HANDLE_NORMAL_COLOR = new Color (51, 204, 51);
    private static final Color RESIZE_HANDLE_HIGHLIGHTED_COLOR = new Color(153, 255, 0);
    private static final float EPSILON_LINE_WIDTH = 1f;
    private static Stroke EPSILON_LINE_STROKE = new BasicStroke( EPSILON_LINE_WIDTH );
    private static final Color EPSILON_LINE_COLOR = RESIZE_HANDLE_NORMAL_COLOR;
    
    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private DualAtomModel m_model;
    private ResizeArrowNode m_sigmaResizeHandle;
    private ResizeArrowNode m_epsilonResizeHandle;
    private PPath m_epsilonLine;
    private boolean m_interactionEnabled;
    private PBasicInputEventHandler m_epsilonChangeHandler;

    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param sigma
     * @param epsilon
     * @param wide    - True if the widescreen version of the graph is needed, false if not.
     */
    public InteractiveInteractionPotentialDiagram(double sigma, double epsilon, boolean wide, 
            final DualAtomModel model) {
        
        super(sigma, epsilon, wide, false);
        
        this.m_model = model;
        model.addListener(new DualAtomModel.Adapter(){
            public void interactionPotentialChanged() {
                setLjPotentialParameters( model.getSigma(), model.getEpsilon() );
            }
            public void fixedAtomAdded(StatesOfMatterAtom particle) {
                updateInteractivityState();
                drawPotentialCurve();
            }
            public void movableAtomAdded(StatesOfMatterAtom particle) {
                updateInteractivityState();
                drawPotentialCurve();
            }
        });
        
        // Create the handler for events that indicate that the user is
        // changing the value of epsilon.
        m_epsilonChangeHandler = new PBasicInputEventHandler(){
        	public void mousePressed(PInputEvent event) {
        		m_model.setMotionPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		m_model.setMotionPaused(false);
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double scaleFactor = StatesOfMatterConstants.MAX_EPSILON / (getGraphHeight() / 2);
                m_model.setEpsilon( m_model.getEpsilon() + d.getHeight() * scaleFactor );
            }
        };
        
        // Add the line that will indicate the value of epsilon.
        double epsilonLineLength = EPSILON_HANDLE_OFFSET_PROPORTION * m_width * 2.2;
        m_epsilonLine = new PPath( new Line2D.Double( -epsilonLineLength / 3, 0, epsilonLineLength / 2.2, 0 ) );
        m_epsilonLine.setStroke( EPSILON_LINE_STROKE );
        m_epsilonLine.setStrokePaint( EPSILON_LINE_COLOR );
        m_epsilonLine.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        m_epsilonLine.addInputEventListener( m_epsilonChangeHandler );
        m_ljPotentialGraph.addChild( m_epsilonLine );
        
        // Add the arrow nodes that will allow the user to control the
        // parameters of the LJ potential.
        m_epsilonResizeHandle = new ResizeArrowNode(RESIZE_HANDLE_SIZE_PROPORTION * m_width, Math.PI/2,
        		RESIZE_HANDLE_NORMAL_COLOR, RESIZE_HANDLE_HIGHLIGHTED_COLOR);
        m_ljPotentialGraph.addChild( m_epsilonResizeHandle );
        m_epsilonResizeHandle.addInputEventListener( m_epsilonChangeHandler );
        
        m_sigmaResizeHandle = new ResizeArrowNode(RESIZE_HANDLE_SIZE_PROPORTION * m_width, 0,
        		RESIZE_HANDLE_NORMAL_COLOR, RESIZE_HANDLE_HIGHLIGHTED_COLOR);
        m_ljPotentialGraph.addChild( m_sigmaResizeHandle );
        m_sigmaResizeHandle.addInputEventListener(new PBasicInputEventHandler(){
        	public void mousePressed(PInputEvent event) {
        		m_model.setMotionPaused(true);
        	}
        	public void mouseReleased(PInputEvent event) {
        		m_model.setMotionPaused(false);
        	}
            public void mouseDragged(PInputEvent event) {
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double scaleFactor = MAX_INTER_ATOM_DISTANCE / getGraphWidth();
                m_model.setAdjustableAtomSigma( m_model.getSigma() + d.getWidth() * scaleFactor );
            }
        });
        
        // Add the ability to grab and move the position marker.
        // This node will need to be pickable so the user can grab it.
        m_positionMarker.setPickable( true );
        m_positionMarker.setChildrenPickable( true );
        m_positionMarker.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
        
        m_positionMarker.addInputEventListener( new PDragEventHandler(){
            
            public void startDrag( PInputEvent event) {
                super.startDrag(event);
                // Stop the particle from moving in the model.
                m_model.setMotionPaused( true );
            }
            
            public void drag(PInputEvent event){
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);

                // Move the particle based on the amount of mouse movement.
                StatesOfMatterAtom atom = m_model.getMovableAtomRef();
                double scaleFactor = MAX_INTER_ATOM_DISTANCE / getGraphWidth();
                double newPosX = Math.max( atom.getX() + (d.width * scaleFactor), atom.getRadius() * 1.8);
                atom.setPosition( newPosX, atom.getY() );
            }
            
            public void endDrag( PInputEvent event ){
                super.endDrag(event);     
                // Let the model move the particle again.  Note that this happens
                // even if the motion was paused by some other means.
                m_model.setMotionPaused( false );
            }
        });
        
        // Redraw the potential curve.
        drawPotentialCurve();
        
        // Update interactivity state.
        updateInteractivityState();
    }

    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Other Public/Protected Methods
    //-----------------------------------------------------------------------------

    /**
     * This is an override of the method in the base class that draws the
     * curve on the graph, and this override draws the controls that allow
     * the user to interact with the graph.
     */
    protected void drawPotentialCurve() {
        
        // The bulk of the drawing is done by the base class.
        super.drawPotentialCurve();
        
        // Now position the control handles.
        if (m_epsilonResizeHandle != null){
            Point2D graphMin = getGraphMin();
            m_epsilonResizeHandle.setOffset( graphMin.getX() + (m_width * EPSILON_HANDLE_OFFSET_PROPORTION), 
                    graphMin.getY() );
            m_epsilonResizeHandle.setVisible( m_interactionEnabled );
            m_epsilonResizeHandle.setPickable( m_interactionEnabled );
            m_epsilonResizeHandle.setChildrenPickable( m_interactionEnabled );
            
            m_epsilonLine.setOffset( graphMin.getX(), graphMin.getY() + EPSILON_LINE_WIDTH );
            m_epsilonLine.setVisible( m_interactionEnabled );
            m_epsilonLine.setPickable( m_interactionEnabled );
        }
        if (m_sigmaResizeHandle != null){
            Point2D zeroCrossingPoint = getZeroCrossingPoint();
            m_sigmaResizeHandle.setOffset( zeroCrossingPoint.getX(), 
                    (getGraphHeight() / 2) - SIGMA_HANDLE_OFFSET_PROPORTION * m_height );
            m_sigmaResizeHandle.setVisible( m_interactionEnabled );
            m_sigmaResizeHandle.setPickable( m_interactionEnabled );
            m_sigmaResizeHandle.setChildrenPickable( m_interactionEnabled );
        }
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------

    private void updateInteractivityState() {
        if (m_model.getFixedAtomType() != AtomType.ADJUSTABLE){
            m_interactionEnabled = false;
        }
        else{
            m_interactionEnabled = true;
        }
    }
}
