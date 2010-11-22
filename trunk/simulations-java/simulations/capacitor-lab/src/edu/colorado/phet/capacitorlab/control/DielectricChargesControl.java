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
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Controls related to the dielectric's charges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricChargesControl extends JPanel {
    
    /*
     * Radio button that corresponds to one choice in the DielectricChargeView enum.
     */
    private static class DielectricChargeViewRadioButton extends JRadioButton {
        public DielectricChargeViewRadioButton( String text, final DielectricChargeView choice, final Property<DielectricChargeView> dielectricChargeViewProperty ) {
            super( text );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dielectricChargeViewProperty.setValue( choice );
                }
            } );
            dielectricChargeViewProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setSelected( dielectricChargeViewProperty.getValue().equals( choice ) );
                }
            } );
        }
    }
    
    public DielectricChargesControl( final DielectricNode dielectricNode ) {
        
        JLabel chargesLabel = new JLabel( CLStrings.DIELECTRIC_CHARGES );
        
        JRadioButton hideAllRadioButton = new DielectricChargeViewRadioButton( CLStrings.HIDE_ALL_CHARGES, DielectricChargeView.NONE, dielectricNode.getDielectricChargeViewProperty() );
        JRadioButton showAllRadioButton = new DielectricChargeViewRadioButton( CLStrings.SHOW_ALL_CHARGES, DielectricChargeView.TOTAL, dielectricNode.getDielectricChargeViewProperty() );
        JRadioButton showExcessRadioButton = new DielectricChargeViewRadioButton( CLStrings.SHOW_EXCESS_CHARGES, DielectricChargeView.EXCESS, dielectricNode.getDielectricChargeViewProperty() );
        
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
    }
}
