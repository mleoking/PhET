/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.AbstractWaveSideView;
import edu.colorado.phet.waveinterference.view.RotationGlyph;
import edu.colorado.phet.waveinterference.view.RotationWaveGraphic;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: May 17, 2006
 * Time: 11:22:16 AM
 * Copyright (c) May 17, 2006 by Sam Reid
 */

public class RotationWaveGraphic3D extends RotationWaveGraphic {
    public RotationWaveGraphic3D( WaveModelGraphic waveModelGraphic, PNode topView, RotationGlyph rotationGlyph ) {
        super( waveModelGraphic, topView, new DummySideView(), rotationGlyph );
    }

    protected void showSideView() {
        super.showSideView();
        showTopView();
    }

    public static class DummySideView extends AbstractWaveSideView {

        public void setSpaceBetweenCells( double dim ) {
        }

        public void update() {
        }
    }
}
