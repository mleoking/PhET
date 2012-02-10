// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.FractionsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class BalanceNode extends PNode {

    public final PImage leftPlatform;
    public final PImage rightPlatform;
    public final double platformOffset = 30.132939438700152;
    public double platformY = 0;

    public BalanceNode() {
        final PImage baseImage = new PImage( FractionsResources.Images.BALANCE_BASE );

        leftPlatform = new PImage( FractionsResources.Images.BALANCE_PLATFORM ) {{
            setOffset( -50.22156573116682, platformOffset );
        }};

        rightPlatform = new PImage( FractionsResources.Images.BALANCE_PLATFORM ) {{
            setOffset( 437.93205317577485, 30.132939438700152 );
        }};
        addChild( new PNode() {{
            leftPlatform.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    removeAllChildren();

                    final Line2D.Double leftLine = new Line2D.Double( baseImage.getFullBounds().getCenter2D(), new Point2D.Double( leftPlatform.getFullBounds().getCenterX(), leftPlatform.getFullBounds().getMaxY() ) );
                    final int beamWidth = 35;
                    final BasicStroke beamStroke = new BasicStroke( 2 );
                    final BasicStroke beamShapeStroke = new BasicStroke( beamWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER );
                    final Color beamAreaColor = Color.white;
                    final Color beamStrokeColor = Color.black;
                    addChild( new PhetPPath( beamShapeStroke.createStrokedShape( leftLine ), beamAreaColor, beamStroke, beamStrokeColor ) );
                    final Line2D.Double rightLine = new Line2D.Double( baseImage.getFullBounds().getCenter2D(), new Point2D.Double( rightPlatform.getFullBounds().getCenterX(), rightPlatform.getFullBounds().getMaxY() ) );
                    addChild( new PhetPPath( beamShapeStroke.createStrokedShape( rightLine ), beamAreaColor, beamStroke, beamStrokeColor ) );

                }
            } );
        }} );

        addChild( baseImage );
        addChild( leftPlatform );
        addChild( rightPlatform );
        scale( 0.5 );
    }

    public void update( double deltaWeight ) {
        double equilibriumPosition = deltaWeight * 5;
        double distanceFromEquilibrium = platformY - equilibriumPosition;
        double k = 0.3;
        double force = -k * distanceFromEquilibrium;
        double dt = 1;
        platformY += force * dt * dt;
        platformY = MathUtil.clamp( -70, platformY, 70 );
        leftPlatform.setOffset( leftPlatform.getOffset().getX(), platformOffset + platformY );
        rightPlatform.setOffset( rightPlatform.getOffset().getX(), platformOffset - platformY );
    }
}