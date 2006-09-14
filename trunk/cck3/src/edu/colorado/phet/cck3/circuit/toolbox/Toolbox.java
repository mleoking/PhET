/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.toolbox;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.cck3.common.CCKCompositePhetGraphic;
import edu.colorado.phet.cck3.model.Branch;
import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.*;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObservable;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.CompositeGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 2:30:15 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class Toolbox extends CompositeGraphic {
    private SimpleObservable simpleObservable = new SimpleObservable();
    private Rectangle2D modelRect;
    private ApparatusPanel parent;
    private ModelViewTransform2D transform;
    private CCKModule module;

    private Color backgroundColor;

    private PhetShapeGraphic boundaryGraphic;
    private BranchSource.WireSource wireSource;
    private BranchSource.BatterySource batterySource;
    private BranchSource.BulbSource bulbSource;
    private BranchSource.ResistorSource resistorSource;
    private BranchSource.SwitchSource switchSource;
    private BranchSource.AmmeterSource ammeterSource;
    private double schematicWireThickness = .1;
    private BranchSource.CapacitorSource capacitorSource;
    private BranchSource.ACSource acSource;
    private BranchSource.InductorSource inductorSource;

    public Toolbox( Rectangle2D modelRect, CCKModule module, Color backgroundColor ) {
        this.module = module;
        this.backgroundColor = backgroundColor;
        this.modelRect = new Rectangle2D.Double( modelRect.getX(), modelRect.getY(), modelRect.getWidth(), modelRect.getHeight() );
        this.parent = module.getApparatusPanel();
        this.transform = module.getTransform();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doUpdate();
            }
        } );
        rebuild();
        doUpdate();
        setVisible( true );
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        rebuild();
    }

    private void rebuild() {
        clear();
        boundaryGraphic = new PhetShapeGraphic( parent, transform.createTransformedShape( modelRect ),
                                                backgroundColor, new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ), Color.black );

//        double fracInsetX = .1;
        double fracInsetX = .2;
        double componentWidthFrac = 1 - fracInsetX * 2;
        double componentWidth = modelRect.getWidth() * componentWidthFrac;
        double componentX = modelRect.getX() + modelRect.getWidth() * fracInsetX;
        double componentX2 = componentX + componentWidth;
        double y = modelRect.getY() + modelRect.getHeight();
        int numComponents = 6;
        if( module.getParameters().getAllowDynamics() ) {
            numComponents += 3;
        }
