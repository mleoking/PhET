// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.Shaker;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * ShakerGraphic
 *
 * @author Ron LeMaster
 */
public class ShakerGraphic extends RegisterablePNode {

    private Shaker shaker;

    public ShakerGraphic( Shaker shaker ) {
        this.shaker = shaker;
        PImage shakerImage = PImageFactory.create( SolubleSaltsConfig.SHAKER_IMAGE_NAME );
        this.addChild( shakerImage );

        this.addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas) event.getComponent();
                ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
            }

            public void mouseExited( PInputEvent event ) {
                PhetPCanvas ppc = (PhetPCanvas) event.getComponent();
                ppc.setCursor( Cursor.getDefaultCursor() );
            }

            public void mouseDragged( PInputEvent event ) {
                Shaker shaker = ShakerGraphic.this.shaker;
                double dy = event.getDelta().getHeight();
                Point2D p = getOffset();
                double y = p.getY() + dy;
                if ( y <= shaker.getMaxY() && y >= shaker.getMinY() ) {
                    setOffset( p.getX(), p.getY() + dy );
                    shaker.shake( dy );
                }

                // If the shaker moved up, then reset it so it can shake out more salt the next time it
                // moves down
                if ( dy < 0 ) {
                    shaker.reset();
                }
            }

            public void mousePressed( PInputEvent event ) {
                ShakerGraphic.this.shaker.reset();
            }
        } );
    }
}
