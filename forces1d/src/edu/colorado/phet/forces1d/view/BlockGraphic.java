/** Sam Reid*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.Force1dControlPanel;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 10:27:14 AM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public class BlockGraphic extends CompositePhetGraphic {
    private Block block;
    private Force1DModel model;
    private ModelViewTransform2D transform2D;
    private LinearTransform1d transform1d;
    private PhetShapeGraphic graphic;
    private Force1DPanel panel;

    public BlockGraphic( Force1DPanel panel, final Block block, final Force1DModel model,
                         ModelViewTransform2D transform2D, final LinearTransform1d transform1d ) {
        super( panel );
        this.panel = panel;
        this.block = block;
        this.model = model;
        this.transform2D = transform2D;
        this.transform1d = transform1d;
        graphic = new PhetShapeGraphic( panel );
        addGraphic( graphic );

        transform1d.addListener( new LinearTransform1d.Listener() {
            public void transformChanged( LinearTransform1d transform ) {
                update();//Need this in case the model is paused.
            }
        } );
        update();

        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point ctr = getCenter();
                double dx = e.getPoint().x - ctr.x;
                dx /= ArrowSetGraphic.forceLengthScale;
                model.setAppliedForce( dx );
            }
        } );
        setCursorHand();
    }

    public Dimension computeDimension() {
        int minWidth = 30;
        int minHeight = 30;
        int width = (int)( block.getMass() + minWidth );
        int height = (int)( block.getMass() + minHeight );
        return new Dimension( width, height );
    }

    public void update() {
        float fracKinetic = (float)( block.getKineticFriction() / Force1dControlPanel.MAX_KINETIC_FRICTION );
        Color color = new Color( fracKinetic, fracKinetic / 2, .75f );

        Dimension dim = computeDimension();
        Point center = getCenter();
        RoundRectangle2D r = new RoundRectangle2D.Double( 0, 0, 0, 0, dim.width * .2, dim.height * .2 );
        r.setFrameFromCenter( center.x, center.y, center.x + dim.width / 2, center.y + dim.height / 2 );
        graphic.setShape( r );
        graphic.setColor( color );
    }

    public Point getCenter() {
        Dimension dim = computeDimension();
        int x = (int)transform1d.transform( block.getPosition() );
        int y = panel.getWalkwayGraphic().getPlatformY() - dim.height / 2;
        return new Point( x, y );
    }
}
