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
import edu.colorado.phet.solublesalts.SolubleSaltsApplication;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.control.ConfigurableSaltControlPanel;
import edu.colorado.phet.solublesalts.model.IonInitializer;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.ConfigurableSalt;
import edu.colorado.phet.solublesalts.model.crystal.Bond;
import edu.colorado.phet.solublesalts.model.ion.*;
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
public class ConfigurableSaltModule extends PiccoloModule {

    static public final double viewScale = 0.1;
    
    private SSCanvas simPanel;

    /**
     * Only constructor
     *
     * @param clock
     */
    public ConfigurableSaltModule( IClock clock ) {
        super( SimStrings.get( "Module.configurableSalt" ), clock );

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
        setControlPanel( new ConfigurableSaltControlPanel( this ) );

        // Set the default salt
        ConfigurableAnion.setClassCharge( 1 );
        ConfigurableCation.setClassCharge( -1 );
        ConfigurableSalt.configure();
        model.setCurrentSalt( new ConfigurableSalt() );

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

    }

    public void activate() {
        super.activate();

        // Calibrate
        new SolubleSaltsConfig.Calibration( 7.83E-16 / 500,
                                            5E-16,
                                            1E-16,
                                            0.5E-16 ).calibrate();
        ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).reset();
    }
}
