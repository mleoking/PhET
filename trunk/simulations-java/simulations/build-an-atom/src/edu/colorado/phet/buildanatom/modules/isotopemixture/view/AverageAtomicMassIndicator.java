/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.IsotopeMixturesModel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A Piccolo2D node that monitors that average atomic mass of a set of
 * isotopes in a model and graphically displays it.
 *
 * @author John Blanco
 */
public class AverageAtomicMassIndicator extends PNode {

    private static double INDICATOR_WIDTH = 200; // In screen units, which is close to pixels.

    public AverageAtomicMassIndicator( IsotopeMixturesModel model ){

        // Add the title.
        // TODO: i18n
        PText title = new PText("Average Atomic Mass"){{
            setFont( new PhetFont(20, true) );
        }};
        addChild( title );

        // Add the bar that makes up "spine" of the indicator.
        final double barOffsetY = title.getFullBoundsReference().getMaxY() + 30;
        DoubleGeneralPath barShape = new DoubleGeneralPath( 0, 0 );
        barShape.lineTo( INDICATOR_WIDTH, 0 );
        PNode barNode = new PhetPPath( barShape.getGeneralPath(), new BasicStroke(5), Color.BLACK ){{
            setOffset( 0, barOffsetY );
        }};
        addChild( barNode );
    }
}
