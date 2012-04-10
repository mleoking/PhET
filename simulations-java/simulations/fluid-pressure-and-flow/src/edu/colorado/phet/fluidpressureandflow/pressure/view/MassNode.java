// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fluidpressureandflow.pressure.model.ChamberPool;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Mass;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.MASS_LABEL_PATTERN;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.MassesLayer.getDottedLineShape;
import static java.text.MessageFormat.format;

/**
 * Node for showing draggable masses in the mass pool.
 *
 * @author Sam Reid
 */
public class MassNode extends PNode {
    public MassNode( final ChamberPool pool, final Property<ObservableList<Mass>> masses, final Mass mass, final ModelViewTransform transform ) {
        final PhetPPath shapeNode = new PhetPPath( transform.modelToView( mass.shape ), Color.gray, new BasicStroke( 2 ), Color.black );
        addChild( shapeNode );

        addChild( new PhetPText( format( MASS_LABEL_PATTERN, new DecimalFormat( "0" ).format( mass.mass ) ), new PhetFont( 13, true ), Color.white ) {{
            centerBoundsOnPoint( shapeNode.getCenterX(), shapeNode.getCenterY() );
        }} );
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
                            boolean intersected = false;
                            final Shape dottedLineShape = getDottedLineShape( pool, m );
                            if ( m.shape.intersects( dottedLineShape.getBounds2D() ) ) {
                                m = m.withMinY( dottedLineShape.getBounds2D().getMinY() ).withCenterX( dottedLineShape.getBounds2D().getCenterX() );
                                intersected = true;
                            }
                            else if ( m.getMinY() < 0 ) {
                                m = m.withMinY( 0.0 );
                            }

                            //if mass is over an opening, move it back to its initial position
                            if ( pool.isOverOpening( m ) && !intersected ) {
                                m = m.withShape( m.initialShape );
                            }
                        }

                        return m.withDragging( false );
                    }
                } ) );
            }
        } );
    }
}