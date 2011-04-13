// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Contains the kit background and controls for switching between kits
 */
public class KitPanel extends PNode {
    public KitPanel( KitCollectionModel model, ModelViewTransform mvt ) {
        assert ( mvt.getTransform().getScaleY() < 0 ); // we assume this and correct for it

        Rectangle2D kitViewBounds = mvt.modelToViewRectangle( model.getAvailableKitBounds() );
        PPath background = PPath.createRectangle(
                (float) kitViewBounds.getX(),
                (float) kitViewBounds.getY(),
                (float) kitViewBounds.getWidth(),
                (float) kitViewBounds.getHeight() );

        background.setPaint( Color.WHITE );
        background.setStrokePaint( Color.BLACK );
        addChild( background );
    }
}
