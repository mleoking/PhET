/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.clock.StopwatchPanel;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BoundNode;
import edu.colorado.phet.common.piccolophet.nodes.ConnectorNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.controls.ResolutionControl;
import edu.colorado.phet.quantumwaveinterference.model.Detector;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.quantumwaveinterference.phetcommon.RulerGraphic;
import edu.colorado.phet.quantumwaveinterference.phetcommon.SchrodingerRulerGraphic;
import edu.colorado.phet.quantumwaveinterference.view.ClockGraphic;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;
import edu.colorado.phet.quantumwaveinterference.view.gun.GunControlPanel;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.IntensityManager;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.detectorscreen.SavedScreenGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Sep 17, 2005
 * Time: 7:52:28 PM
 */

public class QWIScreenNode extends PNode {
    private QWIModule module;
    private QWIPanel QWIPanel;

    private WavefunctionGraphic wavefunctionGraphic;
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private ArrayList detectorGraphics = new ArrayList();

    private AbstractGunNode abstractGunNode;
    private IntensityManager intensityManager;
    private SchrodingerRulerGraphic rulerGraphic;

    private Dimension lastLayoutSize = null;
    private static final int WAVE_AREA_LAYOUT_INSET_X = 20;
    private static final int WAVE_AREA_LAYOUT_INSET_Y = 20;
    public static int numIterationsBetwenScreenUpdate = 2;
    private DetectorSheetPNode detectorSheetPNode;
    private StopwatchPanel stopwatchPanel;
    private ParticleUnits particleUnits = new ParticleUnits.PhotonUnits();
    private Color TEXT_BACKGROUND = new Color( 255, 245, 190 );
    private PNode gunTypeChooserGraphic;
    private PSwing stopwatchPanelPSwing;
    private PNode gunControlPanelPSwing;
    //    private String slowdownText = "Slowing down the simulation to observe faster phenomenon...";
    //    private String speedupText = "Speeding up the simulation to observe slower phenomenon...";
    private String slowdownText = QWIResources.getString( "scale.slowing-down-time" );
    private String speedupText = QWIResources.getString( "scale.speeding-up-time" );
    private boolean rescaleWaveGraphic = false;
    private int cellSize = 8;
    private static final boolean FIXED_SIZE_WAVE = false;
    private String zoomoutText = QWIResources.getString( "scale.zooming-out" );
    private String zoominText = QWIResources.getString( "scale.zooming-in" );
    private PNode detectorScreenGraphics;
    private PNode potentialNode = new PNode();

    public QWIScreenNode( final QWIModule module, final QWIPanel QWIPanel ) {
        this.module = module;
        this.QWIPanel = QWIPanel;
        wavefunctionGraphic = createWavefunctionGraphic();
        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void finishedTimeStep( QWIModel model ) {
                if( model.getTimeStep() % numIterationsBetwenScreenUpdate == 0 || module.getClock().isPaused() ) {
                    updateWaveGraphic();
                }
            }
        } );

        String[] digits = new String[11];
        for( int i = 0; i < digits.length; i++ ) {
            digits[i] = new String( i + "" );
        }
        RulerGraphic rg = new RulerGraphic( digits, QWIResources.getString( "units" ), 500, 60 );
        rulerGraphic = new SchrodingerRulerGraphic( getDiscreteModel(), QWIPanel, rg );

        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                updateRulerUnits();
                layoutChildren( true );
            }
        } );
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateRulerUnits();
            }
        } );

        rulerGraphic.setOffset( 50, 200 );
        setRulerVisible( false );

        detectorSheetPNode = new DetectorSheetPNode( QWIPanel, wavefunctionGraphic, 60 );
        detectorSheetPNode.setOffset( wavefunctionGraphic.getX(), 0 );
        intensityManager = new IntensityManager( getSchrodingerModule(), QWIPanel, detectorSheetPNode );
        addChild( detectorSheetPNode );
        addChild( wavefunctionGraphic );
        detectorScreenGraphics = new PNode();
        addChild( potentialNode );
        addChild( detectorScreenGraphics );
        addChild( rulerGraphic );
        QWIPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }

            public void componentShown( ComponentEvent e ) {
                invalidateFullBounds();
                repaint();
            }

        } );

        layoutChildren();
        stopwatchPanel = new StopwatchPanel( QWIPanel.getSchrodingerModule().getClock(), QWIResources.getString( "ps" ), 1.0, new DecimalFormat( "0.00" ) );
        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                stopwatchPanel.reset();
            }
        } );
        stopwatchPanel.getTimeDisplay().setEditable( false );
        stopwatchPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
        stopwatchPanelPSwing = new PSwing( stopwatchPanel );
