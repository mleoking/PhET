// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.module;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.solublesalts.SolubleSaltResources;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.WiggleMe_org;
import edu.colorado.phet.solublesalts.control.SodiumChlorideControlPanel;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.IonEvent;
import edu.colorado.phet.solublesalts.model.ion.IonListener;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 */
public class SodiumChlorideModule extends SolubleSaltsModule {

    /**
     * Only constructor
     *
     * @param clock
     * @param title
     */
    public SodiumChlorideModule( IClock clock, final String title ) {
        super( title,
               clock,
               new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                   5E-23,
                                                   1E-23,
                                                   0.5E-23 ) );

        // Set up the control panel
        setControlPanel( new SodiumChlorideControlPanel( this ) );

        // Set the default salt
        ( (SolubleSaltsModel) getModel() ).setCurrentSalt( SolubleSaltsConfig.DEFAULT_SALT );

        // Add the wiggle-me
        final SolubleSaltsModel model = (SolubleSaltsModel) getModel();
        double x = model.getShaker().getPosition().getX() - 200;
        double y = model.getShaker().getPosition().getY() - 100;
        final WiggleMe_org wiggleMe = new WiggleMe_org( SolubleSaltResources.getString( "WiggleMe.message" ),
                                                        new Point2D.Double( x, y ),
                                                        50,
                                                        new Color( 30, 100, 60 ) );
        model.addModelElement( wiggleMe );
        getFullScaleCanvasNode().addChild( wiggleMe );
        wiggleMe.setVisible( true );

        // A listener that will remove the wiggle-me when an ion is added to the model
        model.addIonListener( new IonListener() {
            public void ionAdded( IonEvent event ) {
                model.removeModelElement( wiggleMe );
                wiggleMe.setVisible( false );
            }

            public void ionRemoved( IonEvent event ) {
                // intentional noop
            }
        } );
    }
}
