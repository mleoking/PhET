/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Control panel for various "View" settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends PhetTitledPanel {
    
    public ViewControlPanel( final CapacitorNode capacitorNode ) {
        super( CLStrings.VIEW );
        
        final JCheckBox plateChargesCheckBox = new JCheckBox( CLStrings.PLATE_CHARGES );
        plateChargesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                capacitorNode.setPlateChargeVisible( plateChargesCheckBox.isSelected() );
            }
        });
        
        final JCheckBox electricFieldLinesCheckBox = new JCheckBox( CLStrings.ELECTRIC_FIELD_LINES );
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

        // observers
        {
            capacitorNode.addPlateChargeVisibleObserver( new SimpleObserver() {
                public void update() {
                    plateChargesCheckBox.setSelected( capacitorNode.isPlateChargeVisible() );
                }
            });
            
            capacitorNode.addEFieldVisibleObserver( new SimpleObserver() {
                public void update() {
                    electricFieldLinesCheckBox.setSelected( capacitorNode.isEFieldVisible() );
                }
            });
        }
    }
}
