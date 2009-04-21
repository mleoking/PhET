package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

public class HeredityChartNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private NaturalSelectionModel model;

    public HeredityChartNode( NaturalSelectionModel model ) {
        this.model = model;


    }


    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}
