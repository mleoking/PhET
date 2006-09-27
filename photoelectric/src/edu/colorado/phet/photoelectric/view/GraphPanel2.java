/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.view.util.RotatedTextLabel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * GraphPanel2
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphPanel2 extends JPanel {

    private ControlPanel controlPanel;
    private int rowIdx;
    private ArrayList checkBoxes = new ArrayList();
    private Module module;


    public GraphPanel2( Module module ) {
        super( new GridBagLayout() );
        PhotoelectricModel model = (PhotoelectricModel)module.getModel();
        controlPanel = module.getControlPanel();
        this.module = module;
        Insets graphInsets = new Insets( 5, 20, 17, 15 );

        GraphPanel cvgPanel2 = new GraphPanel( module.getClock() );
        cvgPanel2.setGraph( new CurrentVsVoltageGraph( cvgPanel2, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.CurrentVsVoltage" ),
                  cvgPanel2,
                  SimStrings.get( "Voltage" ),
                  SimStrings.get( "Current" ));

        GraphPanel panel2 = new GraphPanel( module.getClock() );
        panel2.setGraph( new CurrentVsIntensityGraph( panel2, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.CurrentVsIntensity" ),
                  panel2,
                  SimStrings.get( "GraphLabel.Intensity" ),
                  SimStrings.get( "GraphLabel.Current" ) );

        GraphPanel panel3 = new GraphPanel( module.getClock() );
        panel3.setGraph( new EnergyVsFrequencyGraph( panel2, model ), graphInsets );
        addGraph( SimStrings.get( "GraphTitle.EnergyVsFrequency" ),
                  panel3,
                  SimStrings.get( "GraphLabel.Frequency"),
                  SimStrings.get( "GraphLabel.Energy" ));

        setBorder( new TitledBorder( "Graphs" ) );
    }

    private void setLogoVisibility( final ControlPanel controlPanel ) {
        boolean isVisible = true;
        for( int i = 0; i < checkBoxes.size(); i++ ) {
            JCheckBox jCheckBox = (JCheckBox)checkBoxes.get( i );
            if( jCheckBox.isSelected() ) {
                isVisible = false;
            }
        }
        module.getLogoPanel().setVisible( isVisible );
    }

    private void addGraph( String title, final GraphPanel graphPanel, String xLabel, String yLabel ) {
        GridBagConstraints checkBoxGbc = new GridBagConstraints( 0, 0,
                                                                 2, 1, 0, 0,
                                                                 GridBagConstraints.NORTHWEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 25 ), 0, 0 );
        GridBagConstraints graphGbc = new GridBagConstraints( 1, 0,
                                                              1, 1, 0, 0,
                                                              GridBagConstraints.NORTHWEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );

        GridBagConstraints xLabelGbc = new GridBagConstraints( 1, 0,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.NORTH,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );

        GridBagConstraints yLabelGbc = new GridBagConstraints( 0, 0,
                                                               1, 1, 0, 0,
                                                               GridBagConstraints.WEST,
                                                               GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );

        final JCheckBox cb = new JCheckBox( title );
        checkBoxes.add( cb );
        final RotatedTextLabel currentVsVoltageYLabel = new RotatedTextLabel( yLabel );
        final JLabel currentVsVoltageXLabel = new JLabel( xLabel );
        cb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphPanel.setVisible( cb.isSelected() );
                graphPanel.clearGraph();
                currentVsVoltageYLabel.setVisible( cb.isSelected() );
                currentVsVoltageXLabel.setVisible( cb.isSelected() );
                setLogoVisibility( controlPanel );
            }
        } );
        graphPanel.setVisible( cb.isSelected() );
        currentVsVoltageYLabel.setVisible( cb.isSelected() );
        currentVsVoltageXLabel.setVisible( cb.isSelected() );

        checkBoxGbc.gridy = rowIdx;
        add( cb, checkBoxGbc );

        rowIdx++;
        graphGbc.gridy = rowIdx;
        yLabelGbc.gridy = rowIdx;
        add( currentVsVoltageYLabel, yLabelGbc );
        add( graphPanel, graphGbc );
        rowIdx++;
        xLabelGbc.gridy = rowIdx;
        add( currentVsVoltageXLabel, xLabelGbc );
        rowIdx++;
    }

}
