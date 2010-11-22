/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.view.DielectricNode;
import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Controls related to the dielectric's charges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricChargesControl extends JPanel {
    
    public DielectricChargesControl( final DielectricNode dielectricNode ) {
        
        JLabel chargesLabel = new JLabel( CLStrings.DIELECTRIC_CHARGES );
        
        final JRadioButton hideAllRadioButton = new JRadioButton( CLStrings.HIDE_ALL_CHARGES );
        hideAllRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dielectricNode.setDielectricChargeView( DielectricChargeView.NONE );
            }
        } );
         
        final JRadioButton showAllRadioButton = new JRadioButton( CLStrings.SHOW_ALL_CHARGES );
        showAllRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dielectricNode.setDielectricChargeView( DielectricChargeView.TOTAL );
            }
        } );
        
        final JRadioButton showExcessRadioButton = new JRadioButton( CLStrings.SHOW_EXCESS_CHARGES );
        showExcessRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dielectricNode.setDielectricChargeView( DielectricChargeView.EXCESS );
            }
        } );
        
        ButtonGroup group = new ButtonGroup();
        group.add( hideAllRadioButton );
        group.add( showAllRadioButton );
        group.add( showExcessRadioButton );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( chargesLabel );
        innerPanel.add( hideAllRadioButton );
        innerPanel.add( showAllRadioButton );
        innerPanel.add( showExcessRadioButton );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // observers
        {
            dielectricNode.addDielectricChargeViewObserver( new SimpleObserver() {
                public void update() {
                    hideAllRadioButton.setSelected( dielectricNode.getDielectricChargeView() == DielectricChargeView.NONE );
                    showAllRadioButton.setSelected( dielectricNode.getDielectricChargeView() == DielectricChargeView.TOTAL );
                    showExcessRadioButton.setSelected( dielectricNode.getDielectricChargeView() == DielectricChargeView.EXCESS );
                }
            } );
        }
    }
}
