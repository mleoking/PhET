/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.dialog.generationchart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

/**
 * Dialog that displays a summary of the generations (either through a genetic hierarchy or grouping of generations)
 *
 * @author Jonathan Olson
 */
public class GenerationChartDialog extends JDialog {

    private GenerationChartCanvas generationChartCanvas;
    private JRadioButton heredityButton;
    private JRadioButton generationButton;

    /**
     * Constructor
     *
     * @param frame Parent frame, which should be the simulation frame
     * @param model The natural selection model
     */
    public GenerationChartDialog( Frame frame, NaturalSelectionModel model ) {
        super( frame );

        setTitle( "Generation Chart" );

        // TODO: if we keep the radio buttons, improve the layout.
        VerticalLayoutPanel contentPanel = new VerticalLayoutPanel();
        contentPanel.setFillHorizontal();
        contentPanel.add( createTypePanel() );
        contentPanel.add( new JSeparator() );
        generationChartCanvas = new GenerationChartCanvas( model );
        contentPanel.add( generationChartCanvas );
        setContentPane( contentPanel );

        pack();
        SwingUtils.centerDialogInParent( this );
    }

    public void reset() {
        generationChartCanvas.reset();
        selectGenerationChart();
    }

    /**
     * Creates a panel that allows the user to select what type of generation chart they want to see.
     *
     * @return The created panel
     */
    public JPanel createTypePanel() {
        JPanel panel = new JPanel();

        heredityButton = new JRadioButton( "Heredity" );
        generationButton = new JRadioButton( "Generation" );

        selectGenerationChart();


        // event handlers

        heredityButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                generationChartCanvas.select( GenerationChartCanvas.TYPE_HEREDITY );
            }
        } );

        generationButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                generationChartCanvas.select( GenerationChartCanvas.TYPE_GENERATION );
            }
        } );

        ButtonGroup group = new ButtonGroup();
        group.add( heredityButton );
        group.add( generationButton );

        panel.add( heredityButton );
        panel.add( generationButton );

        return panel;
    }

    /**
     * Remember what was used last, so if they close the window, it will open up with the same chart type
     */
    private void selectGenerationChart() {
        if ( GenerationChartCanvas.lastType == GenerationChartCanvas.TYPE_HEREDITY ) {
            heredityButton.setSelected( true );
        }
        else {
            generationButton.setSelected( true );
        }
    }
}
