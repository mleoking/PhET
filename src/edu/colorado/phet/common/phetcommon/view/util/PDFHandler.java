//package edu.colorado.phet.common.phetcommon.view.util;
//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.util.Arrays;
//
//import javax.swing.*;
//
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.pdf.DefaultFontMapper;
//import com.lowagie.text.pdf.PdfContentByte;
//import com.lowagie.text.pdf.PdfTemplate;
//import com.lowagie.text.pdf.PdfWriter;
//
///**
// * Created by: Sam
// * Dec 30, 2007 at 2:11:52 AM
// */
//public class PDFHandler {
//    public static void addPDFHandler() {
//        final KeyListener listener = new KeyListener() {
//            public void keyPressed( KeyEvent e ) {
//            }
//
//            public void keyReleased( KeyEvent e ) {
//                if ( e.getKeyCode() == KeyEvent.VK_P ) {
//                    doPrintFrame( e.getComponent() );
//                }
//            }
//
//            private void doPrintFrame( Component component ) {
//                System.out.println( "Print frame: component=" + component );
//            }
//
//            public void keyTyped( KeyEvent e ) {
//            }
//        };
//
//        Timer timer = new Timer( 100, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                Frame[] f = JFrame.getFrames();
////                System.out.println( "f.length = " + f.length );
//                for ( int i = 0; i < f.length; i++ ) {
//                    final Frame frame = f[i];
//                    if ( !Arrays.asList( frame.getKeyListeners() ).contains( listener ) ) {
//                        frame.addKeyListener( listener );
//
//                        if ( frame instanceof JFrame ) {
//                            JFrame jf = (JFrame) frame;
//                            KeyStroke ks = KeyStroke.getKeyStroke( "P" );
//                            Action action = new AbstractAction() {
//                                public void actionPerformed( ActionEvent e ) {
//                                    printFrame( frame );
//                                }
//
//                                private void printFrame( Frame frame ) {
//                                    int width = frame.getWidth();
//                                    int height = frame.getHeight();
//
//                                    File filename = new File( "phet_screenshot_" + System.currentTimeMillis() + ".pdf" );
//                                    System.out.println( "saving to: filename = " + filename.getAbsolutePath() );
//                                    // step 1
//                                    Document document = new Document( new com.lowagie.text.Rectangle( width, height ) );
//                                    try {
//                                        // step 2
//                                        PdfWriter writer;
//                                        writer = PdfWriter.getInstance( document, new FileOutputStream( filename ) );
//                                        // step 3
//                                        document.open();
//                                        // step 4
//                                        PdfContentByte cb = writer.getDirectContent();
//                                        PdfTemplate tp = cb.createTemplate( width, height );
//                                        Graphics2D g2d = tp.createGraphics( width, height, new DefaultFontMapper() );
////                                        Rectangle2D r2d = new Rectangle2D.Double( 0, 0, width, height );
//
//                                        frame.paintAll( g2d );
//
////                                        chart.draw( g2d, r2d );
//                                        g2d.dispose();
//                                        cb.addTemplate( tp, 0, 0 );
//                                    }
//                                    catch( DocumentException de ) {
//                                        de.printStackTrace();
//                                    }
//                                    catch( FileNotFoundException e ) {
//                                        e.printStackTrace();
//                                    }
//                                    // step 5
//                                    document.close();
//                                }
//                            };
//                            Object name = "action_name";
//                            if ( jf.getContentPane() instanceof JComponent ) {
//                                JComponent jc = (JComponent) jf.getContentPane();
//                                InputMap im = jc.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW );
//                                ActionMap am = jc.getActionMap();
//                                im.put( ks, name );
//                                am.put( name, action );
//                            }
//                        }
//
//                    }
//                }
//            }
//        } );
//        timer.start();
//    }
//
//}
