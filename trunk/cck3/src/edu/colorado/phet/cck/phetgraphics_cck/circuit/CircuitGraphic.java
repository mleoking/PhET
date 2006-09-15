/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.common.CCKStrings;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.*;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.cck.phetgraphics_cck.CCKApparatusPanel;
import edu.colorado.phet.cck.phetgraphics_cck.CCKModule;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.*;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.CompositeGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_cck.view.util.BufferedImageUtils;
import edu.colorado.phet.common_cck.view.util.CachingImageLoader;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 10:17:59 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class CircuitGraphic extends CompositeGraphic {
    private static double JUNCTION_RADIUS = CCKModel.JUNCTION_RADIUS;
    public static final Color COPPER = CCKLookAndFeel.COPPER;
    public static final Color SILVER = Color.gray;
    private CompositeGraphic filamentLayer = new CompositeGraphic();
    private CompositeGraphic branches = new CompositeGraphic();
    private CompositeGraphic fires = new CompositeGraphic();
    private CompositeGraphic leverLayer = new CompositeGraphic();
    private CompositeGraphic junctionLayer = new CompositeGraphic();
    private ParticleSetGraphic particleSetGraphic;
    private CompositeGraphic ammeterTopLayer = new CompositeGraphic();
    private CompositeGraphic readouts = new CompositeGraphic();
    private CompositePhetGraphic solderLayer;

    private Circuit circuit;
    private ModelViewTransform2D transform;
    private ApparatusPanel apparatusPanel;
    private double STICKY_THRESHOLD;
    private CCKModule module;
    private ArrayList listeners = new ArrayList();
    private Hashtable readoutMap = new Hashtable();
    private boolean lifelike = true;
    private GraphicSource graphicSource;
    private boolean readoutGraphicsVisible = false;
    private CachingImageLoader hashedImageLoader = new CachingImageLoader();
    public static final boolean GRAPHICAL_DEBUG = false;


    public CircuitGraphic( final CCKModule module, CCKApparatusPanel apparatusPanel ) throws IOException {
        this.apparatusPanel = apparatusPanel;
        graphicSource = new Lifelike();
        lifelike = true;

        STICKY_THRESHOLD = module.getCCKModel().getScale();

        this.module = module;
        this.circuit = module.getCircuit();
        particleSetGraphic = new ParticleSetGraphic( module );
//        this.transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle( 0, 0, 100, 100 ) );
        this.transform = apparatusPanel.getTransform();

        solderLayer = new CompositePhetGraphic( this.apparatusPanel );
        solderLayer.setVisible( true );
        module.getCircuit().addCircuitListener( new CircuitListener() {
            public void junctionRemoved( Junction junction ) {
                deleteSolderGraphic( junction );
            }

            public void branchRemoved( Branch branch ) {
            }

            public void junctionsMoved() {
            }

            public void branchesMoved( Branch[] branches ) {
            }

            public void junctionAdded( Junction junction ) {
                deleteSolderGraphic( junction );
                SolderGraphic sg = new SolderGraphic( module.getApparatusPanel(), junction, transform, circuit, CircuitGraphic.this );
                solderLayer.addGraphic( sg );
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
            }

            public void branchAdded( Branch branch ) {
            }

        } );
        Graphic connectorGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                if( isLifelike() ) {
                    g.setColor( COPPER );
                }
                else {
                    g.setColor( Color.black );
                }
                Graphic[] gr = junctionLayer.getGraphics();
                for( int i = 0; i < gr.length; i++ ) {
                    HasJunctionGraphic ho = (HasJunctionGraphic)gr[i];
                    Junction jo = ho.getJunctionGraphic().getJunction();
                    Branch[] n = circuit.getAdjacentBranches( jo );
                    boolean onlyBranches = true;
                    for( int j = 0; j < n.length; j++ ) {
                        Branch branch = n[j];
                        if( !branch.getClass().getName().equals( Branch.class.getName() ) ) {
                            onlyBranches = false;
                            break;
                        }
                    }
                    if( onlyBranches && n.length > 1 ) {
                        Shape shape = ho.getJunctionGraphic().getShape();
                        g.fill( shape );
                    }
                }
            }
        };

        addGraphic( solderLayer );
        addGraphic( branches );
        addGraphic( connectorGraphic );

        addGraphic( fires );
        addGraphic( filamentLayer );
        addGraphic( leverLayer );

        addGraphic( junctionLayer );
        addGraphic( particleSetGraphic );
        addGraphic( ammeterTopLayer );
        addGraphic( readouts );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                Graphic[] g = junctionLayer.getGraphics();
                for( int i = 0; i < g.length; i++ ) {
                    HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
                    ho.getJunctionGraphic().getJunction().notifyObservers();
                }
            }
        } );
        addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                if( branch instanceof CircuitComponent ) {
                    ReadoutGraphic rg = createReadoutGraphic( (CircuitComponent)branch, module );
                    if( rg != null ) {
                        rg.setVisible( readoutGraphicsVisible );
                        readoutMap.put( branch, rg );
                        readouts.addGraphic( rg );
                    }
                }
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                ReadoutGraphic rg = (ReadoutGraphic)readoutMap.get( branch );
                if( rg != null ) {
                    readouts.removeGraphic( rg );
                }
            }
        } );

    }

    private ReadoutGraphic createReadoutGraphic( CircuitComponent branch, CCKModule module ) {
        if( branch instanceof ACVoltageSource ) {
            return new ACReadoutGraphic( module, branch, transform, module.getApparatusPanel(), module.getDecimalFormat() );
        }
        else if( branch instanceof Battery ) {
            return new ReadoutGraphic.BatteryReadout( module, branch, transform, module.getApparatusPanel(), readoutGraphicsVisible, module.getDecimalFormat() );
        }
        else if( branch instanceof SeriesAmmeter ) {
            return null;
        }
        else if( branch instanceof Capacitor ) {
            return new ReadoutGraphic( module, branch, transform, module.getApparatusPanel(), module.getDecimalFormat() ) {
                protected String[] getText() {
                    String units = CCKStrings.getString( "farads" );
                    return new String[]{format( ( (Capacitor)branch ).getCapacitance() ) + " " + units};
                }
            };
        }
        else if( branch instanceof GrabBagResistor ) {
            return new GrabBagReadoutGraphic( module, branch, transform, module.getApparatusPanel(), module.getDecimalFormat() );
        }
        else if( branch instanceof Inductor ) {
            return new ReadoutGraphic( module, branch, transform, module.getApparatusPanel(), module.getDecimalFormat() ) {
                protected String[] getText() {
                    String units = CCKStrings.getString( "henries" );
                    return new String[]{format( ( (Inductor)branch ).getInductance() ) + " " + units};
                }
            };
        }
        else {
            return new ReadoutGraphic( module, branch, transform, module.getApparatusPanel(), module.getDecimalFormat() );
        }
    }

    public void reapplySolderGraphics() {
        Junction[] j = circuit.getJunctions();
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            deleteSolderGraphic( junction );
            SolderGraphic sg = new SolderGraphic( module.getApparatusPanel(), junction, transform, circuit, CircuitGraphic.this );
            solderLayer.addGraphic( sg );
        }
    }

    private void deleteSolderGraphic( Junction junction ) {
        for( int i = 0; i < solderLayer.numGraphics(); i++ ) {
            SolderGraphic g = (SolderGraphic)solderLayer.graphicAt( i );
            if( g.getJunction() == junction ) {
                solderLayer.removeGraphic( g );
                g.delete();
                i--;
            }
        }
    }

    public boolean isReadoutGraphicsVisible() {
        return readoutGraphicsVisible;
    }

    public ReadoutGraphic getReadoutGraphic( Branch branch ) {
        return (ReadoutGraphic)readoutMap.get( branch );
    }

    public void setAllReadoutsVisible( boolean visible ) {
        this.readoutGraphicsVisible = visible;
        Collection values = readoutMap.values();
        for( Iterator iterator = values.iterator(); iterator.hasNext(); ) {
            ReadoutGraphic readoutGraphic = (ReadoutGraphic)iterator.next();
            if( !( readoutGraphic.getBranch() instanceof GrabBagResistor ) ) {
                readoutGraphic.setVisible( visible );
            }
        }
    }

    public void addCircuitGraphicListener( CircuitGraphicListener cgl ) {
        listeners.add( cgl );
    }

    public String toString() {
        return "CircuitGraphic, branches=" + branches + ", junctionLayer=" + junctionLayer;
    }

    public void addGraphic( Resistor b, BufferedImage image ) {
        CircuitComponentImageGraphic ccbg = new CircuitComponentImageGraphic( image, apparatusPanel, b, transform );
        CircuitGraphic.this.addGraphic( b, ccbg );
        InteractiveGraphic g = getGraphic( b );
        fireGraphicAdded( b, g );
        if( isReadoutGraphicsVisible() ) {
            ReadoutGraphic rg = getReadoutGraphic( b );
            if( rg != null ) {
                rg.setVisible( true );
            }
        }
    }

    public void addGraphic( Branch b ) {
        graphicSource.addGraphic( b );
        InteractiveGraphic g = getGraphic( b );
        fireGraphicAdded( b, g );
        if( isReadoutGraphicsVisible() ) {
            ReadoutGraphic rg = getReadoutGraphic( b );
            if( rg != null ) {
                rg.setVisible( true );
            }
        }
    }

    public void bumpAway( HasJunctionGraphic interactiveWireJunctionGraphic ) {
        Shape shape = interactiveWireJunctionGraphic.getJunctionGraphic().getShape();
        int width = shape.getBounds().width;
        double dx = transform.viewToModelDifferentialX( width );
        Branch[] branches = circuit.getBranches();
        Junction junction = interactiveWireJunctionGraphic.getJunctionGraphic().getJunction();
        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            Graphic graphic = getGraphic( branch );
            if( !branch.hasJunction( junction ) ) {
                if( intersectsShape( graphic, shape ) ) {
                    edu.colorado.phet.common.math.AbstractVector2D vec = branch.getDirectionVector();
                    vec = vec.getNormalVector();
                    vec = vec.getNormalizedInstance().getScaledInstance( dx );
                    Branch[] sc = circuit.getStrongConnections( junction );
                    BranchSet bs = new BranchSet( circuit, sc );
                    bs.addJunction( junction );
                    bs.translate( vec );
                    module.getApparatusPanel().repaint();
                    break;
                }
            }
        }
    }

    private boolean intersectsShape( Graphic branchGraphic, Shape shape ) {
//        System.out.println( "branchGraphic.getClass() = " + branchGraphic.getClass() );
        if( branchGraphic instanceof InteractiveBranchGraphic ) {
            InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)branchGraphic;
            Shape branchShape = ibg.getBranchGraphic().getCoreShape();
            Area area = new Area( branchShape );
            area.intersect( new Area( shape ) );
            return !area.isEmpty();
        }
        else if( branchGraphic instanceof CircuitComponentInteractiveGraphic ) {
            CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)branchGraphic;
