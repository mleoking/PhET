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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.WiggleMe;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.IonInitializer;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.crystal.Bond;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.Sodium;
import edu.colorado.phet.solublesalts.view.BondGraphic;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.solublesalts.view.SSCanvas;
import edu.colorado.phet.solublesalts.view.WorldNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
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

    /**
     * Only constructor
     *
     * @param clock
     */
    public SolubleSaltsModule( IClock clock ) {
        super( SimStrings.get( "Module.title" ), clock );

        // Set up the basics
        final SolubleSaltsModel model = new SolubleSaltsModel( clock );
        setModel( model );
        simPanel = new SSCanvas( new Dimension( (int)( model.getBounds().getWidth() * viewScale ), (int)( model.getBounds().getHeight() * viewScale ) ) );
        setPhetPCanvas( simPanel );

        // Make a graphic for the un-zoomed setup, and add it to the canvax
        final PNode fullScaleCanvas = new WorldNode( model, simPanel );
        fullScaleCanvas.setScale( viewScale );
        simPanel.addWorldChild( fullScaleCanvas );

        // Add a graphic manager to the model that will create and remove IonGraphics
        // when Ions are added to and removed from the model
        model.addIonListener( new IonGraphicManager( fullScaleCanvas ) );

        // Set up the control panel
        setControlPanel( new SolubleSaltsControlPanel( this ) );

        // Set the default salt
        model.setCurrentSalt( SolubleSaltsConfig.DEFAULT_SALT );

        // DEBUG!!! Adds a listener that draws bonds on the screen
        Bond.addConstructionListener( new Bond.ConstructionListener() {
            HashMap bondToGraphic = new HashMap();

            public void instanceConstructed( Bond.ConstructionEvent event ) {
                if( SolubleSaltsConfig.SHOW_BONDS ) {
                    BondGraphic bondGraphic = new BondGraphic( event.getInstance() );
                    fullScaleCanvas.addChild( bondGraphic );
                    bondToGraphic.put( event.getInstance(), bondGraphic );
                }
            }

            public void instanceRemoved( Bond.ConstructionEvent event ) {
                if( SolubleSaltsConfig.SHOW_BONDS ) {
                    BondGraphic bondGraphic = (BondGraphic)bondToGraphic.get( event.getInstance() );
                    fullScaleCanvas.removeChild( bondGraphic );
                }
            }
        } );

        // Add some ions for testing
//        createTestIons( model );
    }

    private void createTestIons( final SolubleSaltsModel model ) {
        Ion ion = null;

        ion = new Chlorine();
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
