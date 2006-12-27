/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.Ramp;
import edu.colorado.phet.theramp.model.RampModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:01:59 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampPanel extends ApparatusPanel2 {
    private RampModule module;
    private RampGraphic rampGraphic;
    private BlockGraphic blockGraphic;
    private BarGraphSet barGraphSet;
    private RampLookAndFeel rampLookAndFeel;
    private AbstractArrowSet abstractArrowSet;
    private AbstractArrowSet cartesian;
    private AbstractArrowSet perp;
    private AbstractArrowSet parallel;
    private XArrowSet xArrowSet;
    private YArrowSet yArrowSet;
    private ArrayList arrowSets = new ArrayList();

    private AnimationStep animationStep;
    private Timer animationTimer;
    private ActionListener animator;

    public static interface AnimationStep {
        boolean step();
    }

    public static class Moving implements AnimationStep {
        private RampPanel rampPanel;
        private int dx;
        private int dy;

        public Moving( RampPanel rampPanel, int dx, int dy ) {
            this.rampPanel = rampPanel;
            this.dx = dx;
            this.dy = dy;
        }

        public boolean step() {
            Point2D vpo = rampPanel.getViewPortOrigin();
            rampPanel.setViewPortOrigin( vpo.getX() + dx, vpo.getY() + dy );
            return true;
        }
    }

    public static class Zooming implements AnimationStep {
        private RampPanel rampPanel;
        private double scaleZ;

        public Zooming( RampPanel rampPanel, double scaleZ ) {
            this.rampPanel = rampPanel;
            this.scaleZ = scaleZ;
        }

        public boolean step() {
//            Rectangle2D vp = getViewPort();

            double scale = rampPanel.getScale();
            double newScale = scale * scaleZ;
//            Point2D viewPortOrigin = rampPanel.getViewPortOrigin();
//            Rectangle ref = rampPanel.getReferenceBounds();
//            Rectangle2D viewRect = new Rectangle2D.Double( viewPortOrigin.getX(), viewPortOrigin.getY(), ref.getWidth(), ref.getHeight() );
//            Rectangle2D expanded = RectangleUtils.expandRectangle2D( vp, scaleZ * vp.getWidth(), scaleZ * vp.getWidth() );
//            Rectangle2D expanded = RectangleUtils.expandRectangle2D( vp, 50, 50 );
//            setViewPort( expanded );

            rampPanel.setScale( newScale );
//            rampPanel.setViewPortOrigin( expanded.getX(), expanded.getY() );
            return false;
        }

//        public void setViewPort( Rectangle2D vp ) {
//            double scale = rampPanel.getWidth() / vp.getWidth();//should be a min for aspect ratio
//            System.out.println( "scale = " + scale );
//            rampPanel.setViewPortOrigin( vp.getX(), vp.getY() );
//            rampPanel.setScale( scale );
//        }
//
//        public Rectangle2D getViewPort() {
//            Point2D viewPortOrigin = rampPanel.getViewPortOrigin();
//            double scale = rampPanel.getScale();
//            Rectangle ref=rampPanel.getReferenceBounds();
//            return ref;
////            double w = ref.getWidth() * scale;//shouldn't this account for rendering size width?
////            double h = ref.getHeight() * scale;
////            Rectangle2D.Double vp = new Rectangle2D.Double( viewPortOrigin.getX(), viewPortOrigin.getY(), w, h );
////            return vp;
//        }
    }

    public RampPanel( RampModule module ) {
        super( module.getModel(), module.getClock() );
        rampLookAndFeel = new RampLookAndFeel();
        addGraphicsSetup( new BasicGraphicsSetup() );
        this.module = module;
        setBackground( new Color( 240, 200, 255 ) );
        RampModel rampModel = module.getRampModel();
        Ramp ramp = rampModel.getRamp();
        rampGraphic = new RampGraphic( this, ramp );
        addGraphic( rampGraphic );

        blockGraphic = new BlockGraphic( this, rampGraphic, rampModel.getBlock() );
        addGraphic( blockGraphic );

        barGraphSet = new BarGraphSet( this, rampModel );
        addGraphic( barGraphSet );

        cartesian = new CartesianArrowSet( this );
        perp = new PerpendicularArrowSet( this );
        parallel = new ParallelArrowSet( this );
        xArrowSet = new XArrowSet( this );
        yArrowSet = new YArrowSet( this );
        addArrowSet( cartesian );
        addArrowSet( perp );
        addArrowSet( parallel );
        addArrowSet( xArrowSet );
        addArrowSet( yArrowSet );

        perp.setVisible( false );
        parallel.setVisible( false );
        xArrowSet.setVisible( false );
        yArrowSet.setVisible( false );

        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
//                defaultArrowSetGraphic.updateGraphics();
                for( int i = 0; i < arrowSets.size(); i++ ) {
                    AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
                    arrowSet.updateGraphics();
                }
            }
        } );
        requestFocus();

        animator = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                if( animationStep != null ) {
                    boolean done = animationStep.step();
                    if( done ) {
                        animationStep = null;
                        animationTimer.stop();
                    }
                }
                else {
                    animationTimer.stop();
                }
            }
        };
        animationTimer = new Timer( 20, animator );
        animationTimer.setInitialDelay( 0 );
        animationTimer.setRepeats( true );
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                int scrollSpeed = 20;
                if( e.getKeyCode() == KeyEvent.VK_UP ) {
                    animationStep = new Moving( RampPanel.this, 0, scrollSpeed );
                    animationTimer.start();
                }
                if( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    animationStep = new Moving( RampPanel.this, 0, -scrollSpeed );
                    animationTimer.start();
                }
                if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    animationStep = new Moving( RampPanel.this, scrollSpeed, 0 );
                    animationTimer.start();
                }
                if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    animationStep = new Moving( RampPanel.this, -scrollSpeed, 0 );
                    animationTimer.start();
                }
                if( e.getKeyCode() == KeyEvent.VK_PAGE_UP ) {
                    animationStep = new Zooming( RampPanel.this, 1.1 );
                    animationTimer.start();
                }
                if( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN ) {
                    animationStep = new Zooming( RampPanel.this, 0.9 );
                    animationTimer.start();
                }
                if (e.getKeyCode()==KeyEvent.VK_CONTROL){
//                    Rectangle2D vp=new Zooming( RampPanel.this, 1).getViewPort();
//                    System.out.println( "vp = " + vp );
                }
            }

            public void keyReleased( KeyEvent e ) {
                animationTimer.stop();
                animationStep = null;
                if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    setReferenceSize( 1061, 871 );//my special little rendering size.
                    setViewPortOrigin( 0, 0 );
                }
                else if( e.getKeyCode() == KeyEvent.VK_1 ) {
                    setReferenceSize( 1000, 1000 );
                }
                else if( e.getKeyCode() == KeyEvent.VK_2 ) {
                    setReferenceSize( 2000, 2000 );
                }
                else if( e.getKeyCode() == KeyEvent.VK_0 ) {
                    setReferenceSize();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }

        } );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                System.out.println( "e = " + e );
                Rectangle b = getBounds();
                System.out.println( "b = " + b );
                setReferenceSize( 1061, 871 );//my special little rendering size.
                requestFocus();
            }
        } );
        removeComponentListener( resizeHandler );
