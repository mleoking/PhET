/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.BufferedPhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:16:32 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DPanel extends ApparatusPanel {
    private Force1DModule module;
    private BlockGraphic blockGraphic;
    private ArrowSetGraphic arrowSetGraphic;
    private ModelViewTransform2D transform2D;
    private Function.LinearFunction walkwayTransform;
    private BufferedPhetGraphic bufferedPhetGraphic;
    private PlotDevice forcePlotDevice;
    private WalkwayGraphic walkwayGraphic;
    private LeanerGraphic leanerGraphic;
    private Force1DModel model;
    private PlotDeviceView plotDeviceView;
    private RepaintDebugGraphic repaintDebugGraphic;
    private FreeBodyDiagram freeBodyDiagram;
    private FreeBodyDiagram.ForceArrow mg;
    private FreeBodyDiagram.ForceArrow normal;

    public Force1DPanel( final Force1DModule module ) throws IOException {
        this.module = module;
        this.model = module.getForceModel();
        addGraphicsSetup( new BasicGraphicsSetup() );
        walkwayTransform = new Function.LinearFunction( -10, 10, 0, 400 );
        walkwayGraphic = new WalkwayGraphic( this, module, 21, getWalkwayTransform() );
        blockGraphic = new BlockGraphic( this, module.getForceModel().getBlock(), model, transform2D, walkwayTransform, module.imageElementAt( 0 ) );
        arrowSetGraphic = new ArrowSetGraphic( this, blockGraphic, model, transform2D );
        leanerGraphic = new LeanerGraphic( this, blockGraphic );
        addGraphic( walkwayGraphic );
        addGraphic( blockGraphic );
        addGraphic( leanerGraphic, 1000 );
        leanerGraphic.setLocation( 400, 100 );

        addGraphic( arrowSetGraphic );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }

            public void componentHidden( ComponentEvent e ) {
                relayout();
            }
        } );

//        int strokeWidth=2;
        int strokeWidth = 3;
        plotDeviceView = new Force1DPlotDeviceView( module, this );
        PlotDevice.ParameterSet params = new PlotDevice.ParameterSet( this, "Applied Force", model.getPlotDeviceModel(),
                                                                      plotDeviceView, model.getAppliedForceDataSeries().getSmoothedDataSeries(), model.getPlotDeviceModel().getRecordingTimer(),
                                                                      Color.red, new BasicStroke( strokeWidth ),
                                                                      new Rectangle2D.Double( 0, -10, model.getPlotDeviceModel().getMaxTime(), 20 ),
                                                                      0, "N", "applied force" );
        forcePlotDevice = new PlotDevice( params );
        forcePlotDevice.setLabelText( "<html>Applied<br>Force</html>" );
        forcePlotDevice.addDataSeries( model.getNetForceSeries(), Color.blue, "Total Force", new BasicStroke( strokeWidth ) );
        forcePlotDevice.addDataSeries( model.getFrictionForceSeries(), Color.green, "Friction Force", new BasicStroke( strokeWidth ) );

        addGraphic( forcePlotDevice );
        forcePlotDevice.addSliderListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                double appliedForce = value;
                model.setAppliedForce( appliedForce );
            }
        } );
        Font checkBoxFont = new Font( "Lucida Sans", Font.PLAIN, 14 );

        final JCheckBox showNetForce = new JCheckBox( "Net Force", true );

        showNetForce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowNetForce( showNetForce.isSelected() );
            }
        } );

        showNetForce.setFont( checkBoxFont );
        final JCheckBox showFrictionForce = new JCheckBox( "Friction Force", true );
        showFrictionForce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowFrictionForce( showFrictionForce.isSelected() );
            }
        } );
        showFrictionForce.setFont( checkBoxFont );
        JPanel checkBoxPanel = new VerticalLayoutPanel();
        checkBoxPanel.add( showNetForce );
        checkBoxPanel.add( showFrictionForce );
        forcePlotDevice.getFloatingControl().add( checkBoxPanel );

        bufferedPhetGraphic = new BufferedPhetGraphic( this, new Graphic() {
            public void paint( Graphics2D g ) {
            }
        }, Color.white );
        relayout();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    if( repaintDebugGraphic.isActive() ) {
                        removeGraphic( repaintDebugGraphic );
                    }
                    else {
                        addGraphic( repaintDebugGraphic, 15000 );
                    }
                    repaintDebugGraphic.setActive( !repaintDebugGraphic.isActive() );
                }
            }
        } );
        repaintDebugGraphic = new RepaintDebugGraphic( Force1DPanel.this, module.getClock() );
        repaintDebugGraphic.setTransparency( 128 );
        repaintDebugGraphic.setActive( false );

        freeBodyDiagram = new FreeBodyDiagram( this, module );
        addGraphic( freeBodyDiagram, Double.POSITIVE_INFINITY );
        mg = new FreeBodyDiagram.ForceArrow( this, freeBodyDiagram, Color.blue, "mg", new Vector2D.Double( 0, 80 ) );
        freeBodyDiagram.addForceArrow( mg );

        normal = new FreeBodyDiagram.ForceArrow( this, freeBodyDiagram, Color.green, "N", new Vector2D.Double( 0, 80 ) );
        freeBodyDiagram.addForceArrow( normal );
        model.addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
            }

            public void gravityChanged() {
                updateMG();
            }
        } );
        model.getBlock().addListener( new Block.Listener() {
            public void positionChanged() {
            }

            public void propertyChanged() {
                updateMG();
            }
        } );
    }

    private void updateMG() {
        double gravity = model.getGravity();
        double mass = model.getBlock().getMass();
        double scale = 1.0 / 30.0;
        Vector2D.Double m = new Vector2D.Double( 0, gravity * mass * scale );
        AbstractVector2D n = m.getScaledInstance( -1 );
        mg.setVector( m );
        normal.setVector( n );
    }

    private void setShowFrictionForce( boolean selected ) {
        forcePlotDevice.setDataSeriesVisible( 2, selected );
        repaint();
    }

    private void setShowNetForce( boolean selected ) {
        forcePlotDevice.setDataSeriesVisible( 1, selected );
        repaint();
    }
