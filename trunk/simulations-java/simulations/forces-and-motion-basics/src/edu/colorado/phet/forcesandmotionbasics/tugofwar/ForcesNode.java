// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.forcesandmotionbasics.common.ForceArrowNode;
import edu.colorado.phet.forcesandmotionbasics.common.TextLocation;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
public class ForcesNode extends PNode {

    public static final Color APPLIED_FORCE_COLOR = new Color( 227, 171, 128 );
    public static final Color SUM_OF_FORCES_COLOR = new Color( 80, 220, 96 );

    public void setForces( boolean transparent, final double leftForce, final double rightForce, final boolean showSumOfForces, final Boolean showValues ) {
        removeAllChildren();
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 - 2, 225 ), leftForce, "Left Force", APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues, 1.0 ) );
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 + 2, 225 ), rightForce, "Right Force", APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues, 1.0 ) );

        if ( showSumOfForces ) {
            addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2, 150 ), leftForce + rightForce, "Sum of Forces", SUM_OF_FORCES_COLOR, TextLocation.TOP, showValues, 1.0 ) );
        }
    }
}