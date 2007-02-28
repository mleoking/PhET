package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.common.SegmentGraphic;
import edu.colorado.phet.bernoulli.spline.segments.Segment;
import edu.colorado.phet.bernoulli.spline.segments.SegmentPath;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 25, 2003
 * Time: 1:14:48 AM
 * Copyright (c) Aug 25, 2003 by Sam Reid
 */
public class SegmentPathGraphic {
    private SegmentPath path;
    private ModelViewTransform2d transform;

    public SegmentPathGraphic( SegmentPath path, ModelViewTransform2d transform ) {
        this.path = path;
        this.transform = transform;
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.white );
        Stroke stroke = new BasicStroke( 4 );
        for( int i = 0; i < path.numSegments(); i++ ) {
            Segment seg = path.segmentAt( i );
            SegmentGraphic sg = new SegmentGraphic( transform, seg.getStartPoint().getX(), seg.getStartPoint().getY(), seg.getFinishPoint().getX(), seg.getFinishPoint().getY(), Color.white, stroke );
            sg.paint( g );

        }
    }
}
