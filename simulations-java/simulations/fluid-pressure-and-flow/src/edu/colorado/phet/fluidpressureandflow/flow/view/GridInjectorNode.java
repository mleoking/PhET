// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.InjectorNode;
import edu.colorado.phet.fluidpressureandflow.flow.model.Pipe;

/**
 * Injects a grid of particles.
 *
 * @author Sam Reid
 */
public class GridInjectorNode extends InjectorNode {
    public GridInjectorNode( final ModelViewTransform mvt, double rotationAngle, final SimpleObserver squirt, final Pipe pipe ) {
        super( rotationAngle, new SimpleObserver() {
            public void update() {
                squirt.update();
            }
        } );

//        observers.addListener( new VoidFunction1<Void>() {
//            public void apply( Void myVoid ) {
//                setEnabled( false );
//                new javax.swing.Timer( 5000, new ActionListener() {
//                    public void actionPerformed( ActionEvent e ) {
//                        setEnabled( true );
//                    }
//                } ) {{setRepeats( false );}}.start();
//            }
//        } );

        final SimpleObserver updateLocation = new SimpleObserver() {
            public void update() {

                //It might be better to rewrite this to be a function of x instead of array index
                final Point2D site = mvt.modelToView( pipe.getSplineCrossSections().get( 11 ).getTop() );
                setOffset( site.getX(), site.getY() - distanceCenterToTip + 5 );
            }
        };
        pipe.addShapeChangeListener( updateLocation );
        updateLocation.update();
    }
}
