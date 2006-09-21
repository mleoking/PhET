/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.SlitPotentialGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideViewFull;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 10:26:56 AM
 * Copyright (c) May 18, 2006 by Sam Reid
 */

public class BarrierSideView extends SlitPotentialGraphic {
    private WaveSideViewFull waveSideView;
    private double fractionalBarrierSize = 3;

    public BarrierSideView( final SlitPotential slitPotential, final LatticeScreenCoordinates latticeScreenCoordinates, WaveSideViewFull waveSideView ) {
        super( slitPotential, latticeScreenCoordinates );
        this.waveSideView = waveSideView;
        waveSideView.addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        update();
    }

    public void update() {
        if( waveSideView == null ) {
            return;
        }
        setVisible( waveSideView.getVisible() );
        setPickable( getVisible() );
        setChildrenPickable( getVisible() );

        removeAllChildren();
        Rectangle[]r = getSlitPotential().getBarrierRectangles();
        for( int i = 0; i < r.length; i++ ) {
            Rectangle rectangle = r[i];
            if( !rectangle.isEmpty() ) {
                Rectangle2D screenRect = getLatticeScreenCoordinates().toScreenRect( rectangle );
                screenRect = new Rectangle2D.Double( screenRect.getX(), getBarrierY(), screenRect.getWidth(), getBarrierHeight() );
                addChild( super.toShape( screenRect ) );
                break;
            }
        }
    }

    private double getBarrierHeight() {
        return getLatticeScreenCoordinates().getScreenRect().getHeight() / fractionalBarrierSize;
    }

    private double getBarrierY() {
        return getLatticeScreenCoordinates().getScreenRect().getY() + getLatticeScreenCoordinates().getScreenRect().getHeight() / 2 - getBarrierHeight() / 2;
    }
}
