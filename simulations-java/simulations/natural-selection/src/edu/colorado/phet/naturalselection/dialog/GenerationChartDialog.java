package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class GenerationChartDialog extends JDialog {

    public GenerationChartPanel generationChartPanel;

    public GenerationChartDialog( Frame frame, NaturalSelectionModel model ) {
        super( frame );

        generationChartPanel = new GenerationChartPanel( model );

        Container container = this.getContentPane();
        container.add( generationChartPanel );

        setTitle( NaturalSelectionStrings.GENERATION_CHART );

        pack();

    }

}
