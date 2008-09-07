//package edu.colorado.phet.common.phetcommon.view.clock.clockcontroltest1;
//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.geom.GeneralPath;
//import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;
//
//import javax.swing.*;
//
//import edu.colorado.phet.common.phetcommon.application.Module;
//import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
//import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
//import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
//import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
//import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
//import edu.umd.cs.piccolo.PNode;
//import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
//import edu.umd.cs.piccolo.event.PInputEvent;
//import edu.umd.cs.piccolo.nodes.PImage;
//
///**
// * Created by: Sam
// * Sep 4, 2008 at 9:41:42 PM
// */
//public class PanelNode extends PNode {
//    private PImage playButton;
//    private PhetPPath surfacePanel;
//    private PhetPPath sidePanel;
//    private double dx;
//    private double dy;
//    private Module module;
//    private ShadowHTMLNode text;
//
//    public PanelNode( final Module module ) {
//        this.module = module;
//        dx = 8;
//        dy = 6;
//        final int iconSize = 60;
//
//        sidePanel = new PhetPPath( Color.gray, new BasicStroke( 1 ), Color.black );
//        addChild( sidePanel );
//
//        surfacePanel = new PhetPPath( new Color( 220, 220, 220 ) );
//        addChild( surfacePanel );
//
//        playButton = new PImage( BufferedImageUtils.multiScaleToHeight( PhetCommonResources.getInstance().getImage( "clock/button-template-wide.png" ), iconSize ) );
//        playButton.addInputEventListener( new PBasicInputEventHandler() {
//            public void mousePressed( PInputEvent event ) {
//                if ( module.getClock().isRunning() ) {
//                    module.getClock().pause();
////                    playButton.setImage( BufferedImageUtils.multiScaleToHeight( PhetCommonResources.getInstance().getImage( "clock/play-black.png" ), iconSize ) );
//                    text.setHtml( "Paused" );
//                }
//                else {
//                    module.getClock().start();
////                    playButton.setImage( BufferedImageUtils.multiScaleToHeight( PhetCommonResources.getInstance().getImage( "clock/pause-black.png" ), iconSize ) );
//                    text.setHtml( "Running" );
//                }
//            }
//        } );
//        addChild( playButton );
//
//        Timer timer = new Timer( 1000, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                updatePaint();
//            }
//        } );
//        timer.start();
//
//        text = new ShadowHTMLNode( "Running" );
//        text.setColor( Color.red );
//        text.setShadowColor( Color.black );
//        text.setFont( new PhetFont( 16, true ) );
//        addChild( text );
//    }
//
//    public void updateLayout( int width, int height ) {
//        playButton.setOffset( width / 2 - playButton.getFullBounds().getWidth() / 2, 2 );
//
//        GeneralPath surfacePath = toGeneralPath( getLinePath( width, height ) );
//        surfacePath.append( new Line2D.Double( width, 0, 0, 0 ), true );
//        surfacePanel.setPathTo( surfacePath );
//
////        GeneralPath sidePath = getEdgeCurve( width, height );
////        sidePath.lineTo( width + dx, 0 + dy );
////
////        Shape curve = getEdgeCurve( width, height );
////
////        curve = AffineTransform.getScaleInstance( -1, 1 ).createTransformedShape( curve );
////        curve = AffineTransform.getTranslateInstance( width + dx, 0 + dy ).createTransformedShape( curve );
////        sidePath.append( curve, true );
////        sidePath.lineTo( 0, 0 );
////        sidePanel.setPathTo( sidePath );
//
//        sidePanel.removeAllChildren();
//        Point2D.Double[]linePath=getLinePath( width, height );
//        for (int i=0;i<linePath.length-1;i++){
//            sidePanel.addChild( new PhetPPath(createShape(linePath[i],linePath[i+1]),Color.gray, new BasicStroke( 1 ), Color.black) );
//        }
//
//        text.setOffset( playButton.getFullBounds().getMaxX() + 4, playButton.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
//
//        updatePaint();
//    }
//
//    private Shape createShape( Point2D.Double a, Point2D.Double b ) {
//        GeneralPath path=new GeneralPath( );
//        path.moveTo( (float)a.getX(),(float)a.getY() );
//        path.lineTo((float)b.getX(),(float)b.getY() );
//        path.lineTo((float)b.getX()+dx,(float)b.getY()+dy );
//        path.lineTo((float)a.getX()+dx,(float)a.getY()+dy );
//        path.lineTo((float)a.getX(),(float)a.getY() );
//        return path;
//    }
//
//    private GeneralPath toGeneralPath( Point2D.Double[] linePath ) {
//        GeneralPath path = new GeneralPath();
//        path.moveTo( (float) linePath[0].getX(), (float) linePath[0].getY() );
//        for ( int i = 1; i < linePath.length; i++ ) {
//            path.lineTo( (float) linePath[i].getX(), (float) linePath[i].getY() );
//        }
//        return path;
//    }
//
//    private void updatePaint() {
//        if ( module != null && module.getSimulationPanel() != null ) {
//            module.getSimulationPanel().setBorder( null );
//            surfacePanel.setPaint( getPanelColor() );
//            sidePanel.setPaint( darker( getPanelColor() ) );
//            for ( int i = 0; i < sidePanel.getChildrenCount(); i++ ) {
//                PhetPPath path = (PhetPPath) sidePanel.getChild( i );
//                path.setPaint( darker( getPanelColor() ) );
//            }
//        }
//    }
//
//    private Color getPanelColor() {
//        Color color = module.getSimulationPanel().getBackground();
//        if ( color.equals( Color.black ) ) {
//            return Color.darkGray;
//        }
//        return color;
//    }
//
//    private Paint darker( Color color ) {
//        return new Color( Math.max( color.getRed() - 40, 0 ), Math.max( color.getGreen() - 40, 0 ), Math.max( color.getBlue() - 60, 0 ) );
//    }
//
//    private Point2D.Double[] getLinePath( double width, double height ) {
//        double insetX = width * 0.3;
//        Point2D.Double[] pt = new Point2D.Double[]{
//                new Point2D.Double( 0, 0 ),
//                new Point2D.Double( insetX, 0 ),
//                new Point2D.Double( width / 3, height - dy - 4 ),
//                new Point2D.Double( 2 * width / 3, height - dy - 4 ),
//                new Point2D.Double( width - insetX, 0 ),
//                new Point2D.Double( width, 0 ),
//        };
//        return pt;
//    }
//
////    private GeneralPath getEdgeCurve( double width, double height ) {
////        GeneralPath path = new GeneralPath();
////        path.moveTo( 0, 0 );
////        double insetX = width * 0.3;
////        path.lineTo( insetX, 0 );
////        path.lineTo( width / 3, height - dy - 2 );
////        path.lineTo( 2 * width / 3, height - dy - 2 );
////        path.lineTo( width - insetX, 0 );
////        path.lineTo( width, 0 );
////        return path;
////    }
//}
