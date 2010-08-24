/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;


/**
 * User: Sam Reid
 * Date: Mar 23, 2004
 * Time: 2:25:44 PM
 */
public class JaggedSplitBandGraphic extends BandSetGraphic {
    VerticalTearLine rightTear;
    VerticalTearLine leftTear;
    private GeneralPath leftPath;
    private GeneralPath rightPath;

    public JaggedSplitBandGraphic( EnergySection diodeSection, ModelViewTransform2D transform, SemiconductorBandSet bandSet, Rectangle2D.Double viewport ) {
        super( diodeSection, transform, bandSet, viewport );
        Rectangle2D.Double bandRect = bandSet.getBounds();
//        Rectangle2D.Double bandRect = viewport;
        Vector2D src = new Vector2D( bandRect.getX() + bandRect.getWidth() / 2, bandRect.getY() );
        Vector2D dst = new Vector2D( bandRect.getX() + bandRect.getWidth() / 2, bandRect.getY() + bandRect.getHeight() );
        double tearDX = bandRect.getWidth() / 35;
        AbstractVector2DInterface leftSrc = src.getAddedInstance( -tearDX, 0 );
        AbstractVector2DInterface rightSrc = src.getAddedInstance( tearDX, 0 );
        AbstractVector2DInterface leftDst = dst.getAddedInstance( -tearDX, 0 );
        AbstractVector2DInterface rightDst = dst.getAddedInstance( tearDX, 0 );
        int tearCount = 28;
        rightTear = new VerticalTearLine( rightSrc, rightDst, bandRect.getWidth() / 10, tearCount );
        leftTear = new VerticalTearLine( leftSrc, leftDst, bandRect.getWidth() / 10, tearCount );

        DoubleGeneralPath leftApp = new DoubleGeneralPath( leftDst.getX(), leftDst.getY() );//leftTear.getPath().getGeneralPath());
        leftApp.lineTo( bandRect.getX(), bandRect.getY() + bandRect.getHeight() );
        leftApp.lineTo( bandRect.getX(), bandRect.getY() );
        leftApp.lineTo( leftSrc );
        leftPath = leftApp.getGeneralPath();
        leftPath.append( leftTear.getPath().getGeneralPath(), false );

        DoubleGeneralPath rightApp = new DoubleGeneralPath( rightDst.getX(), rightDst.getY() );
        rightApp.lineTo( bandRect.getX() + bandRect.getWidth(), bandRect.getY() + bandRect.getHeight() );
        rightApp.lineTo( bandRect.getX() + bandRect.getWidth(), bandRect.getY() );
        rightApp.lineTo( rightSrc );

        rightPath = rightApp.getGeneralPath();
        rightPath.append( rightTear.getPath().getGeneralPath(), false );
    }

    public void paint( Graphics2D graphics2D ) {
        //        Shape leftViewShape=getTransform().createTransformedShape(leftPath);
//        clipGraphic.setModelClip(leftViewShape);
        Area a = new Area( leftPath );
        a.add( new Area( rightPath ) );
//        a.intersect( new Area( bandSet.getBounds() ) );
        a.intersect( new Area( getViewport() ) );
//        clipGraphic.setModelClip(leftPath);
        clipGraphic.setModelClip( a );
//        clipGraphic.setModelClip(bandRect);
        super.paint( graphics2D );

        //        BasicGraphicsSetup bgs=new BasicGraphicsSetup();
//        bgs.setup(graphics2D);
//
        graphics2D.setColor( Color.black );
        graphics2D.setStroke( new BasicStroke( 1 ) );
        Shape s = rightTear.getPath().getGeneralPath();
        Shape trf = getTransform().createTransformedShape( s );
        graphics2D.draw( trf );
        graphics2D.draw( getTransform().createTransformedShape( leftTear.getPath().getGeneralPath() ) );
//        graphics2D.setColor(Color.red);
//        graphics2D.draw(getTransform().createTransformedShape(leftPath));
    }
}
