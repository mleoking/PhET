/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ResizeArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.module.CloseRequestListener;
import edu.colorado.phet.statesofmatter.module.InteractionPotentialDiagramNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class extends the Interaction Potential diagram to allow the user to
 * adjust the interaction strength parameter (i.e. epsilon).
 *
 * @author John Blanco
 */
public class EpsilonControlInteractionPotentialDiagram extends InteractionPotentialDiagramNode {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    private static final double RESIZE_HANDLE_SIZE_PROPORTION = 0.12;    // Size of handles as function of node width.
    private static final double EPSILON_HANDLE_OFFSET_PROPORTION = 0.08; // Position of handle as function of node width.
    private static final Color RESIZE_HANDLE_NORMAL_COLOR = Color.GREEN;
    private static final Color RESIZE_HANDLE_HIGHLIGHTED_COLOR = Color.YELLOW;
    private static final float EPSILON_LINE_WIDTH = 1f;
    private static Stroke EPSILON_LINE_STROKE = new BasicStroke( EPSILON_LINE_WIDTH );
    private static final Color EPSILON_LINE_COLOR = RESIZE_HANDLE_NORMAL_COLOR; 
    private static final double CLOSE_BUTTON_PROPORTION = 0.10;  // Size of button as fraction of diagram height.
    
    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private ResizeArrowNode m_epsilonResizeHandle;
    private PPath m_epsilonLine;
    private boolean m_interactionEnabled;
    private JButton m_closeButton;
    private ArrayList _listeners = new ArrayList();
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
    public EpsilonControlInteractionPotentialDiagram(double sigma, double epsilon, boolean wide, 
            final MultipleParticleModel model) {
        
        super(sigma, epsilon, wide);
        
        this.m_model = model;
        model.addListener(new MultipleParticleModel.Adapter(){
            public void interactionStrengthChanged() {
                setLjPotentialParameters( m_model.getSigma(), m_model.getEpsilon() );
            }
            public void moleculeTypeChanged() {
                updateInteractivityState();
                drawPotentialCurve();
            }
        });
        
        // Create the handler for events that indicate that the user is
        // changing the value of epsilon.
        m_epsilonChangeHandler = new PBasicInputEventHandler(){
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
        m_epsilonLine = new PPath( new Line2D.Double( -epsilonLineLength / 3, 0, epsilonLineLength / 2, 0 ) );
        m_epsilonLine.setStroke( EPSILON_LINE_STROKE );
        m_epsilonLine.setStrokePaint( EPSILON_LINE_COLOR );
        m_epsilonLine.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        m_epsilonLine.addInputEventListener( m_epsilonChangeHandler );
        m_ljPotentialGraph.addChild( m_epsilonLine );
        
        // Add the arrow node that will allow the user to control the value of
        // the epsilon parameter.
        m_epsilonResizeHandle = new ResizeArrowNode(RESIZE_HANDLE_SIZE_PROPORTION * m_width, Math.PI/2,
        		RESIZE_HANDLE_NORMAL_COLOR, RESIZE_HANDLE_HIGHLIGHTED_COLOR);
        m_ljPotentialGraph.addChild( m_epsilonResizeHandle );
        m_epsilonResizeHandle.addInputEventListener( m_epsilonChangeHandler );
        
        // Add the button that will allow the user to close (actually hide) the diagram.
        m_closeButton = new JButton( 
        		new ImageIcon( PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON)));
        m_closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            	notifyCloseRequestReceived();
            }
        } );
        
        PSwing closePSwing = new PSwing( m_closeButton );
        closePSwing.setScale(getFullBoundsReference().height * CLOSE_BUTTON_PROPORTION / 
        		closePSwing.getFullBoundsReference().height);
        closePSwing.setOffset(m_width - closePSwing.getFullBoundsReference().width, 0);
        addChild(closePSwing);
        
        // Update interactivity state.
        updateInteractivityState();
    }

    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Other Public/Protected Methods
    //-----------------------------------------------------------------------------

    public void addListener(CloseRequestListener listener)
    {
        if ( !_listeners.contains( listener )){
            _listeners.add( listener );
        }
    }
    
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
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------

    private void updateInteractivityState() {
        if (m_model.getMoleculeType() != StatesOfMatterConstants.USER_DEFINED_MOLECULE){
            m_interactionEnabled = false;
        }
        else{
            m_interactionEnabled = true;
        }
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
