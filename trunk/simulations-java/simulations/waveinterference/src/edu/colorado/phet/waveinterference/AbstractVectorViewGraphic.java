/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveSideView;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: Mar 31, 2006
 * Time: 7:47:46 PM
 * Copyright (c) Mar 31, 2006 by Sam Reid
 */

public abstract class AbstractVectorViewGraphic extends WaveSideView {
    private int distBetweenSamples;

    public AbstractVectorViewGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, int distBetweenSamples ) {
        super( waveModel, latticeScreenCoordinates );
        this.distBetweenSamples = distBetweenSamples;
        PNode text = new PText( "HELLO" );
        addChild( text );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        update();
    }

    public void update() {
        //todo might not need this class.
//        super.update();
//        if( distBetweenSamples == 0 ) {//not finished constructor yet
//            return;
//        }
//        removeAllChildren();
//        int yValue = super.getYValue();
//        for( int i = 0; i < getLattice2D().getWidth(); i += distBetweenSamples ) {
//            float y = getY( i, yValue );
//            float x = getX( i );
//            addArrow( x, y );
//        }
    }

    protected abstract void addArrow( float x, float y );
}
