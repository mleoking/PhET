/**
 * Class: FieldLatticeView
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Jun 2, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.userinterface.graphics.ObservingGraphic;
import edu.colorado.phet.emf.model.RetardedFieldElement;
import edu.colorado.phet.emf.view.graphics.splines.CubicSpline;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Observable;

public class FieldLatticeView implements ObservingGraphic {

    private Point origin;
    private int latticeSpacingX;
    private int latticeSpacingY;
    private int numLatticePtsX;
    private int numLatticePtsY;
    private Vector2D[][] latticePts;
    private RetardedFieldElement[][] fieldElements;
    private int[] controlPts;
    private Electron sourceElectron;
    private boolean fieldCurvesEnabled;


    public FieldLatticeView( Electron sourceElectron, Point origin,
                             int width, int height,
                             int latticeSpacingX, int latticeSpacingY ) {
        this.origin = origin;
        this.latticeSpacingX = latticeSpacingX;
        this.latticeSpacingY = latticeSpacingY;
        numLatticePtsX = 1 + ( width - 1 ) / latticeSpacingX;
        numLatticePtsY = 1 + ( height - 1 ) / latticeSpacingY;
        latticePts = new Vector2D[numLatticePtsY][numLatticePtsX];
        fieldElements = new RetardedFieldElement[numLatticePtsY][numLatticePtsX];
        for( int i = 0; i < numLatticePtsY; i++ ) {
            for( int j = 0; j < numLatticePtsX; j++ ) {
                latticePts[i][j] = new Vector2D();
                fieldElements[i][j] = new RetardedFieldElement( new Point2D.Double( origin.getX() + j * latticeSpacingX,
                                                                                    origin.getY() + i * latticeSpacingY ),
                                                                sourceElectron );
            }
        }
        controlPts = new int[numLatticePtsX];
        this.sourceElectron = sourceElectron;
        sourceElectron.addObserver( this );
    }

    CubicSpline spline = new CubicSpline();

    public void paint( Graphics2D g2 ) {
        for( int i = 0; i < numLatticePtsY; i++ ) {
            spline.reset();
            for( int j = 0; j < numLatticePtsX; j++ ) {
                int x = (int)origin.getX() + j * latticeSpacingX - 1;
                int y = (int)origin.getY() + i * latticeSpacingY - 1;
                g2.drawArc( x - 1, y - 1,
                            2, 2,
                            0, 360 );
                int lineEndX = (int)( x + latticePts[i][j].getX() / 10 );
                int lineEndY = (int)( y + latticePts[i][j].getY() );
                drawArrow( g2, x, y, lineEndX, lineEndY );
                controlPts[j] = lineEndY;
                spline.addPoint( lineEndX, lineEndY );
            }

            if( fieldCurvesEnabled ) {
                Color oldColor = g2.getColor();
                g2.setColor( Color.BLUE );
                spline.paint( g2 );
                g2.setColor( oldColor );
            }
        }
    }

    private Polygon arrowHead = new Polygon();

    private void drawArrow( Graphics2D g2, int x1, int y1, int x2, int y2 ) {
        if( x1 != x2 || y1 != y2 ) {
            g2.drawLine( x1, y1, x2, y2 );
            int dy = y2 > y1 ? -2 : 2;
            arrowHead.reset();
            arrowHead.addPoint( x2, y2 );
            arrowHead.addPoint( x2 + 2, y2 + dy );
            arrowHead.addPoint( x2 - 2, y2 + dy );
            g2.drawPolygon( arrowHead );
            g2.fillPolygon( arrowHead );
        }
    }

    /**
     * Get the strength of the field at each of the lattice points
     */
    private Point2D.Double latticePtLocation = new Point2D.Double();

    public void update( Observable o, Object arg ) {
        if( o instanceof Electron ) {
            for( int i = 0; i < numLatticePtsY; i++ ) {
                for( int j = 0; j < numLatticePtsX; j++ ) {
//                    Vector2D fs;
//                    fs = fieldElements[i][j].getFieldStrength();
//                    latticePts[i][j] = fs;
                    latticePtLocation.x = j * latticeSpacingX + origin.getX();
                    latticePtLocation.y = i * latticeSpacingY + origin.getY();
                    Vector2D fs = sourceElectron.getFieldAtLocation( latticePtLocation );
//                    Vector2D fs = sourceElectron.getInstantaneousStaticField( latticePtLocation );
                    latticePts[i][j].setX( fs.getX() );
                    latticePts[i][j].setY( fs.getY() );
                }
            }
        }
    }

    public void setFieldCurvesEnabled( boolean enabled ) {
        this.fieldCurvesEnabled = enabled;
    }
}
