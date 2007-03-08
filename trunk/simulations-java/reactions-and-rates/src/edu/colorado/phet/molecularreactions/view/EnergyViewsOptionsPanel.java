/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class EnergyViewsOptionsPanel extends JPanel {
    private final JCheckBox  showSeparationView;
    private final JCheckBox  showEnergyProfileView;

    public EnergyViewsOptionsPanel( final MRModule module ) {
        super( new GridBagLayout() );

        setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "EnergyViewsOptionsPanel.borderCaption" ) ) );

        GridBagConstraints c;

        c = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                    1, 1, 1, 1,
                                    GridBagConstraints.WEST,
                                    GridBagConstraints.NONE,
                                    new Insets( 0, 0, 0, 0 ), 0, 0 );

        showSeparationView    = new JCheckBox( SimStrings.get("EnergyViewOptionsPanel.showSeparationView" ));
        showEnergyProfileView = new JCheckBox( SimStrings.get("EnergyViewOptionsPanel.showEnergyProfileView" ));

        showSeparationView.setSelected( true );
        showEnergyProfileView.setSelected ( true );

        showSeparationView.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergyView().setSeparationViewHidden( !showSeparationView.isSelected() );
            }
        } );

        showEnergyProfileView.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergyView().setEnergyProfileVisible( showEnergyProfileView.isSelected() );
            }
        } );

        add(showSeparationView,    c);
        add(showEnergyProfileView, c);
    }

    
}
