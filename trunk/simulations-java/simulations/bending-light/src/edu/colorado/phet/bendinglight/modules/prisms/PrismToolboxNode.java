// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.bendinglight.modules.prisms.PrismsModel.getPrismPrototypes;

/**
 * Prism toolbox which contains draggable prisms as well as the control panel for their index of refraction.
 *
 * @author Sam Reid
 */
public class PrismToolboxNode extends PNode {
    public PrismToolboxNode( final PrismsCanvas canvas, final ModelViewTransform transform, final PrismsModel model ) {
        //Create and add Title label for the prism toolbox
        final PText titleLabel = new PText( BendingLightStrings.PRISMS ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );

        //Iterate over the prism prototypes in the model and create a draggable icon for each one
        final double[] x = { 0 };
        for ( final Prism prism : getPrismPrototypes() ) {
            //Create a node for the prism, just used to create the thumbnail
            PrismNode prismNode = new PrismNode( transform, prism, model.prismMedium );
            final int thumbnailHeight = 70;
            Image image = prismNode.toImage( (int) ( prismNode.getFullBounds().getWidth() * thumbnailHeight / prismNode.getFullBounds().getHeight() ), thumbnailHeight, null );
            final PImage thumbnail = new PImage( image ) {{
                final PImage thumbnailRef = this;
                setOffset( x[0], titleLabel.getFullBounds().getMaxY() );
                x[0] = x[0] + getFullBounds().getWidth() + 10; //Space out the prism thumbnails in the toolbox

                //Add user interaction
                addInputEventListener( new PBasicInputEventHandler() {
                    PrismNode createdNode = null;//Last created node that events should be forwarded to
                    boolean intersect = false;//Ready to drop the prism back in the toolbox

                    //When the mouse is pressed, create a new prism model and node
                    @Override public void mousePressed( PInputEvent event ) {
                        if ( createdNode == null ) {
                            //Create a new prism model, and add it to the model
                            final Point2D positionRelativeTo = event.getPositionRelativeTo( getParent().getParent().getParent() );//why?
                            Point2D modelPoint = transform.viewToModel( positionRelativeTo );
                            final Prism prismCopy = prism.copy();
                            final Rectangle2D bounds = prismCopy.getBounds();
                            Point2D copyCenter = new Point2D.Double( bounds.getX(), bounds.getY() );
                            prismCopy.translate( modelPoint.getX() - copyCenter.getX() - bounds.getWidth() / 2, modelPoint.getY() - copyCenter.getY() - bounds.getHeight() / 2 );
                            model.addPrism( prismCopy );

                            //there is no callback for node creation here, so we create the node ourselves.
                            //In a normal MVC scheme though, addPrism would create the node, and that would cause problems for this technique

                            //Create the node and keep a reference to it so it can be removed later
                            final PrismNode closureNode = new PrismNode( transform, prismCopy, model.prismMedium );
                            this.createdNode = closureNode;

                            //Check to see if the prism can be dropped back in the toolbox
                            final PropertyChangeListener pcl = new PropertyChangeListener() {
                                public void propertyChange( PropertyChangeEvent evt ) {
                                    intersect = PrismToolboxNode.this.getGlobalFullBounds().contains( closureNode.getGlobalFullBounds().getCenter2D() );
                                }
                            };
                            closureNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                            closureNode.addInputEventListener( new PBasicInputEventHandler() {
                                //When the mouse is released, put the prism back in the toolbox if it was over the toolbox
                                public void mouseReleased( PInputEvent event ) {
                                    if ( intersect ) {
                                        thumbnailRef.setVisible( true );//make sure it can be grabbed again
                                        closureNode.removePropertyChangeListener( pcl );//prevent memory leaks
                                        canvas.removePrismNode( closureNode );//remove the node from the view
                                        createdNode = null;//Signal that a new node needs to be created next time
                                        model.removePrism( prismCopy );//remove the prism model from the model
                                    }
                                }
                            } );

                            canvas.addPrismNode( createdNode );
                        }
                    }

                    //Forward drag events to the created prism node
                    public void mouseDragged( PInputEvent event ) {
                        createdNode.prism.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                    }

                    //Signify that another node should be created when dragged out of the toolbox
                    public void mouseReleased( PInputEvent event ) {
                        createdNode = null;
                    }
                } );
                addInputEventListener( new CursorHandler() );
            }};
            addChild( thumbnail );
        }

        //Allow the user to control the type of material in the prisms
        addChild( new MediumControlPanel( canvas, model.prismMedium, BendingLightStrings.OBJECTS, false, model.wavelengthProperty, "0.0000000", 8 ) {{setOffset( x[0], 0 );}} );
    }
}