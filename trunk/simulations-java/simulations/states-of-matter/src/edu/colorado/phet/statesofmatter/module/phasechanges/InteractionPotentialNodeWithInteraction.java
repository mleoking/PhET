package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
import edu.colorado.phet.statesofmatter.view.ResizeArrowNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;


public class InteractionPotentialNodeWithInteraction extends InteractionPotentialDiagramNode {

    private static final double RESIZE_HANDLE_SIZE_PROPORTION = 0.05;    // Size of handles as function of node width.
    private static final double EPSILON_HANDLE_OFFSET_PROPORTION = 0.08; // Position of handle as function of node width.
    private static final double SIGMA_HANDLE_OFFSET_PROPORTION = 0.08;   // Position of handle as function of node width.
    
    private DualParticleModel m_model;
    private ResizeArrowNode m_sigmaResizeHandle;
    private ResizeArrowNode m_epsilonResizeHandle;

    /**
     * Constructor.
     *
     * @param sigma   TODO
     * @param epsilon TODO
     * @param wide    - True if the widescreen version of the graph is needed,
     *                false if not.
     */
    public InteractionPotentialNodeWithInteraction(double sigma, double epsilon, boolean wide, final DualParticleModel model) {
        
        super(sigma, epsilon, wide);
        
        this.m_model = model;
        model.addListener(new DualParticleModel.Adapter(){
            public void interactionPotentialChanged() {
                setLjPotentialParameters( model.getSigma(), model.getEpsilon() );
            }
        });
        
        // Add the arrow nodes that will allow the user to control the
        // parameters of the LJ potential.
        m_epsilonResizeHandle = new ResizeArrowNode(RESIZE_HANDLE_SIZE_PROPORTION * m_width, Math.PI/2);
        addChild( m_epsilonResizeHandle );
        m_sigmaResizeHandle = new ResizeArrowNode(RESIZE_HANDLE_SIZE_PROPORTION * m_width, 0);
        addChild( m_sigmaResizeHandle );

        getEpsilonArrow().addInputEventListener(new CursorHandler());
        getEpsilonArrow().addInputEventListener(new PBasicInputEventHandler(){
            public PDimension pressPoint;

            public void mousePressed(PInputEvent event) {
                pressPoint=event.getDeltaRelativeTo(getEpsilonArrow().getParent());
            }

            public void mouseDragged(PInputEvent event) {
//                PDimension dx=event.getDeltaRelativeTo(getEpsilonArrow().getParent());
//                System.out.println("dx = " + dx);

//                m_model.setEpsilon(m_model.getEpsilon()+dx.getHeight()*2);

                /*
                 * Sam's stuff.
                Function.LinearFunction f=getLinearFunction();
                Function inverse =f.createInverse();
                double canvasDY=event.getCanvasDelta().getHeight();

                double modelDY=inverse.evaluate(canvasDY);
                System.out.println("canvasDY = " + canvasDY+", modelDY="+modelDY+", linearF="+f);
                m_model.setEpsilon(m_model.getEpsilon()+modelDY);
                 */
                
                PNode draggedNode = event.getPickedNode();
                PDimension d = event.getDeltaRelativeTo(draggedNode);
                draggedNode.localToParent(d);
                double mouseMovementAmount = d.getHeight();
                double scaleFactor = StatesOfMatterConstants.MAX_EPSILON / (getGraphHeight() / 2);
                double newEpsilon = m_model.getEpsilon() + mouseMovementAmount * scaleFactor;
                m_model.setEpsilon( newEpsilon );
                
//                double scaleFactor=m_model


            }
        });
    }

    protected void drawPotentialCurve() {
        
        // The bulk of the drawing is done by the base class.
        super.drawPotentialCurve();
        
        // Now position the control handles.
        if (m_epsilonResizeHandle != null){
            Point2D graphMin = getGraphMin();
            m_epsilonResizeHandle.setOffset( 
                    graphMin.getX() + getGraphOffsetX() + (m_width * EPSILON_HANDLE_OFFSET_PROPORTION), graphMin.getY() );
        }
        if (m_sigmaResizeHandle != null){
            Point2D zeroCrossingPoint = getZeroCrossingPoint();
            m_sigmaResizeHandle.setOffset( zeroCrossingPoint.getX() + getGraphOffsetX(), 
                    (getGraphHeight() / 2) - SIGMA_HANDLE_OFFSET_PROPORTION * m_height );
        }
    }
}
