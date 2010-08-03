package edu.colorado.phet.common.motion.charts;

import edu.colorado.phet.common.motion.MotionResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.AbstractMediaButton;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sam Reid
 */
public class ChartZoomControlNode extends PNode {
    private VerticalZoomControl verticalZoomControl;
    private HorizontalZoomControl horizontalZoomControl;

    public ChartZoomControlNode(final TemporalChart chart) {
        horizontalZoomControl = new HorizontalZoomControl(chart);
        addChild(horizontalZoomControl);

        verticalZoomControl = new VerticalZoomControl(chart);
        addChild(verticalZoomControl);

//        verticalZoomControl.setOffset(0, horizontalZoomControl.getFullBounds().getMaxY());

        final SimpleObserver updateHorizontalZoomButtonLocation = new SimpleObserver() {
            public void update() {
                horizontalZoomControl.setOffset(0, chart.getViewDimension().getHeight() - horizontalZoomControl.getFullBounds().getHeight());
            }
        };
        chart.getViewDimension().addObserver(updateHorizontalZoomButtonLocation);
        updateHorizontalZoomButtonLocation.update();
    }

    public double getZoomControlWidth() {
        return horizontalZoomControl.getFullBounds().getWidth() + verticalZoomControl.getFullBounds().getWidth();
    }

    public void setHorizontalZoomButtonsVisible(boolean visible) {
        horizontalZoomControl.setVisible(visible);
        horizontalZoomControl.setPickable(visible);
        horizontalZoomControl.setChildrenPickable(visible);
    }

    public static interface Listener {
        void actionPerformed();
    }

    private static class ZoomButton extends PNode {
        protected AbstractMediaButton button;

        private ZoomButton(final String imageName, final Listener zoomListener) {
            button = new AbstractMediaButton(30) {
                protected BufferedImage createImage() {
                    try {
                        return BufferedImageUtils.multiScaleToHeight(MotionResources.loadBufferedImage(imageName), 30);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            };

            // this handler ensures that the button won't fire unless the mouse is released while inside the button
            //see DefaultIconButton
            ButtonEventHandler verticalZoomInListener = new ButtonEventHandler();
            button.addInputEventListener(verticalZoomInListener);
            verticalZoomInListener.addButtonEventListener(new ButtonEventHandler.ButtonEventAdapter() {
                public void fire() {
                    if (button.isEnabled())
                        zoomListener.actionPerformed();
                }
            });

            addChild(button);
        }

        public void setEnabled(boolean enabled) {
            button.setEnabled(enabled);
        }
    }

    private static class VerticalZoomControl extends PNode {
        private VerticalZoomControl(final TemporalChart chart) {
            final ZoomButton zoomInButton = new ZoomButton("magnify-plus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomInVertical();
                }
            });
            final ZoomButton zoomOutButton = new ZoomButton("magnify-minus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomOutVertical();
                }
            });
            addChild(zoomOutButton);
            chart.getDataModelBounds().addObserver(new SimpleObserver() {
                public void update() {
                    zoomInButton.setEnabled(chart.getDataModelBounds().getHeight() > chart.getMinimumRangeRange() + 1E-6);
                    zoomOutButton.setEnabled(chart.getDataModelBounds().getHeight() < chart.getMaximumRangeRange() - 1E-6);//20000 so that it goes -10000 to 10000
                    //TODO: cursor hand never goes away
                }
            });
            addChild(zoomInButton);


            //Add small icons to indicate zoom dimension (i.e. horizontal or vertical)
            PNode upIcon = new TriangleIcon();
            {
                upIcon.setOffset(1.5, zoomInButton.getFullBounds().getHeight()-upIcon.getFullBounds().getHeight()-1.5/2.0);
                upIcon.rotateInPlace(Math.PI * 3 / 2.0);//point up
            }
            addChild(upIcon);

            PNode downIcon = new TriangleIcon();
            {
                downIcon.setOffset(1.5, downIcon.getFullBounds().getHeight() + 1.5+zoomInButton.getFullBounds().getHeight()-upIcon.getFullBounds().getHeight()-1.5/2.0);
                downIcon.rotateInPlace(Math.PI / 2);//Point down
            }
            addChild(downIcon);

            zoomInButton.setOffset(downIcon.getFullBounds().getWidth() + 1.5 * 2, 0);
            zoomOutButton.setOffset(downIcon.getFullBounds().getWidth() + 1.5 * 2, zoomInButton.getFullBounds().getHeight());
        }
    }

    private static class HorizontalZoomControl extends PNode {

        private HorizontalZoomControl(final TemporalChart chart) {
            final ZoomButton zoomInButton = new ZoomButton("magnify-plus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomInHorizontal();
                }
            });
            addChild(zoomInButton);

            final ZoomButton zoomOutButton = new ZoomButton("magnify-minus.png", new Listener() {
                public void actionPerformed() {
                    chart.zoomOutHorizontal();
                }
            });
            addChild(zoomOutButton);
            SimpleObserver updateButtonsEnabled = new SimpleObserver() {
                public void update() {
                    zoomInButton.setEnabled(chart.getDataModelBounds().getWidth() > chart.getMinimumDomainRange() + 1E-6);
                    zoomOutButton.setEnabled(chart.getDataModelBounds().getWidth() < chart.getMaximumDomainRange() - 1E-6);
                    //TODO: cursor hand never goes away
                }
            };
            chart.getDataModelBounds().addObserver(updateButtonsEnabled);
            updateButtonsEnabled.update();

            zoomOutButton.setOffset(0, 5);
            zoomInButton.setOffset(zoomOutButton.getFullBounds().getMaxX(), 5);

            //Add small icons to indicate zoom dimension (i.e. horizontal or vertical)
            PNode pathLeft = new TriangleIcon();
            {
                pathLeft.setOffset(zoomOutButton.getFullBounds().getMaxX() - pathLeft.getFullBounds().getWidth() - 0.5, 0);
                pathLeft.rotateInPlace(Math.PI);//Point Left
            }
            addChild(pathLeft);

            PNode pathRight = new TriangleIcon();
            {
                pathRight.setOffset(pathLeft.getFullBounds().getMaxX() + 1.5, 0);
            }
            addChild(pathRight);
        }
    }

    public static class TriangleIcon extends PNode {
        private double triangleHeight = 6;
        private double triangleWidth = 6;

        public TriangleIcon() {
            DoubleGeneralPath trianglePath = new DoubleGeneralPath(triangleWidth, triangleHeight / 2);
            trianglePath.lineToRelative(-triangleWidth, -triangleHeight / 2);
            trianglePath.lineToRelative(0, triangleHeight);
            trianglePath.lineTo(triangleWidth, triangleHeight / 2);
            GeneralPath path1 = trianglePath.getGeneralPath();
            addChild(new PhetPPath(path1, Color.black));
        }
    }
}