//            Shape branchShape=ccig.getCircuitComponentGraphic().
            return false;
        }
        return false;
    }

    public void bumpAway( Branch branch ) {
        bumpAway( branch.getStartJunction() );
        bumpAway( branch.getEndJunction() );
    }

    private void bumpAway( Junction junction ) {
        HasJunctionGraphic hj = getGraphic( junction );
        bumpAway( hj );
    }

    static class AmmeterTopGraphic implements Graphic {
        private SeriesAmmeter sam;
        Graphic graphic;

        public AmmeterTopGraphic( SeriesAmmeter sam, Graphic graphic ) {
            this.sam = sam;
            this.graphic = graphic;
        }

        public void paint( Graphics2D g ) {
            graphic.paint( g );
        }

        public SeriesAmmeter getSeriesAmmeter() {
            return sam;
        }

        public void delete() {
            //nothing to do.
        }
    }

    private void fireGraphicAdded( Branch branch, InteractiveGraphic graphic ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitGraphicListener circuitGraphicListener = (CircuitGraphicListener)listeners.get( i );
            circuitGraphicListener.graphicAdded( branch, graphic );
        }
    }

    private void addGraphic( CircuitComponent component, IComponentGraphic ccbg ) {
        if( ccbg instanceof SchematicCapacitorGraphic ) {
            SchematicCapacitorGraphic scg = (SchematicCapacitorGraphic)ccbg;
            scg.addListener( new SchematicPlatedGraphic.Listener() {
                public void areaChanged() {
                    module.recomputeElectronClip();
                }
            } );
        }
        else if( ccbg instanceof SchematicCapacitor3DGraphic ) {
            SchematicCapacitor3DGraphic scg = (SchematicCapacitor3DGraphic)ccbg;
            scg.addListener( new SchematicPlatedGraphic.Listener() {
                public void areaChanged() {
                    module.recomputeElectronClip();
                }
            } );
        }
        TotalComponentGraphic tcg = new TotalComponentGraphic( this, component, apparatusPanel, transform,
                                                               ccbg, JUNCTION_RADIUS, module );
        branches.addGraphic( tcg.getInteractiveBranchGraphic(), 1 );
        if( getGraphic( component.getStartJunction() ) == null ) {
            junctionLayer.addGraphic( tcg.getInteractiveJunctionGraphic1() );
        }
        else {
            tcg.getInteractiveJunctionGraphic1().delete();
        }
        if( getGraphic( component.getEndJunction() ) == null ) {
            junctionLayer.addGraphic( tcg.getInteractiveJunctionGraphic2() );
        }
        else {
            tcg.getInteractiveJunctionGraphic2().delete();
        }
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void removeGraphic( Junction j ) {
//        System.out.println( "Removing graphic for junction: j = " + j );
        Graphic[] g = junctionLayer.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
            if( ho.getJunctionGraphic().getJunction() == j ) {
                junctionLayer.removeGraphic( ho );
                ho.delete();
            }
        }
        int size = junctionLayer.getGraphics().length;
//        System.out.println( "Removed JG: Number of Junction Graphics= " + size );
    }

    public HasJunctionGraphic getGraphic( Junction j ) {
        Graphic[] g = junctionLayer.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
            if( ho.getJunctionGraphic().getJunction() == j ) {
                return ho;
            }
        }
        return null;
    }

    public boolean wouldConnectionCauseOverlappingBranches( Junction a, Junction b ) {
        Junction[] neighborsOfA = getCircuit().getNeighbors( a );
        Junction[] neighborsOfB = getCircuit().getNeighbors( b );
        for( int i = 0; i < neighborsOfA.length; i++ ) {
            Junction na = neighborsOfA[i];
            for( int j = 0; j < neighborsOfB.length; j++ ) {
                Junction nb = neighborsOfB[j];
                if( na == nb ) {
                    return true;
                }
            }
        }
        return false;
    }

    public DragMatch getBestDragMatch( Branch[] sc, Vector2D dx ) {
        Junction[] sources = Circuit.getJunctions( sc );
        return getBestDragMatch( sources, dx );
    }

    public DragMatch getBestDragMatch( Junction[] sources, Vector2D dx ) {
        Junction[] all = circuit.getJunctions();
        ArrayList rema = new ArrayList();
        rema.addAll( Arrays.asList( all ) );
        rema.removeAll( Arrays.asList( sources ) );
        //now we have all the junctions that are moving,
        //and all the junctions that aren't moving, so we can look for a best match.
        Junction[] remaining = (Junction[])rema.toArray( new Junction[0] );
        DragMatch best = null;
        for( int i = 0; i < sources.length; i++ ) {
            Junction source = sources[i];
            Point2D loc = dx.getDestination( source.getPosition() );
            Junction bestForHim = getBestDragMatch( source, loc, remaining );
            if( bestForHim != null ) {
                DragMatch dm = new DragMatch( source, bestForHim );
                if( best == null || dm.getDistance() < best.getDistance() ) {
                    best = dm;
                }
            }
        }
//        System.out.println( "best = " + best );
        return best;
    }

    public static class DragMatch {
        Junction source;
        Junction target;

        public DragMatch( Junction source, Junction target ) {
            this.source = source;
            this.target = target;
        }

        public Junction getSource() {
            return source;
        }

        public Junction getTarget() {
            return target;
        }

        public double getDistance() {
            return source.getDistance( target );
        }

        public String toString() {
            return "match, source=" + source + ", dest=" + target + ", dist=" + getDistance();
        }

        public Vector2D getVector() {
            return new Vector2D.Double( source.getPosition(), target.getPosition() );
        }
    }

    public Junction getBestDragMatch( Junction dragging, Point2D loc, Junction[] targets ) {
        Branch[] strong = circuit.getStrongConnections( dragging );
        Junction closestJunction = null;
        double closestValue = Double.POSITIVE_INFINITY;
        for( int i = 0; i < targets.length; i++ ) {
            Junction target = targets[i];
            double dist = loc.distance( target.getPosition() );
            if( target != dragging && !getCircuit().hasBranch( dragging, target ) &&
                !wouldConnectionCauseOverlappingBranches( dragging, target ) ) {
                if( closestJunction == null || dist < closestValue ) {
                    boolean legal = !contains( strong, target );
                    if( dist <= STICKY_THRESHOLD && legal ) {
                        closestValue = dist;
                        closestJunction = target;
                    }
                }
            }
        }
        return closestJunction;
    }

    private boolean contains( Branch[] strong, Junction j ) {
        for( int i = 0; i < strong.length; i++ ) {
            Branch branch = strong[i];
            if( branch.hasJunction( j ) ) {
                return true;
            }
        }
        return false;
    }

    public void convertToComponentGraphic( Junction junction, CircuitComponent branch ) {
        JunctionGraphic junctionGraphic = new JunctionGraphic( apparatusPanel, junction, getTransform(), JUNCTION_RADIUS, getCircuit() );
        InteractiveComponentJunctionGraphic ij = new InteractiveComponentJunctionGraphic( this, junctionGraphic, branch, module );
        removeGraphic( junction );
        junctionLayer.addGraphic( ij );
    }

    public void collapseJunctions( Junction j1, Junction j2 ) {
        if( !j1.getPosition().equals( j2.getPosition() ) ) {
            throw new RuntimeException( "Juncitons Not at same coordinates." );
        }
        remove( j1 );
        remove( j2 );
        Junction replacement = new Junction( j1.getX(), j1.getY() );
        InteractiveWireJunctionGraphic ij = newJunctionGraphic( replacement );
        circuit.addJunction( replacement );
        circuit.replaceJunction( j1, replacement );
        circuit.replaceJunction( j2, replacement );

        junctionLayer.addGraphic( ij );
        circuit.fireKirkhoffChanged();
        circuit.fireJunctionsCollapsed( j1, j2, replacement );
//        circuit.fireKirkhoffChanged();
    }

    private void remove( Junction junction ) {
        removeGraphic( junction );
        circuit.remove( junction );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public InteractiveWireJunctionGraphic newJunctionGraphic( Junction junction ) {
        InteractiveWireJunctionGraphic j = new InteractiveWireJunctionGraphic( this, new JunctionGraphic( apparatusPanel, junction, getTransform(), JUNCTION_RADIUS, getCircuit() ), getTransform(), module );
        return j;
    }

    public void split( Junction junction ) {
        Junction[] newJ = circuit.split( junction );
        ArrayList branches = new ArrayList();
        for( int i = 0; i < newJ.length; i++ ) {
            Junction junction1 = newJ[i];
            branches.addAll( Arrays.asList( circuit.getAdjacentBranches( junction1 ) ) );
        }
        Branch[] bo = (Branch[])branches.toArray( new Branch[0] );
        module.layoutElectrons( bo );
        for( int i = 0; i < newJ.length; i++ ) {
            Junction junction1 = newJ[i];
            Branch connection = circuit.getAdjacentBranches( junction1 )[0];
            if( connection instanceof CircuitComponent ) {
                InteractiveComponentJunctionGraphic j = new InteractiveComponentJunctionGraphic( this, new JunctionGraphic( apparatusPanel, junction1, getTransform(), JUNCTION_RADIUS, getCircuit() ), (CircuitComponent)connection, module );
                junctionLayer.addGraphic( j );
            }
            else {
                InteractiveWireJunctionGraphic j = newJunctionGraphic( junction1 );
                junctionLayer.addGraphic( j );
            }
        }
        removeGraphic( junction );
        circuit.fireKirkhoffChanged();
    }

    public Graphic[] getBranchGraphics() {
        return branches.getGraphics();
    }

    public InteractiveGraphic getGraphic( Branch b ) {
        Graphic[] g = branches.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            Graphic graphic = g[i];
            if( graphic instanceof InteractiveBranchGraphic ) {
                InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                if( ibg.getBranch() == b ) {
                    return ibg;
                }

            }
            else if( graphic instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)graphic;
                if( ccig.getBranch() == b ) {
                    return ccig;
                }
            }
            else {
                throw new RuntimeException( "Unknown type.=" + g[i].getClass() );
            }
        }
        return null;
    }

    public CCKModule getModule() {
        return module;
    }

    public void removeGraphic( Branch branch ) {
        InteractiveGraphic g = getGraphic( branch );
        if( g == null ) {
            System.out.println( "No graphic to remove for branch=" + branch );
            return;//no graphic for this element.
        }
        branches.removeGraphic( g );
        if( !( g instanceof Deletable ) ) {
            throw new RuntimeException( g.getClass().getName() + " does not implement Deletable" );
        }
        ( (Deletable)g ).delete();
        ReadoutGraphic rg = getReadoutGraphic( branch );
        if( rg != null ) {
            deleteReadoutGraphic( rg );
        }
        Graphic[] fire = fires.getGraphics();
        for( int i = 0; i < fire.length; i++ ) {
            FlameGraphic graphic = (FlameGraphic)fire[i];
            if( graphic.getBranch() == branch ) {
                fires.removeGraphic( graphic );
                graphic.delete();
            }
        }

        fireGraphicRemoved( branch, g );
        if( branch instanceof Switch ) {
            Graphic[] levers = leverLayer.getGraphics();
            for( int i = 0; i < levers.length; i++ ) {
                if( levers[i] instanceof LeverInteraction ) {
                    LeverInteraction lever = (LeverInteraction)levers[i];
                    if( lever.getComponent() == branch ) {
                        leverLayer.removeGraphic( lever );
                        //hopefully only one.
                        lever.delete();
                    }
                }
            }
        }
        else if( branch instanceof SeriesAmmeter ) {
            Graphic[] gr = ammeterTopLayer.getGraphics();
            for( int i = 0; i < gr.length; i++ ) {
                AmmeterTopGraphic graphic = (AmmeterTopGraphic)gr[i];
                if( graphic.getSeriesAmmeter() == branch ) {
                    ammeterTopLayer.removeGraphic( graphic );
                    graphic.delete();
                }
            }
        }
        else if( branch instanceof Bulb ) {
            Graphic[] gr = filamentLayer.getGraphics();
            Bulb bulb = (Bulb)branch;
            for( int i = 0; i < gr.length; i++ ) {
                Graphic graphic = gr[i];
                FilamentGraphic fg = (FilamentGraphic)graphic;
                if( fg.getFilament() == bulb.getFilament() ) {
                    filamentLayer.removeGraphic( fg );
                    fg.delete();
                }
            }
        }
        module.recomputeElectronClip();
    }

    private void deleteReadoutGraphic( ReadoutGraphic rg ) {
        Branch branch = rg.getBranch();
        rg.setVisible( false );
        readoutMap.remove( branch );
        rg.delete();
    }

    private void fireGraphicRemoved( Branch branch, InteractiveGraphic g ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitGraphicListener circuitGraphicListener = (CircuitGraphicListener)listeners.get( i );
            circuitGraphicListener.graphicRemoved( branch, g );
        }
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return particleSetGraphic;
    }

    public void setLifelike( boolean lifelike ) {
//        System.out.println( "Before setlifelike: JunctionGraphic.getInstanceCount() = " + JunctionGraphic.getInstanceCount() );
        if( this.lifelike == lifelike ) {
            return;
        }
        this.lifelike = lifelike;
        if( lifelike ) {
            this.graphicSource = new Lifelike();
        }
        else {
            this.graphicSource = new Schematic();
        }
        Hashtable table = new Hashtable();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch br = circuit.branchAt( i );
            if( br instanceof CircuitComponent ) {
                CircuitComponent cc = (CircuitComponent)br;
                InteractiveGraphic graphic = getGraphic( cc );
                if( graphic instanceof CircuitComponentInteractiveGraphic ) {
                    CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)graphic;
                    table.put( br, new Boolean( ccig.getMenu().isVisiblityRequested() ) );
                }
            }
        }
        clearAllGraphics();
