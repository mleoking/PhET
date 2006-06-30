/** Sam Reid*/
package edu.colorado.phet.common_cck.tests.basicgraphics;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.basicgraphics.BasicGraphic;
import edu.colorado.phet.common_cck.view.basicgraphics.BasicShapeGraphic;
import edu.colorado.phet.common_cck.view.basicgraphics.RenderedGraphic;
import edu.colorado.phet.common_cck.view.basicgraphics.RepaintDelegate;
import edu.colorado.phet.common_cck.view.basicgraphics.repaint.*;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 8:03:51 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class BasicGraphicsTest implements SynchronizedRepaintDelegate {
    private JFrame frame;
    private ApparatusPanel panel;
    private Thread thread;
    private int ellipseSize;
    private static final int numToShow = 100;
    private ArrayList heaviweights;
    private Random random;
    private RepaintDelegate repaintDelegate;
    private Timer timer;
    private boolean threadAlive = true;
    private Runnable runnable;
    private boolean antialias = true;
    private boolean threadRunning = false;
    private Controls controls;

    public void finishedUpdateCycle() {
        if( repaintDelegate instanceof SynchronizedRepaintDelegate ) {
            SynchronizedRepaintDelegate srd = (SynchronizedRepaintDelegate)repaintDelegate;
            srd.finishedUpdateCycle();
        }
    }

    public void repaint( Component component, Rectangle rect ) {
        repaintDelegate.repaint( component, rect );
    }

    class Controls extends JFrame {
        private JRadioButton threadTime;

        public Controls() throws HeadlessException {
            super( "Controls" );
            JPanel contentPane = new JPanel();
            contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
            final JSpinner numToShowSpinner = new JSpinner( new SpinnerNumberModel( numToShow, 0, 1000, 5 ) );
            numToShowSpinner.setBorder( BorderFactory.createTitledBorder( "Number Shapes" ) );
            numToShowSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Integer value = (Integer)numToShowSpinner.getValue();
                    int newNum = value.intValue();
                    while( heaviweights.size() > newNum ) {
                        Graphic g = (Graphic)heaviweights.remove( 0 );
                        panel.removeGraphic( g );
                    }
                    while( heaviweights.size() < newNum ) {
                        Graphic ns = newShape();
                        heaviweights.add( ns );
                        panel.addGraphic( ns, 0 );
                    }
                }
            } );
            contentPane.add( numToShowSpinner );
            setSize( 400, 400 );
            setLocation( 400, 0 );

            setContentPane( contentPane );
            ButtonGroup bg = new ButtonGroup();
            addButton( contentPane, bg, new Repaint() );
            addButton( contentPane, bg, new UnionRepaint( panel ) );
            addButton( contentPane, bg, new DisjointRepaint( panel ) );
            addButton( contentPane, bg, new ImmediateUnionPaint( panel ) );
            addButton( contentPane, bg, new ImmediateDisjointPaint( panel ) );
            addButton( contentPane, bg, new InvokeAndWaitImmediatePaint( panel ) );
            addButton( contentPane, bg, new InvokeLaterImmediatePaint( panel ) );
            contentPane.add( new JSeparator() );
            JRadioButton swingTimer = new JRadioButton( "Swing Timer" );
            this.threadTime = new JRadioButton( "Thread Timer" );
            swingTimer.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    threadAlive = false;
                    timer.start();
                }
            } );
            threadTime.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    threadAlive = true;
                    timer.stop();
                    thread = new Thread( runnable );
                    thread.start();
                }
            } );

            swingTimer.setSelected( timer.isRunning() );
            threadTime.setSelected( threadRunning );
            contentPane.add( swingTimer );
            contentPane.add( threadTime );
            ButtonGroup bg2 = new ButtonGroup();
            bg2.add( swingTimer );
            bg2.add( threadTime );

            final JCheckBox ant = new JCheckBox( "Antialias", antialias );
            ant.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    antialias = ant.isSelected();
                }
            } );

            contentPane.add( ant );
            pack();

            panel.addKeyListener( new KeyListener() {
                public void keyTyped( KeyEvent e ) {
                    if( e.getKeyChar() == 's' ) {
                        for( int i = 0; i < heaviweights.size(); i++ ) {
                            RenderedGraphic renderedGraphic = (RenderedGraphic)heaviweights.get( i );
                            renderedGraphic.setVisible( true );
                        }
//                        renderedGraphic.setVisible( true );
                    }
                    else if( e.getKeyChar() == 'h' ) {
                        for( int i = 0; i < heaviweights.size(); i++ ) {
                            RenderedGraphic renderedGraphic = (RenderedGraphic)heaviweights.get( i );
                            renderedGraphic.setVisible( false );
                        }
//                        renderedGraphic.setVisible( false );
                    }
                }

                public void keyPressed( KeyEvent e ) {
                }

                public void keyReleased( KeyEvent e ) {
                }
            } );
        }

        private void addButton( JPanel contentPane, ButtonGroup bg, final RepaintDelegate repaintDelegate ) {
            String name = repaintDelegate.getClass().getName().substring( repaintDelegate.getClass().getName().lastIndexOf( '.' ) + 1 );
            JRadioButton imm = new JRadioButton( name );
            imm.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    BasicGraphicsTest.this.repaintDelegate = repaintDelegate;
                }
            } );
            contentPane.add( imm );
            bg.add( imm );
        }

    }

    class MyGraphic extends RenderedGraphic {
        double speed;
        AffineTransform tx;

        public MyGraphic( BasicGraphic basicGraphic, Component component, double speed ) {
            super( basicGraphic, component );
            this.speed = speed;
            tx = AffineTransform.getTranslateInstance( speed, 0 );
        }

        public AffineTransform getTranslateInstance() {
            return tx;
        }
    }

    private RenderedGraphic newShape() {
        Color color = new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() );
        Shape shape = randomShape();
        Stroke stroke = new BasicStroke( random.nextInt( 10 ) + 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        final BasicShapeGraphic shapeGraphic = new BasicShapeGraphic( shape, color, stroke, Color.black );
        RenderedGraphic hg = new MyGraphic( shapeGraphic, panel, random.nextDouble() * 5 + 1 );
        panel.addGraphic( hg, 0 );
        hg.setRepaintDelegate( this );
        return hg;
    }

    interface ShapeInit {
        Shape newShape();
    }

    private Shape randomShape() {
        ShapeInit[] sh = new ShapeInit[]{
                new ShapeInit() {
                    public Shape newShape() {
                        return new Ellipse2D.Double( random.nextInt( panel.getWidth() ), random.nextInt( panel.getHeight() ),
                                                     ellipseSize, ellipseSize );
                    }
                },
                new ShapeInit() {
                    public Shape newShape() {
                        return new Rectangle( random.nextInt( panel.getWidth() ), random.nextInt( panel.getHeight() ), ellipseSize, ellipseSize );
                    }
                },
                new ShapeInit() {
                    public Shape newShape() {
                        GeneralPath gp = new GeneralPath();
                        gp.moveTo( random.nextInt( panel.getWidth() ), random.nextInt( panel.getHeight() ) );
//                    Point2D startPoint = gp.getCurrentPoint();
                        for( int i = 0; i < 3 + random.nextInt( 4 ); i++ ) {
                            gp.lineTo( (float)( gp.getCurrentPoint().getX() + ( random.nextFloat() - .5 ) * ellipseSize * 2 ), (float)( gp.getCurrentPoint().getY() + ( random.nextFloat() - .5 ) * ellipseSize * 2 ) );
                        }
                        gp.closePath();
                        return gp;
                    }
                }

        };
        ShapeInit initer = sh[random.nextInt( sh.length )];
        return initer.newShape();
    }

    public BasicGraphicsTest() {
        frame = new JFrame();

        panel = new ApparatusPanel() {
            public void paintComponent( Graphics graphics ) {
                Graphics2D g2 = (Graphics2D)graphics;
                if( antialias ) {
                    g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                }
                else {
                    g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                }
                super.paintComponent( graphics );
            }
        };
        panel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                System.out.println( panel.getBounds() );
            }
        } );
        frame.setContentPane( panel );
        panel.setSize( 400, 400 );
        frame.setSize( 400, 400 );
        ellipseSize = 30;
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        random = new Random( 0 );
        this.heaviweights = new ArrayList();
