// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.WATER_GLASSES;
import static edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode.cachedWaterGlassNode;

/**
 * Representation control panel icon for water glass.
 *
 * @author Sam Reid
 */
public class WaterGlassIcon extends PNode implements RepresentationIcon {

    public WaterGlassIcon( final SettableProperty<Representation> selected, Color color ) {
        addChild( new PImage( cachedWaterGlassNode( 1, 1, color, 560 * 0.33, 681 * 0.5 ) ) {{
            scale( 0.67 );
        }} );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                selected.set( getRepresentation() );
            }
        } );
        scale( 0.32 );
    }

    public PNode getNode() {
        return this;
    }

    public Representation getRepresentation() {
        return WATER_GLASSES;
    }
}