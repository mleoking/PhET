// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.F;

import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.functions.buildafunction.ValueContext;
import edu.colorado.phet.functions.buildafunction.ValueNode;

/**
 * @author Sam Reid
 */
public class TargetNode extends ValueNode {
    public final BooleanProperty completed = new BooleanProperty( false );
    public static F<TargetNode, Boolean> _isComplete = new F<TargetNode, Boolean>() {
        @Override public Boolean f( final TargetNode targetNode ) {
            return targetNode.completed.get();
        }
    };

    public TargetNode( final ValueContext valueContext, Object originalValue, Stroke stroke, Color paint, Color strokePaint, Color textPaint ) {
        super( valueContext, originalValue, stroke, paint, strokePaint, textPaint );
        setPickable( false );
        setChildrenPickable( false );
    }
}