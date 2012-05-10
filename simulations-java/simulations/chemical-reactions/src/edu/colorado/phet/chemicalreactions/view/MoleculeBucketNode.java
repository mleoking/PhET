// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.awt.Color;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

public class MoleculeBucketNode extends BucketView {
    public MoleculeBucketNode( MoleculeBucket bucket ) {
        super( bucket, ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM );

        addLabelToContainer( new HTMLNode( ChemUtils.toSubscript( bucket.getShape().name ), Color.BLACK, new PhetFont( 18, true ) ) );
    }
}