//        stopwatchPanelPSwing = new PSwing( schrodingerPanel, new JButton( "Test Button" ));
//        stopwatchPanelPSwing.addInputEventListener( new PDragEventHandler() );
//        stopwatchPanelPSwing.addInputEventListener( new HalfOnscreenDragHandler( schrodingerPanel, stopwatchPanelPSwing ) );
        stopwatchPanelPSwing.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatchPanelPSwing.addInputEventListener( new PDragEventHandler() {
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                if( !QWIPanel.getBounds().contains( stopwatchPanelPSwing.getFullBounds() ) ) {
                    stopwatchPanelPSwing.setOffset( 300, 300 );
                }
            }
        } );
//        stopwatchPanelPSwing.addInputEventListener( new PDragEventHandler() );
//        PNode root = new PhetPNode();
//        root.addChild( stopwatchPanelPSwing );
        addChild( stopwatchPanelPSwing );
//        root.addInputEventListener( new PDragEventHandler() );
        stopwatchPanelPSwing.setOffset( 300, 300 );
        setStopwatchVisible( false );

        module.getQWIModel().addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                setUnits( particleUnits );//see note in setUnits.
            }
        } );

        //•	If the simulation is paused and you hit the "Double Slits" button, the slits don't appear until you unpause it.  
        // They should appear right away.  Same for disabling the slits.
        module.getQWIModel().addListener( new QWIModel.Adapter() {
            public void potentialChanged() {
                updateWaveGraphic();
            }

            public void doubleSlitVisibilityChanged() {
                updateWaveGraphic();
            }
        } );
    }

    protected WavefunctionGraphic createWavefunctionGraphic() {
        return new WavefunctionGraphic( getDiscreteModel(), module.getQWIModel().getWavefunction() );
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return wavefunctionGraphic;
    }

    protected QWIModel getDiscreteModel() {
        return getSchrodingerModule().getQWIModel();
    }

    private QWIModule getSchrodingerModule() {
        return QWIPanel.getSchrodingerModule();
    }

    public void setGunGraphic( AbstractGunNode abstractGunNode ) {
        if( abstractGunNode != null ) {
            if( getChildrenReference().contains( abstractGunNode ) ) {
                removeChild( abstractGunNode );
            }
        }
        this.abstractGunNode = abstractGunNode;
        int waveareaIndex = 0;
        if( getChildrenReference().contains( wavefunctionGraphic ) ) {
            waveareaIndex = getChildrenReference().indexOf( wavefunctionGraphic );
        }
        addChild( waveareaIndex + 1, abstractGunNode );
        invalidateLayout();
        repaint();
    }

    private int getGunGraphicOffsetY() {
        return 50;
    }

    public void setRulerVisible( boolean rulerVisible ) {
        rulerGraphic.setVisible( rulerVisible );
    }

    public void reset() {
        detectorSheetPNode.reset();
        intensityManager.reset();
//        getGunGraphic().reset();
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        detectorGraphics.add( detectorGraphic );
        addChild( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        rectanglePotentialGraphics.add( rectangularPotentialGraphic );
        potentialNode.addChild( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        while( rectanglePotentialGraphics.size() > 0 ) {
            removePotentialGraphic( (RectangularPotentialGraphic)rectanglePotentialGraphics.get( 0 ) );
        }
    }

    public IntensityManager getIntensityDisplay() {
        return intensityManager;
    }

    public SchrodingerRulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }

    public AbstractGunNode getGunGraphic() {
        return abstractGunNode;
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        removeChild( detectorGraphic );
        getDiscreteModel().removeDetector( detectorGraphic.getDetector() );
        detectorGraphics.remove( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        for( int i = 0; i < detectorGraphics.size(); i++ ) {
            DetectorGraphic detectorGraphic = (DetectorGraphic)detectorGraphics.get( i );
            if( detectorGraphic.getDetector() == detector ) {
                return detectorGraphic;
            }
        }
        return null;
    }

    public void setWaveGraphicGridSize( int width, int height ) {
        wavefunctionGraphic.setGridDimensions( width, height );
        relayout();
    }

    public void relayout() {
        layoutChildren( true );
    }

    protected void layoutChildren() {
        layoutChildren( false );
    }

    protected void layoutChildren( boolean forceLayout ) {
        boolean sizeChanged = lastLayoutSize == null || !lastLayoutSize.equals( QWIPanel.getSize() );
        if( sizeChanged || forceLayout ) {
            lastLayoutSize = new Dimension( QWIPanel.getSize() );
            super.layoutChildren();
            if( QWIPanel.getWidth() > 0 && QWIPanel.getHeight() > 0 ) {
                wavefunctionGraphic.setCellDimensions( getCellDimensions() );
//                System.out.println( "getCellDimensions() = " + getCellDimensions() );
                int colorGridWidth = wavefunctionGraphic.getColorGrid().getBufferedImage().getWidth();
                int latticeWidth = getWavefunctionGraphic().getWavefunction().getWidth();
//                System.out.println( "latticeWidth = " + latticeWidth );
//                System.out.println( "colorGridImageWidth = " + colorGridWidth );
                double minX = getLayoutMinX();
                double maxX = Math.max( detectorSheetPNode.getFullBounds().getMaxX(), abstractGunNode.getFullBounds().getMaxX() );
//                minX = Math.max( minX, 0 );

//                System.out.println( "minX = " + minX );
//                System.out.println( "maxX = " + maxX );
                double mainWidth = maxX - minX;
//                System.out.println( "mainWidth = " + mainWidth );
                double availableWidth = QWIPanel.getWidth() - mainWidth;
//                System.out.println( "availableWidth = " + availableWidth );
                double wavefunctionGraphicX = getWavefunctionGraphicX( availableWidth );
//                System.out.println( "wavefunctionGraphicX = " + wavefunctionGraphicX );
//                System.out.println( "wavefunctionGraphicX = " + wavefunctionGraphicX );
                wavefunctionGraphic.setOffset( wavefunctionGraphicX, detectorSheetPNode.getDetectorHeight() );
//                wavefunctionGraphic.setOffset( 5, detectorSheetPNode.getDetectorHeight() );

                detectorSheetPNode.setAlignment( wavefunctionGraphic );
                abstractGunNode.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - abstractGunNode.getGunWidth() / 2 + 10,
                                           wavefunctionGraphic.getFullBounds().getMaxY() - getGunGraphicOffsetY() );

                if( gunControlPanelPSwing != null ) {
                    double insetY = 5;
                    double screenHeight = QWIPanel.getHeight();
                    //double xval = wavefunctionGraphic.getFullBounds().getMaxX();
                    Point2D pt = new Point2D.Double();
                    detectorSheetPNode.getDetectorSheetControlPanelPNode().localToGlobal( pt );
                    globalToLocal( pt );

                    double gunControlRequestedY = abstractGunNode.getFullBounds().getCenterY() - gunControlPanelPSwing.getFullBounds().getHeight() / 2.0;
                    double gunControlMaxY = screenHeight - gunControlPanelPSwing.getFullBounds().getHeight() - insetY;
                    double gunControlY = Math.min( gunControlRequestedY, gunControlMaxY );
                    gunControlPanelPSwing.setOffset( pt.getX(), gunControlY );
                }
                if( gunTypeChooserGraphic != null ) {
                    gunTypeChooserGraphic.setOffset( gunControlPanelPSwing.getFullBounds().getCenterX() - gunTypeChooserGraphic.getFullBounds().getWidth() / 2,
                                                     gunControlPanelPSwing.getFullBounds().getY() - gunTypeChooserGraphic.getFullBounds().getHeight() - 2 );
                }
                if( rescaleWaveGraphic ) {
                    wavefunctionGraphic.setScale( 1.0 );
                    double scaleX = getAvailableWaveAreaSize().width / wavefunctionGraphic.getFullBounds().getWidth();
                    double scaleY = getAvailableWaveAreaSize().height / wavefunctionGraphic.getFullBounds().getHeight();
                    double min = Math.min( scaleX, scaleY );
                    wavefunctionGraphic.setScale( min );
                }
            }
        }
    }

    protected double getLayoutMinX() {
        return Math.min( detectorSheetPNode.getFullBounds().getMinX(), abstractGunNode.getFullBounds().getMinX() );
    }

    protected double getWavefunctionGraphicX( double availableWidth ) {
        return availableWidth / 2;
    }

    private Dimension getCellDimensions() {
        if( FIXED_SIZE_WAVE ) {
            return new Dimension( getCellSize(), getCellSize() );
        }
        else {
            Dimension availableAreaForWaveform = getAvailableWaveAreaSize();
            int nx = QWIPanel.getDiscreteModel().getGridWidth() + 5;
            int ny = QWIPanel.getDiscreteModel().getGridHeight() + 5;
            int cellWidth = availableAreaForWaveform.width / nx;
            int cellHeight = availableAreaForWaveform.height / ny;
            int min = Math.min( cellWidth, cellHeight );
            return new Dimension( min, min );
        }
    }

    public Dimension getAvailableWaveAreaSize() {
        Dimension availableSize = QWIPanel.getSize();
        availableSize.width -= getDetectorSheetControlPanelNode().getFullBounds().getWidth();
        availableSize.width -= WAVE_AREA_LAYOUT_INSET_X;

        availableSize.height -= abstractGunNode.getFullBounds().getHeight();
        availableSize.height -= WAVE_AREA_LAYOUT_INSET_Y;

        return new Dimension( availableSize.width, availableSize.height );
    }

    private PNode getDetectorSheetControlPanelNode() {
        return detectorSheetPNode.getDetectorSheetControlPanelPNode();
    }

    public void removePotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        potentialNode.removeChild( rectangularPotentialGraphic );
        rectanglePotentialGraphics.remove( rectangularPotentialGraphic );
    }

    public DetectorSheetPNode getDetectorSheetPNode() {
        return detectorSheetPNode;
    }

    public void setUnits( ParticleUnits particleUnits ) {
        if( particleUnits == null ) {
            return;
        }
//        System.out.println( "particleUnits = " + particleUnits );
        String origTimeUnits = this.particleUnits.getDt().getUnits();

        /*Speed of light is correct at low resolution, but is 2x too slow for
medium resolution and 4x too slow for high resolution.  To fix this,
clock should tick half as many times per time step for medium
resolution, and a quarter as many times for high resolution.*/

        double origLatticeWidth = this.particleUnits.getLatticeWidth();
        this.particleUnits = particleUnits;
//        int numLatticePointsX = getWavefunctionGraphic().getWavefunction().getWidth();
//        double maxMeasurementValue = numLatticePointsX * particleUnits.getDx().getDisplayValue();
        updateRulerUnits();
        stopwatchPanel.setTimeUnits( particleUnits.getDt().getUnits() );
//        System.out.println( "particleUnits.getDt() = " + particleUnits.getDt() );
//        System.out.println( "getTimeFudgeFactor() = " + getTimeFudgeFactor() );
        stopwatchPanel.setScaleFactor( getTimeFudgeFactor() * particleUnits.getDt().getDisplayValue() * particleUnits.getTimeScaleFactor() );
//        stopwatchPanel.setScaleFactor( particleUnits.getDt().getDisplayScaleFactor() * getTimeFudgeFactor() );
        stopwatchPanel.setTimeFormat( new DecimalFormat( "0.000" ) );
        stopwatchPanel.reset();
        String newTimeUnits = particleUnits.getDt().getUnits();
        String[] times = new String[]{QWIResources.getString( "ns" ), QWIResources.getString( "ps" ), QWIResources.getString( "fs" )};
        ArrayList list = new ArrayList( Arrays.asList( times ) );

        int change = list.indexOf( newTimeUnits ) - list.indexOf( origTimeUnits );
//        System.out.println( "change = " + change );
        if( change == 0 ) {
            //do nothing
        }
        else if( change > 0 ) {
            //speed up time
            showTimeSpeedUp();
        }
        else if( change < 0 ) {
            //slow down time
            showTimeSlowDown();
        }
        rulerGraphic.setUnits( particleUnits.getDx().getUnits() );

        if( particleUnits.getLatticeWidth() != origLatticeWidth ) {
            showLatticeSizeChange( origLatticeWidth );
        }
    }

    private double getTimeFudgeFactor() {
        ResolutionControl.ResolutionSetup res = module.getResolution();
        if( module.getQWIModel().getPropagator() instanceof ClassicalWavePropagator ) {
            return res.getTimeFudgeFactorForLight();
        }
        else {
            return res.getTimeFudgeFactorForParticles();
        }
    }

    private void showLatticeSizeChange( double origLatticeWidth ) {
        if( particleUnits.getLatticeWidth() > origLatticeWidth ) {
            showZoomIn();
        }
        else if( particleUnits.getLatticeWidth() < origLatticeWidth ) {
            showZoomOut();
        }
    }

    protected void showZoomOut() {
        final PImage child = PImageFactory.create( "quantum-wave-interference/images/glassMinus.gif" );
        showZoom( child, zoomoutText );
    }

    protected void showZoom( final PImage child, String text ) {
        final Timer timer = new Timer( 0, null );
        timer.setInitialDelay( 3000 );
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                removeChild( child );
                timer.stop();
            }
        };
        timer.addActionListener( listener );
        addChild( child );

        PText shadowPText = new PText( text );
        shadowPText.setTextPaint( Color.blue );

        shadowPText.setOffset( 0, child.getFullBounds().getHeight() + 10 );
        BoundNode boundGraphic = new BoundNode( shadowPText, 4, 4 );

        boundGraphic.setPaint( TEXT_BACKGROUND );
        child.addChild( boundGraphic );
        child.addChild( shadowPText );
        child.setOffset( wavefunctionGraphic.getFullBounds().getCenterX() - child.getFullBounds().getWidth() / 2, wavefunctionGraphic.getFullBounds().getCenterY() - child.getFullBounds().getHeight() / 2 );
        timer.start();
    }

    private void showZoomIn() {
        final PImage child = PImageFactory.create( "quantum-wave-interference/images/glassPlus.gif" );
        showZoom( child, zoominText );
    }

    private void updateRulerUnits() {
        String[] readings = new String[particleUnits.getNumRulerReadings()];
        double dx = particleUnits.getRulerWidth() / ( readings.length - 1 );
        for( int i = 0; i < readings.length; i++ ) {
            double v = i * dx;
            readings[i] = new String( "" + particleUnits.getRulerFormat().format( v ) + "" );
        }
        rulerGraphic.getRulerGraphic().setReadings( readings );

        double waveAreaPixelWidth = wavefunctionGraphic.getWavefunctionGraphicWidth() * particleUnits.getRulerWidth() / particleUnits.getLatticeWidth();
        rulerGraphic.getRulerGraphic().setMeasurementPixelWidth( waveAreaPixelWidth );
        rulerGraphic.setUnits( particleUnits.getDx().getUnits() );
    }

    private double lowDT = 0.3;
    private double highDT = 4.0;
    private double ddt = 0.1;
    int MAX = 50;

    private void showTimeSpeedUp() {
        final ClockGraphic child = new ClockGraphic();

        final Timer timer = new Timer( 30, null );
        ActionListener listener = new ActionListener() {
            double dt = lowDT;
            double t = 0;
            int numAfter = 0;

            public void actionPerformed( ActionEvent e ) {
                t += dt;
                if( !( dt < lowDT || dt > highDT ) ) {
                    dt += ddt;
                }

                child.setTime( t / 10.0 );
                if( dt < lowDT || dt > highDT ) {
                    numAfter++;
                    if( numAfter > MAX ) {
                        timer.stop();
                        removeChild( child );
                    }
                }
            }
        };
        timer.addActionListener( listener );
        addChild( child );

        PText shadowPText = new PText( speedupText );
        shadowPText.setTextPaint( Color.blue );

        BoundNode boundGraphic = new BoundNode( shadowPText, 4, 4 );

        boundGraphic.setPaint( TEXT_BACKGROUND );
        child.addChild( boundGraphic );
        child.addChild( shadowPText );
        child.setOffset( abstractGunNode.getFullBounds().getX(), abstractGunNode.getFullBounds().getY() );
        timer.start();
    }

    private void showTimeSlowDown() {
        final ClockGraphic child = new ClockGraphic();

        final Timer timer = new Timer( 30, null );
        ActionListener listener = new ActionListener() {
            double dt = highDT;
            double t = 0;
            int numAfter = 0;

            public void actionPerformed( ActionEvent e ) {
                t += dt;
                if( !( dt < lowDT || dt > highDT ) ) {
                    dt -= ddt;
                }

                child.setTime( t / 10.0 );
                if( dt < lowDT || dt > highDT ) {
                    numAfter++;

                    if( numAfter > MAX ) {
                        timer.stop();
                        removeChild( child );
                    }
                }
            }
        };
        timer.addActionListener( listener );
        addChild( child );
        PText shadowPText = new PText( slowdownText );
        shadowPText.setTextPaint( Color.blue );

        BoundNode boundGraphic = new BoundNode( shadowPText, 4, 4 );
        boundGraphic.setPaint( TEXT_BACKGROUND );
        child.addChild( boundGraphic );
        child.addChild( shadowPText );
        child.setOffset( abstractGunNode.getFullBounds().getX(), abstractGunNode.getFullBounds().getY() );
        timer.start();
    }

    public void setGunTypeChooserGraphic( PNode chooser ) {
        addChild( chooser );
        this.gunTypeChooserGraphic = chooser;
        invalidateLayout();
        repaint();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchPanelPSwing.setVisible( selected );
    }

    public boolean isRulerVisible() {
        return rulerGraphic.getVisible();
    }

    public void updateWaveGraphic() {
        wavefunctionGraphic.update();
    }

    public void setGunControlPanel( GunControlPanel gunControlPanel ) {
        if( gunControlPanel == null ) {
            if( this.gunControlPanelPSwing != null ) {
                removeChild( this.gunControlPanelPSwing );
            }
            this.gunControlPanelPSwing = null;
            relayout();
        }
        else {
            int gunIndex = 0;
            if( getChildrenReference().contains( abstractGunNode ) ) {
                gunIndex = getChildrenReference().indexOf( abstractGunNode );
            }
            addChild( gunIndex, gunControlPanel.getPSwing() );
            this.gunControlPanelPSwing = gunControlPanel.getPSwing();
            relayout();

            ConnectorNode connectorGraphic = new HorizontalWireConnector( gunControlPanelPSwing, abstractGunNode );
            addChild( gunIndex, connectorGraphic );
        }
    }

    public void setCellSize( int size ) {
        if( this.cellSize != size ) {
            this.cellSize = size;
        }
    }

    public int getCellSize() {
        return cellSize;
    }

    public void addSavedScreenGraphic( SavedScreenGraphic savedScreenGraphic ) {
        detectorScreenGraphics.addChild( savedScreenGraphic );
    }

    public void removeSavedScreenGraphic( SavedScreenGraphic savedScreenGraphic ) {
        detectorScreenGraphics.removeChild( savedScreenGraphic );
    }

    public QWIModel getQWIModel() {
        return getDiscreteModel();
    }
}
