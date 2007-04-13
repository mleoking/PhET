/**
 * Class: StarMapModule
 * Class: edu.colorado.games4education.lostinspace.controller
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:45:51 PM
 */
package edu.colorado.games4education.lostinspace.controller;

import edu.colorado.games4education.lostinspace.model.UniverseModel;
import edu.colorado.games4education.lostinspace.view.StarMapGraphic;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;

public class StarMapModule extends Module {
    private UniverseModel model;

    public StarMapModule( UniverseModel model ) {
        super( "Star Map" );
        this.model = model;

        ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        setModel( model );

        apparatusPanel.addGraphic( new StarMapGraphic( apparatusPanel, model.getStarField() ) );
    }
}
