/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.util.HighContrastImageFilter;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Displays the background for the environment
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionBackgroundNode extends PNode implements NaturalSelectionModel.Listener {

    private int climate;
    private PImage equatorImage;
    private PImage arcticImage;

    private double baseWidth;
    private double baseHeight;

    /**
     * Constructor
     *
     * @param initialClimate The initial climate value
     */
    public NaturalSelectionBackgroundNode( int initialClimate ) {
        // initialize images
        equatorImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_BACKGROUND_EQUATOR );
        arcticImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_BACKGROUND_ARCTIC );

        if ( NaturalSelectionApplication.isHighContrast() ) {
            equatorImage = HighContrastImageFilter.getEquator().getPImage( NaturalSelectionConstants.IMAGE_BACKGROUND_EQUATOR );
        }

        baseWidth = equatorImage.getWidth();
        baseHeight = equatorImage.getHeight();

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

    public void onEvent( NaturalSelectionModel.Event event ) {
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_CLIMATE_CHANGE ) {
            setClimate( event.getNewClimate() );
        }
    }

    public AffineTransform getBackgroundTransform( int width, int height ) {
        return new AffineTransform( (double) width / baseWidth, 0, 0, (double) height / baseHeight, 0, 0 );
    }

    public void updateLayout( int width, int height ) {
        this.setTransform( getBackgroundTransform( width, height ) );
    }

}
