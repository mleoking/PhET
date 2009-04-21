package edu.colorado.phet.naturalselection.dialog.generationchart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class GenerationChartDialog extends JDialog {

    private GenerationChartCanvas generationChartCanvas;

    public GenerationChartDialog( Frame frame, NaturalSelectionModel model ) {
        super( frame );

        setTitle( "Generation Chart" );

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
    }

    public JPanel createTypePanel() {
        JPanel panel = new JPanel();

        JRadioButton heredityButton = new JRadioButton( "Heredity" );
        JRadioButton generationButton = new JRadioButton( "Generation" );

        if ( GenerationChartCanvas.lastType == GenerationChartCanvas.TYPE_HEREDITY ) {
            heredityButton.setSelected( true );
        }
        else {
            generationButton.setSelected( true );
        }

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
}
