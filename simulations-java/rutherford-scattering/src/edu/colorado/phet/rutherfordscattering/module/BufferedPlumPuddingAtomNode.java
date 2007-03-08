package edu.colorado.phet.rutherfordscattering.module;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rutherfordscattering.view.PlumPuddingAtomNode;
import edu.colorado.phet.rutherfordscattering.view.ModelViewTransform;
import edu.colorado.phet.rutherfordscattering.model.PlumPuddingAtom;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PDebug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Mar 7, 2007
 * Time: 1:17:35 PM
 * Copyright (c) Mar 7, 2007 by Sam Reid
 */

public class BufferedPlumPuddingAtomNode extends PhetPNode {
    private PlumPuddingAtomNode atomNode;
    private PhetRootPNode phetRootPNode;
    private MyCanvas canvas;
    private boolean buffered = true;
    private Point2D offset;

    public BufferedPlumPuddingAtomNode( PlumPuddingAtomNode atomNode, PhetRootPNode myNode, MyCanvas canvas, PlumPuddingAtom atom ) {
        this.atomNode = atomNode;
        this.phetRootPNode = myNode;
        this.canvas = canvas;

        offset = atomNode.getOffset();

        addChild( atomNode );
        update();//todo: add update whenever screen dimension changes

        JFrame dialog = new JFrame();
        final JCheckBox cp = new JCheckBox( "buffered", buffered );
        cp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setBuffered( cp.isSelected() );
            }
        } );
        dialog.setContentPane( cp );
        dialog.pack();
        dialog.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        dialog.setLocation( (int)( Toolkit.getDefaultToolkit().getScreenSize().getWidth() - dialog.getWidth() ), 0 );
        dialog.setVisible( true );
        PDebug.debugBounds=true;
        
//        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
//        removeAllChildren();
//        addChild( imageNode );
        
        Point2D atomPosition = atom.getPositionRef();
        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
        setOffset( nodePosition.getX()-getFullBounds().getWidth()/2,nodePosition.getY()-getFullBounds().getHeight()/2 );
        
                
        
//        Point2D atomPosition = atom.getPositionRef();
//        Point2D nodePosition = ModelViewTransform.transform( atomPosition );
//        setOffset( nodePosition );
    }

    private void setBuffered( boolean selected ) {
        this.buffered = selected;
        update();
    }

    public void update() {
        removeAllChildren();
        if( buffered ) {
            Image image = atomNode.toImage();
            ScalingPImage scalingPImage = new ScalingPImage( image );

            addChild( scalingPImage );
        }
        else {
            addChild( atomNode );
        }
    }

    class ScalingPImage extends PImage {
        BufferedImage image;
        BufferedImage origImage;

        public ScalingPImage( Image image ) {
            super( image );
            this.origImage = BufferedImageUtils.toBufferedImage( image );
            updateImage( image.getWidth( null ) );
        }

        public void updateImage( int width ) {
            this.image = BufferedImageUtils.rescaleXMaintainAspectRatio( BufferedImageUtils.copyImage( origImage ), width );

        }

        protected void paint( PPaintContext paintContext ) {
            if( getImage() != null ) {
                Rectangle2D.Double bounds = getGlobalFullBounds();
                phetRootPNode.globalToScreen( bounds );

                int desiredSizeX = (int)bounds.getWidth();
//                System.out.println( "desiredSizeX = " + desiredSizeX+", bw="+((int)bounds.getWidth()) );
                if( desiredSizeX != image.getWidth() ) {
                    updateImage( desiredSizeX );
                }
                Graphics2D g2 = paintContext.getGraphics();
                AffineTransform origTx = g2.getTransform();
//                System.out.println( "canvas.getG2().getTransform() = " + canvas.getAffineTransform() );
                g2.setTransform( canvas.getAffineTransform() );
                g2.drawRenderedImage( image, AffineTransform.getTranslateInstance( bounds.getX(),bounds.getY()) );
                g2.setTransform( origTx );
            }
        }
    }
    public static class MyCanvas extends PhetPCanvas {

    private AffineTransform affineTransform;

    public MyCanvas( Dimension canvasRenderingSize ) {
        super(canvasRenderingSize );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        affineTransform = g2.getTransform();
//            System.out.println( "g2.getTransform() = " + g2.getTransform() );
        super.paintComponent( g );
    }

    public AffineTransform getAffineTransform() {
        return affineTransform;
    }
}
    
}