//        setUseOffscreenBuffer( true );
        requestFocus();
    }

    private void addArrowSet( AbstractArrowSet arrowSet ) {
        addGraphic( arrowSet );
        arrowSets.add( arrowSet );
    }

    public RampModule getRampModule() {
        return module;
    }

    public RampLookAndFeel getLookAndFeel() {
        return rampLookAndFeel;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    public void setCartesianArrowsVisible( boolean selected ) {
        cartesian.setVisible( selected );
    }

    public void setParallelArrowsVisible( boolean selected ) {
        parallel.setVisible( selected );
    }

    public void setPerpendicularArrowsVisible( boolean selected ) {
        perp.setVisible( selected );
    }

    public void setXArrowsVisible( boolean selected ) {
        xArrowSet.setVisible( selected );
    }

    public void setYArrowsVisible( boolean selected ) {
        yArrowSet.setVisible( selected );
    }

    public boolean isCartesianVisible() {
        return cartesian.isVisible();
    }

    public boolean isParallelVisible() {
        return parallel.isVisible();
    }

    public boolean isPerpendicularVisible() {
        return perp.isVisible();
    }

    public boolean isXVisible() {
        return xArrowSet.isVisible();
    }

    public boolean isYVisible() {
        return yArrowSet.isVisible();
    }

    public void setForceVisible( String force, boolean selected ) {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.setForceVisible( force, selected );
        }
    }
}
