/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight.tests;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.lightweight.ApparatusPanel2;
import edu.colorado.phet.common.view.lightweight.HeavyweightGraphic;
import edu.colorado.phet.common.view.lightweight.LightweightGraphic;
import edu.colorado.phet.common.view.lightweight.LightweightShapeGraphic;
import edu.colorado.phet.common.view.lightweight.repaint.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 8:03:51 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class LightweightGraphicsTest implements SynchronizedRepaintDelegate {
    private JFrame frame;
    private ApparatusPanel2 panel;
    private Thread thread;
    private int ellipseSize;
    private static final int numToShow = 100;
    private ArrayList heaviweights;
    private Random random;
    private SynchronizedRepaintDelegate repaintDelegate;
    private Stroke stroke;
    private Timer timer;
    private boolean threadAlive = true;
    private Runnable runnable;
    private boolean antialias = true;

    public void finishedUpdateCycle() {
        repaintDelegate.finishedUpdateCycle();
    }

    public void repaint( Component component, Rectangle rect ) {
        repaintDelegate.repaint( component, rect );
    }

    class Controls extends JFrame {
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
            JRadioButton imm = new JRadioButton( "Immediate" );
            imm.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    repaintDelegate = new ImmediatePaint( panel );
                }
            } );
            JRadioButton swing = new JRadioButton( "Swing" );
            swing.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    repaintDelegate = new SwingPaint( panel );
                }
            } );
            JRadioButton sync = new JRadioButton( "Sync'ed" );
            sync.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    repaintDelegate = new SyncedRepaintDelegate( panel );
                }
            } );
            JRadioButton syncSWING = new JRadioButton( "Immediate under Swing" );
            syncSWING.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    repaintDelegate = new ImmediatePaintSwingThread( panel );
                }
            } );
            JRadioButton immSep = new JRadioButton( "Immediate Separate Rectangles" );
            immSep.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    repaintDelegate = new ImmediatePaintSeparateRectangles( panel );
                }
            } );
            contentPane.add( imm );
            contentPane.add( swing );
            contentPane.add( sync );
            contentPane.add( syncSWING );
            contentPane.add( immSep );
            ButtonGroup bg = new ButtonGroup();
            bg.add( imm );
            bg.add( swing );
            bg.add( sync );
            bg.add( syncSWING );
            bg.add( immSep );
            contentPane.add( new JSeparator() );
            JRadioButton swingTimer = new JRadioButton( "Swing Timer" );
            JRadioButton threadTime = new JRadioButton( "Thread Timer" );
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
        }

    }

    class MyGraphic extends HeavyweightGraphic {
        double speed;

        public MyGraphic( LightweightGraphic lightweightGraphic, Component component, double speed ) {
            super( lightweightGraphic, component );
            this.speed = speed;
        }

    }

    private HeavyweightGraphic newShape() {
        Color color = new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() );
        Shape shape = randomShape();
        Stroke stroke = new BasicStroke( random.nextInt( 10 ) + 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        final LightweightShapeGraphic shapeGraphic = new LightweightShapeGraphic( shape, color, stroke, Color.black );
        HeavyweightGraphic hg = new MyGraphic( shapeGraphic, panel, random.nextDouble() * 5 + 1 );
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
                    Point2D startPoint = gp.getCurrentPoint();
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

    public LightweightGraphicsTest() {
        frame = new JFrame();
        panel = new ApparatusPanel2() {
            protected void paintComponent( Graphics graphics ) {
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

        frame.setContentPane( panel );
        panel.setSize( 400, 400 );
        frame.setSize( 400, 400 );
        ellipseSize = 30;
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        random = new Random( 0 );
        this.heaviweights = new ArrayList();
        stroke = new BasicStroke( 5.0f );
        repaintDelegate = new ImmediatePaint( panel );
        for( int i = 0; i < numToShow; i++ ) {
            heaviweights.add( newShape() );
        }
        runnable = new Runnable() {
            public void run() {

                System.out.println( "Thread started run." );
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
            }
        };
        thread = new Thread( runnable );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                stepOnce();
            }
        } );
        new Controls().setVisible( true );
    }

    private void stepOnce() {
        for( int i = 0; i < heaviweights.size(); i++ ) {
            MyGraphic heavyweightGraphic = (MyGraphic)heaviweights.get( i );
            LightweightShapeGraphic shapeGraphic = (LightweightShapeGraphic)heavyweightGraphic.getLightweightGraphic();
            Shape old = shapeGraphic.getShape();
            double speed = heavyweightGraphic.speed;
            Shape newShape = AffineTransform.getTranslateInstance( speed, 0 ).createTransformedShape( old );
            if( newShape.getBounds().getX() > panel.getWidth() ) {
                int x = newShape.getBounds().x + newShape.getBounds().width;
                newShape = AffineTransform.getTranslateInstance( -x, 0 ).createTransformedShape( newShape );
            }
            shapeGraphic.setShape( newShape );
        }
        repaintDelegate.finishedUpdateCycle();
    }

    public static void main( String[] args ) {
        new LightweightGraphicsTest().start();
    }

    private void start() {
        frame.setVisible( true );
        panel.repaint();
        thread.start();
//        timer.start();
    }
}
