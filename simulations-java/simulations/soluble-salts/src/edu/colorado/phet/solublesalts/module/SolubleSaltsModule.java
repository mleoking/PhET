// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.module;

import java.awt.*;
import java.util.EventListener;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
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

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 */
public class SolubleSaltsModule extends PiccoloModule {
    boolean debug = false;
    static public final double viewScale = 0.1;

    private SSCanvas simPanel;
    private SolubleSaltsConfig.Calibration calibration;
    private PNode fullScaleCanvas;
    private final SolubleSaltsModel solubleSaltsModel;

    public SolubleSaltsModule( String title, IClock clock, SolubleSaltsConfig.Calibration calibration ) {
        super( title, clock );

        // Set the calibration parameters
        setCalibration( calibration );

        // Set up the basics
        solubleSaltsModel = new SolubleSaltsModel( clock, this );
        setModel( solubleSaltsModel );
        simPanel = new SSCanvas( new Dimension( (int) ( solubleSaltsModel.getBounds().getWidth() * viewScale ), (int) ( solubleSaltsModel.getBounds().getHeight() * viewScale ) ) );
        setSimulationPanel( simPanel );

        // Make a graphic for the un-zoomed setup, and add it to the canvax
        fullScaleCanvas = new WorldNode( this, simPanel );
        fullScaleCanvas.setScale( viewScale );
        simPanel.addWorldChild( fullScaleCanvas );

        // Add a graphic manager to the model that will create and remove IonGraphics
        // when Ions are added to and removed from the model
        solubleSaltsModel.addIonListener( new IonGraphicManager( fullScaleCanvas ) );

        // DEBUG!!! Adds a listener that draws bonds on the screen
        Bond.addConstructionListener( new Bond.ConstructionListener() {
            HashMap bondToGraphic = new HashMap();

            public void instanceConstructed( Bond.ConstructionEvent event ) {
                if ( SolubleSaltsConfig.SHOW_BONDS ) {
                    BondGraphic bondGraphic = new BondGraphic( event.getInstance() );
                    fullScaleCanvas.addChild( bondGraphic );
                    bondToGraphic.put( event.getInstance(), bondGraphic );
                }
            }

            public void instanceRemoved( Bond.ConstructionEvent event ) {
                if ( SolubleSaltsConfig.SHOW_BONDS ) {
                    BondGraphic bondGraphic = (BondGraphic) bondToGraphic.get( event.getInstance() );
                    fullScaleCanvas.removeChild( bondGraphic );
                }
            }
        } );

        // Add some ions for testing
        if ( debug ) {
            createTestIons( solubleSaltsModel );
        }
    }

    private void createTestIons( final SolubleSaltsModel model ) {
        Ion ion;

        ion = new Chlorine();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 130, 200 );
        ion.setVelocity( 0, 5 );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 70, 230 );
        ion.setVelocity( 5, 0 );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 600, 435 );
        ion.setVelocity( 5, 0 );
    }

    protected PNode getFullScaleCanvasNode() {
        return fullScaleCanvas;
    }

    public void setCalibration( SolubleSaltsConfig.Calibration calibration ) {
        this.calibration = calibration;
    }

    public SolubleSaltsConfig.Calibration getCalibration() {
        return calibration;
    }

    public void reset() {
        resetListenerProxy.reset( calibration );
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel resetEventChannel = new EventChannel( ResetListener.class );
    private ResetListener resetListenerProxy = (ResetListener) resetEventChannel.getListenerProxy();

    public interface ResetListener extends EventListener {
        void reset( SolubleSaltsConfig.Calibration calibration );
    }

    public void addResetListener( ResetListener listener ) {
        resetEventChannel.addListener( listener );
    }

    public void removeResetListener( ResetListener listener ) {
        resetEventChannel.removeListener( listener );
    }

    public SolubleSaltsModel getSolubleSaltsModel() {
        return solubleSaltsModel;
    }
}