// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author Sam Reid
 */
public class View {
    public View( ModelElement m ) {
    }
}

class ModelElement {

    public void addRemovalListener( VoidFunction0 voidFunction0 ) {
    }
}

class Model {

    public void addModelElementAddedListneer( VoidFunction1<ModelElement> function0 ) {

    }

    public void addModelElement( ModelElement modelElement ) {
    }

    public void addRemovalListener( ModelElement modelElement, VoidFunction0 voidFunction0 ) {
//        removalListeners.add(modelElement,voidFunction0);
    }

    public void removeElement( ModelElement element ) {
//        ..
//        element.notifyThatYouWereRemovde();
    }
}

class Test {
    public static void main( String[] args ) {
        final Model model = new Model();
        model.addModelElementAddedListneer( new VoidFunction1<ModelElement>() {
            public void apply( ModelElement modelElement ) {
                final View view = new View( modelElement );
                addNode( view );
                modelElement.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        removeNode( view );
                    }
                } );
            }
        } );
        model.addModelElement( new ModelElement() );
    }

    private static void removeNode( View view ) {
    }

    private static void addNode( View view ) {
    }
}