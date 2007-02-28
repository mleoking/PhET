package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.Drop;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 8, 2003
 * Time: 2:12:46 AM
 * Copyright (c) Sep 8, 2003 by Sam Reid
 */
public class HoleInThePipe extends ModelElement {
    private Pipe pipe;
    private ControlSection cs;
    private boolean top;
    private BernoulliModule module;
    Random rand = new Random();
    private double density = 1;
    private double gravity = 9.8;
    private int count = 0;
    private int modulo = 3;

    public HoleInThePipe( Pipe pipe, ControlSection cs, boolean top, BernoulliModule module ) {
        this.pipe = pipe;
        this.cs = cs;
        this.top = top;
        this.module = module;
    }

    public void createSpurtedDrop( double radius ) {
        PhetVector loc = null;
        if( top ) {
            loc = cs.getTopPoint();
        }
        else {
            loc = cs.getBottomPoint();
        }
        double dxSpread = .00007;
        double vx = rand.nextDouble() * dxSpread;
        vx -= ( dxSpread / 2 );
        double height = cs.getHeight();

        double speed = CrossSectionalVolume.widthToSpeed( height );
        speed += .001;
//        speed=
        speed = 1 / ( speed * 400000 );
        vx += speed / Math.sqrt( 2 );
        double vy = speed / Math.sqrt( 2 );

        Drop drop = new Drop( loc.getX(), loc.getY(), radius, vx, vy );
        module.addDrop( drop );
    }

    public void stepInTime( double dt ) {
        count++;
        if( count % modulo == 0 ) {
            createSpurtedDrop( .071 );
        }
    }
}
