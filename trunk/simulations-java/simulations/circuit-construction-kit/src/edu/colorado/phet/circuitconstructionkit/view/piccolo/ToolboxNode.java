// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Bulb;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.model.components.Inductor;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.circuitconstructionkit.model.components.SeriesAmmeter;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.view.CCKLookAndFeel;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.BulbComponentNode;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.BulbNode;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:28 PM
 */

public class ToolboxNode extends PhetPNode {
    private PPath toolboxBounds;
    private PhetPCanvas canvas;
    private CCKModel model;
    private BranchNodeFactory branchNodeFactory;
    private CCKSimulationPanel cckSimulationPanel;
    private BooleanProperty lifelikeProperty;
    private ArrayList branchMakers = new ArrayList();
    private static final int TOP_INSET = 30;
    private double betweenInset = 6;
    private CircuitInteractionModel circuitInteractionModel;
    private CCKModule module;
    private AmmeterMaker ammeterMaker;
    private ToolboxNode.WireMaker wireMaker;

    public ToolboxNode( PhetPCanvas canvas, CCKModel model, CCKModule module, BranchNodeFactory branchNodeFactory, CCKSimulationPanel cckSimulationPanel, BooleanProperty lifelikeProperty ) {
        this.module = module;
        this.canvas = canvas;
        this.model = model;
        this.branchNodeFactory = branchNodeFactory;
        this.cckSimulationPanel = cckSimulationPanel;
        this.lifelikeProperty = lifelikeProperty;
        this.circuitInteractionModel = new CircuitInteractionModel( model );
        this.toolboxBounds = new PPath( new Rectangle( 100, 100 ) );
        toolboxBounds.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        toolboxBounds.setPaint( CCKLookAndFeel.toolboxColor );
        addChild( toolboxBounds );
        if ( !getAllowsDynamics() ) {
            betweenInset = 40;
        }
        wireMaker = new WireMaker();
        addBranchMaker( wireMaker );
        addBranchMaker( new ResistorMaker() );
        addBranchMaker( new BatteryMaker() );
        addBranchMaker( new BulbMaker() );
        addBranchMaker( new SwitchMaker() );
        if ( getAllowsDynamics() ) {
            addBranchMaker( new ACVoltageMaker() );
            addBranchMaker( new CapacitorMaker() );
            addBranchMaker( new InductorMaker() );
        }
        ammeterMaker = new AmmeterMaker();
        addBranchMaker( ammeterMaker );

        toolboxBounds.setPathTo( new Rectangle2D.Double( 0, 0, 100, getYForNextItem( new ResistorMaker() ) ) );
        setSeriesAmmeterVisible( false );
    }

    private boolean getAllowsDynamics() {
        return module.getParameters().getAllowDynamics();
    }

    private void addBranchMaker( BranchMaker branchMaker ) {
        double y = getYForNextItem( branchMaker );
        branchMaker.setOffset( toolboxBounds.getFullBounds().getCenterX() - branchMaker.getFullBounds().getWidth() / 2 - branchMaker.getFullBounds().getMinX(), y );
        addChild( branchMaker );
        branchMakers.add( branchMaker );
    }

    private double getYForNextItem( BranchMaker nextItem ) {
        if ( branchMakers.size() == 0 ) {
            return TOP_INSET;
        }
        else {
            BranchMaker prev = (BranchMaker) branchMakers.get( branchMakers.size() - 1 );
            double val = prev.getFullBounds().getMaxY() + betweenInset;
            if ( nextItem.getFullBounds().getMinY() < 0 ) {
                val -= nextItem.getFullBounds().getMinY();
            }
            return val;
        }
    }

    public void setBackground( Paint paint ) {
        toolboxBounds.setPaint( paint );
    }

    public Color getBackgroundColor() {
        return (Color) toolboxBounds.getPaint();
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        ammeterMaker.setVisible( selected );
    }

    abstract class BranchMaker extends PComposite {//tricky way of circumventing children's behaviors
        private PText label;
        private Branch createdBranch;
        private double scale;

        public BranchMaker( String name, double scale ) {
            this.scale = scale;
            label = new PText( name );
            label.setFont( createFont() );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    // If we haven't created the branch yet
                    if ( createdBranch == null ) {
                        createdBranch = createBranch();
                        // Position the branch so it's centered on the mouse event.
                        setBranchLocationFromEvent( event );
                        model.getCircuit().addBranch( createdBranch );
                        model.getCircuit().setSelection( createdBranch );
                    }
                    circuitInteractionModel.translate( createdBranch, getWorldLocation( event ) );
                }

