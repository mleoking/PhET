package edu.colorado.phet.forces1d.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common_force1d.math.Function;
import edu.colorado.phet.common_force1d.math.Vector2D;
import edu.colorado.phet.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.common_force1d.view.BasicGraphicsSetup;
import edu.colorado.phet.common_force1d.view.ContentPanel;
import edu.colorado.phet.common_force1d.view.PhetFrame;
import edu.colorado.phet.common_force1d.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_force1d.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_force1d.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common_force1d.view.phetgraphics.*;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.Force1DUtil;
import edu.colorado.phet.forces1d.Forces1DModule;
import edu.colorado.phet.forces1d.common.*;
import edu.colorado.phet.forces1d.common.phetcomponents.PhetButton;
import edu.colorado.phet.forces1d.common.plotdevice.FloatingControl;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDevice;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceView;
import edu.colorado.phet.forces1d.model.Force1DModel;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:16:32 PM
 */
public class Force1DPanel extends ApparatusPanel2 {
    private Forces1DModule module;
    private BlockGraphic blockGraphic;
    private ArrowSetGraphic arrowSetGraphic;
    private ModelViewTransform2D transform2D;
    private Function.LinearFunction walkwayTransform;
    private PlotDevice forcePlotDevice;
    private WalkwayGraphic walkwayGraphic;
    private LeanerGraphic leanerGraphic;
    private Force1DModel model;
    private PlotDeviceView forcePlotDeviceView;
    private RepaintDebugGraphic repaintDebugGraphic;
    private Force1DLookAndFeel lookAndFeel = new Force1DLookAndFeel();
    private OffscreenPointerGraphic offscreenPointerGraphic;
    private BufferedPhetGraphic backgroundGraphic;
    private PlotDevice accelPlotDevice;
    private PlotDevice velPlotDevice;
    private PlotDevice posPlotDevice;
    private WiggleMe wiggleMe;
    private Color top = new Color( 230, 255, 230 );
    private Color bottom = new Color( 180, 200, 180 );
    private FloatingControl floatingControl;
    private WiggleMe sliderWiggleMe;
    //    private HelpItem2 goButtonHelp;
    private HelpItem2 soloGoButtonHelp;
    private boolean goButtonPressed;
    private PhetGraphic floatingControlGraphic;
    private PhetGraphic checkBoxPanelGraphic;

