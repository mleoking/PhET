/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PNode that displays a slider, a numerical value, and a "key" isotope, and
 * that connects with a model element to allow users to add or remove isotopes
 * from the model.
 *
 * @author John Blanco
 */
public class IsotopeSliderNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 170, 75 );

    public IsotopeSliderNode( IsotopeMixturesModel.LinearAddRemoveIsotopesControl modelControl, ModelViewTransform mvt ){
        PNode enclosure = new PhetPPath(
                new Rectangle2D.Double( -SIZE.getWidth() / 2, -SIZE.getHeight() / 2, SIZE.getWidth(), SIZE.getHeight() ),
                Color.WHITE,
                new BasicStroke( 2 ),
                Color.BLACK);
        enclosure.setOffset( mvt.modelToView( modelControl.getCenterPositionRef() ) );
        addChild( enclosure );
        // TODO: temp - add a label.
        enclosure.addChild( new PText(modelControl.getAtomConfig().getName() + "-" + modelControl.getAtomConfig().getMassNumber()) );
    }
}
