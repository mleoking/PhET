/*
 * Copyright (c) 2008, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.umd.cs.piccolox.nodes;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * An extension to PCamera that provides a fast image based
 * animationToCenterBounds method
 * 
 * @author Lance Good
 */
public class PCacheCamera extends PCamera {

    private BufferedImage paintBuffer;
    private boolean imageAnimate;
    private PBounds imageAnimateBounds;

    /**
     * Get the buffer used to provide fast image based animation
     */
    protected BufferedImage getPaintBuffer() {
        PBounds fRef = getFullBoundsReference();
        // TODO eclipse formatting made this ugly
        if (paintBuffer == null || paintBuffer.getWidth() < fRef.getWidth()
                || paintBuffer.getHeight() < fRef.getHeight()) {
            paintBuffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration().createCompatibleImage((int) Math.ceil(fRef.getWidth()),
                            (int) Math.ceil(fRef.getHeight()));
        }
        return paintBuffer;
    }

    /**
     * Caches the information necessary to animate from the current view bounds
     * to the specified centerBounds
     */
    private AffineTransform cacheViewBounds(Rectangle2D centerBounds, boolean scaleToFit) {
        PBounds viewBounds = getViewBounds();

        // Initialize the image to the union of the current and destination
        // bounds
        PBounds imageBounds = new PBounds(viewBounds);
        imageBounds.add(centerBounds);

        animateViewToCenterBounds(imageBounds, scaleToFit, 0);

        imageAnimateBounds = getViewBounds();

        // Now create the actual cache image that we will use to animate fast

        BufferedImage buffer = getPaintBuffer();
        Paint fPaint = Color.white;
        if (getPaint() != null) {
            fPaint = getPaint();
        }
        toImage(buffer, fPaint);

        // Do this after the painting above!
        imageAnimate = true;

        // Return the bounds to the previous viewbounds
        animateViewToCenterBounds(viewBounds, scaleToFit, 0);

        // The code below is just copied from animateViewToCenterBounds to
        // create the correct transform to center the specified bounds

        PDimension delta = viewBounds.deltaRequiredToCenter(centerBounds);
        PAffineTransform newTransform = getViewTransform();
        newTransform.translate(delta.width, delta.height);

        if (scaleToFit) {
            double s = Math.min(viewBounds.getWidth() / centerBounds.getWidth(), viewBounds.getHeight()
                    / centerBounds.getHeight());
            newTransform.scaleAboutPoint(s, centerBounds.getCenterX(), centerBounds.getCenterY());
        }

        return newTransform;
    }

    /**
     * Turns off the fast image animation and does any other applicable cleanup
     */
    private void clearViewCache() {
        imageAnimate = false;
        imageAnimateBounds = null;
    }

    /**
     * Mimics the standard animateViewToCenterBounds but uses a cached image for
     * performance rather than re-rendering the scene at each step
     */
    public PTransformActivity animateStaticViewToCenterBoundsFast(Rectangle2D centerBounds, boolean shouldScaleToFit,
            long duration) {
        if (duration == 0) {
            return animateViewToCenterBounds(centerBounds, shouldScaleToFit, duration);
        }

        AffineTransform newViewTransform = cacheViewBounds(centerBounds, shouldScaleToFit);

        return animateStaticViewToTransformFast(newViewTransform, duration);
    }

    /**
     * This copies the behavior of the standard animateViewToTransform but
     * clears the cache when it is done
     */
    protected PTransformActivity animateStaticViewToTransformFast(AffineTransform destination, long duration) {
        if (duration == 0) {
            setViewTransform(destination);
            return null;
        }

        PTransformActivity.Target t = new PTransformActivity.Target() {
            public void setTransform(AffineTransform aTransform) {
                PCacheCamera.this.setViewTransform(aTransform);
            }

            public void getSourceMatrix(double[] aSource) {
                getViewTransformReference().getMatrix(aSource);
            }
        };

        PTransformActivity ta = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, destination) {
            protected void activityFinished() {
                clearViewCache();
                repaint();
                super.activityFinished();
            }
        };

        PRoot r = getRoot();
        if (r != null) {
            r.getActivityScheduler().addActivity(ta);
        }

        return ta;
    }

    /**
     * Overrides the camera's full paint method to do the fast rendering when
     * possible
     */
    public void fullPaint(PPaintContext paintContext) {
        if (imageAnimate) {
            PBounds fRef = getFullBoundsReference();
            PBounds viewBounds = getViewBounds();
            double scale = getFullBoundsReference().getWidth() / imageAnimateBounds.getWidth();
            double xOffset = (viewBounds.getX() - imageAnimateBounds.getX()) * scale;
            double yOffset = (viewBounds.getY() - imageAnimateBounds.getY()) * scale;
            double scaleW = viewBounds.getWidth() * scale;
            double scaleH = viewBounds.getHeight() * scale;
            paintContext.getGraphics().drawImage(paintBuffer, 0, 0, (int) Math.ceil(fRef.getWidth()),
                    (int) Math.ceil(fRef.getHeight()), (int) Math.floor(xOffset), (int) Math.floor(yOffset),
                    (int) Math.ceil(xOffset + scaleW), (int) Math.ceil(yOffset + scaleH), null);
        }
        else {
            super.fullPaint(paintContext);
        }
    }
}
