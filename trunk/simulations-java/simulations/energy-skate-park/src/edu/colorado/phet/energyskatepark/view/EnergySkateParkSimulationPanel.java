// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.piccolophet.event.PanZoomWorldKeyHandler;
import edu.colorado.phet.common.spline.ControlPointParametricFunction2D;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.BumpUpSplines;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.PreFabSplines;
import edu.colorado.phet.energyskatepark.model.TraversalState;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkRootNode;
import edu.colorado.phet.energyskatepark.view.piccolo.SkaterNode;
import edu.colorado.phet.energyskatepark.view.piccolo.SplineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:51 AM
 */

public class EnergySkateParkSimulationPanel extends PhetPCanvas implements EnergySkateParkSplineEnvironment {
    private final AbstractEnergySkateParkModule module;
    private final EnergySkateParkModel energySkateParkModel;
    private final MultiKeyHandler multiKeyHandler = new MultiKeyHandler();
    private final EnergySkateParkRootNode rootNode;
    private final double matchThresholdWorldCoordinates = 1.5 * 0.33;
    private final ArrayList<Listener> listeners = new ArrayList<Listener>();

    //Flag that indicates whether the keyboard can activate rocket thrusters
    private boolean thrustEnabled = true;
    public static final double VIEW_WIDTH = 15;

    private final ArrayList<VoidFunction0> splineDeletedByUserListeners = new ArrayList<VoidFunction0>();

