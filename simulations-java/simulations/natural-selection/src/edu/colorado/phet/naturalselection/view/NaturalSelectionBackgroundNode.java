/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays the background for the environment
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionBackgroundNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private int climate;
    private PImage equatorImage;
    private PImage arcticImage;

    /**
     * Constructor
     * @param initialClimate The initial climate value
     */
    public NaturalSelectionBackgroundNode( int initialClimate ) {
        // initialize images
        equatorImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_BACKGROUND_EQUATOR );
        arcticImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_BACKGROUND_ARCTIC );

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

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {
        setClimate( climate );
    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}
