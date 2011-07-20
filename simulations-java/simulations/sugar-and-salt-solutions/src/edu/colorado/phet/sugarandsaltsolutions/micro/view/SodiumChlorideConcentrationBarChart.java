// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM;

/**
 * Bar chart that shows Na+ and Cl- concentrations for table salt.
 *
 * @author Sam Reid
 */
public class SodiumChlorideConcentrationBarChart extends ConcentrationBarChart {
    public SodiumChlorideConcentrationBarChart( ObservableProperty<Double> sodiumConcentration,
                                                ObservableProperty<Color> sodiumColor,
                                                ObservableProperty<Double> chlorideConcentration,
                                                ObservableProperty<Color> chlorideColor,
                                                SettableProperty<Boolean> showValues,
                                                Property<Boolean> visible,
                                                ModelViewTransform transform, ObservableProperty<Boolean> showChargeColor ) {
        super( showValues, visible );

        //Convert from model units (mol/L) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 4;

        //Add a Sodium concentration bar
        addChild( new Bar( sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new SodiumIonParticle(), showChargeColor ) ), sodiumConcentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 - WIDTH, abscissaY );
        }} );

        //Add a Chloride concentration bar
        addChild( new Bar( chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new ChlorideIonParticle(), showChargeColor ) ), chlorideConcentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 + WIDTH + 25, abscissaY );
        }} );
    }
}