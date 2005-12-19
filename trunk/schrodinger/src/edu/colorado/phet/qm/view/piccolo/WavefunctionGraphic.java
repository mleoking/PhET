/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.expectations.PxValue;
import edu.colorado.phet.qm.model.expectations.XValue;
import edu.colorado.phet.qm.model.expectations.YValue;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 12:48:33 PM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class WavefunctionGraphic extends SimpleWavefunctionGraphic {
    private boolean displayXExpectation;
    private boolean displayYExpectation;
    private boolean displayCollapsePoint;
    private boolean displayPyExpectation = false;
    private DiscreteModel discreteModel;

    public WavefunctionGraphic( DiscreteModel discreteModel, Wavefunction wavefunction ) {
        super( wavefunction );
        this.discreteModel = discreteModel;
    }

    public void setDisplayXExpectation( boolean displayXExpectation ) {
        this.displayXExpectation = displayXExpectation;
    }

    public void setDisplayYExpectation( boolean displayYExpectation ) {
        this.displayYExpectation = displayYExpectation;
    }

    public void setDisplayCollapsePoint( boolean displayCollapsePoint ) {
        this.displayCollapsePoint = displayCollapsePoint;
    }

    protected void decorateBuffer() {
        BufferedImage image = getColorGridNode().getBufferedImage();
        Graphics2D g2 = image.createGraphics();

        if( displayXExpectation ) {
            double xFractional = new XValue().compute( getWavefunction() );
            int x = (int)( xFractional * getCellWidth() * getWavefunction().getWidth() );
            g2.setColor( Color.blue );
            g2.fillRect( (int)x, 0, 2, image.getHeight() );
        }
        if( displayYExpectation ) {
            double yFractional = new YValue().compute( getWavefunction() );
            int y = (int)( yFractional * getCellHeight() * getWavefunction().getHeight() );
            g2.setColor( Color.blue );
            g2.fillRect( 0, (int)y, image.getWidth(), 2 );
        }
//        if( displayCollapsePoint ) {
//            Point collapsePoint = getDiscreteModel().getCollapsePoint();
//            Rectangle rect = colorGrid.getRectangle( collapsePoint.x, collapsePoint.y );
//            g2.setColor( Color.green );
//            g2.fillOval( rect.x, rect.y, rect.width, rect.height );
//        }
        if( displayPyExpectation ) {
            double px = new PxValue().compute( getWavefunction() );
            System.out.println( "px = " + px );
        }
    }

    private int getCellHeight() {
        return getColorGridNode().getCellHeight();
    }

    private int getCellWidth() {
        return getColorGridNode().getCellWidth();
    }

//    public void setPhoton( Photon photon ) {
//        magnitudeColorMap.setPhoton( photon );
//        realColorMap.setPhoton( photon );
//        imagColorMap.setPhoton( photon );
//    }

    public int getWavefunctionGraphicWidth() {
        return getColorGrid().getWidth();
    }

    static class ColorMapWithPotential implements ColorMap {
        private ColorMap delegate;
        private Potential potential;

        public ColorMapWithPotential( ColorMap delegate, Potential potential ) {
            this.delegate = delegate;
            this.potential = potential;
        }

        public Paint getColor( int i, int k ) {
            if( potential.getPotential( i, k, 0 ) != 0 ) {
                return Color.red;
            }
            else {
                return delegate.getColor( i, k );
            }
        }
    }

    public void setColorMap( ColorMap colorMap ) {
        super.setColorMap( new ColorMapWithPotential( colorMap, discreteModel.getPotential() ) );
    }
}
