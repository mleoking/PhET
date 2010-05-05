/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Control for selecting a dielectric material.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricMaterialControl extends JPanel {
    
    private final JComboBox comboBox;
    private final EventListenerList listeners;
    
    public DielectricMaterialControl( DielectricMaterial[] materials, DielectricMaterial selectedMaterial ) {
        
        listeners = new EventListenerList();
        
        JLabel label = new JLabel( CLStrings.LABEL_DIELECTRIC_MATERIAL );
        
        comboBox = new JComboBox( materials );
        comboBox.setRenderer( new DielectricMaterialRenderer() );
        comboBox.setSelectedItem( selectedMaterial );
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
    }
    
    public void setMaterial( DielectricMaterial material ) {
        boolean found = false;
        for ( int i = 0; i < comboBox.getItemCount(); i++ ) {
            if ( comboBox.getItemAt( i ) == material ) { /* yes, referential equality */
                comboBox.setSelectedIndex( i );
                found = true;
                break;
            }
        }
        if ( !found ) {
            throw new IllegalArgumentException( "material is not one of the combo box items: " + material.getName() );
        }
    }
    
    public DielectricMaterial getMaterial() {
        return (DielectricMaterial) comboBox.getSelectedItem();
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
    
    private static class DielectricMaterialRenderer extends JLabel implements ListCellRenderer {
        
        private static final double COLOR_CHIP_SIZE = 11;
        
        private final PPath colorChipNode;
        
        public DielectricMaterialRenderer() {
            super();
            setOpaque( true ); // for Macintosh
            setHorizontalTextPosition( SwingConstants.RIGHT ); // text to right of icon
            colorChipNode = new PPath( new Rectangle2D.Double( 0, 0, COLOR_CHIP_SIZE, COLOR_CHIP_SIZE ) );
            colorChipNode.setStroke( null );
        }
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            // Set the icon and text.
            DielectricMaterial material = (DielectricMaterial) value;
            setText( getText( material ) );
            setIcon( getIcon( material ) );

            return this;
        }
        
        public String getText( DielectricMaterial material ) {
            return MessageFormat.format( CLStrings.PATTERN_DIELECTRIC_MATERIAL, material.getName(), material.getDielectricConstant() );
        }
        
        public Icon getIcon( DielectricMaterial material ) {
            colorChipNode.setPaint( material.getColor() );
            return new ImageIcon( colorChipNode.toImage() );
        }
    }
}