                public void mouseReleased( PInputEvent event ) {
                    if ( createdBranch != null ) { // This check was added to resolve #1826, not sure why the createdBranch was sometimes null, or why that was causing problems.
                        circuitInteractionModel.dropBranch( createdBranch );
                        createdBranch = null;
                    }
                }
            } );

            setupNode( scale );
        }

        protected void setupNode( double scale ) {
            PNode node = createNode( createBranch() );
            node.scale( scale );//todo choose scale based on insets?
            setDisplayGraphic( node );
        }

        public void setVisible( boolean isVisible ) {
            super.setVisible( isVisible );
            setPickable( isVisible );
            setChildrenPickable( isVisible );
        }

        private Font createFont() {
            return new PhetFont( 16 );
        }

        public Point2D getWorldLocation( PInputEvent event ) {
            Point2D pt = event.getPositionRelativeTo( this );
            this.localToGlobal( pt );
            CCKModule m = (CCKModule) module;
            m.getCckSimulationPanel().getCircuitNode().globalToLocal( pt );
            return pt;
        }

        //This assumes the branch is always centered on the mouse.

        private void setBranchLocationFromEvent( PInputEvent event ) {
            Point2D location = getWorldLocation( event );
            double dx = createdBranch.getEndPoint().getX() - createdBranch.getStartPoint().getX();
            double dy = createdBranch.getEndPoint().getY() - createdBranch.getStartPoint().getY();
            createdBranch.getStartJunction().setPosition( location.getX() - dx / 2,
                                                          location.getY() - dy / 2 );
            createdBranch.getEndJunction().setPosition( location.getX() + dx / 2,
                                                        location.getY() + dy / 2 );
        }

        protected abstract Branch createBranch();

        public void setDisplayGraphic( PNode child ) {
            addChild( child );
            addChild( label );
            double labelInsetDY = 4;
            label.setOffset( child.getFullBounds().getWidth() / 2 - label.getFullBounds().getWidth() / 2, child.getFullBounds().getMaxY() + labelInsetDY );
        }

        public PNode createNode( Branch branch ) {
            return branchNodeFactory.createNode( branch );
        }
    }

    class WireMaker extends BranchMaker {
        public WireMaker() {
            super( CCKStrings.getString( "BranchSource.Wire" ), 40 );
        }

        protected Branch createBranch() {
            Wire wire = new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) );
            wire.setThickness( lifelikeProperty.get() ? Wire.LIFELIKE_THICKNESS : Wire.SCHEMATIC_THICKNESS );
            return wire;
        }
    }

    class ResistorMaker extends BranchMaker {
        public ResistorMaker() {
            super( CCKStrings.getString( "BranchSource.Resistor" ), 60 );
        }

        protected Branch createBranch() {
            double L = CCKModel.RESISTOR_DIMENSION.getLength() * 1.3 * 1.3;
            double H = CCKModel.RESISTOR_DIMENSION.getHeight() * 1.3 * 1.3;
            Resistor resistor = new Resistor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, H );
            resistor.setResistance( 10.0 );
            return resistor;
        }
    }

    private static double batteryScale = 0.75;

    class BatteryMaker extends BranchMaker {

        public BatteryMaker() {
            super( CCKStrings.getString( "BranchSource.Battery" ), 45 / batteryScale );
        }

        protected Branch createBranch() {
            return createBattery();
        }

        private Battery createBattery() {
            double L = 1.6 * batteryScale;

            return new Battery( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, 1.8, 1E-4, true );
        }
    }

    class BulbMaker extends BranchMaker {
        public BulbMaker() {
            super( CCKStrings.getString( "BranchSource.LightBulb" ), 1 );
            PNode child = new BulbNode( createBulb() );
            child.transformBy( AffineTransform.getScaleInstance( 50, 75 ) );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected void setupNode( double scale ) {
        }

        protected Branch createBranch() {
            return createBulb();
        }

        private Bulb createBulb() {
            Bulb dummyBulb = new Bulb( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1, 0.01, true );
            double tilt = BulbComponentNode.getTiltValue( dummyBulb );
            Bulb bulb = new Bulb( new Point(), Vector2D.createPolar( 1, -tilt - Math.PI / 2 ), 0.43, 1, 1, model.getCircuitChangeListener() );
            bulb.flip( null );
            return bulb;
        }
    }

    class SwitchMaker extends BranchMaker {
        public SwitchMaker() {
            super( CCKStrings.getString( "BranchSource.Switch" ), 60 );
        }

        protected Branch createBranch() {
            return createSwitch();
        }

        private Switch createSwitch() {
            return new Switch( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), false, 1, 1 );
        }
    }

    class ACVoltageMaker extends BranchMaker {
        public ACVoltageMaker() {
            super( CCKStrings.getString( "BranchSource.AC" ), 60 );
        }

        protected Branch createBranch() {
            return createSwitch();
        }

        private ACVoltageSource createSwitch() {
            return new ACVoltageSource( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1, 0.01, true );
        }
    }

    class CapacitorMaker extends BranchMaker {
        public CapacitorMaker() {
            super( CCKStrings.getString( "BranchSource.Capacitor" ), 60 );
        }

        protected Branch createBranch() {
            return createCapacitor();
        }

        private Capacitor createCapacitor() {
            return new Capacitor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1 );
        }
    }

    class InductorMaker extends BranchMaker {
        public InductorMaker() {
            super( CCKStrings.getString( "BranchSource.Inductor" ), 42 );
        }

        protected Branch createBranch() {
            return createInductor();
        }

        private Inductor createInductor() {
            double L = 1.2;
            return new Inductor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, 0.4 );
        }
    }

    class AmmeterMaker extends BranchMaker {
        public AmmeterMaker() {
            super( CCKStrings.getString( "BranchSource.Ammeter" ), 30 );
        }

        protected Branch createBranch() {
            return createAmmeter();
        }

        private SeriesAmmeter createAmmeter() {
            double L = 2.0;
            return new SeriesAmmeter( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, 0.6 );
        }
    }

    public WireMaker getWireMaker() {
        return wireMaker;
    }
}
