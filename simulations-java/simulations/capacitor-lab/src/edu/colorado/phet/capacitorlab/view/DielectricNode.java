/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Cursor;

import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandler;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {

    public DielectricNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        super( capacitor.getDielectricMaterial().getColor() );
        addInputEventListener( new CursorHandler( Cursor.E_RESIZE_CURSOR ) );
        addInputEventListener( new DielectricOffsetDragHandler( this, capacitor, mvt, valueRange ) );
    }
}
