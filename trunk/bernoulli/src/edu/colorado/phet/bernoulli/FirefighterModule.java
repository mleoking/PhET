/**
 * Class: FirefighterModule
 * Package: edu.colorado.phet.bernoulli
 * Author: Another Guy
 * Date: Sep 29, 2003
 */
package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.tube.Tube;
import edu.colorado.phet.bernoulli.tube.TubeGraphic;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.clock2.DefaultClock;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class FirefighterModule extends BernoulliModule {

    static private double firefighterX = 10;
    static private double firefighterWidth = 2;
    static private double firefighterHeight = 2;
    static private double burningHouseX = firefighterX + 10;
    static private double burningHouseWidth = 2;
    private Tube towerToFirefighterTube;
    private SimpleObservable firefighter;
    static private double firefighterY = firefighterHeight / 2;
    static private double nozzleLength = 2;
    static private double nozzleAngle = Math.PI / 4;;
    private int nozzleLevel = super.tubeGraphicLevel + 1;
    private Nozzle nozzle;
    private int time;
    private Random r;
    private double flameYRange;
    private double flameYMIN;
    private double flameXRange;
    private double flameXMIN;
    private double flameWidth;
    private double flameHeight;

    public FirefighterModule( DefaultClock dc ) {
        super( "Firefighter", dc );

        // Make nozzle
        DropAdder da = new DropAdder() {
            public void addDrop( Drop drop ) {
                getTower().getTank().removeWater( drop.getRadius() );
                FirefighterModule.this.addDrop( drop );
            }
        };
        nozzle = new Nozzle( new Point2D.Double( firefighterX, firefighterY ),
                             nozzleLength, nozzleAngle, da, 2 );
        getModel().addModelElement( nozzle.getModelElement() );
        NozzleView nozzleView = new NozzleView( nozzle, getTransform() );
        getApparatusPanel().addGraphic( nozzleView, nozzleLevel );
        addViewedObservable( nozzle );

        // Make a tube from the tower to the firefighter
        makeTowerToNozzleTube();

        FireDog firedog = new FireDog( nozzle.getPivot().getX(), nozzle.getPivot().getY(), getTransform() );
        getApparatusPanel().addGraphic( firedog, 1000 );

        getTower().getTank().setWaterVolume( 0 );

        Castle castle = new Castle( 16.8, -2.5, 6, 20 );
        CastleView cv = new CastleView( castle, getTransform(), getApparatusPanel() );
        getApparatusPanel().addGraphic( cv, nozzleLevel + 1 );

        flameWidth = 1.5;
        flameHeight = 1.5;
        flameXMIN = castle.getX();
        double flameXMAX = castle.getX() + castle.getWidth() - flameWidth;
        flameXRange = flameXMAX - flameXMIN;

        flameYMIN = 0;
        double flameYMAX = castle.getY() + castle.getHeight() - 2 - flameHeight;
        flameYRange = flameYMAX - flameYMIN;
        r = new Random();

        double flameX = r.nextDouble() * flameXRange + flameXMIN;
        double flameY = r.nextDouble() * flameYRange + flameYMIN;
        final Flame flame = new Flame( flameX, flameY, flameWidth, flameHeight );
        FlameGraphic fg = new FlameGraphic( flame, getTransform(), getApparatusPanel() );
        getApparatusPanel().addGraphic( fg, nozzleLevel + 2 );
        ModelElement flameDetector = new ModelElement() {
            public void stepInTime( double dt ) {
                Rectangle2D.Double rect = flame.getRectangle();
                for( int i = 0; i < drops.size(); i++ ) {
                    Drop d = (Drop)drops.get( i );
                    if( rect.contains( d.getPosition().getX(), d.getPosition().getY() ) ) {
                        if( rect.width <= .5 ) {
                            double flameX = r.nextDouble() * flameXRange + flameXMIN;
                            double flameY = r.nextDouble() * flameYRange + flameYMIN;
                            flame.setState( flameX, flameY, flameWidth, flameHeight );
                        }
                        else {
                            flame.reduceSize( .08 );
                        }
                    }
                }

            }
        };
        getModel().addModelElement( flameDetector );
//        Flame flame=castle.newFlame();
    }

    private void makeTowerToNozzleTube() {
        towerToFirefighterTube = new Tube();
        setTowerToPipeTube();

        final TubeGraphic towerToNozzleTubeGraphic = new TubeGraphic( towerToFirefighterTube, getTransform() );

        getTower().addPressureListener( new PressureListener() {
            public void pressureChanged( double pressure ) {
//                System.out.println("pressure = " + pressure);
                nozzle.setInputPressure( pressure <= 0 ? 0 :
//                        pressure + Water.rho * Water.g * (getTower().getY() - nozzle.getOutlet().getY() )
                                         getInputPressure() );
            }
        } );
        getTower().notifyPressureListeners();

        getApparatusPanel().addGraphic( towerToNozzleTubeGraphic, tubeGraphicLevel );
        getTower().addObserver( new SimpleObserver() {
            public void update() {
                setTowerToPipeTube();
            }
        } );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( time > 4000 ) {//workaround for Weird bug I don't understand.
                    getModel().removeModelElement( this );
//                    getTower().getTank().updateObservers();
//                    getTower().getTank().setWaterVolume(getTower().getTank().getWaterVolume());
                    getTower().notifyPressureListeners();
//                    getTower().translate(0);
//                    getApparatusPanel().addGraphic(new Graphic() {
//                        public void paint(Graphics2D g) {
//                            g.drawString("Time="+time,100,100);
//                        }
//                    }, 1000);
                }
                else {
                    time += dt;
                }
            }
        } );
        getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                getTower().updateObservers();
                getTower().notifyPressureListeners();
            }
        } );
        getTower().notifyPressureListeners();
    }

    private double getInputPressure() {
        double h = getTower().getY() + getTower().getTank().getWaterHeight() - nozzle.getOutlet().getY();
        double g = Water.g;
        double rho = Water.rho;
        return rho * g * h;
    }

    private void setTowerToPipeTube() {
        towerToFirefighterTube.clear();
        towerToFirefighterTube.addPoint( new Point2D.Double( ( getTower().getX() + getTower().getWidth() / 2 ), getTower().getY() ) );
        towerToFirefighterTube.addPoint( new Point2D.Double( ( getTower().getX() + getTower().getWidth() / 2 ), firefighterHeight / 2 ) );
        towerToFirefighterTube.addPoint( new Point2D.Double( nozzle.getPivot().getX(), nozzle.getPivot().getY() ) );
        towerToFirefighterTube.updateObservers();
    }

    protected void createModelViewTransform() {
        Rectangle2D.Double viewport = new Rectangle2D.Double( -2, -1, burningHouseX + burningHouseWidth + 2, 20 );
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
