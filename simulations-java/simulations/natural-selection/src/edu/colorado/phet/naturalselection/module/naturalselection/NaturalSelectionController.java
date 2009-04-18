package edu.colorado.phet.naturalselection.module.naturalselection;

import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;

public class NaturalSelectionController {

    public NaturalSelectionController( NaturalSelectionModel model, NaturalSelectionCanvas canvas, NaturalSelectionControlPanel controlPanel ) {

        model.addListener( canvas.bunnies );

        controlPanel.traitCanvas.colorTraitNode.addListener( ColorGene.getInstance() );
        controlPanel.traitCanvas.teethTraitNode.addListener( TeethGene.getInstance() );
        controlPanel.traitCanvas.tailTraitNode.addListener( TailGene.getInstance() );

        ColorGene.getInstance().addListener( controlPanel.traitCanvas.colorTraitNode );
        TeethGene.getInstance().addListener( controlPanel.traitCanvas.teethTraitNode );
        TailGene.getInstance().addListener( controlPanel.traitCanvas.tailTraitNode );
    }

}
