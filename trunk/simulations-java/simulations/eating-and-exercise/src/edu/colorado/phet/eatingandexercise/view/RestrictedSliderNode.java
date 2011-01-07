// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 24, 2008 at 6:48:47 PM
 */
public class RestrictedSliderNode extends SliderNode {

    private double dragmin;
    private double dragmax;
    private RestrictedRangeNode lowerRestrictedRange;
    private RestrictedRangeNode upperRestrictedRange;

    public RestrictedSliderNode( double min, double max, double value ) {
        super( max, value, min );
        this.dragmin = min;
        this.dragmax = max;
        lowerRestrictedRange = new RestrictedRangeNode( min, dragmin );
        upperRestrictedRange = new RestrictedRangeNode( dragmax, max );

        addChild( lowerRestrictedRange );
        addChild( upperRestrictedRange );

        updateThumb();
    }

    protected double clamp( double a ) {
        return MathUtil.clamp( dragmin, super.clamp( a ), dragmax );
    }

    private class RestrictedRangeNode extends PNode {
        private PhetPPath path;
        private double min;
        private double max;

        public RestrictedRangeNode( double min, double max ) {
            this.min = min;
            this.max = max;
            path = new PhetPPath( Color.red, new BasicStroke( 1 ), Color.black );
            addChild( path );
            updatePath();
        }

        private void updatePath() {
            path.setPathTo( createTrackShape( min, max ) );
            path.setVisible( min != max );
        }

        public void setRange( double min, double max ) {
            this.min = min;
            this.max = max;
            updatePath();
        }
    }

    private void updateRestrictedRanges() {
        lowerRestrictedRange.setRange( super.getMin(), dragmin );
        System.out.println( "min = " + getMin() + ", dragmin=" + dragmin );
        upperRestrictedRange.setRange( dragmax, super.getMax() );
    }

    public void setDragRange( double dragmin, double dragmax ) {
        this.dragmin = dragmin;
        this.dragmax = dragmax;
        updateRestrictedRanges();
    }
}
