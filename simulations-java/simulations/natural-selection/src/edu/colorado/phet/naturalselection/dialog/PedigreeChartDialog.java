package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;

public class PedigreeChartDialog extends JDialog {

    public PedigreeChartPanel pedigreeChartPanel;

    public PedigreeChartDialog( Frame frame, NaturalSelectionModel model ) {
        super( frame );

        pedigreeChartPanel = new PedigreeChartPanel( model );

        Container container = this.getContentPane();
        container.add( pedigreeChartPanel );

        setTitle( NaturalSelectionStrings.GENERATION_CHART );

        pack();

        SwingUtils.centerWindowOnScreen( this );

    }

    public void displayBunny( Bunny bunny ) {
        pedigreeChartPanel.displayBunny( bunny );
    }

    public void reset() {
        pedigreeChartPanel.reset();
    }
}
