package edu.colorado.phet.naturalselection.module.naturalselection;

import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;

public class NaturalSelectionController {

    public NaturalSelectionController( NaturalSelectionModel model, NaturalSelectionCanvas canvas, NaturalSelectionControlPanel controlPanel ) {

        model.addListener( canvas.bunnies );


    }

}
