/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

/**
 * PhetShapeGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetShapeGraphic extends PhetGraphic {
    private Shape shape;
    private Stroke stroke;
    private Paint fill;
    private Paint border;


    /**
     * Provided for Java Beans conformance
     */
    public PhetShapeGraphic() {
        // noop
    }

    public PhetShapeGraphic( Component component, Shape shape, Color fill ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
    }

    public PhetShapeGraphic( Component component, Shape shape, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.stroke = stroke;
        this.border = border;
    }

    public PhetShapeGraphic( Component component, Shape shape, Color fill, Stroke stroke, Color border ) {
        super( component );
        this.shape = shape;
        this.fill = fill;
        this.stroke = stroke;
        this.border = border;
    }

    public PhetShapeGraphic( Component component ) {
        this( component, null, null );
    }

    public Shape getShape() {
        return shape;
    }

    public Paint getFill() {
        return fill;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public Paint getBorder() {
        return border;
    }

    public void setBorderPaint( Paint border ) {
        this.border = border;
    }

    public Paint getBorderPaint() {
        return this.border;
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            super.saveGraphicsState( g );
            RenderingHints hints = super.getRenderingHints();
            if( hints != null ) {
                g.setRenderingHints( hints );
            }
            if( shape != null ) {
                g.transform( getNetTransform() );
                if( fill != null ) {
                    g.setPaint( fill );
                    g.fill( shape );
                }
                if( stroke != null ) {
                    g.setPaint( border );
                    Stroke origStroke = g.getStroke();
                    g.setStroke( stroke );
                    g.draw( shape );
                    g.setStroke( origStroke );
                }
                try {
                    g.transform( getNetTransform().createInverse() );
                }
                catch( NoninvertibleTransformException e ) {
                    e.printStackTrace();
                }
            }
            super.restoreGraphicsState();
        }
    }

    protected Rectangle determineBounds() {
        if( shape == null ) {
            return null;
        }
        if( stroke == null ) {
            return getNetTransform().createTransformedShape( shape.getBounds() ).getBounds();
        }
        else {
            Shape outlineShape = stroke.createStrokedShape( shape );
            Rectangle bounds = outlineShape.getBounds();
            Rectangle expanded = new Rectangle( bounds.x, bounds.y, bounds.width + 1, bounds.height + 1 ); //necessary to capture the entire bounds.
            return getNetTransform().createTransformedShape( expanded ).getBounds();
        }
    }

    public void setShape( Shape shape ) {
        boolean sameShape = sameShape( this.shape, shape );
        if( !sameShape ) {
            this.shape = shape;
            setBoundsDirty();
            autorepaint();
        }
    }

    private boolean sameShape( Shape a, Shape b ) {
        if( a == null && b == null ) {
            return true;
        }
        else if( a == null && b != null ) {
            return false;
        }
        else if( a != null && b != null ) {
            return false;
        }
        else if( a.equals( b ) ) {
            return true;
        }
        else {
            //            //use specific comparators, not provided by java API.
            if( new GeneralPathComparator().isMatch( a, b ) ) {
                return true;
            }
            else if( new Rectangle2DComparator().isMatch( a, b ) ) {
                return true;
            }
            //this could default to comparinng new Areas., again, that may be better than drawing to the screen
            return false;
        }
    }

    /**
     * A separate class for ease of change when we move it.
     */
    private static class Rectangle2DComparator {
        public boolean isMatch( Shape a, Shape b ) {
            if( a.getClass().equals( Rectangle2D.Double.class ) && b.getClass().equals( Rectangle2D.Double.class ) ) {
                Rectangle2D.Double r = (Rectangle2D.Double)a;
                Rectangle2D.Double s = (Rectangle2D.Double)b;
                boolean same = r.x == s.x && r.y == s.y && r.width == s.width && r.height == s.height;
                return same;
            }
            return false;
        }
    }

    /**
     * A separate class for ease of change when we move it.  This class may not be working currently.
     */
    private static class GeneralPathComparator {
        public boolean isMatch( Shape a, Shape b ) {
            if( a.getClass().equals( GeneralPath.class ) && b.getClass().equals( GeneralPath.class ) ) {
//                GeneralPath x = (GeneralPath)a;
//                GeneralPath y = (GeneralPath)b;
                Area m = new Area( a );
                Area n = new Area( b );
                if( m.equals( n ) ) {//slow, but better than drawing to the screen. //TODO is this working..?
                    return true;
                }
            }
            return false;
        }
    }

    public boolean contains( int x, int y ) {
        if( getShape() != null ) {
            Shape txShape = getNetTransform().createTransformedShape( getShape() );
            return isVisible() && txShape.contains( x, y );
        }
        return false;
    }

    public void setBorderColor( Color color ) {
        this.border = color;
        autorepaint();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        autorepaint();
    }

    public void setFill( Paint fill ) {
        this.fill = fill;
    }

    public void setBorder( Paint border ) {
        this.border = border;
    }

    public void setPaint( Paint paint ) {
        boolean changed = false;
        if( this.fill == null && paint != null ) {
            changed = true;
        }
        else if( this.fill != null && paint == null ) {
            changed = true;
        }
        else if( !this.fill.equals( paint ) ) {
            changed = true;
        }
        if( changed ) {
            this.fill = paint;
            autorepaint();
        }
    }

    public void setColor( Color color ) {
        setPaint( color );
    }


}
