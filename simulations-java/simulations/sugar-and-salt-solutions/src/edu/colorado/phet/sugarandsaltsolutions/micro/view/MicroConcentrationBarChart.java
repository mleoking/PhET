// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.ConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;

/**
 * Bar chart that shows Na+ and Cl- concentrations for table salt.
 *
 * @author Sam Reid
 */
public class MicroConcentrationBarChart extends ConcentrationBarChart {

    private final PNode barLayer = new PNode();
    private final SettableProperty<Boolean> showValues;

    public MicroConcentrationBarChart( Property<Boolean> visible,
                                       SettableProperty<Boolean> showValues,
                                       BarItem... bars ) {
        super( showValues, visible,

               //Leave enough space for the captions.  This is hard coded since the value is difficult to compute dynamically: largest of all (insets + text + insets + caption + insets)
               //It is also okay that this is hard-coded since translations typically expand horizontally instead of vertically
               35 );
        addChild( barLayer );
        this.showValues = showValues;
        setBars( bars );
    }

    //Clear the previous bars and display the specified bars
    public void setBars( BarItem... bars ) {
        barLayer.removeAllChildren();

        //Convert from model units (mol/L) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 8 / 1000.0;

        //Add the bar node for each of the specified bars
        double spacing = background.getFullBounds().getWidth() / ( bars.length + 1 );
        double barX = spacing;
        for ( BarItem bar : bars ) {
            final double finalBarX = barX;

            //Use a StandardizedNodeX here to center the bars on the desired points horizontally so the bars will be equidistant
            barLayer.addChild( new StandardizedNodeX( new Bar( bar.color, bar.caption, bar.icon.apply(), bar.concentration, showValues, verticalAxisScale ) ) {{
                setOffset( finalBarX - getFullBoundsReference().width / 2, abscissaY );
            }} );

            barX = barX + spacing;
        }
    }
}