/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.theramp.common.ShadowHTMLGraphic;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 8:22:35 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */
public class ForceArrowGraphic extends CompositePhetGraphic {
    public static final double forceLengthScale = 4.0;
    private double arrowTailWidth = 30;
    private double arrowHeadHeight = 55;

    private String name;
    private Color color;
    private int dy;
    private AbstractArrowSet.ForceComponent forceComponent;
    ShadowHTMLGraphic textGraphic;
    PhetShapeGraphic shapeGraphic;
    final Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
    private Arrow lastArrow;
    private BlockGraphic blockGraphic;
    private boolean userVisible = true;
    private boolean nonZero = true;
    private String sub;

    public ForceArrowGraphic( Component component, String name, Color color, int dy, AbstractArrowSet.ForceComponent forceComponent, BlockGraphic blockGraphic ) {
        this( component, name, color, dy, forceComponent, blockGraphic, null );
    }

    public ForceArrowGraphic( Component component, String name, Color color, int dy, AbstractArrowSet.ForceComponent forceComponent, BlockGraphic blockGraphic, String sub ) {
        super( component );
        this.blockGraphic = blockGraphic;
        this.name = name;
        this.sub = sub;
        if( sub != null && !sub.trim().equals( "" ) ) {
            name = "<html>" + name + "<sub>" + sub + "</sub></html>";
        }
        color = RampUtil.transparify( color, 128 );
        this.color = color;
        this.dy = dy;
        this.forceComponent = forceComponent;
        textGraphic = new ShadowHTMLGraphic( component, name, font, Color.black, 1, 1, Color.yellow );
        shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 1 ), Color.black );
        addGraphic( shapeGraphic );
        addGraphic( textGraphic );
        setIgnoreMouse( true );
        update();
    }

    public void update() {
        AbstractVector2D force = new ImmutableVector2D.Double( forceComponent.getForce() );
        force = force.getScaledInstance( forceLengthScale );
        if( force.getMagnitude() == 0 ) {
            setVisible( false );
            nonZero = false;
            return;
        }
        else {
            nonZero = true;
            setVisible( true && userVisible );
        }

        Point viewCtr = blockGraphic.getCenter();
        viewCtr.y += blockGraphic.computeDimension().height / 2;
        viewCtr.y -= dy;
        Point2D.Double tail = new Point2D.Double( viewCtr.x, viewCtr.y );
        Point2D tip = new Vector2D.Double( force.getX(), force.getY() ).getDestination( tail );
        Arrow forceArrow = new Arrow( tail, tip, arrowHeadHeight, arrowHeadHeight, arrowTailWidth, 0.5, false );

        Shape forceArrowShape = forceArrow.getShape();
        if( this.lastArrow == null || !this.lastArrow.equals( forceArrow ) ) {
            shapeGraphic.setShape( forceArrowShape );

            Shape forceArrowBody = forceArrow.getTailShape();
            double tgHeight = textGraphic.getHeight();
            double arrowHeight = forceArrowBody.getBounds().getHeight();
            double y = forceArrowBody.getBounds().getY() + arrowHeight / 2 - tgHeight / 2;
            textGraphic.setLocation( forceArrowBody.getBounds().x, (int)y );
        }
        this.lastArrow = forceArrow;
    }

    public String getName() {
        return name;
    }

    public void setUserVisible( boolean userVisible ) {
        this.userVisible = userVisible;
        setVisible( userVisible && nonZero );
    }
}
