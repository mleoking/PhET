// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.ObservableFractionNumberNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FractionRepNode extends RepNode {
    public FractionRepNode( ModelViewTransform transform, final Fraction fractionRepresentation ) {
        super( transform, fractionRepresentation );

        addChild( new ZeroOffsetNode( new PNode() {{
            final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 67, 0 ), new BasicStroke( 4 ), Color.black );
            addChild( line );
            addChild( new PhetPText( fraction.numerator + "", ObservableFractionNumberNode.FONT ) {{
                setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
            }} );

            addChild( new PhetPText( fraction.denominator + "", ObservableFractionNumberNode.FONT ) {{
                setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
            }} );
        }} ) );

        scale( 0.5 );
    }
}
