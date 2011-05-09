// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PiccoloUtils;
import edu.colorado.phet.solublesalts.model.Shaker;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.WaterSource;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.umd.cs.piccolo.PNode;

/**
 * WorldNode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WorldNode extends PNode {

    public WorldNode( final SolubleSaltsModule module, PhetPCanvas phetPCanvas ) {

        final SolubleSaltsModel model = (SolubleSaltsModel) module.getModel();

        double graphicsScale = 1;
//        double graphicsScale = model.getBounds().getWidth() / phetPCanvas.getRenderingSize().getWidth();

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel(), module );
        this.addChild( vesselGraphic );

        // Add the stove
        PNode stove = new StoveGraphic();
        Point2D refPt = PiccoloUtils.getBorderPoint( vesselGraphic, PiccoloUtils.SOUTH );
        stove.setOffset( refPt.getX(), refPt.getY() + 50 );

        // Add the shaker
        Shaker shaker = model.getShaker();
        RegisterablePNode shakerGraphic = new ShakerGraphic( shaker );
        shakerGraphic.setRegistrationPoint( shakerGraphic.getFullBounds().getWidth() / 3,
                                            shakerGraphic.getFullBounds().getHeight() / 2 );
        shakerGraphic.rotateInPlace( -Math.PI / 4 );
        shakerGraphic.setOffset( shaker.getPosition().getX(), shaker.getPosition().getY() );
        shakerGraphic.setScale( 0.8 * graphicsScale );
        this.addChild( shakerGraphic );

        // Add the faucet and drain graphics
        WaterSource waterSource = model.getFaucet();
        Vessel vessel = model.getVessel();
        double scale = 1.3 * graphicsScale;
        FaucetGraphic faucetGraphic = new FaucetGraphic( phetPCanvas,
                                                         FaucetGraphic.RIGHT_FACING,
                                                         FaucetGraphic.SPOUT,
                                                         waterSource,
                                                         vessel.getLocation().getY() + vessel.getDepth() /*,
                                                         module*/ );
        faucetGraphic.setScale( scale );
        faucetGraphic.setOffset( model.getFaucet().getPosition() );
        this.addChild( faucetGraphic );
        faucetGraphic.moveInBackOf( vesselGraphic );

        FaucetGraphic drainGraphic = new FaucetGraphic( phetPCanvas,
                                                        FaucetGraphic.LEFT_FACING,
                                                        FaucetGraphic.WALL_ATTACHMENT,
                                                        model.getDrain(),
                                                        800 /*,
                                                        module*/ );
        drainGraphic.setScale( scale );
        drainGraphic.setOffset( model.getDrain().getPosition() );
        this.addChild( drainGraphic );
    }
}
