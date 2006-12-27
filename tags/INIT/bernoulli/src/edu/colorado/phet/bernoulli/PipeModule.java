/**
 * Class: PipeModule
 * Package: edu.colorado.phet.bernoulli
 * Author: Another Guy
 * Date: Sep 29, 2003
 */
package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.pipe.*;
import edu.colorado.phet.bernoulli.tube.Tube;
import edu.colorado.phet.bernoulli.tube.TubeGraphic;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.clock2.DefaultClock;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class PipeModule extends BernoulliModule {

    Pipe pipe;
    PipeGraphic pipeGraphic;
    VolumeGraphic volumeGraphic;
    ModelElement volumeTimeStep;
    private Tube towerToPipeTube;
    static double pipeX = 9;
    static double pipeY = 0;
    static double pipeLength = 10;
    static double pipeHeight = 2;
    static boolean pipeOn = true;

    public PipeModule( DefaultClock dc ) {
        super( "Pipe Module", dc );

        // Make the pipe
        createPipe();

        // Run a tube from the water tower to the pipe
        makeTowerToPipeTube();

        getBernoulliModel().addVessel( pipe );
    }


    protected void createPipe() {
//        ControlSection opening = new ControlSection(2.5, -1.2, .6);
        ControlSection opening = new ControlSection( pipeX, pipeY, pipeHeight );
        pipe = new Pipe( opening, pipeLength, 5, 14, 7 );
        int numFlowLines = 7;
        pipeGraphic = new PipeGraphic( getTransform(), pipe, rm, numFlowLines, this );
        getApparatusPanel().addGraphic( pipeGraphic, vesselGraphicLevel );

        final CrossSectionalVolume volume = new CrossSectionalVolume( pipe, 0 );
        volumeGraphic = new VolumeGraphic( volume, getTransform(), rm );
        //Add the volume graphic inside the pipe graphic.  Sweet.
        pipeGraphic.addGraphic( volumeGraphic, vesselGraphicLevel + 1 );
        volumeTimeStep =
        ( new ModelElement() {
            public void stepInTime( double dt ) {
                volume.stepInTime( dt );
            }
        } );
        getModel().addModelElement( volumeTimeStep );
        pipe.recomputeState();
        pipeOn = true;
        pipeGraphic.translatePoint( new Point2D.Double( 0, .001 ), true, 0 );
        pipeGraphic.translatePoint( new Point2D.Double( 0, -.001 ), true, 0 );
//        volume.parentChanged();
//        pipeGraphic.bottomControlPointAt(0).mouseDragged();
    }


    private void makeTowerToPipeTube() {
        towerToPipeTube = new Tube();
        setTowerToPipeTube();
        pipe.addObserver( new SimpleObserver() {
            public void update() {
                setTowerToPipeTube();
            }
        } );
        final TubeGraphic towerToPipeTubeGraphic = new TubeGraphic( towerToPipeTube, getTransform() );

        getApparatusPanel().addGraphic( towerToPipeTubeGraphic, tubeGraphicLevel );
        getTower().addObserver( new SimpleObserver() {
            public void update() {
                setTowerToPipeTube();
            }
        } );
    }

    private void setTowerToPipeTube() {
        PhetVector midPipeHeight = pipe.controlSectionAt( 0 ).getMidpoint();
        towerToPipeTube.clear();
        towerToPipeTube.addPoint( new Point2D.Double( ( getTower().getX() + getTower().getWidth() / 2 ), getTower().getY() ) );
        towerToPipeTube.addPoint( new Point2D.Double( ( getTower().getX() + getTower().getWidth() / 2 ), midPipeHeight.getY() ) );
        towerToPipeTube.addPoint( new Point2D.Double( pipeX, midPipeHeight.getY() ) );
        towerToPipeTube.updateObservers();
    }

    public void setPipeOn( boolean pipeOn ) {
        if( this.pipeOn == pipeOn ) {
            return;
        }
        else if( pipeOn ) {
            createPipe();
        }
        else {
            deactivatePipe();
        }
    }

    private void deactivatePipe() {
        pipeOn = false;
        getApparatusPanel().removeGraphic( pipeGraphic );
        getApparatusPanel().removeGraphic( volumeGraphic );
        getModel().removeModelElement( volumeTimeStep );
        getApparatusPanel().repaint();
    }

    public void zoomToPipe() {
        double minx = pipe.controlSectionAt( 0 ).getBottomX();
        double maxx = pipe.controlSectionAt( pipe.numControlSections() - 1 ).getBottomX();
        double miny = pipe.controlSectionAt( 0 ).getBottomY();
        double maxy = pipe.controlSectionAt( 0 ).getTopY();
        minx -= 1.5;
        maxx += 1.5;
        miny -= 1.5;
        maxy += 1.5;
        zoomTo( new Rectangle2D.Double( minx, miny, maxx - minx, maxy - miny ) );
    }

    protected void createModelViewTransform() {
        Rectangle2D.Double viewport = new Rectangle2D.Double( -2, -1, pipeX + pipeLength + 2 + 2, 20 );
        this.viewPortInit = new Rectangle2D.Double( viewport.x, viewport.y, viewport.width, viewport.height );
        backgroundColor = new Color( 120, 170, 245 );
        getApparatusPanel().setBackground( backgroundColor );
        getApparatusPanel().addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                if( antialias ) {
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                }
                else {
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                }
            }
        }, -1 );
        setTransform( new ModelViewTransform2d( viewport, new Rectangle( 0, 0, 1, 1 ) ) );
    }
}
