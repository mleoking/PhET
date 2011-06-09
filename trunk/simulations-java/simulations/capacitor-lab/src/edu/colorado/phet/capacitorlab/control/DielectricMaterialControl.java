// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Control for selecting a dielectric material, a labeled combo box.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricMaterialControl extends JPanel {

    private final JComboBox comboBox;

    private final ArrayList<ChangeListener> listeners;

    public DielectricMaterialControl( DielectricMaterial[] materials, DielectricMaterial selectedMaterial ) {

        listeners = new ArrayList<ChangeListener>();

        JLabel label = new JLabel( MessageFormat.format( CLStrings.PATTERN_LABEL, CLStrings.MATERIAL ) );

        comboBox = new JComboBox( materials );
        comboBox.setRenderer( new DielectricMaterialRenderer() );
        comboBox.setSelectedItem( selectedMaterial );
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent event ) {
                if ( event.getStateChange() == ItemEvent.SELECTED ) {
                    fireStateChanged();
                }
            }
        } );

        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setFill( Fill.HORIZONTAL );
        innerPanel.setGridY( 0 ); // one row
        innerPanel.add( label );
        innerPanel.add( comboBox );

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

    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : new ArrayList<ChangeListener>( listeners ) ) {
            listener.stateChanged( event );
        }
    }

    /*
     * Custom renderer for a dielectric material, used by the combo box.
     * Displays the name and constant for the material, with a color chip to the left showing its color.
     * For custom dielectric, the constant is omitted, since it varies dynamically.
     */
    private static class DielectricMaterialRenderer extends JLabel implements ListCellRenderer {

        private static final double COLOR_CHIP_SIZE = 11;

        public DielectricMaterialRenderer() {
            super();
            setOpaque( true ); // for Macintosh
            setHorizontalTextPosition( SwingConstants.RIGHT ); // text to right of icon
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
            String s;
            if ( material instanceof CustomDielectricMaterial ) {
                s = material.getName();
            }
            else {
                s = MessageFormat.format( CLStrings.PATTERN_MATERIAL_CONSTANT, material.getName(), material.getDielectricConstant() );
            }
            return s;
        }

        public Icon getIcon( DielectricMaterial material ) {
            PPath colorChipNode = new PPath( new Rectangle2D.Double( 0, 0, COLOR_CHIP_SIZE, COLOR_CHIP_SIZE ) );
            colorChipNode.setStroke( null );
            colorChipNode.setPaint( material.getColor() );
            return new ImageIcon( colorChipNode.toImage() );
        }
    }
}
