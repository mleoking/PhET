package edu.colorado.phet.semiconductor.macro.doping;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.semiconductor.common.SimpleBufferedImageGraphic;
import edu.colorado.phet.semiconductor.common.TransformGraphic;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.bounds.Boundary;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 2:04:53 AM
 */
public class DopantGraphic extends TransformGraphic implements Boundary {
    Dopant dopant;
    private Point viewLoc;
    private SimpleBufferedImageGraphic imageGraphic;
    //    private Rectangle2D.Double rect;
    private BufferedImage buffer;

    public DopantGraphic( Dopant dopant, ModelViewTransform2D transform, BufferedImage image, double width ) {
        super( transform );
        buffer = new BufferedImage( image.getWidth() * 3, image.getHeight() * 3, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = buffer.createGraphics();
        g.setPaint( new TexturePaint( image, new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() ) ) );
        g.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );
        this.imageGraphic = new SimpleBufferedImageGraphic( buffer );

        this.dopant = dopant;
//        this.random = new Random( seed++ );
//        double minx=0;
//        double miny=0;
//        double maxx=0;
//        double maxy=0;
//        for (int i = 0; i < numPics; i++) {
//            double x = (random.nextDouble() * width - width / 2) * rad;
//            double y = (random.nextDouble() * width - width / 2) * rad;
//            loc.add(new Point2D.Double(x, y));
//            if (x>maxx)
//                maxx=x;
//            if (x<minx)
//                minx=x;
//            if (y>maxy)
//                maxy=y;
//            if (y<miny)
//                miny=y;
//        }
//        rect=new Rectangle2D.Double(minx,miny,maxx-minx,maxy-miny);
//        imageGraphic = new SimpleBufferedImageGraphic(image);
        update();
        dopant.addObserver( new SimpleObserver() {
            public void update() {
                DopantGraphic.this.update();
            }
        } );
    }

    public void paint( Graphics2D graphics2D ) {
//        graphics2D.drawRenderedImage(buffer, AffineTransform.getTranslateInstance(viewLoc.x,viewLoc.y));
        imageGraphic.paint( graphics2D );
//        for (int i = 0; i < loc.size(); i++) {
//            Point2D.Double pt = (Point2D.Double) loc.get(i);
//            Point dx = getTransform().modelToViewDifferential(pt.x, pt.y);
//            imageGraphic.setPosition(viewLoc.x + dx.x, viewLoc.y + dx.y);
//            imageGraphic.paint(graphics2D);
//        }
//        graphics2D.setStroke(new BasicStroke(2));
//        graphics2D.setColor(Color.red);
//        graphics2D.draw(rect);
    }

    public void update() {
        viewLoc = super.getTransform().modelToView( dopant.getPosition() );
        imageGraphic.setPosition( viewLoc );
    }

    public boolean contains( int x, int y ) {
        Shape sh = getShape();
        return sh.contains( x, y );
//        if (rect == null)
//            return false;
//        return rect.contains(x, y);
    }

    public Shape getShape() {
        return imageGraphic.getShape();
//        int halfWidth=(mageGraphic.getBufferedImage().getWidth());
//
//        double inset=getTransform().viewToModelDifferentialX(imageGraphic.getBufferedImage().getWidth())/2;
//        double width=max-min;
//        Rectangle2D.Double r=new Rectangle2D.Double(min-inset,min-inset,width+inset*2,width+inset*2);
//        return r;

//        Rectangle2D r = null;
//        for (int i = 0; i < loc.size(); i++) {
//            Point2D.Double pt = (Point2D.Double) loc.get(i);
//            Point dx = getTransform().modelToViewDifferential(pt.x, pt.y);
//            imageGraphic.setPosition(viewLoc.x + dx.x, viewLoc.y + dx.y);
//            if (r == null)
//                r = imageGraphic.getShape().getBounds2D();
//            else
//                r = r.createUnion(imageGraphic.getShape().getBounds2D());
//        }
//        return r;
//        AffineTransform at=AffineTransform.getTranslateInstance(dx.x)
//        return rect;
    }

    public void translate( double dx, double dy ) {
        Point2D.Double trf = getTransform().viewToModelDifferential( (int) dx, (int) dy );
        dopant.translate( trf.getX(), trf.getY() );
    }

    public Dopant getDopant() {
        return dopant;
    }

    public DopantType getType() {
        return dopant.getType();
    }

    public PhetVector getCenter() {
        return new PhetVector( viewLoc );
    }


}
