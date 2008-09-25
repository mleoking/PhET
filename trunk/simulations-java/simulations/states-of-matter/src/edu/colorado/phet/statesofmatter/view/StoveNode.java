/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;


public class StoveNode extends PNode {

    // Offset in Y direction for stove, tweak as needed.
    private static final double BURNER_Y_OFFSET = 20;
    
    // Scaling used to set relative size of burner to control panel.  Tweak
    // as needed.
    private static final double INITIAL_STOVE_SCALING = 1.5;

    // Heat value, ranges from -1 to +1.
    private double m_heat;
    private PImage m_fireImage;
    private PImage m_iceImage;
    private PImage m_stoveImage;
    private StoveControlSlider m_stoveControlSlider;
    private MultipleParticleModel m_model;

    public StoveNode(MultipleParticleModel model) {

        m_model = model;
        
        m_fireImage = StatesOfMatterResources.getImageNode("flames.gif");
        m_fireImage.setOffset( 0, BURNER_Y_OFFSET );
        m_fireImage.setScale( INITIAL_STOVE_SCALING );
        addChild(m_fireImage);

        m_iceImage = StatesOfMatterResources.getImageNode("ice.gif");
        m_iceImage.setOffset( 0, BURNER_Y_OFFSET );
        m_iceImage.setScale( INITIAL_STOVE_SCALING );
        addChild(m_iceImage);

        m_stoveImage = StatesOfMatterResources.getImageNode("stove.png");
        m_stoveImage.setOffset( 0, BURNER_Y_OFFSET );
        m_stoveImage.setScale( INITIAL_STOVE_SCALING );
        addChild(m_stoveImage);

        /*
        m_stoveControlPanel = new StoveControlPanel();
        m_stoveControlPanel.addListener(new StoveControlPanel.Listener() {
            public void valueChanged(double value) {
                m_heat = value;
                update();
                if (m_model != null){
                    m_model.setHeatingCoolingAmount( m_heat );
                }
            }
        });
        */
        m_stoveControlSlider = new StoveControlSlider();
        m_stoveControlSlider.setOpaque( true ); // Mac slider is transparent by default
        m_stoveControlSlider.addChangeListener( new ChangeListener(){
            public void stateChanged( ChangeEvent e ) {
                m_heat = m_stoveControlSlider.getNormalizedValue();
                update();
                if (m_model != null){
                    m_model.setHeatingCoolingAmount( m_heat );
                }
            }
        });
        PSwing stoveControlPanelNode = new PSwing(m_stoveControlSlider);
        addChild(stoveControlPanelNode);
        stoveControlPanelNode.setOffset(m_stoveImage.getFullBoundsReference().getWidth() + 15, 0);

        update();
    }

    private void update() {

        if (m_heat > 0) {
            m_fireImage.setOffset(m_stoveImage.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2,
                    -m_heat * m_stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET);
            m_iceImage.setOffset(m_stoveImage.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2, 0);
        } else if (m_heat <= 0) {
            m_iceImage.setOffset(m_stoveImage.getFullBoundsReference().width / 2 - m_iceImage.getFullBoundsReference().width / 2,
                    m_heat * m_stoveImage.getFullBoundsReference().height + BURNER_Y_OFFSET);
            m_fireImage.setOffset(m_stoveImage.getFullBoundsReference().width / 2 - m_fireImage.getFullBoundsReference().width / 2, 0);
        }
        m_iceImage.setVisible(m_heat<0);
        m_fireImage.setVisible(m_heat>0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiccoloTestFrame testFrame = new PiccoloTestFrame("Stove Node Test");
                StoveNode stoveNode = new StoveNode(null);
                stoveNode.setOffset(100, 200);
                testFrame.addNode(stoveNode);
                testFrame.setVisible(true);
            }
        });
    }
}
