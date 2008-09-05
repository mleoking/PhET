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
    private DualParticleModel m_model;

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
        
        // TODO JPB TBD - Testing out the adjustment thingie.
        ResizeArrowNode resizeArrow = new ResizeArrowNode(20, 0);
        resizeArrow.setOffset( getGraphWidth()/2, getGraphHeight()/2 );
        addChild( resizeArrow );

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
}
