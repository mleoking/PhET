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
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.util.PiccoloUtils;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.solublesalts.view.VesselGraphic;
import edu.colorado.phet.solublesalts.view.StoveGraphic;
import edu.colorado.phet.solublesalts.view.ShakerGraphic;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;
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

        // Test code
//        test();

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel() );
        simPanel.addWorldChild( vesselGraphic );
        vesselGraphic.addInputEventListener( new PDragEventHandler() );
        model.getVessel().setWaterLevel( model.getVessel().getDepth() * .7 );

        // Add the stove
        {
            PNode stove = new StoveGraphic();
            Point2D refPt = PiccoloUtils.getBorderPoint( vesselGraphic, PiccoloUtils.SOUTH );
            stove.setOffset( refPt.getX(), refPt.getY() + 50 );
            simPanel.addWorldChild( stove );
        }

        // Add the shaker
        {
        PNode shaker = new ShakerGraphic();
            shaker.rotateInPlace( -Math.PI / 4 );
        Point2D refPt = PiccoloUtils.getBorderPoint( vesselGraphic, PiccoloUtils.NORTH );
            shaker.setOffset( refPt.getX(), refPt.getY() - 50 );
            simPanel.addWorldChild( shaker );
        }

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

}