    public Force1DPanel( final Forces1DModule module ) throws IOException {
        super( module.getClock() );
//        setAutoPaint( false );
        this.backgroundGraphic = new BufferedPhetGraphic( this, 800, 800, Color.white );
        backgroundGraphic.setGraphicsSetup( new BasicGraphicsSetup() );
        this.module = module;
        this.model = module.getForceModel();
        addGraphicsSetup( new BasicGraphicsSetup() );
        walkwayTransform = new Function.LinearFunction( -12, 12, 0, 400 );
        walkwayGraphic = new WalkwayGraphic( this, module, 21, walkwayTransform );
        blockGraphic = new BlockGraphic( this, module.getForceModel().getBlock(), model, transform2D, walkwayTransform, module.imageElementAt( 0 ) );
        arrowSetGraphic = new ArrowSetGraphic( this, blockGraphic, model, transform2D );
        leanerGraphic = new LeanerGraphic( this, blockGraphic );
        backgroundGraphic.addGraphic( walkwayGraphic );
        backgroundGraphic.repaintBuffer();
        addGraphic( backgroundGraphic );
        addGraphic( blockGraphic );
        addGraphic( leanerGraphic, 1000 );

        leanerGraphic.setLocation( 400, 100 );

        addGraphic( arrowSetGraphic );

        int strokeWidth = 3;
        forcePlotDeviceView = new Force1DPlotDeviceView( module, this );

        double appliedForceRange = 1000;
        Force1DLookAndFeel laf = module.getForce1DLookAndFeel();
        PlotDevice.ParameterSet forceParams = new PlotDevice.ParameterSet( this, Force1DResources.get( "Force1DPanel.appliedForce" ), model.getPlotDeviceModel(),
                                                                           forcePlotDeviceView, model.getAppliedForceDataSeries().getSmoothedDataSeries(),
//                                                                           laf.getAppliedForceColor(), new BasicStroke( 0 ),
Color.black, new BasicStroke( 10 ),
new Rectangle2D.Double( 0, -appliedForceRange, model.getPlotDeviceModel().getMaxTime(), appliedForceRange * 2 ),
0, Force1DResources.get( "Force1DPanel.newtonsAbbrev" ), Force1DResources.get( "Force1DPanel.appliedForce" ), true, Force1DResources.get( "Force1DPanel.forceWithUnits" ) );

        forceParams.setZoomRates( 300, 100, 5000 );

        forcePlotDevice = new PlotDevice( forceParams, backgroundGraphic );
        forcePlotDevice.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                checkBoxPanelGraphic.setVisible( phetGraphic.isVisible() );
            }
        } );
        forcePlotDevice.removeDefaultDataSeries();
        forcePlotDevice.setAdorned( true );
        forcePlotDevice.setLabelText( Force1DResources.get( "Force1DPanel.appliedForceHTML" ) );
        float frictionForceStrokeWidth = 3;
        float appliedForceStrokeWidth = 3;
        float totalForceStrokeWidth = 3;

        int alpha = 190;
        Color tf = Force1DUtil.transparify( laf.getFrictionForceColor(), alpha );
        Color ta = Force1DUtil.transparify( laf.getAppliedForceColor(), alpha );
        Color tn = Force1DUtil.transparify( laf.getNetForceColor(), alpha );
        int cap = BasicStroke.CAP_BUTT;
        int join = BasicStroke.JOIN_ROUND;
        forcePlotDevice.addDataSeries( model.getFrictionForceSeries(), tf, Force1DResources.get( "ArrowSetGraphic.frictionForce" ), new BasicStroke( frictionForceStrokeWidth, cap, join ) );
        forcePlotDevice.addDataSeries( model.getAppliedForceSeries().getSmoothedDataSeries(), ta, Force1DResources.get( "ArrowSetGraphic.appliedForce" ), new BasicStroke( appliedForceStrokeWidth, cap, join ) );
        forcePlotDevice.addDataSeries( model.getNetForceSeries(), tn, Force1DResources.get( "ArrowSetGraphic.totalForce" ), new BasicStroke( totalForceStrokeWidth, cap, join ) );

        backgroundGraphic.addGraphic( forcePlotDevice );
        addGraphic( forcePlotDevice.getVerticalChartSlider() );
        double accelRange = 10;
        PlotDevice.ParameterSet accelParams = new PlotDevice.ParameterSet( this, Force1DResources.get( "Force1DPanel.acceleration" ), model.getPlotDeviceModel(), forcePlotDeviceView, model.getAccelerationDataSeries(),
                                                                           laf.getAccelerationColor(), new BasicStroke( strokeWidth ),
                                                                           new Rectangle2D.Double( 0, -accelRange, model.getPlotDeviceModel().getMaxTime(), accelRange * 2 ), 0, Force1DResources.get( "Force1DPanel.accelerationUnitsHTML" ), Force1DResources.get( "Force1DPanel.acceleration" ), false, Force1DResources.get( "Force1DPanel.accelerationAndUnitsHTML" ) );

        accelPlotDevice = new PlotDevice( accelParams, backgroundGraphic );
        backgroundGraphic.addGraphic( accelPlotDevice );
        double velRange = 10;
        PlotDevice.ParameterSet velParams = new PlotDevice.ParameterSet( this, Force1DResources.get( "Force1DPanel.velocity" ), model.getPlotDeviceModel(), forcePlotDeviceView, model.getVelocityDataSeries().getSmoothedDataSeries(),
                                                                         laf.getVelocityColor(), new BasicStroke( strokeWidth ),
                                                                         new Rectangle2D.Double( 0, -velRange, model.getPlotDeviceModel().getMaxTime(), velRange * 2 ), 0, Force1DResources.get( "Force1DPanel.velocityUnits" ),
                                                                         Force1DResources.get( "Force1DPanel.velocity" ), false, Force1DResources.get( "Force1DPanel.velocityAndUnits" ) );
        velPlotDevice = new PlotDevice( velParams, backgroundGraphic );
        backgroundGraphic.addGraphic( velPlotDevice );

        double posRange = 10;
        PlotDevice.ParameterSet posParams = new PlotDevice.ParameterSet( this, Force1DResources.get( "Force1DPanel.position" ), model.getPlotDeviceModel(), forcePlotDeviceView, model.getPositionDataSeries().getSmoothedDataSeries(),
                                                                         laf.getPositionColor(), new BasicStroke( strokeWidth ),
                                                                         new Rectangle2D.Double( 0, -posRange, model.getPlotDeviceModel().getMaxTime(), posRange * 2 ), 0, Force1DResources.get( "Force1DPanel.positionUnits" ),
                                                                         Force1DResources.get( "Force1DPanel.position" ), false, Force1DResources.get( "Force1DPanel.positionAndUnits" ) );
        posPlotDevice = new PlotDevice( posParams, backgroundGraphic );
        backgroundGraphic.addGraphic( posPlotDevice );
        forcePlotDevice.addListener( new PlotDevice.Listener() {
            public void readoutChanged( double value ) {
            }

            public void valueChanged( double value ) {
                model.setAppliedForce( value );
            }
        } );
        model.addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                forcePlotDevice.setValue( model.getAppliedForce() );
            }

            public void gravityChanged() {
            }

            public void wallForceChanged() {
            }
        } );
        Font checkBoxFont = new Font( PhetFont.getDefaultFontName(), Font.PLAIN, 13 );
        String stf = Force1DResources.get( "Force1DPanel.showFTotalHTML" );