//        double dy = -modelRect.getHeight() / 6.2;
        double dy = -modelRect.getHeight() / ( numComponents + 0.2 );
        Vector2D.Double dir = new Vector2D.Double( 1, 0 );
        y += dy / 2;
        y = addWireBranch( componentX, y, componentX2, dy );

        if( module.getParameters().allowPlainResistors() ) {
            y = addPlainResistors( componentWidth, componentX, y, dir, dy );
        }
        y = addBattery( componentWidth, componentX, y, dir, dy );
        y = addBulb( componentWidth, y, componentX, dy );
        y = addSwitch( componentWidth, componentX, y, dir, dy );

        if( module.getParameters().getAllowDynamics() ) {
            y = addAC( componentWidth, componentX, y, dir, dy );
            y = addCapacitor( componentWidth, componentX, y, dir, dy );
            y = addInductor( componentWidth, componentX, y, dir, dy );
        }

        addAmmeter( componentWidth, componentX, y, dir );
        setLifelike( module.getCircuitGraphic().isLifelike() );
        setVisible( true );
    }

    private void addAmmeter( double componentWidth, double componentX, double y, Vector2D.Double dir ) {
        double samLength = componentWidth;
        double samHeight = CCKModel.SERIES_AMMETER_DIMENSION.getHeightForLength( samLength );
        SeriesAmmeter sam = new SeriesAmmeter( module.getCircuitChangeListener(), new Point2D.Double( componentX, y ),
                                               new ImmutableVector2D.Double( 1, 0 ), samLength, samHeight );
        SeriesAmmeterGraphic sag = new SeriesAmmeterGraphic( parent, sam, transform, module, SimStrings.get( "Toolbox.AmmeterTitle" ) );
        sag.setFont( new Font( "Lucida Sans", Font.PLAIN, 8 ) );
        SchematicAmmeterGraphic schAg = new SchematicAmmeterGraphic( parent, sam, transform, schematicWireThickness, module.getDecimalFormat() );
        ammeterSource = new BranchSource.AmmeterSource( sag, schAg, module.getCircuitGraphic(), parent, sam, module.getCircuitChangeListener(), dir,
                                                        CCKModel.SERIES_AMMETER_DIMENSION.getLength(),
                                                        CCKModel.SERIES_AMMETER_DIMENSION.getHeight(), module );
        addSource( ammeterSource );
    }

    private double addSwitch( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {
        BufferedImage baseImage = module.getImageSuite().getKnifeBoardImage();
        double initialSwitchHeight = CCKModel.SWITCH_DIMENSION.getHeightForLength( componentWidth );
        Switch mySwitch = new Switch( new Point2D.Double( componentX + componentWidth, y ), dir.getScaledInstance( -1 ),
                                      componentWidth, initialSwitchHeight, module.getCircuitChangeListener() );
        BufferedImage leverImage = module.getImageSuite().getKnifeHandleImage();
        CircuitComponentImageGraphic sg = new CircuitComponentImageGraphic( baseImage, parent, mySwitch, transform );
        final TestCG switchGraphic = new TestCG( module.getApparatusPanel() );
        switchGraphic.addGraphic( sg );
        double scale = componentWidth / CCKModel.SWITCH_DIMENSION.getLength();
        double leverLength = scale * CCKModel.LEVER_DIMENSION.getLength();
        double leverHeight = scale * CCKModel.LEVER_DIMENSION.getHeight();
        LeverGraphic lg = new LeverGraphic( sg, leverImage, parent, transform, leverLength, leverHeight );
        switchGraphic.addGraphic( lg );
        lg.setRelativeAngle( LeverGraphic.OPEN_ANGLE );
        SchematicSwitchGraphic ssg = new SchematicSwitchGraphic( parent, mySwitch, transform, schematicWireThickness );
        SchematicLeverGraphic slg = new SchematicLeverGraphic( ssg, parent, transform, schematicWireThickness, leverLength * scale );

        final TestCG schSwitchGraphic = new TestCG( module.getApparatusPanel() );
        schSwitchGraphic.addGraphic( ssg );
        schSwitchGraphic.addGraphic( slg );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                switchGraphic.setBoundsDirty();
                schSwitchGraphic.setBoundsDirty();
            }
        } );
        switchSource = new BranchSource.SwitchSource( switchGraphic, schSwitchGraphic, module.getCircuitGraphic(), parent,
                                                      mySwitch, module.getCircuitChangeListener(),
                                                      CCKModel.SWITCH_DIMENSION, module );
        addSource( switchSource );
        y += dy;
        return y;
    }

    private double addBulb( double componentWidth, double y, double componentX, double dy ) {
        double bulbToolWidth = componentWidth;
        double bulbToolHeight = CCKModel.BULB_DIMENSION.getHeightForLength( bulbToolWidth );
        AbstractVector2D bulbDir = new Vector2D.Double( 0, 1 );
        bulbDir = bulbDir.getRotatedInstance( BulbComponentGraphic.determineTilt() );
        double bulbY = y - CCKModel.BULB_DIMENSION.getHeight() * modelRect.getHeight() / 30;
        Bulb bulb = new Bulb( new Point2D.Double( componentX + componentWidth / 2, bulbY ),
                              bulbDir, 1, bulbToolWidth, bulbToolHeight, module.getCircuitChangeListener() );//TODO 1 is broken
        BulbComponentGraphic bulbGraphic = new BulbComponentGraphic( parent, bulb, transform, module );
        Vector2D schBulbDir = new Vector2D.Double( 1, 0 );
        Bulb schematicBulb = new Bulb( new Point2D.Double( componentX, bulbY ),
                                       schBulbDir, componentWidth, bulbToolWidth, bulbToolHeight, module.getCircuitChangeListener() );

        SchematicBulbGraphic schBulbGraphic = new SchematicBulbGraphic( parent, schematicBulb, transform, schematicWireThickness );
        bulbSource = new BranchSource.BulbSource( bulbGraphic, module.getCircuitGraphic(), parent, bulb, schBulbGraphic, CCKModel.BULB_DIMENSION, module.getCircuitChangeListener(),
                                                  CCKModel.BULB_DIMENSION.getDistBetweenJunctions(), module );
        addSource( bulbSource );
        y += dy * 1.2;
        return y;
    }

    private double addBattery( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {
        double battToolHeight = CCKModel.BATTERY_DIMENSION.getHeightForLength( componentWidth );

        Battery batt = new Battery( new Point2D.Double( componentX, y ), dir, componentWidth, battToolHeight, module.getCircuitChangeListener(), module.isInternalResistanceOn() );
        CircuitComponentImageGraphic battGraphic = new CircuitComponentImageGraphic( module.getImageSuite().getLifelikeSuite().getBatteryImage(), parent, batt, transform );
        SchematicBatteryGraphic battG = new SchematicBatteryGraphic( parent, batt, transform, schematicWireThickness );
        batterySource = new BranchSource.BatterySource( battGraphic, battG, module.getCircuitGraphic(), parent, batt, CCKModel.BATTERY_DIMENSION, module.getCircuitChangeListener(), module );
        addSource( batterySource );
        y += dy;
        return y;
    }

    private double addPlainResistors( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {

        BufferedImage resistorImage = module.getImageSuite().getResistorImage();
        double initalResistorHeight = CCKModel.RESISTOR_DIMENSION.getHeightForLength( componentWidth );
        Resistor resistor = new Resistor( new Point2D.Double( componentX, y ), dir, componentWidth, initalResistorHeight, module.getCircuitChangeListener() );
        ResistorGraphic rg = new ResistorGraphic( resistorImage, parent, resistor, transform );
        SchematicResistorGraphic srg = new SchematicResistorGraphic( parent, resistor, transform, schematicWireThickness );
        resistorSource = new BranchSource.ResistorSource( rg, srg, module.getCircuitGraphic(), parent, resistor, module.getCircuitChangeListener(), CCKModel.RESISTOR_DIMENSION, module );
        addSource( resistorSource );
        y += dy;

        return y;
    }

    private double addCapacitor( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {
//        BufferedImage im = module.getImageSuite().getCapacitorImage();
        double initialHeight = CCKModel.CAP_DIM.getHeightForLength( componentWidth );
        Capacitor resistor = new Capacitor( new Point2D.Double( componentX, y ), dir, componentWidth, initialHeight, module.getCircuitChangeListener() );
        SchematicCapacitor3DGraphic schematicGraphic = new SchematicCapacitor3DGraphic( parent, resistor, transform, schematicWireThickness );
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 1280, 1024 ), new Rectangle( 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height ) );
        schematicGraphic.setHeight( modelViewTransform2D.modelToViewDifferentialY( 50 ) );
        schematicGraphic.setWidth( modelViewTransform2D.modelToViewDifferentialX( 35 ) );
        schematicGraphic.setDistBetweenPlates( modelViewTransform2D.modelToViewDifferentialX( 10 ) );
        capacitorSource = new BranchSource.CapacitorSource( schematicGraphic, schematicGraphic, module.getCircuitGraphic(), parent, resistor, module.getCircuitChangeListener(), CCKModel.CAP_DIM, module );
        addSource( capacitorSource );
        y += dy;
        return y;
    }

    private double addInductor( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {
        double initialHeight = CCKModel.CAP_DIM.getHeightForLength( componentWidth );
        Inductor inductor = new Inductor( new Point2D.Double( componentX, y ), dir, componentWidth, initialHeight, module.getCircuitChangeListener() );
        CircuitComponentImageGraphic lifelike = new CircuitComponentImageGraphic( module.getImageSuite().getInductorImage(), parent, inductor, transform );
        SchematicInductorGraphic schematic = new SchematicInductorGraphic( parent, inductor, transform, schematicWireThickness );
        inductorSource = new BranchSource.InductorSource( lifelike, schematic, module.getCircuitGraphic(), parent, inductor, module.getCircuitChangeListener(), CCKModel.INDUCTOR_DIM, module );
        addSource( inductorSource );
        y += dy;
        return y;
    }

    private double addAC( double componentWidth, double componentX, double y, Vector2D.Double dir, double dy ) {
        double acToolHeight = CCKModel.AC_DIM.getHeightForLength( componentWidth );

        ACVoltageSource ac = new ACVoltageSource( new Point2D.Double( componentX, y ), dir,
                                                  componentWidth, acToolHeight, module.getCircuitChangeListener(), module.isInternalResistanceOn() );
        CircuitComponentImageGraphic lifelike = new CircuitComponentImageGraphic( module.getImageSuite().getACImage(), parent, ac, transform );
//        SchematicOscillateGraphic schematic = new SchematicOscillateGraphic( parent, batt, transform, schematicWireThickness );
        SchematicOscillateGraphic schematic = new SchematicACGraphic( parent, ac, transform, schematicWireThickness );
        acSource = new BranchSource.ACSource( lifelike, schematic, module.getCircuitGraphic(),
                                              parent, ac, CCKModel.AC_DIM, module.getCircuitChangeListener(), module );
        addSource( acSource );
        y += dy;
        return y;
    }

    private double addWireBranch( double componentX, double y, double componentX2, double dy ) {
        Branch wireBranch = new Branch( module.getCircuitChangeListener(), new Junction( componentX, y ), new Junction( componentX2, y ) );
        BranchGraphic bg = new BranchGraphic( wireBranch, parent, .14, transform, CircuitGraphic.COPPER );
        BranchGraphic schematicBranchGraphic = new BranchGraphic( wireBranch, parent, .1, transform, Color.black );
        wireSource = new BranchSource.WireSource( wireBranch, bg, schematicBranchGraphic, module.getCircuitGraphic(), parent, module.getCircuitChangeListener(), CCKModel.WIRE_LENGTH, module );
        addSource( wireSource );
        y += dy;
        return y;
    }

    public void addObserver( SimpleObserver observer ) {
        simpleObservable.addObserver( observer );
    }

    class TestCG extends CCKCompositePhetGraphic {
        public TestCG( Component component ) {
            super( component );
            setVisible( true );
        }

        public void setBoundsDirty() {
            super.setBoundsDirty();
        }
    }

    private void doUpdate() {
        Shape viewShape = transform.createTransformedShape( modelRect ).getBounds();
        boundaryGraphic.setShape( viewShape );//that's a view shape.
        simpleObservable.notifyObservers();
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        boundaryGraphic.setVisible( visible );
        wireSource.setVisible( visible );
        batterySource.setVisible( visible );
        bulbSource.setVisible( visible );
        if( resistorSource != null ) {
            resistorSource.setVisible( visible );
        }
        switchSource.setVisible( visible );
        ammeterSource.setVisible( visible );
    }

    public void addSource( BranchSource branchSource ) {
        super.addGraphic( branchSource );
    }

    public void paint( Graphics2D g ) {
        boundaryGraphic.paint( g );
        super.paint( g );
    }

    //todo: refactor this to composite pattern
    public void setLifelike( boolean lifelike ) {
        wireSource.setLifelike( lifelike );
        batterySource.setLifelike( lifelike );
        bulbSource.setLifelike( lifelike );
        if( resistorSource != null ) {
            resistorSource.setLifelike( lifelike );
        }
        switchSource.setLifelike( lifelike );
        ammeterSource.setLifelike( lifelike );
        if( capacitorSource != null ) {
            capacitorSource.setLifelike( lifelike );
        }
        if( acSource != null ) {
            acSource.setLifelike( lifelike );
        }
        if( inductorSource != null ) {
            inductorSource.setLifelike( lifelike );
        }
    }

    /**
     * Returns the model rectangle
     */
    public Rectangle2D getBounds2D() {
        return modelRect;
    }

    public void removeObserver( SimpleObserver observer ) {
        simpleObservable.removeObserver( observer );
    }

    public Shape getShape() {
        return boundaryGraphic.getShape();
    }

    public void setModelBounds( Rectangle2D modelRect, boolean showAmmeter ) {
        this.modelRect = new Rectangle2D.Double( modelRect.getX(), modelRect.getY(), modelRect.getWidth(), modelRect.getHeight() );//modelRect;

        rebuild();
        doUpdate();
        setSeriesAmmeterVisible( showAmmeter );
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        ammeterSource.setVisible( selected );
    }

    public BranchSource.WireSource getWireSource() {
        return wireSource;
    }

    public BranchSource.BatterySource getBatterySource() {
        return batterySource;
    }

    public BranchSource.BulbSource getBulbSource() {
        return bulbSource;
    }

    public BranchSource.ResistorSource getResistorSource() {
        return resistorSource;
    }

    public BranchSource.SwitchSource getSwitchSource() {
        return switchSource;
    }

    public BranchSource.AmmeterSource getAmmeterSource() {
        return ammeterSource;
    }
}
