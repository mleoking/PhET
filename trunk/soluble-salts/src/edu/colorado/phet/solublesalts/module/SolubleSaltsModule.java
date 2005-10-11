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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.DefaultDragHandler;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.solublesalts.view.VesselGraphic;
import edu.colorado.phet.solublesalts.view.ShakerGraphic;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Ion;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModule extends PiccoloModule {

    public SolubleSaltsModule( AbstractClock clock ) {
        super( SimStrings.get("Module.title"), clock );

        SolubleSaltsModel model = new SolubleSaltsModel();
        setModel( model );
        PhetPCanvas simPanel = new PhetPCanvas();
        setPhetPCanvas( simPanel );

        Rectangle r = new Rectangle( 100, 150, 20, 70 );
        PPath pp = new PPath( r );
        pp.setPaint( Color.red );

        simPanel.addWorldChild( pp );

        pp.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR) );
        pp.addInputEventListener( new PDragEventHandler() );

        // Create a graphic for the vessel
        VesselGraphic vesselGraphic = new VesselGraphic( model.getVessel() );
        simPanel.addWorldChild( vesselGraphic );
        vesselGraphic.addInputEventListener( new PDragEventHandler());

        // Create an ion and a graphic for it
        Vessel vessel = model.getVessel();
        Ion ion = new Ion( new Point2D.Double( vessel.getLocation().getX() + 20,
                                               vessel.getLocation().getY() + vessel.getDepth() - 20),
                           new Vector2D.Double( 2,-2),
                           new Vector2D.Double( 0,0),
                           1 );
        model.addModelElement( ion );
        IonGraphic ionGraphic = new IonGraphic( ion, SolubleSaltsConfig.BLUE_ION_IMAGE_NAME );
        simPanel.addWorldChild( ionGraphic );
    }
}
