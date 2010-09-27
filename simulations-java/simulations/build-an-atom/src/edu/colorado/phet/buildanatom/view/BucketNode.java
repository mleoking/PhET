/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 *
 * @author John Blanco
 */
public class BucketNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public BucketNode( Bucket bucket, ModelViewTransform2D mvt ) {
        // Create a scaling transform based on the provided MVT, since we only
        // want the scaling portion and we want to avoid any translation.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getAffineTransform().getScaleX(),
                mvt.getAffineTransform().getScaleY() );
        addChild( new PhetPPath( scaleTransform.createTransformedShape( bucket.getContainerShape() ), bucket.getBaseColor() ) );
        addChild( new PhetPPath( scaleTransform.createTransformedShape( bucket.getHoleShape() ), Color.black ) );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
