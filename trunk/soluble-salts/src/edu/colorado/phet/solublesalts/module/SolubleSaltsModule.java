/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.MouseTracker;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PiccoloUtils;
import edu.colorado.phet.piccolo.util.PMouseTracker;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.view.*;
import edu.colorado.phet.solublesalts.view.charts.Concentrations;
import edu.colorado.phet.solublesalts.view.charts.ConcentrationsSgt;
import edu.colorado.phet.solublesalts.view.charts.ConcentrationsPTPlot2;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModule extends PiccoloModule {

    Random random = new Random( System.currentTimeMillis() );
    private PhetPCanvas simPanel;

    public SolubleSaltsModule( AbstractClock clock ) {
        super( SimStrings.get( "Module.title" ), clock );

        // Set up the basics
        final SolubleSaltsModel model = new SolubleSaltsModel();
        setModel( model );
        simPanel = new PhetPCanvas( new Dimension( 900, 600 ) );
        setPhetPCanvas( simPanel );

        // Add a graphic manager to the model that will create and remove IonGraphics
        // when Ions are added to and removed from the model
        model.addIonListener( new IonGraphicManager( simPanel ) );

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel() );
        simPanel.addWorldChild( vesselGraphic );
        vesselGraphic.addInputEventListener( new PDragEventHandler() );
        model.getVessel().setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL );
//        model.getVessel().setWaterLevel( model.getVessel().getDepth() * .7 );

        // Add the stove
        {
            PNode stove = new StoveGraphic();
            Point2D refPt = PiccoloUtils.getBorderPoint( vesselGraphic, PiccoloUtils.SOUTH );
            stove.setOffset( refPt.getX(), refPt.getY() + 50 );
//            simPanel.addWorldChild( stove );
        }

        // Add the shaker
        {
            Shaker shaker = model.getShaker();
            RegisterablePNode shakerGraphic = new ShakerGraphic( shaker );
            shakerGraphic.setRegistrationPoint( shakerGraphic.getFullBounds().getWidth() / 2,
                                                shakerGraphic.getFullBounds().getHeight() / 2 );
            shakerGraphic.rotateInPlace( -Math.PI / 4 );
            shakerGraphic.setOffset( shaker.getPosition().getX(), shaker.getPosition().getY() );
            simPanel.addWorldChild( shakerGraphic );
        }

        // Add the faucet and drain graphics
        WaterSource waterSource = model.getFaucet();
        Vessel vessel = model.getVessel();
        double scale = 1.3;
        FaucetGraphic faucetGraphic = new FaucetGraphic( simPanel,
                                                         FaucetGraphic.RIGHT_FACING,
                                                         FaucetGraphic.SPOUT,
                                                         waterSource,
                                                         vessel.getLocation().getY() + vessel.getDepth()  );
        faucetGraphic.setScale( scale );
        faucetGraphic.setOffset( model.getFaucet().getPosition() );
        simPanel.addWorldChild( faucetGraphic );
        faucetGraphic.moveInBackOf( vesselGraphic );

        FaucetGraphic drainGraphic = new FaucetGraphic( simPanel,
                                                        FaucetGraphic.LEFT_FACING,
                                                        FaucetGraphic.WALL_ATTACHMENT,
                                                        model.getDrain(),
                                                        800 );
        drainGraphic.setScale( scale );
        drainGraphic.setOffset( model.getDrain().getPosition() );
        simPanel.addWorldChild( drainGraphic );

        // Concentrations strip chart
//        ConcentrationsPTPlot2 concentrations = new ConcentrationsPTPlot2( PhetApplication.instance().getPhetFrame(),
//                                                            model );
//        Concentrations concentrations = new Concentrations( PhetApplication.instance().getPhetFrame(),
//                                                            model );
//        concentrations.setVisible( true );

//        ConcentrationsSgt concentrationsSgt = new ConcentrationsSgt( PhetApplication.instance().getPhetFrame(),
//                                                                     model );
//        concentrationsSgt.setVisible( true );

        // Create some ions and add it to the model

        {
//            int numIons = 10;
//            for( int i = 0; i < numIons; i++ ) {
//                Ion ion = new Sodium();
//                IonInitializer.initialize( ion, model );
//                model.addModelElement( ion );
//            }
        }

        {
//            int numIons = 0;
//            for( int i = 0; i < numIons; i++ ) {
//                Ion ion = new Chloride();
//                IonInitializer.initialize( ion, model );
//                model.addModelElement( ion );
//            }
        }
        Ion ion = null;

        ion = new Chloride();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 130, 200 );
        ion.setVelocity( 0, 5 );
//        model.addModelElement( ion );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 70, 230 );
        ion.setVelocity( 5, 0 );
//        model.addModelElement( ion );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 600, 435 );
        ion.setVelocity( 5, 0 );
//        model.addModelElement( ion );

//        ion = new Chloride();
//        IonInitializer.initialize( ion, model );
//        ion.setPosition( 280, 200 );
//        ion.setVelocity( 0, 5 );
//        model.addModelElement( ion );


        // Set up the control panel
        setControlPanel( new SolubleSaltsControlPanel( this ) );


//        TestGraphic tg = new TestGraphic();
//        tg.setScale( 2 );
//        tg.setOffset( 500, 500 );
////        tg.setScale( 2 );
//        simPanel.addWorldChild( tg );
//
//        PPath pp = new PPath( new Ellipse2D.Double(-2,-2,4,4));
//        pp.setOffset( tg.getOffset() );
//        pp.setPaint( Color.red );
//        simPanel.addWorldChild( pp );
//        PPath pp2 = new PPath( new Ellipse2D.Double(-2,-2,4,4));
//        pp2.setOffset( 500, 500 );
//        pp2.setPaint( Color.green );
//        simPanel.addWorldChild( pp2 );

    }

    private void test() {
        // A test graphic
        Rectangle r = new Rectangle( 100, 150, 20, 70 );
        PPath pp = new PPath( r );
        pp.setPaint( Color.red );
        simPanel.addWorldChild( pp );
        pp.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pp.addInputEventListener( new PDragEventHandler() );
    }







    class TestGraphic extends RegisterablePNode {

        public TestGraphic() {

            PPath pPath = new PPath( new Rectangle2D.Double(0,0, 60, 30 ));
            pPath.setPaint( Color.cyan);
            setRegistrationPoint( pPath.getWidth() / 3, pPath.getHeight() / 3);
            addChild( pPath );

        }
    }

}
