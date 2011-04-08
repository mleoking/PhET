// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Prism toolbox which contains draggable prisms as well as the control panel for their index of refraction.
 *
 * @author Sam Reid
 */
public class PrismToolboxNode extends PNode {
    public PrismToolboxNode( final PrismsCanvas canvas, final ModelViewTransform transform, final PrismsModel model ) {
        final PText titleLabel = new PText( "Prisms" ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );

        final double[] x = { 0 };
        for ( final Prism prism : PrismsModel.getPrismPrototypes() ) {
            PrismNode prismNode = new PrismNode( transform, prism, model.prismMedium );
            final int thumbnailHeight = 70;
            Image image = prismNode.toImage( (int) ( prismNode.getFullBounds().getWidth() * thumbnailHeight / prismNode.getFullBounds().getHeight() ), thumbnailHeight, null );
            final PImage thumbnail = new PImage( image ) {{
                final PImage thumbnailRef = this;
                setOffset( x[0], titleLabel.getFullBounds().getMaxY() );
                x[0] = x[0] + getFullBounds().getWidth() + 10;
                addInputEventListener( new PBasicInputEventHandler() {
                    PrismNode createdNode = null;//Last created node that events should be forwarded to
                    boolean intersect = false;

                    @Override
                    public void mousePressed( PInputEvent event ) {
                        if ( createdNode == null ) {
                            final Point2D positionRelativeTo = event.getPositionRelativeTo( getParent().getParent().getParent() );//why?
                            Point2D modelPoint = transform.viewToModel( positionRelativeTo );
                            final Prism copy = prism.copy();
                            final Rectangle2D bounds = copy.getBounds();
                            Point2D copyCenter = new Point2D.Double( bounds.getX(), bounds.getY() );
                            copy.translate( modelPoint.getX() - copyCenter.getX() - bounds.getWidth() / 2, modelPoint.getY() - copyCenter.getY() - bounds.getHeight() / 2 );
                            model.addPrism( copy );
                            //there is no callback for node creation here, so we create the node ourselves.
                            //In a normal MVC scheme though, addPrism would create the node, and that would cause problems for this technique
                            final PrismNode boundNode = new PrismNode( transform, copy, model.prismMedium );
                            this.createdNode = boundNode;
                            final PropertyChangeListener pcl = new PropertyChangeListener() {
                                public void propertyChange( PropertyChangeEvent evt ) {
                                    intersect = PrismToolboxNode.this.getGlobalFullBounds().contains( boundNode.getGlobalFullBounds().getCenter2D() );
                                }
                            };
                            boundNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                            boundNode.addInputEventListener( new PBasicInputEventHandler() {
                                public void mouseReleased( PInputEvent event ) {
                                    if ( intersect ) {
                                        thumbnailRef.setVisible( true );
                                        boundNode.removePropertyChangeListener( pcl );
                                        canvas.removePrismNode( boundNode );
                                        createdNode = null;
                                        model.removePrism( copy );
                                    }
                                }
                            } );

                            canvas.addPrismNode( createdNode );
                        }
                    }

                    public void mouseDragged( PInputEvent event ) {
                        createdNode.prism.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                    }

                    public void mouseReleased( PInputEvent event ) {
                        createdNode = null;
                    }
                } );
                addInputEventListener( new CursorHandler() );
            }};
            addChild( thumbnail );
        }
        addChild( new MediumControlPanel( canvas, model.prismMedium, "Objects:", false, model.wavelengthProperty, "0.0000000", 8 ) {{setOffset( x[0], 0 );}} );
    }
}