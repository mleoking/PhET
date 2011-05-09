// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Controls related to the dielectric's charges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricChargesControl extends JPanel {

    public DielectricChargesControl( Property<DielectricChargeView> dielectricChargeView ) {

        JLabel chargesLabel = new JLabel( MessageFormat.format( CLStrings.PATTERN_LABEL, CLStrings.DIELECTRIC_CHARGES ) );

        JRadioButton hideAllRadioButton = new PropertyRadioButton<DielectricChargeView>( CLStrings.HIDE_ALL_CHARGES, dielectricChargeView, DielectricChargeView.NONE );
        JRadioButton showAllRadioButton = new PropertyRadioButton<DielectricChargeView>( CLStrings.SHOW_ALL_CHARGES, dielectricChargeView, DielectricChargeView.TOTAL );
        JRadioButton showExcessRadioButton = new PropertyRadioButton<DielectricChargeView>( CLStrings.SHOW_EXCESS_CHARGES, dielectricChargeView, DielectricChargeView.EXCESS );

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
