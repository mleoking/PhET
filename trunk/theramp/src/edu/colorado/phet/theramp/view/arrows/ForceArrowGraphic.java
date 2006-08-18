/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.BoundGraphic;
import edu.colorado.phet.piccolo.HTMLGraphic;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampFontSet;
import edu.colorado.phet.theramp.view.RampWorld;
import edu.colorado.phet.theramp.view.SurfaceGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 8:22:35 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */
public class ForceArrowGraphic extends PNode {
    private double arrowTailWidth = 7;
    private double arrowHeadHeight = 14;

    private String name;
    private Color color;
    private int dy;
    private AbstractArrowSet.ForceComponent forceComponent;
    private HTMLGraphic textGraphic;
    private PPath shapeGraphic;
//    private final Font font = new Font( "Lucida Sans", Font.BOLD, 18 );
    private final Font font = RampFontSet.getFontSet().getForceArrowLabelFont();
    private Arrow lastArrow;
    private BlockGraphic blockGraphic;
    private boolean userVisible = true;
    private boolean nonZero = true;
    private String sub;
    private static final double THRESHOLD = 10E-8;
    private Color baseColor;
    private double verticalOffset = 0;
//    private int textOffset=42;
    private int textOffset = 30;
    private int boundGraphicInsetX = 2;
//    private double boundGraphicInsetY=0.5;
    private double boundGraphicInsetY = 0;

    public ForceArrowGraphic( Component component, String name, Color color,
                              int dy, AbstractArrowSet.ForceComponent forceComponent,
                              BlockGraphic blockGraphic ) {
        this( component, name, color, dy, forceComponent, blockGraphic, null );
    }

    public ForceArrowGraphic( Component component, String name, Color color,
                              int dy, AbstractArrowSet.ForceComponent forceComponent,
                              BlockGraphic blockGraphic, String sub ) {
        super();
        if( name.equals( AbstractArrowSet.TOTAL ) ) {
            verticalOffset = 30;
        }
        if( name.equals( AbstractArrowSet.WALL ) ) {
            verticalOffset = -30;
        }
        this.blockGraphic = blockGraphic;
        this.name = name;
        this.baseColor = color;
        this.sub = sub;
        if( sub != null && !sub.trim().equals( "" ) ) {
            name = "<html>" + name + "<sub>" + sub + "</sub></html>";
        }
        this.color = baseColor;
        this.dy = dy;
        this.forceComponent = forceComponent;
        textGraphic = new HTMLGraphic( name );
        textGraphic.setFont( font );
        textGraphic.setColor( Color.black );

        shapeGraphic = new PPath( null );
        shapeGraphic.setPaint( this.color );

        addChild( shapeGraphic );

        BoundGraphic boundGraphic = new BoundGraphic( textGraphic, boundGraphicInsetX, boundGraphicInsetY );
        boundGraphic.setPaint( Color.white );
        addChild( boundGraphic );
        addChild( textGraphic );
        //setIgnoreMouse( true );
        setPickable( false );
        setChildrenPickable( false );
        update();
    }

    public AbstractArrowSet.ForceComponent getForceComponent() {
        return forceComponent;
    }

    public void update() {
        AbstractVector2D force = new ImmutableVector2D.Double( forceComponent.getForce() );
        force = force.getScaledInstance( RampModule.FORCE_LENGTH_SCALE );
        if( force.getMagnitude() <= THRESHOLD ) {
            setVisible( false );
            nonZero = false;
            return;
        }
        else {
            nonZero = true;
            setVisible( userVisible );
        }
        RampWorld rampWorld = getRampWorld();
        if( rampWorld == null ) {
//            System.out.println( "rampWorld = " + rampWorld );
            return;
        }
//        System.out.println( "blockGraphic.getBounds() = " + blockGraphic.getBlockBounds() );
        Point2D blockCenter = blockGraphic.getBlockBounds().getCenter2D();

        Point2D arrowTail = new Point2D.Double( blockCenter.getX(), blockCenter.getY() );
        arrowTail = offsetTail( arrowTail );
        Point2D tip = force.getDestination( arrowTail );

        Arrow forceArrow = new Arrow( arrowTail, tip, arrowHeadHeight, arrowHeadHeight, arrowTailWidth, 0.5, false );
        Shape forceArrowShape = forceArrow.getShape();

//        if (getName().toLowerCase().indexOf("applied" )>=0){
//            System.out.println( "forceComponent.getForce() = " + forceComponent.getForce() );
//        }

        if( this.lastArrow == null || !this.lastArrow.equals( forceArrow ) ) {
            shapeGraphic.setPathTo( forceArrowShape );

            AbstractVector2D dstVector = force.getInstanceOfMagnitude( force.getMagnitude() + textOffset );
            Point2D dest = dstVector.getDestination( arrowTail );
            textGraphic.setOffset( dest.getX() - textGraphic.getFullBounds().getWidth() / 2, dest.getY() - textGraphic.getFullBounds().getHeight() / 2 );
        }
        this.lastArrow = forceArrow;
    }

    private Point2D offsetTail( Point2D tail ) {
        double viewAngle = blockGraphic.getCurrentSurfaceGraphic().getViewAngle();
        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( verticalOffset, viewAngle );
        v = v.getNormalVector();
        return v.getDestination( tail );
    }

    private RampWorld getRampWorld() {
        PNode parent = getParent();
        while( parent != null ) {
            if( parent instanceof RampWorld ) {
                return (RampWorld)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private Point2D.Double translate( Point2D viewCtr ) {
        SurfaceGraphic surfaceGraphic = blockGraphic.getCurrentSurfaceGraphic();
        double viewAngle = surfaceGraphic.getViewAngle();
//        System.out.println( "viewAngle = " + viewAngle );
        Point offset = new Point( (int)( Math.sin( viewAngle ) * dy ), (int)( Math.cos( viewAngle ) * dy ) );
        return new Point2D.Double( viewCtr.getX() + offset.x, viewCtr.getY() - offset.y );
    }

    public String getName() {
        return name;
    }

    public void setUserVisible( boolean userVisible ) {
        this.userVisible = userVisible;
        setVisible( userVisible && nonZero );
    }

    public void setPaint( Paint paint ) {
        shapeGraphic.setPaint( paint );
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public double getVerticalOffset() {
        return verticalOffset;
    }

    public void setVerticalOffset( double verticalOffset ) {
        this.verticalOffset = verticalOffset;
    }
}
