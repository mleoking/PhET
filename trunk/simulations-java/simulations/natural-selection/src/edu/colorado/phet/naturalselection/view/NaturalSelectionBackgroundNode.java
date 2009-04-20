package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class NaturalSelectionBackgroundNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private int climate;
    private PImage equatorImage;
    private PImage arcticImage;

    public NaturalSelectionBackgroundNode( int initialClimate ) {
        equatorImage = NaturalSelectionResources.getImageNode( "natural_selection_background_equator_2.png" );
        arcticImage = NaturalSelectionResources.getImageNode( "natural_selection_background_arctic_2.png" );

        climate = initialClimate;

        if ( climate == NaturalSelectionModel.CLIMATE_EQUATOR ) {
            addChild( equatorImage );
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            addChild( arcticImage );
        }
    }

    public void reset() {
        setClimate( NaturalSelectionModel.CLIMATE_EQUATOR );
    }

    public void setClimate( int newClimate ) {
        if ( climate == newClimate ) {
            return;
        }

        climate = newClimate;

        if ( climate == NaturalSelectionModel.CLIMATE_EQUATOR ) {
            removeChild( arcticImage );
            addChild( equatorImage );
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            removeChild( equatorImage );
            addChild( arcticImage );
        }
    }

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {
        setClimate( climate );
    }
}
