/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.nuclearphysics.controller.MultipleNucleusFissionModule;
import edu.colorado.phet.nuclearphysics.controller.NuclearPhysicsModule;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ExplodingContainmentGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExplodingContainmentGraphic {

    private static Random random = new Random();
    private boolean kaboomed;
    private List kaboomGraphics = new ArrayList();
//    private JLabel labelMessage;
//    private PhetShapeGraphic backgroundGraphic;
//    private double blackBacgroundLayer = Double.MAX_VALUE - 2;
    private double tileLayer = Double.MAX_VALUE;
    private ApparatusPanel apparatusPanel;


    public ExplodingContainmentGraphic( MultipleNucleusFissionModule module, ContainmentGraphic containmentGraphic ) {

        apparatusPanel = module.getPhysicalPanel();

        // Unless the module has an apparatus panel at this time things won't work right
        if( module.getApparatusPanel() == null ) {
            throw new RuntimeException( "Module doesn't have an apparatus panel" );
        }

        // Draw a buffered image of the source ContainmentGraphic
        BufferedImage snapshot = new BufferedImage( containmentGraphic.getWidth() * 2,
                                                    containmentGraphic.getHeight() * 2,
                                                    BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D)snapshot.getGraphics();
        containmentGraphic.paint( g2 );


        Rectangle2D bounds = new Rectangle2D.Double( 0, 0, snapshot.getWidth(), snapshot.getHeight() );

        int numCols = 4;
        int tileWidth = (int)( bounds.getWidth() / numCols );
        int tileHeight = tileWidth;
        for( double x = bounds.getMinX(); x < bounds.getMaxX() - 1; x += tileWidth ) {
            for( double y = bounds.getMinY(); y < bounds.getMaxY() - 1; y += tileHeight ) {
                x = Math.min( x, bounds.getMaxX() - tileWidth - 1 );
                y = Math.min( y, bounds.getMaxY() - tileHeight - 1 );
                BufferedImage tile = snapshot.getSubimage( (int)x, (int)y, tileWidth, tileHeight );

                double spin = random.nextDouble() * 10 * ( random.nextBoolean() ? 1 : -1 );
                double zoom = 0;
                double flipFactorX = random.nextDouble();
                while( flipFactorX > 0.2 ) {
                    flipFactorX = random.nextDouble();
                }
                double flipFactorY = random.nextDouble();
                while( flipFactorY > 0.2 ) {
                    flipFactorY = random.nextDouble();
                }
                while( zoom < .97 || zoom > .99 ) {
                    zoom = random.nextDouble();
                }
                double txX = ( ( x + tile.getWidth() / 2 ) - snapshot.getWidth() / 2 ) * 0.03;
                double txY = ( ( y + tile.getHeight() / 2 ) - snapshot.getHeight() / 2 ) * 0.03;
                PhetGraphic graphic = new KaboomGraphic(                         module,
                                                                                 tile, new Point( (int)x + snapshot.getWidth() / 2,
                                                                                                  (int)y + snapshot.getHeight() / 2 ),
                                                                                 zoom, spin, 0, 0, flipFactorX, flipFactorY, txX, txY );
                apparatusPanel.addGraphic( graphic, tileLayer );
                graphic.setLocation( (int)x, (int)y );
                // We need to do this because otherwise the mouse never gets through after a reset. This is
                // obviously due to a bug somewhere else!!!!!
                graphic.setIgnoreMouse( true);
                kaboomGraphics.add( graphic );
            }
        }
        apparatusPanel.revalidate();
    }

    public void clearGraphics() {
        for( int i = 0; i < kaboomGraphics.size(); i++ ) {
            KaboomGraphic graphic = (KaboomGraphic)kaboomGraphics.get( i );
            apparatusPanel.removeGraphic( graphic );
            graphic.clear();
        }
        apparatusPanel.revalidate();
        apparatusPanel.repaint();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    class KaboomGraphic extends PhetImageGraphic {
        private AffineTransformOp atxOp;
        private double zoom;
        private double txX;
        private double dx;
        private double txY;
        private double dy;
        private double spin;
        private double dSpin;
        private NuclearPhysicsModule module;
        BufferedImage image;
        private double alpha = 1.0;
        private Stepper stepper;

        public KaboomGraphic( NuclearPhysicsModule module,
                              BufferedImage bImg,
                              Point location,
                              double zoom,
                              double spin,
                              double d1,
                              double d2,
                              double flipFactorX,
                              double flipFactorY,
                              final double txX,
                              final double txY ) {

            super( module.getApparatusPanel(), bImg );
            this.module = module;
            this.image = bImg;
            this.zoom = zoom;
            this.dx = txX;
            this.dy = txY;
            this.dSpin = spin / 500;

            setLocation( location );
            stepper = new Stepper();
            module.getClock().addClockListener( stepper );
        }

        public void paint( Graphics2D g2 ) {
            GraphicsState gs = new GraphicsState( g2 );
            GraphicsUtil.setAlpha( g2, alpha );
            g2.translate( txX, txY );
            g2.rotate( spin, getLocation().getX() + txX, getLocation().getY() + txY );
            super.paint( g2 );
            gs.restoreGraphics();
        }


        void clear() {
            module.getClock().removeClockListener( stepper );
        }

        private class Stepper extends ClockAdapter {

            public void clockTicked( ClockEvent clockEvent ) {
                spin += dSpin;
                txX += dx;
                txY += dy;
                alpha = Math.max( 0, alpha - 0.03 );
                setBoundsDirty();
                repaint();
            }
        }
    }
}