    public final Property<ImmutableRectangle2D> viewRect = new Property<ImmutableRectangle2D>( new ImmutableRectangle2D( 0, 0 ) ) {{
        addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                set( new ImmutableRectangle2D( getWidth(), getHeight() ) );
            }
        } );
    }};
    public final ObservableProperty<ImmutableRectangle2D> modelRect;

    public EnergySkateParkSimulationPanel( final AbstractEnergySkateParkModule module, boolean hasZoomControls, double gridHighlightX ) {
        super( new Rectangle2D.Double( 0, -1.5, VIEW_WIDTH, 10 ) );
        this.module = module;
        this.energySkateParkModel = module.getEnergySkateParkModel();
        this.rootNode = new EnergySkateParkRootNode( module, this, hasZoomControls, gridHighlightX );
        modelRect = new CompositeProperty<ImmutableRectangle2D>( new Function0<ImmutableRectangle2D>() {
            public ImmutableRectangle2D apply() {
                //Shrink by 50px so less chance of jumping out of the screen
                return new ImmutableRectangle2D( rootNode.gridNode.globalToLocal( viewRect.get().shrink( 50 ).toRectangle2D() ) );
            }
        }, viewRect );
        setPhetRootNode( rootNode );
        addFocusRequest();
        addKeyHandling();
        addKeyListener( new PanZoomWorldKeyHandler( this ) );
        energySkateParkModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void preStep() {
                updateThrust();
            }
        } );
        addKeyListener( new PDebugKeyHandler() );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if ( energySkateParkModel.getGravity() != EnergySkateParkModel.G_SPACE &&
                     module.bumpUpSplines ) {
                    new BumpUpSplines( energySkateParkModel, modelRect.get() ).bumpUpSplines();
                }

                //Run it later since other events must be handled first in SplineNode so that a track can be dropped directly from toolbox to connect to another track
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        getRootNode().updateSplineNodes();//todo this hack is in place for now because spline nodes don't follow good mvc pattern yet
                    }
                } );
            }
        } );

        //Some Events should be captured even if the simulation panel doesn't have focus, e.g. for thrust
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if ( !hasFocus() ) {
                    int id = e.getID();
                    switch( id ) {
                        case KeyEvent.KEY_PRESSED:
                            keyPressed( e );
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keyReleased( e );
                            break;
                        case KeyEvent.KEY_TYPED:
                            keyTyped( e );
                            break;
                        default:
                            EnergySkateParkLogging.println( "unknown key event type" );
                            break;
                    }
                }
                return false;
            }
        } );
    }

    protected void updateWorldScale() {
        super.updateWorldScale();
        if ( rootNode != null ) {
            rootNode.updateScale();
        }
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        super.paintComponent( g );
    }

    private void addFocusRequest() {
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
    }

    private void addKeyHandling() {
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                EnergySkateParkSimulationPanel.this.keyPressed( e );
            }

            public void keyReleased( KeyEvent e ) {
                EnergySkateParkSimulationPanel.this.keyReleased( e );
            }

            public void keyTyped( KeyEvent e ) {
                EnergySkateParkSimulationPanel.this.keyTyped( e );
            }
        } );
    }

    private void updateThrust() {
        if ( energySkateParkModel.getNumBodies() > 0 && thrustEnabled ) {
            Body body = energySkateParkModel.getBody( 0 );
            double xThrust = 0.0;
            double yThrust = 0.0;
            int thrustValue = 15;
            if ( multiKeyHandler.isPressed( KeyEvent.VK_RIGHT ) ) {
                xThrust = thrustValue;
            }
            else if ( multiKeyHandler.isPressed( KeyEvent.VK_LEFT ) ) {
                xThrust = -thrustValue;
            }
            if ( multiKeyHandler.isPressed( KeyEvent.VK_UP ) ) {
                yThrust = thrustValue;
            }
            else if ( multiKeyHandler.isPressed( KeyEvent.VK_DOWN ) ) {
                yThrust = -thrustValue;
            }
            body.setThrust( xThrust, yThrust );
        }
    }

    private void removeSkater() {
        if ( getEnergySkateParkModel().getNumBodies() > 1 ) {
            energySkateParkModel.removeBody( 1 );
        }
    }

    private void addSkater() {
        Body body = module.createBody();
        energySkateParkModel.addBody( body );
        module.reinitializeSkater( body );
        module.updatePrimarySkaterSpeed();
    }

    public SplineMatch proposeMatch( SplineNode splineNode, final Point2D toMatch ) {
        ArrayList<SplineMatch> matches = new ArrayList<SplineMatch>();
        for ( int i = 0; i < numSplineGraphics(); i++ ) {
            SplineNode target = getSplineNode( i );
            PNode startNode = target.getControlPointGraphic( 0 );
            double dist = distance( toMatch, startNode );

            if ( dist < matchThresholdWorldCoordinates && ( splineNode.getSpline() != target.getSpline() ) ) {
                SplineMatch match = new SplineMatch( target, 0 );
                matches.add( match );
            }

            PNode endNode = target.getControlPointGraphic( target.numControlPointGraphics() - 1 );
            double distEnd = distance( toMatch, endNode );
            if ( distEnd < matchThresholdWorldCoordinates && ( splineNode.getSpline() != target.getSpline() ) ) {
                SplineMatch match = new SplineMatch( target, target.numControlPointGraphics() - 1 );
                matches.add( match );
            }
        }
        Collections.sort( matches, new Comparator<SplineMatch>() {
            public int compare( SplineMatch a, SplineMatch b ) {
                return Double.compare( distance( toMatch, a.getTarget() ), distance( toMatch, b.getTarget() ) );
            }
        } );
        if ( matches.size() == 0 ) {
            return null;
        }
        return matches.get( 0 );
    }

    private double distance( Point2D toMatch, PNode startNode ) {
        return startNode.getFullBounds().getCenter2D().distance( toMatch );
    }

    private SplineNode getSplineNode( int i ) {
        return rootNode.splineGraphicAt( i );
    }

    private int numSplineGraphics() {
        return rootNode.numSplineGraphics();
    }

    public EnergySkateParkSpline attach( SplineNode splineNode, int index, SplineMatch match ) {
//        System.out.println( "attaching a = " + splineNode.getParametricFunction2D() );
//        System.out.println( "attaching b = " + match.getSplineGraphic().getParametricFunction2D() );
//        System.out.println( "index = " + index );
        TraversalState origState = getEnergySkateParkModel().getBody( 0 ).getTraversalState();
        boolean change = false;
        boolean rollerCoaster = getRollerCoaster( splineNode.getSpline(), match.getEnergySkateParkSpline() );
        if ( getEnergySkateParkModel().getBody( 0 ).getSpline() == splineNode.getParametricFunction2D() || getEnergySkateParkModel().getBody( 0 ).getSpline() == match.getSplineGraphic().getParametricFunction2D() ) {
            change = true;
        }

        PreFabSplines.CubicSpline spline = new PreFabSplines.CubicSpline();
        ControlPointParametricFunction2D a = splineNode.getSpline().getParametricFunction2D();
        EnergySkateParkSpline b = match.getEnergySkateParkSpline();
        if ( index == 0 ) {
            for ( int i = a.numControlPoints() - 1; i >= 0; i-- ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        else {
            for ( int i = 0; i < a.numControlPoints(); i++ ) {
                spline.addControlPoint( a.controlPointAt( i ) );
            }
        }
        if ( match.matchesBeginning() ) {
            for ( int i = 1; i < b.numControlPoints(); i++ ) {
                spline.addControlPoint( b.getControlPoint( i ) );
            }
        }
        else if ( match.matchesEnd() ) {
            for ( int i = b.numControlPoints() - 2; i >= 0; i-- ) {
                spline.addControlPoint( b.getControlPoint( i ) );
            }
        }
        EnergySkateParkSpline energySkateParkSpline = new EnergySkateParkSpline( spline.getControlPoints() );
//        System.out.println( "energySkateParkSpline @ creation= " + energySkateParkSpline );
        energySkateParkModel.setRollerCoasterMode( rollerCoaster );
        energySkateParkSpline.setRollerCoasterMode( rollerCoaster );

//        EnergySkateParkLogging.println( "change = " + change );
        if ( change ) {
            TraversalState traversalState = energySkateParkModel.getBody( 0 ).getBestTraversalState( origState );
            energySkateParkModel.getBody( 0 ).setSpline( energySkateParkModel.getEnergySkateParkSpline( traversalState.getParametricFunction2D() ), traversalState.isTop(), traversalState.getAlpha() );
        }
//        System.out.println( "energySkateParkSpline @ end= " + energySkateParkSpline );

        //I recently moved these lines below the addition and logic code above because when these lines of code were above it was causing buggy attachment behavior.
        //I suspect that some of the above code is looking up control points by index in the model rather than using the argument references
        // delete both of those, add one new parent.
        removeSpline( splineNode );
        removeSpline( match.getSplineGraphic() );

        //Also this has to be added afterwards?  Why?  It makes no sense.
        energySkateParkModel.addSplineSurface( energySkateParkSpline );
        return energySkateParkSpline;
    }

    //Can't drag below ground
    public double getMinDragY() {
        return 0;
    }

    public double getMaxDragY() {
        return modelRect.get().getMaxY();
    }

    public double getMinDragX() {
        return modelRect.get().x;
    }

    public double getMaxDragX() {
        return modelRect.get().getMaxX();
    }

    public void notifySplineDeletedByUser() {
        for ( VoidFunction0 splineDeletedByUserListener : splineDeletedByUserListeners ) {
            splineDeletedByUserListener.apply();
        }
    }

    public void setRollerCoasterMode( boolean rollerCoasterMode ) {
        module.getEnergySkateParkModel().setRollerCoasterMode( rollerCoasterMode );
    }

    public void addSplineDeletedByUserListener( VoidFunction0 listener ) {
        splineDeletedByUserListeners.add( listener );
    }

    private boolean getRollerCoaster( EnergySkateParkSpline s1, EnergySkateParkSpline s2 ) {
        return s1.isRollerCoasterMode() || s2.isRollerCoasterMode();
//        return s1.numControlPoints() > s2.numControlPoints() ? s1.isRollerCoasterMode() : s2.isRollerCoasterMode();
    }

    public EnergySkateParkModel getEnergySkateParkModel() {
        return energySkateParkModel;
    }

    public void removeSpline( SplineNode splineNode ) {
        energySkateParkModel.removeSplineSurface( splineNode.getSpline() );
    }

    public AbstractEnergySkateParkModule getEnergySkateParkModule() {
        return module;
    }

    public void reset() {
        rootNode.reset();
        setZeroPointVisible( false );
        setIgnoreThermal( EnergySkateParkApplication.IGNORE_THERMAL_DEFAULT );
    }

    private void keyPressed( KeyEvent e ) {
        multiKeyHandler.keyPressed( e );
        if ( hasFocus() ) {

            //For debugging with multiple skaters
//            if ( e.getKeyCode() == KeyEvent.VK_A ) {
//                addSkater();
//            }
//            else if ( e.getKeyCode() == KeyEvent.VK_R ) {
//                removeSkater();
//            }
        }
    }

    private void keyReleased( KeyEvent e ) {
        multiKeyHandler.keyReleased( e );
    }

    private void keyTyped( KeyEvent e ) {
        multiKeyHandler.keyTyped( e );
    }

    public boolean isMeasuringTapeVisible() {
        return rootNode.isMeasuringTapeVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        rootNode.setMeasuringTapeVisible( selected );
    }

    public boolean isPieChartVisible() {
        return rootNode.isPieChartVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        rootNode.setPieChartVisible( selected );
        for ( Listener listener : listeners ) {
            listener.pieChartVisibilityChanged();
        }
    }

    public EnergySkateParkRootNode getRootNode() {
        return rootNode;
    }

    public void setZeroPointVisible( boolean selected ) {
        rootNode.setZeroPointVisible( selected );
        notifyZeroPointVisibleChanged();
    }

    private void notifyZeroPointVisibleChanged() {
        for ( Object listener1 : listeners ) {
            Listener listener = (Listener) listener1;
            listener.zeroPointEnergyVisibilityChanged();
        }
    }

    public boolean isZeroPointVisible() {
        return rootNode.isZeroPointVisible();
    }

    public void dragSplineSurface( PInputEvent event, EnergySkateParkSpline spline ) {
        SplineNode splineNode = getSplineNode( spline );
        if ( splineNode == null ) {
            throw new RuntimeException( "Spline node not found for spline=" + spline );
        }
        PDimension delta = event.getCanvasDelta();
        rootNode.screenToWorld( delta );
        splineNode.processExternalDragEvent( event, delta.width, delta.height );
    }

    public SplineNode getSplineNode( EnergySkateParkSpline spline ) {
        for ( int i = 0; i < numSplineGraphics(); i++ ) {
            if ( getSplineNode( i ).getSpline() == spline ) {
                return getSplineNode( i );
            }
        }
        return null;
    }

    public void setEnergyErrorVisible( boolean selected ) {
        rootNode.setEnergyErrorVisible( selected );
    }

    public boolean isEnergyErrorVisible() {
        return rootNode.isEnergyErrorVisible();
    }

    public boolean isSkaterOnscreen( Body body ) {
        return isSkaterOnscreen( getRootNode().getSkaterNode( body ) );
    }

    public boolean isSkaterOnscreen( SkaterNode skaterNode ) {
        Rectangle screenRect = new Rectangle( module.getEnergySkateParkSimulationPanel().getSize() );
        Rectangle2D d = skaterNode.getRedDotGlobalFullBounds();
        return screenRect.contains( d );
    }

    public boolean getIgnoreThermal() {
        return rootNode.getIgnoreThermal();
    }

    public void setIgnoreThermal( boolean b ) {
        if ( rootNode.getIgnoreThermal() != b ) {
            rootNode.setIgnoreThermal( b );
            for ( Listener listener : listeners ) {
                listener.ignoreThermalChanged();
            }
        }
    }

    public static interface Listener {
        void zoomChanged();

        void zeroPointEnergyVisibilityChanged();

        void pieChartVisibilityChanged();

        void ignoreThermalChanged();
    }

    public static class Adapter implements Listener {

        public void zoomChanged() {
        }

        public void zeroPointEnergyVisibilityChanged() {
        }

        public void pieChartVisibilityChanged() {
        }

        public void ignoreThermalChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void fireZoomEvent() {
        for ( Listener listener : listeners ) {
            listener.zoomChanged();
        }
    }

    //Enable or disable keyboard control of rocket thrusters
    public void setThrustEnabled( boolean thrustEnabled ) {
        this.thrustEnabled = thrustEnabled;
    }
}
