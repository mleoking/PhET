/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.geom.Point2D;

public class IdealGasModule extends Module {
    private IdealGasModel idealGasModel;
    private Box2D box;

    public IdealGasModule() {
        super( Strings.idealGasModuleName );

        idealGasModel = new IdealGasModel();
        setModel( idealGasModel );
        setApparatusPanel( new BaseIdealGasApparatusPanel( this ) );

        float xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        float yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        float xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        float yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new Box2D( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel );
//        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
//                                      new Point2D.Double( xDiag, yDiag ),
//                                      idealGasModel, null );
        Box2DGraphic boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );
    }
}
