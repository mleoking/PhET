package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;

public class NaturalSelectionController {

    public NaturalSelectionController( final NaturalSelectionModel model, final NaturalSelectionCanvas canvas, final NaturalSelectionControlPanel controlPanel ) {

        model.addListener( canvas.bunnies );
        model.addListener( canvas.backgroundNode );

        controlPanel.traitCanvas.colorTraitNode.addListener( ColorGene.getInstance() );
        controlPanel.traitCanvas.teethTraitNode.addListener( TeethGene.getInstance() );
        controlPanel.traitCanvas.tailTraitNode.addListener( TailGene.getInstance() );

        ColorGene.getInstance().addListener( controlPanel.traitCanvas.colorTraitNode );
        TeethGene.getInstance().addListener( controlPanel.traitCanvas.teethTraitNode );
        TailGene.getInstance().addListener( controlPanel.traitCanvas.tailTraitNode );

        

        controlPanel.climatePanel.arcticButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_ARCTIC );
            }
        } );

        controlPanel.climatePanel.equatorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_EQUATOR );
            }
        } );
    }

}
