// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ElectronNode extends PNode {
    public ElectronNode( Electron m ) {
    }
}

class Electron implements Removable {

    public void addRemoveListener( VoidFunction0 removeListener ) {
    }
}

class PhotonNode extends PNode {
    public PhotonNode( Photon m ) {
    }
}

class Photon implements Removable {

    public void addRemoveListener( VoidFunction0 removeListener ) {
    }
}

class Model {

    public void addElectronAddedListener( VoidFunction1<Electron> function0 ) {
    }

    public void addPhotonAddedListener( VoidFunction1<Photon> function0 ) {
    }

    public void addModelElement( Electron modelElement ) {
    }

    public void addRemovalListener( Electron modelElement, VoidFunction0 voidFunction0 ) {
//        removalListeners.add(modelElement,voidFunction0);
    }

    public void removeElement( Electron element ) {
//        ..
//        element.notifyThatYouWereRemovde();
    }
}

interface Removable {
    void addRemoveListener( VoidFunction0 removeListener );
}

class ViewFactory2<T extends Removable, U> implements VoidFunction1<T> {
    private final Function1<T, U> newView;
    private final VoidFunction1<U> addView;
    private final VoidFunction1<U> removeView;

    ViewFactory2( final Function1<T, U> newView,
                  final VoidFunction1<U> addView,
                  final VoidFunction1<U> removeView ) {
        this.newView = newView;
        this.addView = addView;
        this.removeView = removeView;
    }

    public void apply( T modelElement ) {
        final U view = newView.apply( modelElement );
        addView.apply( view );
        modelElement.addRemoveListener( new VoidFunction0() {
            public void apply() {
                removeView.apply( view );
            }
        } );
    }
}

class TestCanvas extends PhetPCanvas {
    PNode getPhotonLayer() {
        return null;
    }
}

class Test2 {
    public static void main( String[] args ) {
        final Model model = new Model();
        final TestCanvas canvas = new TestCanvas();

        model.addElectronAddedListener( new ViewFactory2<Electron, ElectronNode>(
                new Function1<Electron, ElectronNode>() {
                    public ElectronNode apply( Electron modelElement ) {
                        return new ElectronNode( modelElement );
                    }
                },
                new VoidFunction1<ElectronNode>() {
                    public void apply( ElectronNode view ) {
                        canvas.addScreenChild( view );
                    }
                },
                new VoidFunction1<ElectronNode>() {
                    public void apply( ElectronNode view ) {
                        canvas.removeScreenChild( view );
                    }
                } ) );

        model.addPhotonAddedListener( new ViewFactory2<Photon, PhotonNode>(
                new Function1<Photon, PhotonNode>() {
                    public PhotonNode apply( Photon modelElement ) {
                        return new PhotonNode( modelElement );
                    }
                },
                new VoidFunction1<PhotonNode>() {
                    public void apply( PhotonNode view ) {
                        canvas.getPhotonLayer().addChild( view );
                    }
                },
                new VoidFunction1<PhotonNode>() {
                    public void apply( PhotonNode view ) {
                        canvas.getPhotonLayer().removeChild( view );
                    }
                } ) );
    }
}

class Test {
    public static void main( String[] args ) {
        final Model model = new Model();
        model.addElectronAddedListener( new VoidFunction1<Electron>() {
            public void apply( Electron modelElement ) {
                final ElectronNode view = new ElectronNode( modelElement );
                addNode( view );
                modelElement.addRemoveListener( new VoidFunction0() {
                    public void apply() {
                        removeNode( view );
                    }
                } );
            }
        } );
        model.addModelElement( new Electron() );
    }

    private static void removeNode( ElectronNode view ) {
    }

    private static void addNode( ElectronNode view ) {
    }
}