// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * @author Sam Reid
 */
public class Tool extends PNode {

    public static interface NodeFactory {
        ToolNode createNode( ModelViewTransform transform, Property<Boolean> visible, Point2D location );
    }

    public Tool( final Image thumbnail,
                 final Property<Boolean> showTool,
                 final ModelViewTransform transform,
                 final BendingLightCanvas canvas,
                 final NodeFactory nodeMaker,
                 final ResetModel resetModel,
                 final Function0<Rectangle2D> getGlobalDropTargetBounds ) {
        final PImage thumbnailIcon = new PImage( thumbnail ) {{
            showTool.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !showTool.getValue() );
                }
            } );
            final PImage thumbRef = this;
            addInputEventListener( new PBasicInputEventHandler() {
                {
                    resetModel.addResetListener( new VoidFunction0() {
                        public void apply() {
                            reset();
                        }
                    } );
                }

                ToolNode node = null;
                boolean intersect = false;

                @Override
                public void mousePressed( PInputEvent event ) {
                    showTool.setValue( true );
                    setVisible( false );
                    if ( node == null ) {
                        node = nodeMaker.createNode( transform, showTool, transform.viewToModel( event.getPositionRelativeTo( canvas.getRootNode() ) ) );
                        final PropertyChangeListener boundChangeListener = new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                boolean t = false;//TODO: fold left
                                for ( PNode child : node.getDroppableComponents() ) {
                                    PBounds bound = child.getGlobalFullBounds();
                                    if ( getGlobalDropTargetBounds.apply().contains( bound.getCenterX(), bound.getCenterY() ) ) {
                                        t = true;
                                    }
                                }
                                intersect = t;
                            }
                        };
                        node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, boundChangeListener );
                        node.addInputEventListener( new PBasicInputEventHandler() {
                            public void mouseReleased( PInputEvent event ) {
                                if ( intersect ) {
                                    showTool.setValue( false );
                                    thumbRef.setVisible( true );
                                    if ( node != null ) {
                                        node.removePropertyChangeListener( boundChangeListener );
                                    }
                                    reset();
                                }
                            }
                        } );
                        Tool.this.addChild( canvas, node );
                    }
                }

                public void mouseDragged( PInputEvent event ) {
                    node.dragAll( event.getDeltaRelativeTo( node.getParent() ) );
                }

                //This is when the user drags the object out of the toolbox then drops it right back in the toolbox.
                public void mouseReleased( PInputEvent event ) {
                    if ( intersect ) {
                        showTool.setValue( false );
                        thumbRef.setVisible( true );
                        reset();
                        //TODO: how to remove pcl?
                    }
                }

                private void reset() {
                    canvas.removeChild( node );
                    node = null;
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};

        addChild( thumbnailIcon );
    }

    protected void addChild( BendingLightCanvas canvas, ToolNode node ) {
        canvas.addChild( node );
    }
}