//
//    public void repaint( int x, int y, int width, int height ) {
//        super.repaint( x, y, width, height );
//        StackTraceElement[] str = new Exception( "Repaint" ).getStackTrace();
//        for( int i = 0; i < str.length && i < 9; i++ ) {
//            StackTraceElement stackTraceElement = str[i];
//            System.out.println( "" + i + ": " + stackTraceElement );
//        }
//        System.out.println( "..." );
//    }

    public void relayout() {
        if( getWidth() > 0 && getHeight() > 0 ) {
            bufferedPhetGraphic.setSize( getWidth(), getHeight() );
            int insetX = 50;
            walkwayTransform.setOutput( 0 + insetX, getWidth() - insetX );
            int plotInsetX = 200;
            int plotWidth = getWidth() - plotInsetX - 25;
            int y = walkwayGraphic.getHeight() + 20 + walkwayGraphic.getY();
            int yInsetBottom = forcePlotDevice.getChart().getHorizontalTicks().getMajorTickTextBounds().height * 2;
            Rectangle newViewBounds = new Rectangle( plotInsetX, y + yInsetBottom, plotWidth, getHeight() - y - yInsetBottom * 2 );
//            System.out.println( "newViewBounds = " + newViewBounds );
            if( newViewBounds.width > 0 && newViewBounds.height > 0 ) {
                forcePlotDevice.setViewBounds( newViewBounds );
            }
            updateGraphics();
            repaint();
        }
//        requestFocus();
    }

    public Function.LinearFunction getWalkwayTransform() {
        return walkwayTransform;
    }

    public BufferedPhetGraphic getBufferedGraphic() {
        return bufferedPhetGraphic;
    }

    public WalkwayGraphic getWalkwayGraphic() {
        return walkwayGraphic;
    }

    public void updateGraphics() {
        arrowSetGraphic.updateGraphics();
        blockGraphic.update();
    }

    public void reset() {
        forcePlotDevice.reset();
        repaint( 0, 0, getWidth(), getHeight() );
    }

    public Force1DModule getModule() {
        return module;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }
}
