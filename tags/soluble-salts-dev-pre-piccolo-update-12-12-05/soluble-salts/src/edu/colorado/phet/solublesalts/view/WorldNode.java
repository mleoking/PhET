/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PiccoloUtils;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Shaker;
import edu.colorado.phet.solublesalts.model.WaterSource;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * WorldCanvas
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WorldNode extends PNode {
//public class WorldCanvas extends PhetPCanvas {

    public WorldNode( SolubleSaltsModel model, PhetPCanvas phetPCanvas ) {

        double graphicsScale = 1;
//        double graphicsScale = model.getBounds().getWidth() / phetPCanvas.getRenderingSize().getWidth();

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel() );
//        vesselGraphic.setScale( graphicsScale );
        this.addChild( vesselGraphic );
        model.getVessel().setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL );

        // Add the stove
        {
            PNode stove = new StoveGraphic();
            Point2D refPt = PiccoloUtils.getBorderPoint( vesselGraphic, PiccoloUtils.SOUTH );
            stove.setOffset( refPt.getX(), refPt.getY() + 50 );
        }

        // Add the shaker
        {
            Shaker shaker = model.getShaker();
            RegisterablePNode shakerGraphic = new ShakerGraphic( shaker );
            shakerGraphic.setRegistrationPoint( shakerGraphic.getFullBounds().getWidth() / 2,
                                                shakerGraphic.getFullBounds().getHeight() / 2 );
            shakerGraphic.rotateInPlace( -Math.PI / 4 );
            shakerGraphic.setOffset( shaker.getPosition().getX(), shaker.getPosition().getY() );
            shakerGraphic.setScale( 0.8 * graphicsScale );
            this.addChild( shakerGraphic );
        }

        // Add the faucet and drain graphics
        WaterSource waterSource = model.getFaucet();
        Vessel vessel = model.getVessel();
        double scale = 1.3 * graphicsScale;
        FaucetGraphic faucetGraphic = new FaucetGraphic( phetPCanvas,
                                                         FaucetGraphic.RIGHT_FACING,
                                                         FaucetGraphic.SPOUT,
                                                         waterSource,
                                                         vessel.getLocation().getY() + vessel.getDepth()  );
        faucetGraphic.setScale( scale );
        faucetGraphic.setOffset( model.getFaucet().getPosition() );
        this.addChild( faucetGraphic );
        faucetGraphic.moveInBackOf( vesselGraphic );

        FaucetGraphic drainGraphic = new FaucetGraphic( phetPCanvas,
                                                        FaucetGraphic.LEFT_FACING,
                                                        FaucetGraphic.WALL_ATTACHMENT,
                                                        model.getDrain(),
                                                        800 );
        drainGraphic.setScale( scale );
        drainGraphic.setOffset( model.getDrain().getPosition() );
        this.addChild( drainGraphic );
    }
}
