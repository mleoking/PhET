/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.piccolo.PImageFactory;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.Shaker;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * ShakerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ShakerGraphic extends RegisterablePNode {

    private PImage shakerImage;
    private Shaker shaker;

    public ShakerGraphic( Shaker shaker ) {
        this.shaker = shaker;
        shakerImage = PImageFactory.create( SolubleSaltsConfig.SHAKER_IMAGE_NAME );
        this.addChild( shakerImage );

        this.addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
            }

            public void mouseExited( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( Cursor.getDefaultCursor() );
            }

            public void mouseDragged( PInputEvent event ) {
                double dy = event.getDelta().getHeight();
                Point2D p = getOffset();
                double y = p.getY() + dy;
                if( y <= ShakerGraphic.this.shaker.getMaxY() ) {
                    setOffset( p.getX(), p.getY() + dy );
                    ShakerGraphic.this.shaker.shake( dy );
                }
            }

            public void mousePressed( PInputEvent event ) {
                ShakerGraphic.this.shaker.reset();
            }
        } );
    }
}
