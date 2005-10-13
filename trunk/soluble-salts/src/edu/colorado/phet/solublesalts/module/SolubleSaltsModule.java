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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.solublesalts.control.IonGraphicManager;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.view.VesselGraphic;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

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

    public SolubleSaltsModule( AbstractClock clock ) {
        super( SimStrings.get( "Module.title" ), clock );

        // Set up the basics
        final SolubleSaltsModel model = new SolubleSaltsModel();
        setModel( model );
        final PhetPCanvas simPanel = new PhetPCanvas();
        setPhetPCanvas( simPanel );

        // Add a graphic manager to the model that will manage IonGraphics
        model.addIonListener( new IonGraphicManager( simPanel ) );

        // A test graphic
        Rectangle r = new Rectangle( 100, 150, 20, 70 );
        PPath pp = new PPath( r );
        pp.setPaint( Color.red );
        simPanel.addWorldChild( pp );
        pp.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pp.addInputEventListener( new PDragEventHandler() );

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel() );
        simPanel.addWorldChild( vesselGraphic );
        vesselGraphic.addInputEventListener( new PDragEventHandler() );
        model.getVessel().setWaterLevel( 100 );

        // Create an ion and add it to the model
        {
            Vessel vessel = model.getVessel();
            Ion ion = new Sodium( new Point2D.Double( vessel.getLocation().getX() + 100,
                                                      vessel.getLocation().getY() + vessel.getDepth() - 50 ),
                                  new Vector2D.Double( 2, -1 ),
                                  new Vector2D.Double() );
//            model.addModelElement( ion );

            Ion ion2 = new Chloride( new Point2D.Double( vessel.getLocation().getX() + 100,
                                                         vessel.getLocation().getY() - 50 ),
                                     new Vector2D.Double( 0, 5 ),
                                     new Vector2D.Double() );
//        model.addModelElement( ion2 );
        }

        {
            int numIons = 10;
            for( int i = 0; i < numIons; i++ ) {
                Ion ion = new Sodium();
                ion.setPosition( genIonPosition( ion ) );
                ion.setVelocity( genVelocity() );
                model.addModelElement( ion );
            }
        }

    }

    private Point2D genIonPosition( Ion ion ) {
        SolubleSaltsModel model = (SolubleSaltsModel)getModel();
        Vessel vessel = model.getVessel();
        double x = vessel.getWater().getMinX() + ion.getRadius() * 2
                   + random.nextDouble() * ( vessel.getWater().getWidth() - ion.getRadius() * 2 );
        double y = vessel.getWater().getMinY() + ion.getRadius() * 2
                   + random.nextDouble() * ( vessel.getWater().getHeight() - ion.getRadius() * 2 );
        return new Point2D.Double( x, y );
    }

    private Vector2D genVelocity() {
        double vMax = 4;
        return new Vector2D.Double( random.nextDouble() * vMax, random.nextDouble() * vMax );
    }
}
