package edu.colorado.phet.cck3.phetgraphics_cck;

import edu.colorado.phet.cck3.CCK3ControlPanel;
import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.model.*;
import edu.colorado.phet.cck3.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck3.model.analysis.CircuitSolver;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.CircuitGraphicListener;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.ReadoutGraphic;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.toolbox.Toolbox;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.tools.VirtualAmmeter;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.tools.Voltmeter;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.Boundary;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 13, 2006
 * Time: 12:03:58 PM
 * Copyright (c) Sep 13, 2006 by Sam Reid
 */

public class CCKApparatusPanel extends RectangleRepaintApparatusPanel {

    private CircuitGraphic circuitGraphic;

    private ModelViewTransform2D transform;
    private Toolbox toolbox;
    private VirtualAmmeter virtualAmmeter;
    private InteractiveVoltmeter interactiveVoltmeter;
    private VoltmeterGraphic voltmeterGraphic;
    private WiggleMe wiggleMe;
    private CCKHelp help;
    private PhetShadowTextGraphic timeScaleTextGraphic;
    private Area electronClip = new Area();
    private boolean initedLayout = false;
    private static final Color apparatusPanelColor = new Color( 100, 160, 255 );
    private CCKModel cckmodel;
    private CCKModule module;

    public CCKApparatusPanel( CCKModule module, CCKModel cckmodel ) throws IOException {
        this.module = module;
        this.cckmodel = cckmodel;
        transform = new ModelViewTransform2D( cckmodel.getModelBounds(), new Rectangle( 100, 100 ) );
        circuitGraphic = new CircuitGraphic( module, this );
        setMyBackground( apparatusPanelColor );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }
        } );
        timeScaleTextGraphic = new PhetShadowTextGraphic( getApparatusPanel(), " ", new Font( "Lucida Sans", Font.BOLD, 13 ), 50, 100, Color.red, 1, 1, Color.black );
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                int x = timeScaleTextGraphic.getBounds().height;
                int y = getApparatusPanel().getHeight() - timeScaleTextGraphic.getBounds().height;
                timeScaleTextGraphic.setPosition( x, y );
            }
        } );

        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );
        getApparatusPanel().setFocusable( true );
        getApparatusPanel().setBackground( new Color( 0, 0, 0, 0 ) );


        DefaultInteractiveGraphic backgroundGraphic = new DefaultInteractiveGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
            }
        }, new Boundary() {
            public boolean contains( int x, int y ) {
                return true;
            }
        } );
        backgroundGraphic.addMouseInputListener( new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                getCircuit().clearSelection();
            }
        } );
        getApparatusPanel().addGraphic( backgroundGraphic, Double.NEGATIVE_INFINITY );
        BasicGraphicsSetup setup = new BasicGraphicsSetup();
        setup.setBicubicInterpolation();
        getApparatusPanel().addGraphicsSetup( setup );

        addTimeScaleListener();
    }

    private void addTimeScaleListener() {
        getCCKModel().getParticleSet().getPropagator().addListener( new ConstantDensityPropagator.Listener() {
            public void timeScaleChanged() {
                ConstantDensityPropagator propagator = getCCKModel().getParticleSet().getPropagator();
                String percent = propagator.getPercentString();
                if( !percent.equals( propagator.getDecimalFormat().format( 100 ) ) && propagator.getTimeScalingPercentValue() < 95 )
                {
                    if( !module.getParameters().hideAllElectrons() ) {
                        module.getTimescaleGraphic().setText( SimStrings.get( "ConstantDensityPropagator.SpeedLimitReached1" )
                                                              + " " + percent + SimStrings.get( "ConstantDensityPropagator.SpeedLimitReached2" ) );
                        module.getTimescaleGraphic().setVisible( true );
                    }
                }
                else {
                    module.getTimescaleGraphic().setText( "" );
                    module.getTimescaleGraphic().setVisible( false );
                }
            }
        } );
    }

    private CCKApparatusPanel getApparatusPanel() {
        return this;
    }


    private void relayout() {
        if( getApparatusPanel().getWidth() == 0 || getApparatusPanel().getHeight() == 0 ) {
            return;
        }
        if( !initedLayout ) {
            init();
            initedLayout = true;
            if( !transform.getViewBounds().equals( getViewBounds() ) ) {
                transform.setViewBounds( getViewBounds() );
                getApparatusPanel().repaint();
                getCircuit().updateAll();
            }
        }

        if( transform != null && !transform.getViewBounds().equals( getViewBounds() ) ) {
            transform.setViewBounds( getViewBounds() );
            getApparatusPanel().repaint();
            getCircuit().updateAll();
        }
    }

    private Circuit getCircuit() {
        return getCircuitGraphic().getCircuit();
    }

    public Rectangle getViewBounds() {
        return new Rectangle( 0, 0, getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
    }

    public CircuitGraphic getCircuitGraphic() {
        return circuitGraphic;
    }

    public void init() {
        transform.setModelBounds( getCCKModel().getModelBounds() );
        transform.setViewBounds( new Rectangle( getApparatusPanel().getWidth(), getApparatusPanel().getHeight() ) );

        toolbox = new Toolbox( createToolboxBounds(), module, CCKModule.toolboxColor );
        getApparatusPanel().addGraphic( toolbox );
        addGraphic( getCircuitGraphic(), 2 );
        addVirtualAmmeter();
        Voltmeter voltmeter = new Voltmeter( 5, 5, .7, module );
        try {
            voltmeterGraphic = new VoltmeterGraphic( voltmeter, getApparatusPanel(), module );
            interactiveVoltmeter = new InteractiveVoltmeter( voltmeterGraphic, module );
            getApparatusPanel().addGraphic( interactiveVoltmeter, 1000 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        getCircuitGraphic().addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                voltmeterGraphic.recomputeVoltage();
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                voltmeterGraphic.recomputeVoltage();
            }

            public void junctionsMoved() {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        getCircuitSolver().addSolutionListener( new FireHandler( getCircuitGraphic() ) );
        Rectangle2D rect = toolbox.getBounds2D();
        Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
        pt.translate( -130, 5 );
        wiggleMe = new WiggleMe( getApparatusPanel(), pt, new ImmutableVector2D.Double( 0, 1 ), 10, .025,
                                 SimStrings.get( "CCK3Module.GrabAWire" ) );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                Rectangle2D rect = toolbox.getBounds2D();
                Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
                pt.translate( -130, 5 );
                wiggleMe.setVisible( true );
                wiggleMe.setCenter( pt );
            }
        } );
        toolbox.addObserver( new SimpleObserver() {
            public void update() {
                Rectangle2D rect = toolbox.getBounds2D();
                Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
                pt.translate( -130, 5 );
                wiggleMe.setVisible( true );
                wiggleMe.setCenter( pt );
            }
        } );
        getApparatusPanel().addGraphic( wiggleMe, 100 );
        module.getModel().addModelElement( wiggleMe );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                if( branches.length > 0 ) {
                    getCircuit().removeCircuitListener( this );
                    wiggleMe.setVisible( false );
                    getApparatusPanel().removeGraphic( wiggleMe );
                    module.getModel().removeModelElement( wiggleMe );
                    if( wiggleMe.getBounds() != null ) {
                        getApparatusPanel().repaint( wiggleMe.getBounds() );
                    }
                }
            }
        } );
        help = new CCKHelp( module );
        module.setSeriesAmmeterVisible( false );
    }

    private CircuitSolver getCircuitSolver() {
        return cckmodel.getCircuitSolver();
    }

    private CCKModel getCCKModel() {
        return cckmodel;
    }

    public void setHelpEnabled( boolean h ) {
        help.setEnabled( h );
    }

    private Rectangle2D createToolboxBounds() {
        double toolBoxWidthFrac = .085;
        double toolBoxInsetXFrac = 1 - toolBoxWidthFrac * 1.5;
        double toolBoxHeightFrac = 0.82;
        double toolBoxInsetYFrac = 0.015;
        Rectangle2D modelBounds = transform.getModelBounds();
        double y = modelBounds.getY() + modelBounds.getHeight() * toolBoxInsetYFrac;
        double x = modelBounds.getX() + modelBounds.getWidth() * toolBoxInsetXFrac;
        double h = modelBounds.getHeight() * toolBoxHeightFrac;
        return new Rectangle2D.Double( x, y, modelBounds.getWidth() * toolBoxWidthFrac, h );
    }

    private void addVirtualAmmeter() {
        virtualAmmeter = new VirtualAmmeter( getCircuitGraphic(), getApparatusPanel(), module );
        getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                virtualAmmeter.recompute();
            }
        } );
        getCircuitGraphic().addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                virtualAmmeter.recompute();
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                virtualAmmeter.recompute();
            }
        } );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                virtualAmmeter.recompute();
            }

            public void junctionsMoved() {
                virtualAmmeter.recompute();
            }
        } );

        getApparatusPanel().addGraphic( virtualAmmeter, 30 );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void setZoom( double scale ) {

        double newWidth = getCCKModel().getModelWidth() * scale;
        double newHeight = getCCKModel().getModelHeight() * scale;

        Rectangle2D jb = getJunctionBounds();
        Point2D.Double center = null;
        if( jb == null ) {
            center = RectangleUtils.getCenter2D( getCCKModel().getModelBounds() );
        }
        else {
            center = RectangleUtils.getCenter2D( jb );
        }
        Rectangle2D.Double r = new Rectangle2D.Double( center.x - newWidth / 2, center.y - newHeight / 2, newWidth, newHeight );
        //could prevent people from zooming in beyond the boundary of the getCircuit(),
        //but someone may want to zoom in on just the bulb or something.
        transform.setModelBounds( r );
        toolbox.setModelBounds( createToolboxBounds(), getControlPanel().isSeriesAmmeterSelected() );
        getApparatusPanel().repaint();
    }

    private CCK3ControlPanel getControlPanel() {
        return module.getCCKControlPanel();
    }

    public Rectangle2D.Double getJunctionBounds() {
        Junction[] j = getCircuit().getJunctions();
        Rectangle2D.Double rect = null;
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            if( rect == null ) {
                rect = new Rectangle2D.Double( junction.getX(), junction.getY(), 0, 0 );
            }
            else {
                rect.add( new Point2D.Double( junction.getX(), junction.getY() ) );
            }
        }
        return rect;
    }

    public void setVirtualAmmeterVisible( boolean visible ) {
        virtualAmmeter.setVisible( visible );
    }

    public void setVoltmeterVisible( boolean visible ) {
        interactiveVoltmeter.setVisible( visible );
    }

    public void setLifelike( boolean lifelike ) {
        toolbox.setLifelike( lifelike );
        getCircuitGraphic().setLifelike( lifelike );
        getApparatusPanel().repaint();
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public PhetShadowTextGraphic getTimescaleGraphic() {
        return timeScaleTextGraphic;
    }

    public Shape getElectronClip() {
        return electronClip;
    }

    public void recomputeElectronClip() {
        this.electronClip = determineElectronClip();
    }

    private Area determineElectronClip() {
        Area area = new Area( new Rectangle2D.Double( -500, -500, 10000, 10000 ) );
        for( int i = 0; i < getCircuitGraphic().getBranchGraphics().length; i++ ) {
            //            if( circuitGraphic.getBranchGraphics()[i] instanceof SchematicCapacitorGraphic ) {
            if( getCircuitGraphic().getBranchGraphics()[i] instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)getCircuitGraphic().getBranchGraphics()[i];
                if( ccig.getCircuitComponentGraphic() instanceof HasCapacitorClip ) {
                    HasCapacitorClip c = (HasCapacitorClip)ccig.getCircuitComponentGraphic();
                    Shape capacitorClip = c.getCapacitorClip();
                    //                    System.out.println( "capacitorClip.getBounds2D() = " + capacitorClip.getBounds2D() );
                    area.subtract( new Area( capacitorClip ) );
                }
            }
        }
        return area;
    }

    public void convertJunctionGraphic( Junction junction ) {
        if( getCircuit().hasJunction( junction ) ) {
            Branch[] outputs = getCircuit().getAdjacentBranches( junction );
            if( outputs.length == 1 ) {
                Branch out = outputs[0];
                if( out instanceof CircuitComponent ) {
                    CircuitComponent cc = (CircuitComponent)out;
                    getCircuitGraphic().convertToComponentGraphic( junction, cc );
                }
            }
        }
    }

    public void updateReadoutGraphics() {
        ReadoutGraphic[] rg = getCircuitGraphic().getReadoutGraphics();
        for( int i = 0; i < rg.length; i++ ) {
            ReadoutGraphic readoutGraphic = rg[i];
            readoutGraphic.recompute();
        }
    }
}
