// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.intro.intro.view.ObservableFractionNumberNode;
import edu.colorado.phet.fractions.intro.matchinggame.model.FractionRepresentation;

/**
 * @author Sam Reid
 */
public class FractionRepresentationNode extends RepresentationNode {
    public FractionRepresentationNode( ModelViewTransform transform, final FractionRepresentation fractionRepresentation ) {
        super( transform, fractionRepresentation );
        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 67, 0 ), new BasicStroke( 4 ), Color.black );
        addChild( line );
        addChild( new PhetPText( fractionRepresentation.fraction.numerator + "", ObservableFractionNumberNode.FONT ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );

        addChild( new PhetPText( fractionRepresentation.fraction.denominator + "", ObservableFractionNumberNode.FONT ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
        }} );
    }
}
