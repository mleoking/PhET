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

        Insets insets = new Insets(0, 0, 0, 0);

        showSeparationView    = new JCheckBox( SimStrings.get("EnergyViewOptionsPanel.showSeparationView" ));
        showEnergyProfileView = new JCheckBox( SimStrings.get("EnergyViewOptionsPanel.showEnergyProfileView" ));

        showSeparationView.setSelected( true );
        showEnergyProfileView.setSelected ( true );

        showSeparationView.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getEnergyView().setSeparationViewVisible( showSeparationView.isSelected() );
            }
        } );

        add(showSeparationView,    c);
        add(showEnergyProfileView, c);
        /*


            showBarChartBtn = new JRadioButton( SimStrings.get( "Control.showBarChart" ) );
            showBarChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showBarChartBtn );

            showPieChartBtn = new JRadioButton( SimStrings.get( "Control.showPieChart" ) );
            showPieChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showPieChartBtn );

            showStripChartBtn = new JRadioButton( SimStrings.get( "Control.showStripChart" ) );
            showStripChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            chartOptionsBG.add( showStripChartBtn );

            showNoneBtn = new JRadioButton( SimStrings.get( "Control.none" ) );
            showNoneBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setEnergyViewChartOptions();
                }
            } );
            showNoneBtn.setSelected( true );
            chartOptionsBG.add( showNoneBtn );

            gbc.gridy = 0;
            chartOptionsPanel.add( showBarChartBtn, gbc );
            gbc.gridx = 1;
            chartOptionsPanel.add( showStripChartBtn, gbc );
            gbc.gridx = 0;
            gbc.gridy = 1;
            chartOptionsPanel.add( showPieChartBtn, gbc );
            gbc.gridx = 1;
            chartOptionsPanel.add( showNoneBtn, gbc );
         */
    }

    
}
