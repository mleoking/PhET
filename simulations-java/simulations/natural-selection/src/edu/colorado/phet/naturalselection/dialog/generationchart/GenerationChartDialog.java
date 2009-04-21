package edu.colorado.phet.naturalselection.dialog.generationchart;

import java.awt.*;

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

    public JPanel createTypePanel() {
        JPanel panel = new JPanel();

        JRadioButton heredityButton = new JRadioButton( "Heredity" );
        heredityButton.setSelected( true );
        JRadioButton generationButton = new JRadioButton( "Generation" );

        ButtonGroup group = new ButtonGroup();
        group.add( heredityButton );
        group.add( generationButton );

        panel.add( heredityButton );
        panel.add( generationButton );

        return panel;
    }
}
