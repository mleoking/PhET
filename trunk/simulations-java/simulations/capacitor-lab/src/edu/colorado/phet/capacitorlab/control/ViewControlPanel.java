/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode.CapacitorNodeChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;

/**
 * Control panel for various "View" settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends PhetTitledPanel {
    
    private final CapacitorNode capacitorNode;
    private final JCheckBox plateChargesCheckBox, electricFieldLinesCheckBox;
    
    public ViewControlPanel( final CapacitorNode capacitorNode ) {
        super( CLStrings.TITLE_VIEW );
        
        this.capacitorNode = capacitorNode;
        capacitorNode.addCapacitorNodeChangeListener( new CapacitorNodeChangeAdapter() {
            @Override
            public void plateChargeVisibleChanged() {
                update();
            }
            @Override
            public void eFieldVisibleChanged() {
                update();
            }
        });
        
        plateChargesCheckBox = new JCheckBox( CLStrings.CHECKBOX_PLATE_CHARGES );
        plateChargesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setPlateChargeVisible( plateChargesCheckBox.isSelected() );
            }
        });
        
        electricFieldLinesCheckBox = new JCheckBox( CLStrings.CHECKBOX_EFIELD_LINES );
        electricFieldLinesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setEFieldVisible( electricFieldLinesCheckBox.isSelected() );
            }
        });
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( plateChargesCheckBox );
        innerPanel.add( electricFieldLinesCheckBox );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // default state
        update();
    }
    
    private void update() {
        plateChargesCheckBox.setSelected( capacitorNode.isPlateChargeVisible() );
        electricFieldLinesCheckBox.setSelected( capacitorNode.isEFieldVisible() );
    }
}
