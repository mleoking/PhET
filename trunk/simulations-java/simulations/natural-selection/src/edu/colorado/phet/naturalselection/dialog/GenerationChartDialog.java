package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class GenerationChartDialog extends JDialog {

    public GenerationChartPanel generationChartPanel;

    public GenerationChartDialog( Frame frame, NaturalSelectionModel model ) {
        super( frame );

        generationChartPanel = new GenerationChartPanel( model );
        this.setContentPane( generationChartPanel );

        pack();

    }

}
