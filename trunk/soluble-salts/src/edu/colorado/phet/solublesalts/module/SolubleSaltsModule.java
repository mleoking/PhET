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
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.solublesalts.view.SSCanvas;
import edu.colorado.phet.solublesalts.view.WorldNode;
import edu.colorado.phet.solublesalts.view.VesselGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModule extends PiccoloModule {

//    static public final double viewScale = 1;
    static public final double viewScale = 0.1;

    private Random random = new Random( System.currentTimeMillis() );
    private SSCanvas simPanel;

    public SolubleSaltsModule( AbstractClock clock ) {
        super( SimStrings.get( "Module.title" ), clock );

        // Set up the basics
        final SolubleSaltsModel model = new SolubleSaltsModel();
        setModel( model );
        simPanel = new SSCanvas( new Dimension( (int)( model.getBounds().getWidth() * viewScale ), (int)( model.getBounds().getHeight() * viewScale ) ));
        setPhetPCanvas( simPanel );

        // Make a graphic for the un-zoomed setup, and add it to the canvax
        PNode fullScaleCanvas = new WorldNode( model, simPanel );
        fullScaleCanvas.setScale( viewScale );
        simPanel.addWorldChild( fullScaleCanvas );

        // Add a graphic manager to the model that will create and remove IonGraphics
        // when Ions are added to and removed from the model
        model.addIonListener( new IonGraphicManager( fullScaleCanvas ) );

        // Set up the control panel
        setControlPanel( new SolubleSaltsControlPanel( this ) );

        // Add some ions for testing
//        createTestIons( model );



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

    private void createTestIons( final SolubleSaltsModel model ) {
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
    }


    public void setZoomEnabled( boolean zoomEnabled ) {
        simPanel.setZoomEnabled( zoomEnabled );
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

            PPath pPath = new PPath( new Rectangle2D.Double( 0, 0, 60, 30 ) );
            pPath.setPaint( Color.cyan );
            setRegistrationPoint( pPath.getWidth() / 3, pPath.getHeight() / 3 );
            addChild( pPath );

        }
    }

}
