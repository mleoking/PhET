/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.Animation;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.view.Box2DGraphic;
import edu.colorado.phet.idealgas.view.Mannequin;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.geom.Point2D;
import java.awt.*;
import java.io.IOException;

public class IdealGasModule extends Module {
    private IdealGasModel idealGasModel;
    private PressureSensingBox box;

    public IdealGasModule( AbstractClock clock ) {
        super( Strings.idealGasModuleName );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

        // Create the box
        double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );

        setApparatusPanel( new BaseIdealGasApparatusPanel( this, box ) );

        // Set up the box
        Box2DGraphic boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );

        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box );
        addGraphic( pusher, 10 );
    }
}
