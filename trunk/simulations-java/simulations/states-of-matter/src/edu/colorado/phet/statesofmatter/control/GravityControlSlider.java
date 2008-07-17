/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class implements a simple slider with the appropriate labels for
 * controlling the gravity level in the multi-particle model.
 *
 * @author John Blanco
 */
public class GravityControlSlider extends JPanel {
    
    private static final int GRAV_SLIDER_RANGE = 100;
    private JSlider m_gravitationalAccControl;
    private Font labelFont = new PhetFont(14, true);
    
    private MultipleParticleModel m_model;
    
    public GravityControlSlider(MultipleParticleModel model){
    
        m_model = model;
        
        setLayout( new GridLayout(0, 1) );
        
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                "Gravity Control", // JPB TBD - Make this into a string if we keep it.
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Register as a listener with the model so that we know when it gets
        // reset.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void resetOccurred(){
                m_gravitationalAccControl.setValue((int)((m_model.getGravitationalAcceleration() / 
                        MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL) * GRAV_SLIDER_RANGE));
            }
        });
        
        // Add the labels.
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout( new GridLayout(1,5) );
        JLabel leftLabel = new JLabel("None");
        leftLabel.setFont( labelFont );
        labelPanel.add( leftLabel );
        labelPanel.add(new JLabel(""));
        labelPanel.add(new JLabel(""));
        labelPanel.add(new JLabel(""));
        JLabel rightLabel = new JLabel("Lots");
        rightLabel.setFont( labelFont );
        labelPanel.add( rightLabel );
        add( labelPanel );
        
        // Add the slider that will control the gravitational acceleration of the system.
        m_gravitationalAccControl = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        m_gravitationalAccControl.setFont( new PhetFont( Font.PLAIN, 14 ) );
        m_gravitationalAccControl.setPaintTicks( true );
        m_gravitationalAccControl.setMajorTickSpacing( GRAV_SLIDER_RANGE / 10 );
        m_gravitationalAccControl.setMinorTickSpacing( GRAV_SLIDER_RANGE / 20 );
        m_gravitationalAccControl.setValue((int)((m_model.getGravitationalAcceleration() / 
                MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL) * GRAV_SLIDER_RANGE));
        m_gravitationalAccControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                m_model.setGravitationalAcceleration( m_gravitationalAccControl.getValue() * 
                        (MultipleParticleModel.MAX_GRAVITATIONAL_ACCEL / GRAV_SLIDER_RANGE));
            }
        });
        
        add(m_gravitationalAccControl);
    }
}
