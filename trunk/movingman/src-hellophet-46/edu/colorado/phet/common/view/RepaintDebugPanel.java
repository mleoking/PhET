/** Sam Reid*/
package edu.colorado.phet.common.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 25, 2004
 * Time: 10:09:04 AM
 * Copyright (c) Aug 25, 2004 by Sam Reid
 */
public class RepaintDebugPanel extends ApparatusPanel {
//    RectangleDisplayPanel rectangleDisplayPanel = new RectangleDisplayPanel();
//    Stroke stroke = new BasicStroke( 1.0f );

    public RepaintDebugPanel() {
        super.setDoubleBuffered( false );
//        rectangleDisplayPanel.showInFrame();
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        super.repaint( tm, x, y, width, height );
    }

    public void repaint( Rectangle r ) {
        super.repaint( r );
    }

    public void repaint() {
        super.repaint();
    }

//    public void repaint( long tm ) {
//        super.repaint( tm );
//    }
//
//    public void repaint( int x, int y, int width, int height ) {
//        super.repaint( x, y, width, height );
//    }

//    public void paintImmediately( int x, int y, int w, int h ) {
////        System.out.println( "PaintImm: = x=" + x + ", y=" + y + ", w=" + w + ", h=" + h );
//        StringWriter sw = new StringWriter();
//        new Exception().printStackTrace( new PrintWriter( sw ) );
////        String text = sw.getBuffer().toString();
////        text += "Size=" + w * h;
//
//        long time = System.currentTimeMillis();
//        super.paintImmediately( x, y, w, h );
//        long now = System.currentTimeMillis();
//
//        long DT = now - time;
//
////        text += "Time =" + DT;
//        RectangleDisplayPanel.Data data = new RectangleDisplayPanel.Data( x, y, w, h, new Exception(), randomColor(), stroke, false, DT, w * h );
//        if( DT > 0 ) {
////            System.out.println( "DT = " + DT );
//            System.out.println( "data = " + data );
//        }
//        rectangleDisplayPanel.addData( data );
////        rectangleDisplayPanel.addRectangle( x, y, w, h, text, randomColor(), stroke, false );
////        rectangleDisplayPanel.repaint( x - 1, y - 1, w + 2, h + 2 );
//        rectangleDisplayPanel.repaint();
//    }
//
//    Random random = new Random();
//
//    private Color randomColor() {
//        return new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() );
//    }
//
//    public void paintImmediately( final Rectangle r ) {
//        paintImmediately( r.x, r.y, r.width, r.height );
////            super.paintImmediately( r );
////            repaint( r.x,r.y,r.width, r.height);
////            if( mode instanceof PlaybackMode ) {
////                RepaintDebugPanel.super.paintImmediately( r );
////            }
////            else {
//
////            SwingUtilities.invokeLater( new Runnable() {
////                public void run() {
////                    RepaintDebugPanel.super.paintImmediately( r );
////                }
////            } );
//    }
//        }
    public void paintSoon( Rectangle union ) {
        repaint( union );
//        repaint( );
//        paintImmediately( union );
    }

//    public static class RectangleDisplayPanel extends JPanel {
//        ArrayList data = new ArrayList();
//
//        public static class Data {
//            int x;
//            int y;
//            int width;
//            int height;
//            private Exception exception;
//            private Color color;
//            private Stroke stroke;
//            private boolean fill;
//            private long time;
//            private int size;
//
//            public Data( int x, int y, int width, int height, Exception exception, Color color, Stroke stroke, boolean fill, long time, int size ) {
//                this.x = x;
//                this.y = y;
//                this.width = width;
//                this.height = height;
//                this.exception = exception;
//                this.color = color;
//                this.stroke = stroke;
//                this.fill = fill;
//                this.time = time;
//                this.size = size;
//            }
//
//            public String toString() {
//                return "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", time=" + time + ", size=" + size;
//            }
//        }
//
//        public RectangleDisplayPanel() {
//        }
//
//        public void showInFrame() {
//            JFrame frame = new JFrame();
//            frame.setContentPane( this );
//            frame.setSize( 400, 400 );
//            frame.setVisible( true );
//        }
//
////        public void addRectangle( Rectangle rect, String data, Color color, Stroke stroke, boolean fill ) {
////            addRectangle( rect.x, rect.y, rect.width, rect.height, data, color, stroke, fill );
////        }
//
//        public void addData( Data datum ) {
//            data.add( datum );
//            if( data.size() > 100 ) {
//                data.remove( 0 );
//            }
//        }
//
////        private void addRectangle( int x, int y, int width, int height, String str, Color color, Stroke stroke, boolean fill ) {
////            Data d = new Data( x, y, width, height, str, color, stroke, fill );
////            data.add( d );
////            if( data.size() > 100 ) {
////                data.remove( 0 );
////            }
////        }
//        Font font = new Font( "Dialog", 0, 12 );
//
//        protected void paintComponent( Graphics g ) {
//            super.paintComponent( g );
//            Graphics2D g2 = (Graphics2D)g;
//            int numToShow = 30;
//            for( int i = data.size() - numToShow; i >= 0 && i < data.size(); i++ ) {
//                Data data1 = (Data)data.get( i );
//                g.setColor( data1.color );
//                g2.setStroke( data1.stroke );
//                if( data1.fill ) {
//                    g2.fillRect( data1.x, data1.y, data1.width, data1.height );
//                }
//                else {
//                    g2.drawRect( data1.x, data1.y, data1.width, data1.height );
//                }
//                g2.setFont( font );
//                g2.drawString( "" + data1.time + ":" + data1.size, data1.x, data1.y + 50 );
//            }
//        }
//
//    }
}