//        System.out.println( "After clear graphics: " + JunctionGraphic.getInstanceCount() );
        //have to preprocess to fix the bulb model.
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if( branch instanceof Bulb ) {
                Bulb bulb = (Bulb)branch;
                bulb.setSchematic( !lifelike, circuit );
            }
        }
//        System.out.println( "After bulbing: " + JunctionGraphic.getInstanceCount() );
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            addGraphic( circuit.branchAt( i ) );
        }

        Enumeration keys = table.keys();
        while( keys.hasMoreElements() ) {
            Branch branch = (Branch)keys.nextElement();
            Boolean value = (Boolean)table.get( branch );
            InteractiveGraphic graphic = getGraphic( branch );
            if( graphic instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)graphic;
                ccig.getMenu().setVisibilityRequested( value.booleanValue() );
            }
        }
//        System.out.println( "After setlifelike: JunctionGraphic.getInstanceCount() = " + JunctionGraphic.getInstanceCount() );
    }

    private void clearAllGraphics() {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch br = circuit.branchAt( i );
            removeGraphic( br.getStartJunction() );
            removeGraphic( br.getEndJunction() );
            removeGraphic( br );
        }
        for( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            removeGraphic( j );
        }
        System.out.println( "Cleared all graphics." );
    }

    public boolean isLifelike() {
        return lifelike;
    }

    public FlameGraphic getFlameGraphic( Branch b ) {
        Graphic[] g = fires.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            Graphic graphic = g[i];
            FlameGraphic fg = (FlameGraphic)graphic;
            if( fg.getBranch() == b ) {
                return fg;
            }
        }
        return null;
    }

    public void setOnFire( Branch branch, boolean onFire ) {
        if( onFire ) {
            if( getFlameGraphic( branch ) != null ) {
                return;
            }
            else {
                if( branch instanceof CircuitComponent ) {
                    CircuitComponent cc = (CircuitComponent)branch;
                    FlameGraphic flameG = createFlameGraphic( cc );
                    if( flameG != null ) {
                        fires.addGraphic( flameG );
                        flameG.repaint();
                    }
                }
            }
        }
        else {
            FlameGraphic graphic = getFlameGraphic( branch );
            if( graphic != null ) {
                graphic.delete();
                graphic.setVisible( false );//paint over yourself.
                fires.removeGraphic( graphic );
            }
        }
    }

    private FlameGraphic createFlameGraphic( CircuitComponent b ) {
        if( b instanceof Bulb ) {
            return null;
        }
        else {
            try {
                return new FlameGraphic( apparatusPanel, b, hashedImageLoader.loadImage( "images/flame.gif" ), getTransform() );
            }
            catch( IOException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public int numJunctionGraphics() {
        return junctionLayer.getGraphics().length;
    }

    public Graphic[] getJunctionGraphics() {
        return junctionLayer.getGraphics();
    }

    public ReadoutGraphic[] getReadoutGraphics() {
        Collection val = readoutMap.values();
        ArrayList out = new ArrayList();
        for( Iterator iterator = val.iterator(); iterator.hasNext(); ) {
            ReadoutGraphic o = (ReadoutGraphic)iterator.next();
            out.add( o );
        }
        return (ReadoutGraphic[])out.toArray( new ReadoutGraphic[0] );
    }

    interface GraphicSource {
        void addGraphic( Branch b );
    }

    class Lifelike implements GraphicSource {
        double wireThickness = CCKLookAndFeel.WIRE_THICKNESS;

        public Lifelike() {
        }

        public void addGraphic( Branch b ) {
            if( b instanceof Inductor ) {
                addInductorGraphic( (Inductor)b );
            }
            else if( b instanceof Capacitor ) {
                addCapacitorGraphic( (Capacitor)b );
            }
            else if( b instanceof ACVoltageSource ) {
                addACGraphic( (ACVoltageSource)b );
            }
            else if( b instanceof Battery ) {
                addBatteryGraphic( (Battery)b );
            }
            else if( b instanceof Bulb ) {
                addBulbGraphic( (Bulb)b );
            }
            else if( b instanceof GrabBagResistor ) {
                addGrabBagGraphic( (GrabBagResistor)b );
            }
            else if( b instanceof Resistor ) {
                addResistorGraphic( (Resistor)b );
            }
            else if( b instanceof Switch ) {
                addSwitchGraphic( (Switch)b );
            }
            else if( b instanceof SeriesAmmeter ) {
                addSeriesAmmeterGraphic( (SeriesAmmeter)b );
            }
            else {
                addWireGraphic( b );
            }
        }

        private void addInductorGraphic( Inductor inductor ) {
            CircuitComponentImageGraphic lifelike = new CircuitComponentImageGraphic( module.getImageSuite().getInductorImage(), apparatusPanel, inductor, transform );
            CircuitGraphic.this.addGraphic( inductor, lifelike );
        }

        private void addACGraphic( ACVoltageSource acVoltageSource ) {
            CircuitComponentImageGraphic graphic = new CircuitComponentImageGraphic( module.getImageSuite().getACImage(), apparatusPanel, acVoltageSource, transform );
            CircuitGraphic.this.addGraphic( acVoltageSource, graphic );
        }

        private void addCapacitorGraphic( Capacitor b ) {
            SchematicCapacitor3DGraphic schematicCapacitorGraphic = new SchematicCapacitor3DGraphic( apparatusPanel, b, getTransform(), new Schematic().wireThickness );
            CircuitGraphic.this.addGraphic( b, schematicCapacitorGraphic );
        }

        private void addGrabBagGraphic( GrabBagResistor b ) {
            BufferedImage image = b.getItemInfo().getImage();
            CircuitGraphic.this.addGraphic( b, BufferedImageUtils.flipY( image ) );
        }

        private void addBatteryGraphic( Battery component ) {
            CircuitComponentImageGraphic ccbg = new CircuitComponentImageGraphic( module.getImageSuite().getLifelikeSuite().getBatteryImage(), apparatusPanel, component, transform );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addSeriesAmmeterGraphic( SeriesAmmeter seriesAmmeter ) {
            final SeriesAmmeterGraphic sag = new SeriesAmmeterGraphic( apparatusPanel, seriesAmmeter, getTransform(), module );
            CircuitGraphic.this.addGraphic( seriesAmmeter, sag );
            AmmeterTopGraphic atg = new AmmeterTopGraphic( seriesAmmeter, sag );
            ammeterTopLayer.addGraphic( atg );
        }

        private void addSwitchGraphic( Switch aSwitch ) {
            CircuitComponentImageGraphic switchGraphic = new CircuitComponentImageGraphic( module.getImageSuite().getKnifeBoardImage(), apparatusPanel,
                                                                                           aSwitch, getTransform() );
            CircuitGraphic.this.addGraphic( aSwitch, switchGraphic );
            BufferedImage lever = module.getImageSuite().getKnifeHandleImage();
            ComponentDimension leverDimension = CCKModel.LEVER_DIMENSION;
            LeverGraphic leverGraphic = new LeverGraphic( switchGraphic, lever, apparatusPanel, getTransform(), leverDimension.getLength(), leverDimension.getHeight() );
            InteractiveLever interactiveLever = new InteractiveLever( transform, apparatusPanel, leverGraphic );
            leverLayer.addGraphic( interactiveLever );
        }

        private void addWireGraphic( Branch branch ) {
            TotalBranchGraphic totalBranchGraphic = new TotalBranchGraphic( CircuitGraphic.this, branch, apparatusPanel, transform, COPPER, JUNCTION_RADIUS, module, wireThickness );
            branches.addGraphic( totalBranchGraphic.getInteractiveBranchGraphic(), 0 );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic1() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic2() );
        }

        private void addBulbGraphic( Bulb component ) {
            BulbComponentGraphic ccbg = new BulbComponentGraphic( apparatusPanel, component, transform, module );
            FilamentGraphic fg = new FilamentGraphic( component.getFilament(), transform, ccbg );
            component.setSchematic( false, getCircuit() );
            filamentLayer.addGraphic( fg );
            //TODO could also be coupled at the bulb layer
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addResistorGraphic( Resistor component ) {
            ResistorGraphic ccbg = new ResistorGraphic( module.getImageSuite().getLifelikeSuite().getResistorImage(), apparatusPanel, component, transform );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }
    }

    class Schematic implements GraphicSource {
        double wireThickness = .13;

        public void addGraphic( Branch b ) {
            if( b instanceof ACVoltageSource ) {
                addACGraphic( (ACVoltageSource)b );
            }
            else if( b instanceof Battery ) {
                addBatteryGraphic( (Battery)b );
            }
            else if( b instanceof Bulb ) {
                addBulbGraphic( (Bulb)b );
            }
            else if( b instanceof Resistor ) {
                addResistorGraphic( (Resistor)b );
            }
            else if( b instanceof Switch ) {
                addSwitchGraphic( (Switch)b );
            }
            else if( b instanceof SeriesAmmeter ) {
                addSeriesAmmeterGraphic( (SeriesAmmeter)b );
            }
            else if( b instanceof Capacitor ) {
                addCapatitorGraphic( (Capacitor)b );
            }
            else if( b instanceof Inductor ) {
                addInductorGraphic( (Inductor)b );
            }
            else {
                addWireGraphic( b );
            }
        }

        private void addInductorGraphic( Inductor inductor ) {
//            CircuitComponentImageGraphic lifelike = new CircuitComponentImageGraphic( module.getImageSuite().getInductorImage(), apparatusPanel, inductor, transform );
//            CircuitGraphic.this.addGraphic( inductor, lifelike );
            SchematicInductorGraphic schematicInductorGraphic = new SchematicInductorGraphic( apparatusPanel, inductor, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( inductor, schematicInductorGraphic );
        }

        private void addBulbGraphic( Bulb bulb ) {
            SchematicBulbGraphic sebulb = new SchematicBulbGraphic( apparatusPanel, bulb, getTransform(), wireThickness );
//            BulbComponentGraphic ccbg = new BulbComponentGraphic( apparatusPanel, bulb, transform, module );
//            FilamentGraphic fg = new FilamentGraphic( bulb.getFilament(), transform );
//            filamentLayer.addGraphic( fg );
//            //TODO could also be coupled at the bulb layer
            bulb.setSchematic( true, getCircuit() );
            CircuitGraphic.this.addGraphic( bulb, sebulb );
        }

        private void addSeriesAmmeterGraphic( SeriesAmmeter seriesAmmeter ) {
            SchematicAmmeterGraphic ccbg = new SchematicAmmeterGraphic( apparatusPanel, seriesAmmeter, getTransform(), wireThickness, module.getDecimalFormat() );
            CircuitGraphic.this.addGraphic( seriesAmmeter, ccbg );
        }

        private void addSwitchGraphic( Switch aSwitch ) {
            SchematicSwitchGraphic ssg = new SchematicSwitchGraphic( apparatusPanel, aSwitch, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( aSwitch, ssg );
            SchematicLeverGraphic schematicSwitchGraphic = new SchematicLeverGraphic( ssg, apparatusPanel, getTransform(),
                                                                                      wireThickness, ssg.getLeverLengthModelCoordinates() );
            InteractiveSchematicLever isl = new InteractiveSchematicLever( getTransform(), apparatusPanel, schematicSwitchGraphic );
            leverLayer.addGraphic( isl );
        }

        private void addResistorGraphic( Resistor resistor ) {
            SchematicResistorGraphic ccbg = new SchematicResistorGraphic( apparatusPanel, resistor, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( resistor, ccbg );
        }

        private void addWireGraphic( Branch b ) {
            TotalBranchGraphic totalBranchGraphic = new TotalBranchGraphic( CircuitGraphic.this, b, apparatusPanel, transform, Color.black, JUNCTION_RADIUS, module, wireThickness );
            branches.addGraphic( totalBranchGraphic.getInteractiveBranchGraphic() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic1() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic2() );
        }

        private void addBatteryGraphic( Battery component ) {
            SchematicBatteryGraphic ccbg = new SchematicBatteryGraphic( apparatusPanel, component, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addACGraphic( ACVoltageSource component ) {
            SchematicACGraphic ccbg = new SchematicACGraphic( apparatusPanel, component, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addCapatitorGraphic( Capacitor capactior ) {
            SchematicCapacitor3DGraphic schematicCapacitorGraphic = new SchematicCapacitor3DGraphic( apparatusPanel, capactior, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( capactior, schematicCapacitorGraphic );
        }

    }
}
