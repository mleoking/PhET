// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.pressure.model.ChamberPool;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Mass;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Node that shows a collection of masses for the chamber pool.
 *
 * @author Sam Reid
 */
public class MassesLayer extends PNode {
    public MassesLayer( final ChamberPool pool, final ModelViewTransform transform ) {
        final Property<ObservableList<Mass>> massesProperty = pool.masses;
        final SimpleObserver observer = new SimpleObserver() {
            @Override public void update() {
                final ObservableList<Mass> masses = massesProperty.get();
                removeAllChildren();
                Mass dragging = findDragging( masses );

                //Show a dotted line where the user can drop the block.
                //But don’t show dotted line while water equalizing--it looks too much like there is something on it when the user removes a block
                if ( dragging != null && pool.showDropRegion() ) {
                    addChild( new DottedLineDropRegion( pool, dragging, transform ) );
                }
                for ( Mass mass : masses ) {
                    addChild( new MassNode( pool, massesProperty, mass, transform ) );
                }
            }
        };
        massesProperty.addObserver( observer );

        //Update when equalization changes since we are using that to determine when to show the dotted line drop region
        pool.leftWaterHeightAboveChamber.addObserver( observer );
    }

    private static Mass findDragging( ObservableList<Mass> masses ) {
        for ( Mass mass : masses ) {
            if ( mass.dragging ) {
                return mass;
            }
        }
        return null;
    }

    private static class MassNode extends PNode {
        public MassNode( final ChamberPool pool, final Property<ObservableList<Mass>> masses, final Mass mass, final ModelViewTransform transform ) {
            addChild( new PhetPPath( transform.modelToView( mass.shape ), Color.gray, new BasicStroke( 2 ), Color.black ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    masses.set( masses.get().map( new Function1<Mass, Mass>() {
                        @Override public Mass apply( final Mass m ) {
                            return m == mass ? m.withDragging( true ) : m;
                        }
                    } ) );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    super.mouseDragged( event );
                    final Dimension2D modelDelta = transform.viewToModelDelta( event.getDelta() );
                    masses.set( masses.get().map( new Function1<Mass, Mass>() {
                        @Override public Mass apply( final Mass mass ) {
                            Mass translatedMass = mass.translate( modelDelta );
//                            if ( translatedMass.shape.getBounds2D().getMinY() < 0 ) {
//                                translatedMass = translatedMass.translate( new Dimension2DDouble( 0, -translatedMass.shape.getBounds2D().getMinY() ) );
//                            }
                            return mass.dragging ? translatedMass : mass;
                        }
                    } ) );
                }

                @Override public void mouseReleased( final PInputEvent event ) {

                    //Turn off the "dragging" flag
                    masses.set( masses.get().map( new Function1<Mass, Mass>() {
                        @Override public Mass apply( Mass m ) {
                            //Snap to the dotted line or above ground
                            if ( m.dragging ) {
                                final Shape dottedLineShape = getDottedLineShape( pool, m );
                                if ( m.shape.intersects( dottedLineShape.getBounds2D() ) ) {
                                    m = m.withMinY( dottedLineShape.getBounds2D().getMinY() ).withCenterX( dottedLineShape.getBounds2D().getCenterX() );
                                }
                                else if ( m.getMinY() < 0 ) {
                                    m = m.withMinY( 0.0 );
                                }
                            }
                            m = m.withDragging( false );
                            return m;
                        }
                    } ) );
                }
            } );
        }
    }

    private class DottedLineDropRegion extends PNode {
        public DottedLineDropRegion( ChamberPool pool, final Mass dragging, ModelViewTransform transform ) {
            addChild( new PhetPPath( transform.modelToView( getDottedLineShape( pool, dragging ) ), new Color( 255, 220, 240 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[] { 8, 6 }, 0 ), Color.black ) );
        }
    }

    private static Shape getDottedLineShape( final ChamberPool pool, final Mass dragging ) {
        ArrayList<Mass> stackedMasses = pool.getStackedMasses();
        Rectangle2D bounds = pool.getLeftOpeningWaterShape().getBounds2D();
        double maxY = bounds.getMaxY();
        for ( Mass stackedMass : stackedMasses ) {
            if ( stackedMass.getMaxY() > maxY ) {
                maxY = stackedMass.getMaxY();
            }
        }
        return dragging.withCenterX( bounds.getCenterX() ).withMinY( maxY ).shape;
    }
}