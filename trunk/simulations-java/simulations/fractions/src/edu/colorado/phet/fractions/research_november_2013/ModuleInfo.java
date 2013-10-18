// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractions.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionsIntroCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;

/**
 * Created by Sam on 10/18/13.
 */
public class ModuleInfo {
    boolean running;
    private long startTime;
    private long elapsedTime = 0;
    private int clicks = 0;
    private Module module;
    //For Fractions Intro
    private Representation representation;
    private HashSet<String> usedRepresentations = new HashSet<String>();

    public ModuleInfo( Module module ) {
        this.module = module;
        this.module.getSimulationPanel().addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {

            }

            public void mousePressed( MouseEvent e ) {
                clicks++;
            }

            public void mouseReleased( MouseEvent e ) {

            }

            public void mouseEntered( MouseEvent e ) {

            }

            public void mouseExited( MouseEvent e ) {

            }
        } );

        if ( module instanceof FractionsIntroModule ) {
            FractionsIntroModule m = (FractionsIntroModule) module;
            FractionsIntroCanvas canvas = (FractionsIntroCanvas) m.getSimulationPanel();
            FractionsIntroModel model = canvas.model;
            model.representation.addObserver( new VoidFunction1<Representation>() {
                public void apply( Representation representation ) {
                    ModuleInfo.this.representation = representation;
                    usedRepresentations.add( representation.name() );
                }
            } );
        }
    }

    @Override public String toString() {
        return getElapsedTime() / 1000.0 + " sec, clicks: " + getClicks() + ", " + ( ( module instanceof FractionsIntroModule ) ? "representations: " + usedRepresentations.toString() : "" );
    }

    public long getElapsedTime() {
        if ( running ) {
            return ( System.currentTimeMillis() - startTime ) + elapsedTime;
        }
        return elapsedTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning( boolean running ) {
        if ( running != this.running ) {
            this.running = running;

            if ( this.running ) {
                startTime = System.currentTimeMillis();
            }
            else {
                elapsedTime += ( System.currentTimeMillis() - startTime );
            }
        }
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks( int clicks ) {
        this.clicks = clicks;
    }
}