//        final JCheckBox showNetForce = new JCheckBox( "Show Total Force", true );
        final JCheckBox showNetForce = new JCheckBox( stf, true );

        showNetForce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowForceSeries( 2, showNetForce.isSelected() );
            }
        } );
        showNetForce.setFont( checkBoxFont );


        String text = Force1DResources.get( "Force1DPanel.showFrictionHTML" );
//        String text = "Show Friction Force";
        final JCheckBox showFrictionForce = new JCheckBox( text, true );
        showFrictionForce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowForceSeries( 0, showFrictionForce.isSelected() );
            }
        } );
        showFrictionForce.setFont( checkBoxFont );

        final JCheckBox showAppliedForce = new JCheckBox( Force1DResources.get( "Force1DPanel.showAppliedHTML" ), true );
        showAppliedForce.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setShowForceSeries( 1, showAppliedForce.isSelected() );
            }
        } );
        showAppliedForce.setFont( checkBoxFont );

        JPanel checkBoxPanel = new VerticalLayoutPanel();
        checkBoxPanel.add( showNetForce );
        checkBoxPanel.add( showFrictionForce );
        checkBoxPanel.add( showAppliedForce );

        checkBoxPanelGraphic = PhetJComponent.newInstance( this, checkBoxPanel );
        floatingControl = new FloatingControl( forcePlotDevice.getPlotDeviceModel(), this );

        floatingControlGraphic = PhetJComponent.newInstance( this, floatingControl );
        floatingControlGraphic.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                checkBoxPanelGraphic.setLocation( floatingControlGraphic.getX(), floatingControlGraphic.getY() + floatingControlGraphic.getHeight() + 2 );
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );

        addGraphic( floatingControlGraphic, Double.POSITIVE_INFINITY );
        addGraphic( checkBoxPanelGraphic, Double.POSITIVE_INFINITY );
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
                if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    if ( repaintDebugGraphic.isActive() ) {
                        removeGraphic( repaintDebugGraphic );
                    }
                    else {
                        addGraphic( repaintDebugGraphic, Double.POSITIVE_INFINITY );
                    }
                    repaintDebugGraphic.setActive( !repaintDebugGraphic.isActive() );
                }
            }
        } );
        repaintDebugGraphic = new RepaintDebugGraphic( Force1DPanel.this, module.getClock() );
        repaintDebugGraphic.setTransparency( 128 );
        repaintDebugGraphic.setActive( false );

        offscreenPointerGraphic = new OffscreenPointerGraphic( this, blockGraphic, walkwayGraphic );
        addGraphic( offscreenPointerGraphic, 1000 );
        offscreenPointerGraphic.setLocation( 400, 50 );

        PhetButton phetButton = new PhetButton( this, Force1DResources.get( "Force1DPanel.reset" ) );
        phetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "Resetted." );
                module.reset();
            }
        } );
        addGraphic( phetButton );
        TitleLayout.layout( phetButton, offscreenPointerGraphic );

        MouseInputAdapter listener = new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                forcePlotDevice.getPlotDeviceModel().setRecordMode();
                forcePlotDevice.getPlotDeviceModel().setPaused( false );
            }
        };
        blockGraphic.addMouseInputListener( listener );

        wiggleMe = new WiggleMe( this, module.getClock(), Force1DResources.get( "Force1DPanel.applyForce" ), blockGraphic );
        wiggleMe.setOscillationAxis( new Vector2D.Double( 1, 0 ) );
        addGraphic( wiggleMe, 10000 );
        model.addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
                wiggleMe.setVisible( false );
                removeGraphic( wiggleMe );

            }

            public void gravityChanged() {
            }

            public void wallForceChanged() {
            }
        } );

        sliderWiggleMe = new WiggleMe( this, module.getClock(), Force1DResources.get( "Force1DPanel.wiggleMeText" ),
//                                       new WiggleMe.SwingComponentTarget( this.forcePlotDevice.getVerticalChartSlider().getSlider() ) );
new WiggleMe.Target() {
    public Point getLocation() {
        int x = forcePlotDevice.getVerticalChartSlider().getSlider().getX() + sliderWiggleMe.getWidth() + 10;
        int y = forcePlotDevice.getY() + forcePlotDevice.getHeight() / 2;
        return new Point( x, y );
    }

    public int getHeight() {
        return forcePlotDevice.getVerticalChartSlider().getSlider().getHeight();
    }
} );
        sliderWiggleMe.setArrow( -30, 5 );
        forcePlotDevice.getVerticalChartSlider().getSlider().addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( javax.swing.event.ChangeEvent e ) {
                sliderWiggleMe.setVisible( false );
//                goButtonHelp.setVisible( true );
                if ( !goButtonPressed && module.getForceModel().getPlotDeviceModel().isPaused() ) {
                    soloGoButtonHelp.setVisible( true );
                }
            }
        } );

        floatingControl.getGoButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                soloGoButtonHelp.setVisible( false );
                goButtonPressed = true;
            }
        } );

        addGraphic( sliderWiggleMe, Double.POSITIVE_INFINITY );
        sliderWiggleMe.setVisible( false );

        accelPlotDevice.setVisible( false );
        velPlotDevice.setVisible( false );
        posPlotDevice.setVisible( false );

        model.getPlotDeviceModel().addListener( new PlotDeviceModel.ListenerAdapter() {
            public void rewind() {
                updateGraphics();
            }

        } );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setReferenceSize();
            }
        } );

        HelpItem2 sliderHelp = new HelpItem2( this, Force1DResources.get( "Force1DPanel.sliderHelp" ) );
