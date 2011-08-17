// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Sucrose;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for sucrose that can show either individual atom colors or neutral for all atoms
 *
 * @author Sam Reid
 */
public class MultiSucroseNode extends PNode {
    public MultiSucroseNode( ModelViewTransform transform, Sucrose sucrose, VoidFunction1<VoidFunction0> addFrameListener, final ObservableProperty<Boolean> showSugarAtoms ) {
        addChild( new SucroseNode( transform, sucrose, addFrameListener, Element.O.getColor(), Element.H.getColor(), Color.gray ) {{
            showSugarAtoms.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showSugarAtoms ) {
                    setVisible( showSugarAtoms );
                }
            } );
        }} );

        addChild( new SucroseNode( transform, sucrose, addFrameListener, Color.yellow, Color.yellow, Color.yellow ) {{
            showSugarAtoms.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showSugarAtoms ) {
                    setVisible( !showSugarAtoms );
                }
            } );
        }} );
    }
}
