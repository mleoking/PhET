package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fitness.FitnessResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Apr 17, 2008 at 11:52:39 AM
 */
public class IconStrip extends PNode {
    private double height = 30;

    public IconStrip( IconItem[] iconItems ) {
        final CaloriePanel.BoxedImage[] im = new CaloriePanel.BoxedImage[iconItems.length];
        for ( int i = 0; i < iconItems.length; i++ ) {
            final IconItem iconItem = iconItems[i];
            im[i] = createNode( iconItem, i, im );
            final int i1 = i;
            im[i].addInputEventListener( new PDragEventHandler() {
                CaloriePanel.BoxedImage createdNode = null;

                protected void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    createdNode = createNode( iconItem, i1, im );
                    createdNode.addInputEventListener( new PDragEventHandler() {
                        protected void startDrag( PInputEvent event ) {
                            super.startDrag( event );
                            createdNode.moveToFront();
                        }
                    } );
                    createdNode.setDrawBorder( false );
                    IconStrip.this.addChild( createdNode );
                    createdNode.moveToFront();
                }

                protected void drag( PInputEvent event ) {
                    PDimension d = event.getDeltaRelativeTo( im[i1] );
                    createdNode.localToParent( d );
                    createdNode.offset( d.getWidth(), d.getHeight() );
                }
            } );
        }
    }

    protected CaloriePanel.BoxedImage createNode( IconItem foodItem, int index, CaloriePanel.BoxedImage[] others ) {
        BufferedImage image = FitnessResources.getImage( foodItem.getImageName() );
        double sy = height / image.getHeight();
        int width = (int) ( sy * image.getWidth() );
        CaloriePanel.BoxedImage imx = new CaloriePanel.BoxedImage( BufferedImageUtils.getScaledInstance( image, width, (int) height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true ) );

//            imx.scale( height / imx.getFullBounds().getHeight() );
        addChild( imx );
        imx.addInputEventListener( new CursorHandler() );
        if ( index > 0 ) {
            imx.setOffset( others[index - 1].getFullBounds().getMaxX(), others[index - 1].getFullBounds().getY() );
        }

        return imx;
    }

    public static class IconItem {
        private String image;

        public IconItem( String image ) {
            this.image = image;
        }

        public String getImageName() {
            return image;
        }
    }
}
