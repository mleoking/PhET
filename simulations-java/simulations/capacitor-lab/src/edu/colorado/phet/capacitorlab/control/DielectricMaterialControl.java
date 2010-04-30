/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Polystyrene;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control for selecting a dielectric material.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricMaterialControl extends JPanel {
    
    private final JComboBox comboBox;
    private final EventListenerList listeners;
    
    public DielectricMaterialControl() {
        
        listeners = new EventListenerList();
        
        JLabel label = new JLabel( CLStrings.LABEL_DIELECTRIC_MATERIAL );
        
        DielectricMaterial custom = new CustomDielectricMaterial();
        DielectricMaterial teflon = new Teflon();
        DielectricMaterial polystyrene = new Polystyrene();
        DielectricMaterial paper = new Paper();
        DielectricMaterial[] materials = { custom, teflon, polystyrene, paper };
        comboBox = new JComboBox( materials );
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent event ) {
                if ( event.getStateChange() == ItemEvent.SELECTED ) {
                    fireStateChanged();
                }
            }
        });
        
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( label, row++, column );
        layout.addComponent( comboBox, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        //XXX
    }
    
    public void setMaterial( Class<DielectricMaterial> materialClass ) {
        boolean found = false;
        for ( int i = 0; i < comboBox.getItemCount(); i++ ) {
            if ( comboBox.getItemAt( i ).getClass().equals( materialClass ) ) {
                comboBox.setSelectedIndex( i );
                found = true;
                break;
            }
        }
        if ( !found ) {
            throw new IllegalArgumentException( "material is not one of the combo box items: " + materialClass.getName() );
        }
    }
    
    public DielectricMaterial getMaterial() {
        return (DielectricMaterial) comboBox.getSelectedItem();
    }
    
    public boolean isCustomMaterial() {
        return getMaterial() instanceof CustomDielectricMaterial;
    }
    
    public void addChangeListener ( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
