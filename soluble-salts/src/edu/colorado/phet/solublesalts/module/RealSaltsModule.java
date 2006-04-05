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
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.WiggleMe;
import edu.colorado.phet.solublesalts.WiggleMe_org;
import edu.colorado.phet.solublesalts.control.RealSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.IonListener;
import edu.colorado.phet.solublesalts.model.ion.IonEvent;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RealSaltsModule extends SolubleSaltsModule {

    /**
     * Only constructor
     *
     * @param clock
     */
    public RealSaltsModule( IClock clock ) {
        super( SimStrings.get( "Module.title" ),
               clock,
               new SolubleSaltsConfig.Calibration( 1.5E-16 / 500,
                                                   1E-16,
                                                   0.5E-16,
                                                   0.1E-16 ) );
//               new SolubleSaltsConfig.Calibration( 1.7342E-25,
//                                                   5E-23,
//                                                   1E-23,
//                                                   0.5E-23 ) );

        // Set up the control panel
        setControlPanel( new RealSaltsControlPanel( this ) );

        // Set the default salt
//        ( (SolubleSaltsModel)getModel() ).setCurrentSalt( SolubleSaltsConfig.DEFAULT_SALT );

        // Add the wiggle-me
//        final SolubleSaltsModel model = (SolubleSaltsModel)getModel();
//        double x = model.getShaker().getPosition().getX() - 200;
//        double y = model.getShaker().getPosition().getY() - 100;
//        final WiggleMe_org wiggleMe = new WiggleMe_org( SimStrings.get("WiggleMe.message"),
//                                                        new Point2D.Double( x, y ),
//                                                        50,
//                                                        new Color( 30, 100, 60 ) );
//        model.addModelElement( wiggleMe );
//        getFullScaleCanvas().addChild( wiggleMe );
//        wiggleMe.setVisible( true );

        // A listener that will remove the wiggle-me when an ion is added to the model
//        model.addIonListener( new IonListener() {
//            public void ionAdded( IonEvent event ) {
//                model.removeModelElement( wiggleMe );
//                wiggleMe.setVisible( false );
//            }
//
//            public void ionRemoved( IonEvent event ) {
//                // noop
//            }
//        } );

    }
}
