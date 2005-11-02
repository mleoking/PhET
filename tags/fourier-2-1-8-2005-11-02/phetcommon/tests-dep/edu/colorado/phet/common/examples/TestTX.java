/**
 * Class: TestTX
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.common.examples;


public class TestTX {
//    static double particleRadius = .1;
//
//    static class TestModule extends Module {
//        ApparatusPanel panel = new ApparatusPanel();
//        ModelViewTransform2D transform;
//        Particle particle;
//
//        public TestModule() {
//            super( "" );
//            Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, 1, 1 );
//            Rectangle viewBounds = new Rectangle( 0, 0, 600, 600 );
//            panel.addComponentListener( new ComponentAdapter() {
//                public void componentResized( ComponentEvent e ) {
//                    transform.setViewBounds( panel.getBounds() );
//                }
//            } );
//            transform = new ModelViewTransform2D( modelBounds, viewBounds );
//            particle = new Particle();
//            particle.setPosition( .5, .5 );
//            ParticleGraphic pg = new ParticleGraphic( particle, transform );
//
//            setApparatusPanel( panel );
//            DefaultInteractiveGraphic dig = new DefaultInteractiveGraphic( pg, pg );
//            dig.addCursorHandBehavior();
//            dig.addTranslationBehavior( new Translatable() {
//                public void translate( double dx, double dy ) {
//                    Point2D pt = transform.viewToModelDifferential( (int)dx, (int)dy );
//                    particle.translate( pt.getX(), pt.getY() );
//                    getApparatusPanel().repaint();
//                }
//            } );
//            //            panel.addGraphic( pg );
//            panel.addGraphic( dig );
//            setModel( new BaseModel() );
//        }
//    }
//
//    static class ParticleGraphic implements BoundedGraphic {
//        private Particle particle;
//        private ModelViewTransform2D transform;
//        private Point screenCoords;
//        private Rectangle r;
//
//        public ParticleGraphic( Particle particle, ModelViewTransform2D transform ) {
//            this.particle = particle;
//            this.transform = transform;
//            particle.addObserver( new SimpleObserver() {
//                public void update() {
//                    changed();
//                }
//            } );
//            transform.addTransformListener( new TransformListener() {
//                public void transformChanged( ModelViewTransform2D mvt ) {
//                    changed();
//                }
//            } );
//            changed();
//        }
//
//        private void changed() {
//            screenCoords = transform.modelToView( particle.getPosition() );
//            int width = transform.modelToViewDifferentialX( particleRadius );
//            r = new Rectangle( screenCoords.x, screenCoords.y, width, width );
//        }
//
//        public void paint( Graphics2D g ) {
//            g.setColor( Color.blue );
//            if( r != null ) {
//                g.fill( r );
//            }
//
//        }
//
//        public boolean contains( int x, int y ) {
//            return r.contains( x, y );
//        }
//
//    }
//
//    public static void main( String[] args ) {
//        JFrame frame = new JFrame();
//        Module m = new TestModule();
//        frame.setContentPane( m.getApparatusPanel() );
//        frame.setSize( 600, 600 );
//        frame.setVisible( true );
//    }
}
