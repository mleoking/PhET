// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.common.ForceArrowNode;
import edu.colorado.phet.forcesandmotionbasics.common.TextLocation;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.STAGE_SIZE;

/**
 * Shows the left, right and sum of forces, along with their labels and (optionally) values.
 *
 * @author Sam Reid
 */
public class ForcesNode extends PNode {

    public static final Color APPLIED_FORCE_COLOR = new Color( 230, 110, 35 );
    public static final Color SUM_OF_FORCES_COLOR = new Color( 150, 200, 60 );

    public void setForces( boolean transparent, final double leftForce, final double rightForce, final boolean showSumOfForces, final Boolean showValues ) {
        removeAllChildren();
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 - 2, 225 ), leftForce, Strings.LEFT_FORCE, APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues ) );
        addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2 + 2, 225 ), rightForce, Strings.RIGHT_FORCE, APPLIED_FORCE_COLOR, TextLocation.SIDE, showValues ) );

        if ( showSumOfForces ) {
            addChild( new ForceArrowNode( transparent, Vector2D.v( STAGE_SIZE.width / 2, 150 ), leftForce + rightForce, Strings.SUM_OF_FORCES, SUM_OF_FORCES_COLOR, TextLocation.TOP, showValues ) );
        }
    }
}