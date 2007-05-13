///*
//
//   Licensed to the Apache Software Foundation (ASF) under one or more
//   contributor license agreements.  See the NOTICE file distributed with
//   this work for additional information regarding copyright ownership.
//   The ASF licenses this file to You under the Apache License, Version 2.0
//   (the "License"); you may not use this file except in compliance with
//   the License.  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
// */
//
//package edu.colorado.phet.rotation.util.batik;
//
//import edu.umd.cs.piccolox.pswing.PSwing;
//import edu.umd.cs.piccolox.pswing.PSwingCanvas;
//import org.apache.batik.swing.JSVGCanvas;
//import org.apache.batik.swing.svg.SVGUserAgentGUIAdapter;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.File;
//
///**
// * Simplest "complete" SVG Viewer using Batik.
// * <p/>
// * This is about as simple as an SVG viewer application can get.
// * It shuts it's self down when all windows are closed.
// * It reports errors interactively, and it takes a list of URI's
// * to open.
// *
// * @author <a href="mailto:Thomas.DeWeese@Kodak.com">deweese</a>
// * @version $Id: JSVG.java 479617 2006-11-27 13:43:51Z dvholten $
// */
//public class SVGDemo extends JFrame {
//    static int windowCount = 0;
//
//    public SVGDemo( String url ) {
//        super( url );
//        JSVGCanvas canvas = new JSVGCanvas( new SVGUserAgentGUIAdapter( this ),
//                                            true, true ) {
//            /**
//             * This method is called when the component knows the desired
//             * size of the window (based on width/height of outermost SVG
//             * element). We override it to immediately pack this frame.
//             */
//            public void setMySize( Dimension d ) {
//                setPreferredSize( d );
//                invalidate();
//                SVGDemo.this.pack();
//            }
//        };
//
//        getContentPane().add( canvas, BorderLayout.CENTER );
//        canvas.setURI( url );
//        setVisible( true );
//        addWindowListener( new WindowAdapter() {
//            public void windowClosing( WindowEvent e ) {
//                windowCount--;
//                if( windowCount == 0 ) {
//                    System.exit( 0 );
//                }
//            }
//        } );
//        windowCount++;
//    }
//
//    public static void main( String[] args ) {
//        File file = new File( "C:\\Users\\Sam\\Pictures\\Microsoft Clip Organizer\\j0346925.svg" );
//
//        PSwingCanvas pSwingCanvas = new PSwingCanvas();
//
//        JSVGCanvas canvas = new JSVGCanvas() {
//            /**
//             * This method is called when the component knows the desired
//             * size of the window (based on width/height of outermost SVG
//             * element). We override it to immediately pack this frame.
//             */
//            public void setMySize( Dimension d ) {
//                setPreferredSize( d );
//                invalidate();
////                    SVGDemo.this.pack();
//            }
//        };
//
//        canvas.setURI( file.toURI().toString() );
//        canvas.setPreferredSize( new Dimension( 400,400) );
//        pSwingCanvas.getLayer().addChild( new PSwing( canvas ) );
//        JFrame frame = new JFrame();
//        frame.setContentPane( pSwingCanvas );
//        frame.setSize( 600, 400 );
//        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//        frame.setVisible( true );
//    }
//}
