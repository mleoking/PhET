/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.toolbox;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 2:30:15 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class Toolbox extends CompositeInteractiveGraphic {
    Rectangle2D modelRect;
    private ApparatusPanel parent;
    private ModelViewTransform2D transform;
    FastPaintShapeGraphic boundaryGraphic;
    private CCK3Module module;

    public Toolbox( Rectangle2D modelRect, CCK3Module module ) {
        this.module = module;
        this.modelRect = new Rectangle2D.Double( modelRect.getX(), modelRect.getY(), modelRect.getWidth(), modelRect.getHeight() );
        this.parent = module.getApparatusPanel();
        this.transform = module.getTransform();

        boundaryGraphic = new FastPaintShapeGraphic( transform.createTransformedShape( modelRect ),
                                                     Color.yellow, Color.black, new BasicStroke( 1 ), parent );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doUpdate();
            }
        } );
        doUpdate();
        double fracInsetX = .1;
        double componentWidthFrac = 1 - fracInsetX * 2;
        double componentWidth = modelRect.getWidth() * componentWidthFrac;
        double componentX = modelRect.getX() + modelRect.getWidth() * fracInsetX;
        double componentX2 = componentX + componentWidth;
        double y = modelRect.getY() + CCK3Module.BATTERY_DIMENSION.getHeightForLength( componentWidth ) * 1.2;

        Branch wireBranch = createBranch( componentX, componentX2, y );//new Branch( new Junction( componentX, y ), new Junction( componentX + componentWidth, y ) );
        BranchGraphic bg = new BranchGraphic( wireBranch, parent, .2, transform, CircuitGraphic.COPPER );
        BranchSource.WireSource ws = new BranchSource.WireSource( wireBranch, bg, module.getCircuitGraphic(), parent, module.getKirkhoffListener(), CCK3Module.WIRE_LENGTH );
        addSource( ws );

        y += modelRect.getHeight() / 5;
        double battToolHeight = CCK3Module.BATTERY_DIMENSION.getHeightForLength( componentWidth );
        Vector2D.Double dir = new Vector2D.Double( 1, 0 );
        Battery batt = new Battery( new Point2D.Double( componentX, y ), dir, componentWidth, battToolHeight, module.getKirkhoffListener() );
        CircuitComponentImageGraphic battGraphic = new CircuitComponentImageGraphic( module.getImageSuite().getLifelikeSuite().getBatteryImage(), parent, batt, transform );
        BranchSource.BatterySource battSource = new BranchSource.BatterySource( battGraphic, module.getCircuitGraphic(), parent, batt, CCK3Module.BATTERY_DIMENSION, module.getKirkhoffListener() );
        addSource( battSource );

        y += modelRect.getHeight() / 5;
        double bulbToolWidth = componentWidth;
        double bulbToolHeight = CCK3Module.BULB_DIMENSION.getHeightForLength( bulbToolWidth );
        Vector2D bulbDir = new Vector2D.Double( 0, 1 );
        Bulb bulb = new Bulb( new Point2D.Double( componentX + componentWidth / 2, y - CCK3Module.BULB_DIMENSION.getHeight() / 3 ),
                              bulbDir, 1, bulbToolWidth, bulbToolHeight, module.getKirkhoffListener() );//TODO 1 is broken
        BulbComponentGraphic bulbGraphic = new BulbComponentGraphic( parent, bulb, transform, module );
        BranchSource.BulbSource bulbSource = new BranchSource.BulbSource( bulbGraphic, module.getCircuitGraphic(), parent, bulb, CCK3Module.BULB_DIMENSION, module.getKirkhoffListener(),
                                                                          CCK3Module.BULB_DIMENSION.getDistBetweenJunctions() );
        addSource( bulbSource );

        y += modelRect.getHeight() / 5;
        BufferedImage resistorImage = module.getImageSuite().getResistorImage();
        double initalResistorHeight = CCK3Module.RESISTOR_DIMENSION.getHeightForLength( componentWidth );
        Resistor resistor = new Resistor( new Point2D.Double( componentX, y ), dir, componentWidth, initalResistorHeight, module.getKirkhoffListener() );
        ResistorGraphic rg = new ResistorGraphic( resistorImage, parent, resistor, transform );
        BranchSource.ResistorSource resistorSource = new BranchSource.ResistorSource( rg, module.getCircuitGraphic(), parent, resistor, module.getKirkhoffListener(), CCK3Module.RESISTOR_DIMENSION );
        addSource( resistorSource );

        y += modelRect.getHeight() / 5;
        BufferedImage baseImage = module.getImageSuite().getKnifeBoardImage();
        double initialSwitchHeight = CCK3Module.SWITCH_DIMENSION.getHeightForLength( componentWidth );
        Switch mySwitch = new Switch( new Point2D.Double( componentX + componentWidth, y ), dir.getScaledInstance( -1 ), componentWidth, initialSwitchHeight, module.getKirkhoffListener() );
        BufferedImage leverImage = module.getImageSuite().getKnifeHandleImage();
        CircuitComponentImageGraphic sg = new CircuitComponentImageGraphic( baseImage, parent, mySwitch, transform );
        CompositeInteractiveGraphic switchGraphic = new CompositeInteractiveGraphic();
        switchGraphic.addGraphic( sg );
        double scale = componentWidth / CCK3Module.SWITCH_DIMENSION.getLength();
        double leverLength = scale * CCK3Module.LEVER_DIMENSION.getLength();
        double leverHeight = scale * CCK3Module.LEVER_DIMENSION.getHeight();
        LeverGraphic lg = new LeverGraphic( sg, leverImage, parent, transform, leverLength, leverHeight );
        switchGraphic.addGraphic( lg );
        lg.setRelativeAngle( LeverGraphic.OPEN_ANGLE );
        BranchSource.SwitchSource switchSource = new BranchSource.SwitchSource( switchGraphic, module.getCircuitGraphic(), parent, mySwitch, module.getKirkhoffListener(),
                                                                                CCK3Module.SWITCH_DIMENSION );
        addSource( switchSource );

        y += modelRect.getHeight() / 5;
        double samLength = componentWidth;
        double samHeight = CCK3Module.SERIES_AMMETER_DIMENSION.getHeightForLength( samLength );
        SeriesAmmeter sam = new SeriesAmmeter( module.getKirkhoffListener(), new Point2D.Double( componentX, y ),
                                               new ImmutableVector2D.Double( 1, 0 ), samLength, samHeight );
        SeriesAmmeterGraphic sag = new SeriesAmmeterGraphic( parent, sam, transform, module, "Ammeter" );
        BranchSource.AmmeterSource ammeterSource = new BranchSource.AmmeterSource( sag, module.getCircuitGraphic(), parent, sam, module.getKirkhoffListener(), dir,
                                                                                   CCK3Module.SERIES_AMMETER_DIMENSION.getLength(),
                                                                                   CCK3Module.SERIES_AMMETER_DIMENSION.getHeight() );
        addSource( ammeterSource );
    }

    static class Layout {
        ArrayList list = new ArrayList();
    }

    private Branch createBranch( double x0, double x1, double y ) {
        return new Branch( module.getKirkhoffListener(), new Junction( x0, y ), new Junction( x1, y ) );
    }

    private void doUpdate() {
//        System.out.println( "transform.getViewBounds() = " + transform.getViewBounds() );
        Shape sh = transform.createTransformedShape( modelRect ).getBounds();
//        System.out.println( "sh = " + sh );
        boundaryGraphic.setShape( sh );
    }

    public void addSource( BranchSource branchSource ) {
        super.addGraphic( branchSource );
    }

    public void paint( Graphics2D g ) {
        boundaryGraphic.paint( g );
        super.paint( g );
    }

}
