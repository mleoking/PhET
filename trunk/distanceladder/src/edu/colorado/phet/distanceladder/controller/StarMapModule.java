/**
 * Class: StarMapModule
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:45:51 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.UniverseModel;
import edu.colorado.phet.distanceladder.view.StarMapGraphic;
import edu.colorado.phet.distanceladder.view.StarshipCoordsGraphic;

public class StarMapModule extends Module {
    private UniverseModel model;
    private double starshipCoordsLayer = Config.starLayer - 1;
    private StarshipCoordsGraphic starshipCoords;
    private StarMapGraphic starMapGraphic;
    private StarMapControlPanel starMapControlPanel;

    public StarMapModule( UniverseModel model ) {
        super( "Star Map" );
        this.model = model;

        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        setModel( model );

        starMapGraphic = new StarMapGraphic( apparatusPanel, model.getStarField() );
        apparatusPanel.addGraphic( starMapGraphic );
        this.starshipCoords = new StarshipCoordsGraphic( model.getStarShip(), getApparatusPanel() );

        starMapControlPanel = new StarMapControlPanel( this );
        setControlPanel( starMapControlPanel );
    }

    public void setStarshipCoordsEnabled( boolean coordsOn ) {
        if( coordsOn ) {
            starMapGraphic.addGraphic( starshipCoords, starshipCoordsLayer );
        }
        else {
            starMapGraphic.remove( starshipCoords );
        }
    }

    public void setStarshipCordinateGraphicEnabled( boolean isEnabled ) {
        setStarshipCoordsEnabled( isEnabled );
        starMapControlPanel.setStarshipCordinateGraphicEnabled( isEnabled );
    }
}
