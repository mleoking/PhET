// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.FractionsResources;
import edu.colorado.phet.fractionsintro.intro.view.representations.dilutions.BeakerNode;
import edu.colorado.phet.fractionsintro.intro.view.representations.dilutions.Solute;
import edu.colorado.phet.fractionsintro.intro.view.representations.dilutions.Solution;
import edu.colorado.phet.fractionsintro.intro.view.representations.dilutions.SolutionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shows a glass possibly with some water in it.
 *
 * @author Sam Reid
 */
public class WaterGlassNode extends PNode {

    // properties common to all 3 beakers
    private static final double BEAKER_SCALE_X = 0.33;
    private static final double BEAKER_SCALE_Y = 0.50;
    private static final PhetFont BEAKER_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension BEAKER_LABEL_SIZE = new PDimension( 100, 50 );

    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 0.2, 0.05 ); // moles
    private static final DoubleRange DILUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / DILUTION_VOLUME_RANGE.getMax(),
                                                                            SOLUTE_AMOUNT_RANGE.getMax() / DILUTION_VOLUME_RANGE.getMin() ); // M

    public WaterGlassNode( Integer numerator, Integer denominator, final VoidFunction0 addWater, final VoidFunction0 removeWater ) {

        final boolean full = numerator.equals( denominator );
        final boolean empty = numerator == 0;

        final BeakerNode waterBeakerNode = new BeakerNode( 1, BEAKER_SCALE_X, BEAKER_SCALE_Y, null, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, 1.0 / denominator, 2, FractionsResources.Images.WATER_GLASS_FRONT );
        waterBeakerNode.setLabelVisible( false );
        final PDimension cylinderSize = waterBeakerNode.getCylinderSize();

        Solute solute = new Solute( "solute", "?", CONCENTRATION_RANGE.getMax(), new Color( 0xE0FFFF ), 5, 200 ); // hypothetical solute with unknown formula
        Solution solution = new Solution( solute, SOLUTE_AMOUNT_RANGE.getDefault(), numerator / (double) denominator );
        // Water beaker, with water inside of it
        SolutionNode waterNode = new SolutionNode( cylinderSize, waterBeakerNode.getCylinderEndHeight(), solution, new DoubleRange( 0, 1 ) ) {
            @Override protected Color getColor() {
                return FractionsIntroCanvas.FILL_COLOR;
            }
        };

        BeakerNode waterBeakerBackgroundNode = new BeakerNode( 1, BEAKER_SCALE_X, BEAKER_SCALE_Y, null, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, 1.0 / denominator, 2, FractionsResources.Images.WATER_GLASS_BACK );
        waterBeakerBackgroundNode.setLabelVisible( false );

        //Show a background layer so that the water appears between the layers (for showing the surface of the water)
        addChild( waterBeakerBackgroundNode );
        addChild( waterNode );
        addChild( waterBeakerNode );

        scale( 0.67 );

        // this node not interactive
        // make pickable here instead of in BeakerNode to maintain compatibility with dilutions implementation.
        waterBeakerNode.setPickable( false );
        waterBeakerNode.setChildrenPickable( false );

        waterBeakerBackgroundNode.setPickable( true );
        waterBeakerBackgroundNode.setChildrenPickable( true );

        addInputEventListener( new CursorHandler() );

        //if click is above water level and glass not full, add water
        //if click is below water level and glass not empty, remove water
        waterNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                if ( !empty ) {
                    removeWater.apply();
                }
            }
        } );

        waterBeakerNode.setPickable( false );
        waterBeakerNode.setChildrenPickable( false );
        waterBeakerBackgroundNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                if ( !full ) {
                    addWater.apply();
                }
            }
        } );
    }
}