//        stroke = new BasicStroke( 5.0f );
        repaintDelegate = new ImmediateUnionPaint( panel );
        for( int i = 0; i < numToShow; i++ ) {
            heaviweights.add( newShape() );
        }
        runnable = new Runnable() {
            public void run() {
                if( threadRunning == true ) {//race condition.
                    return;
                }
                System.out.println( "Thread started run." );
                controls.threadTime.setSelected( true );
                threadRunning = true;
                while( threadAlive ) {
                    try {
                        Thread.sleep( 30 );
                        stepOnce();
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
                System.out.println( "Thread exited." );
                threadRunning = false;
            }
        };
        thread = new Thread( runnable );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stepOnce();
            }
        } );
        controls = new Controls();
        controls.setVisible( true );
    }

    private void stepOnce() {
        for( int i = 0; i < heaviweights.size(); i++ ) {
            MyGraphic heavyweightGraphic = (MyGraphic)heaviweights.get( i );
            BasicShapeGraphic shapeGraphic = (BasicShapeGraphic)heavyweightGraphic.getBasicGraphic();
            Shape old = shapeGraphic.getShape();
            double speed = heavyweightGraphic.speed;
            Shape newShape = heavyweightGraphic.getTranslateInstance().createTransformedShape( old );
            if( newShape.getBounds().getX() > panel.getWidth() ) {
                int x = newShape.getBounds().x + newShape.getBounds().width;
                newShape = AffineTransform.getTranslateInstance( -x, 0 ).createTransformedShape( newShape );
            }
            shapeGraphic.setShape( newShape );
        }
        finishedUpdateCycle();

    }

    public static void main( String[] args ) {
        new BasicGraphicsTest().start();
    }

    private void start() {
        frame.setVisible( true );
        panel.repaint();
        thread.start();
        panel.requestFocus();
//        timer.start();
    }
}