//        sliderHelp.pointLeftAt( new RelativeLocationSetter.JComponentTarget( forcePlotDevice.getVerticalChartSlider().getSlider(), this ), 30 );
        sliderHelp.pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( forcePlotDevice.getVerticalChartSlider().getSliderGraphic() ), 30 );

        HelpItem2 goButtonHelp = new HelpItem2( this, Force1DResources.get( "Force1DPanel.goRecordHelp" ) );
        PhetGraphic goButtonGraphic = getGoButtonGraphic();

        goButtonHelp.pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( goButtonGraphic ), 30 );

        HelpItem2 dragHelpItem = new HelpItem2( this, Force1DResources.get( "Force1DPanel.applyForceHelp" ) );
        dragHelpItem.pointUpAt( blockGraphic, 15 );

        HelpItem2 zoomHelpButton = new HelpItem2( this, Force1DResources.get( "Force1DPanel.zoomHelp" ) );
        zoomHelpButton.pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( forcePlotDevice.getChartComponent().getMagPlusGraphic() ), 30 );

        HelpItem2 typeInButton = new HelpItem2( this, Force1DResources.get( "Force1DPanel.typeHelp" ) );
//        typeInButton.pointLeftAt( new RelativeLocationSetter.JComponentTarget( forcePlotDevice.getTextBox() ), 20 );
        typeInButton.pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( forcePlotDevice.getTextFieldGraphic() ), 20 );

        module.getHelpManager().addGraphic( sliderHelp );
        module.getHelpManager().addGraphic( goButtonHelp );
        module.getHelpManager().addGraphic( dragHelpItem );
        module.getHelpManager().addGraphic( zoomHelpButton );
        module.getHelpManager().addGraphic( typeInButton );

        soloGoButtonHelp = new HelpItem2( this, Force1DResources.get( "Force1DPanel.pressGoHelp" ) );
