/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class implements a simple slider with the appropriate labels for
 * controlling the gravity level in the multi-particle model.
 *
 * @author John Blanco
 */
public class GravityControlPanel extends JPanel {
    
    private static final Font LABEL_FONT = new PhetFont(14, true);
    
    private LinearValueControl m_gravityControl;
    
    private MultipleParticleModel m_model;
    
    public GravityControlPanel(MultipleParticleModel model){
    
        m_model = model;
        
        setLayout( new GridLayout(0, 1) );

        // Create the border.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                StatesOfMatterStrings.GRAVITY_CONTROL_TITLE,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Add the gravity control slider.
        m_gravityControl = new LinearValueControl( 0, MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL, "", "0", "", 
                new GravitySliderLayoutStrategy() );
        m_gravityControl.setValue( m_model.getGravitationalAcceleration() );
        m_gravityControl.setUpDownArrowDelta( 0.01 );
        m_gravityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                m_model.setGravitationalAcceleration( m_gravityControl.getValue() );
            }
        });
        Hashtable gravityControlLabelTable = new Hashtable();
        JLabel leftLabel = new JLabel(StatesOfMatterStrings.GRAVITY_CONTROL_NONE);
        leftLabel.setFont( LABEL_FONT );
        gravityControlLabelTable.put( new Double( m_gravityControl.getMinimum() ), leftLabel );
        JLabel rightLabel = new JLabel(StatesOfMatterStrings.GRAVITY_CONTROL_LOTS);
        rightLabel.setFont( LABEL_FONT );
        gravityControlLabelTable.put( new Double( m_gravityControl.getMaximum() ), rightLabel );
        m_gravityControl.setTickLabels( gravityControlLabelTable );

        // Register as a listener with the model so that we know when it gets
        // reset.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void resetOccurred(){
                m_gravityControl.setValue(m_model.getGravitationalAcceleration());
            }
        });
        
        add(m_gravityControl);
    }
    
    /**
     * Layout strategy for gravity slider.
     */
    public class GravitySliderLayoutStrategy implements ILayoutStrategy {

        public GravitySliderLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {

            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();

            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addFilledComponent( slider, 1, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}
