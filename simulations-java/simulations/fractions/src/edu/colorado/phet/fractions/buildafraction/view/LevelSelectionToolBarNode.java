// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Readout for level number and spinner buttons for going to next/previous level.
 *
 * @author Sam Reid
 */
public class LevelSelectionToolBarNode extends PNode {
    public LevelSelectionToolBarNode( final VoidFunction1<Integer> goToNumberLevel, final PhetFont scoreboardFont, final IntegerProperty level, int levelCount ) {
        final SpinnerButtonNode leftButtonNode = new SpinnerButtonNode( spinnerImage( Images.LEFT_BUTTON_UP ), spinnerImage( Images.LEFT_BUTTON_PRESSED ), spinnerImage( Images.LEFT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel.apply( level.get() - 1 );
            }
        }, level.greaterThan( 0 ) );
        final SpinnerButtonNode rightButtonNode = new SpinnerButtonNode( spinnerImage( Images.RIGHT_BUTTON_UP ), spinnerImage( Images.RIGHT_BUTTON_PRESSED ), spinnerImage( Images.RIGHT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                goToNumberLevel.apply( level.get() + 1 );
            }
        }, level.lessThan( levelCount - 1 ) );
        addChild( rightButtonNode );

        //Level indicator and navigation buttons for number mode
        final HBox levelSelectionBox = new HBox( 30, leftButtonNode, new PhetPText( "Level 100", scoreboardFont ) {{
            level.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setText( "Level " + ( integer + 1 ) );
                }
            } );
        }}, rightButtonNode );

        addChild( levelSelectionBox );
    }

    private BufferedImage spinnerImage( final BufferedImage image ) { return BufferedImageUtils.multiScaleToHeight( image, 30 ); }
}