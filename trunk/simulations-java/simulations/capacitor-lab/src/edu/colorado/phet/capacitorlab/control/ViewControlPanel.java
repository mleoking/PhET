/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;

/**
 * Control panel for various "View" settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends PhetTitledPanel {
    
    public ViewControlPanel( final DielectricCanvas canvas ) {
        super( CLStrings.TITLE_VIEW );
        
        final JCheckBox plateChargesCheckBox = new JCheckBox( CLStrings.CHECKBOX_PLATE_CHARGES );
        plateChargesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                canvas.getCapacitorNode().setPlateChargeVisible( plateChargesCheckBox.isSelected() );
            }
        });
        
        final JCheckBox electricFieldLinesCheckBox = new JCheckBox( CLStrings.CHECKBOX_ELECTRIC_FIELD_LINES );
        electricFieldLinesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                canvas.getCapacitorNode().setEFieldVisible( electricFieldLinesCheckBox.isSelected() );
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
        plateChargesCheckBox.setSelected( canvas.getCapacitorNode().isPlateChargeVisible() );
        electricFieldLinesCheckBox.setSelected( canvas.getCapacitorNode().isEFieldVisible() );
    }
}
