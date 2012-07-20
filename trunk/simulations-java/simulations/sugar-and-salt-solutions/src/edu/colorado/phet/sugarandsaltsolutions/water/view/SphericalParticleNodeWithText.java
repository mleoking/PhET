// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SphericalParticleNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * For water molecules, optionally show the partial charge on each atom
 *
 * @author Sam Reid
 */
public class SphericalParticleNodeWithText extends SphericalParticleNode {

    //Text symbol to show for the partial charge delta
    public static final char DELTA = '\u03B4';

    //The default "-" sign on Windows is too short, the team requested to use a longer symbol, so I switched to the unicode figure dash
    //As described on this page: http://www.fileformat.info/info/unicode/char/2012/index.htm
    //The unicode figure dash also has the benefit that it looks further away from the delta symbol
    //These symbols are not translatable
    public static final String MINUS = "\u2012";
    public static final String PLUS = "+";

    public SphericalParticleNodeWithText( final ModelViewTransform transform, final SphericalParticle particle, final ObservableProperty<Boolean> showChargeColor, final ObservableProperty<Boolean> showWaterCharge ) {
        super( transform, particle, showChargeColor );

        //Add the text, which is shown if the user selected "show water charges"
        final String text = particle.getPartialChargeDisplayValue() > 0 ? DELTA + PLUS :
                            particle.getPartialChargeDisplayValue() < 0 ? DELTA + MINUS :
                            "";
        addChild( new PText( text ) {{
            showWaterCharge.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showPartialCharge ) {
                    setVisible( showPartialCharge );
                }
            } );

            //Symbols for H partial charges about 1/2 the size of the O partial charges
            setFont( new PhetFont( particle instanceof WaterMolecule.Hydrogen ? 10 : 18 ) );

            //Center on the particle
            particle.addPositionObserver( new VoidFunction1<Vector2D>() {
                public void apply( Vector2D immutableVector2D ) {
                    setOffset( transform.modelToView( particle.getPosition() ).minus( getFullBounds().getWidth() / 2, getFullBounds().getHeight() / 2 ).toPoint2D() );
                }
            } );
        }} );
    }
}