//        soloGoButtonHelp.pointLeftAt( new RelativeLocationSetter.JComponentTarget( floatingControl.getGoButton(), this ), 30 );
        soloGoButtonHelp.pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( goButtonGraphic ), 30 );
        addGraphic( soloGoButtonHelp, Double.POSITIVE_INFINITY );
        soloGoButtonHelp.setVisible( false );

        setUseOffscreenBuffer( true );//wow!
        //TODO is offscreen buffer better here?
    }

    private PhetGraphic getGoButtonGraphic() {
        PhetGraphic goButtonGraphic = ( (GraphicLayerSet) floatingControlGraphic ).getGraphics()[1];//TODO this will break when we fix PhetJComponent to have the right tree structure.
        return goButtonGraphic;
    }

    public void paint( Graphics g ) {
        super.paintComponent( g );
    }

    private void setShowForceSeries( int series, boolean selected ) {
        forcePlotDevice.setDataSeriesVisible( series, selected );
        repaintBuffer();
        repaint();
    }

    public Force1DLookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }
//
//    public void repaint( int x, int y, int width, int height ) {
//        super.repaint( x, y, width, height );
//    }

//    private void printStack( int max ) {
//        StackTraceElement[] str = new Exception( "Repaint" ).getStackTrace();
//        for( int i = 0; i < str.length && i < max; i++ ) {
//            StackTraceElement stackTraceElement = str[i];
//            System.out.println( "" + i + ": " + stackTraceElement );
//        }
//        System.out.println( "..." );
//    }

    private boolean didLayout = false;//TODO fix this.

    public void resetDidLayout() {
        this.didLayout = false;
    }

    public void firstLayout() {
        if ( !didLayout ) {
            forceLayout( getWidth(), getHeight() );
        }
    }

    public void setReferenceSize() {
        super.setReferenceSize();
        forceLayout( getWidth(), getHeight() );
        leanerGraphic.screenSizeChanged();
    }

    public void forceLayout( int width, int height ) {
        if ( getWidth() > 0 && getHeight() > 0 ) {
            backgroundGraphic.setSize( width, height );
            Paint background = module.getPhetLookAndFeel().getBackgroundColor();
            backgroundGraphic.setBackground( background );
            int walkwayHeight = width / 6;

            int walkwayInset = 5;
            int walkwayX = walkwayInset;
            int walkwayY = 0;
            int walkwayWidth = width - walkwayInset * 2;
            walkwayTransform.setOutput( walkwayX, walkwayX + walkwayWidth );
            walkwayGraphic.setBounds( walkwayX, walkwayY, walkwayWidth, walkwayHeight );

            layoutPlots( width, height );

            Dimension floDim = floatingControl.getPreferredSize();
            int floX = 5;
            int floY = getHeight() / 2 - floDim.height / 2;

            if ( forcePlotDevice.isVisible() ) {
                Rectangle r = forcePlotDevice.getTextBox().getBounds();
                floY = r.y + r.height + 10;

            }
//            floatingControl.reshape( floX, floY, floDim.width, floDim.height );
            floatingControlGraphic.setLocation( floX, floY );
            updateGraphics();
            repaint();
            didLayout = true;
        }
    }

    public void layoutPlots( int width, int height ) {
        int panelWidth = width;
        int plotInsetX = 200;
        int plotWidth = panelWidth - plotInsetX - 25;
        int plotY = walkwayGraphic.getY() + walkwayGraphic.getHeight() + 20;
        int yInsetBottom = forcePlotDevice.getChart().getHorizontalTicks().getMajorTickTextBounds().height * 2;

        Rectangle chartArea = new Rectangle( plotInsetX, plotY + yInsetBottom, plotWidth, height - plotY - yInsetBottom * 2 );
        if ( chartArea.width > 0 && chartArea.height > 0 ) {
            int separatorWidth = 7;
//            System.out.println( "separatorWidth = " + separatorWidth );
            LayoutUtil layoutUtil = new LayoutUtil( chartArea.getY(), chartArea.getHeight() + chartArea.getY(), separatorWidth );
            PlotDevice[] devices = new PlotDevice[]{forcePlotDevice, accelPlotDevice, velPlotDevice, posPlotDevice};

            LayoutUtil.LayoutElement[] elements = new LayoutUtil.LayoutElement[devices.length];
            int buttonHeight = devices[0].getButtonHeight();
            for ( int i = 0; i < elements.length; i++ ) {
                LayoutUtil.LayoutElement le = new LayoutUtil.Dynamic();
                elements[i] = devices[i].isVisible() ? le : new LayoutUtil.Fixed( buttonHeight );//TODO button size.
            }

            layoutUtil.layout( elements );

            for ( int i = 0; i < elements.length; i++ ) {
                LayoutUtil.LayoutElement element = elements[i];
                if ( devices[i].isVisible() ) {
                    devices[i].setViewBounds( restrictBounds( chartArea, element ) );
                }
                else {
                    devices[i].setButtonLoc( plotInsetX, element.getMin() );
                }
            }
        }
        repaintBuffer();
    }

    public void repaintBuffer() {

        backgroundGraphic.repaintBuffer();
        forcePlotDevice.repaintBuffer();
        accelPlotDevice.repaintBuffer();
        velPlotDevice.repaintBuffer();
        posPlotDevice.repaintBuffer();
    }

    private Rectangle restrictBounds( Rectangle area, LayoutUtil.LayoutElement element ) {
        return new Rectangle( area.x, (int) element.getMin(), area.width, (int) element.getSize() );
    }

    public Function.LinearFunction getWalkwayTransform() {
        return walkwayTransform;
    }

    public WalkwayGraphic getWalkwayGraphic() {
        return walkwayGraphic;
    }

    public void updateGraphics() {
        arrowSetGraphic.updateGraphics();
        blockGraphic.update();
    }

    public void reset() {
        repaintBuffer();
        forcePlotDevice.reset();
        repaint( 0, 0, getWidth(), getHeight() );
        handleWiggleMes();
    }

    private void handleWiggleMes() {
        boolean moved = forcePlotDevice.getVerticalChartSlider().hasMoved();
        if ( !moved ) {
            sliderWiggleMe.setVisible( true );
        }
    }

    public Forces1DModule getModule() {
        return module;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    public void cursorMovedToTime( double time, int index ) {
        model.setPlaybackIndex( index );
        forcePlotDevice.cursorMovedToTime( time, index );
        accelPlotDevice.cursorMovedToTime( time, index );
        velPlotDevice.cursorMovedToTime( time, index );
        posPlotDevice.cursorMovedToTime( time, index );
        updateGraphics();
    }

    public void setHelpEnabled( boolean h ) {
        if ( h ) {
            removeGraphic( wiggleMe );
            addGraphic( wiggleMe, 10000 );
            wiggleMe.setVisible( true );
        }
        else {
            wiggleMe.setVisible( false );
            removeGraphic( wiggleMe );
        }
    }

    public void layoutPlots() {
        layoutPlots( getWidth(), getHeight() );
    }

    public void setChartBackground( Color color ) {
        System.out.println( "color = " + color.getRed() + ", " + color.getBlue() + ", " + color.getGreen() );
        forcePlotDevice.setChartBackground( color );
        accelPlotDevice.setChartBackground( color );
        velPlotDevice.setChartBackground( color );
        posPlotDevice.setChartBackground( color );
        repaintBuffer();
        paintImmediately( 0, 0, getWidth(), getHeight() );
        setReferenceSize();
    }

    public PlotDevice getPlotDevice() {
        return forcePlotDevice;
    }

    public void clearData() {
        forcePlotDevice.clearData();
    }


    public void setPhetFrame( PhetFrame phetFrame ) {
        ContentPanel contentPanel = phetFrame.getBasicPhetPanel();
//        contentPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                setReferenceSize();//will this happen before the ApparatusPanel resizes?
//            }
//
//            public void componentShown( ComponentEvent e ) {
//                setReferenceSize();
//            }
//        } );
    }
}
