/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.branch.components.Resistor;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.junction.JunctionGraphic;
import edu.colorado.phet.cck.elements.particles.ParticleSetGraphic;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 3:15:26 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class ConnectibleImageGraphic extends CompositeInteractiveGraphic implements BranchObserver, AbstractBranchGraphic {
    protected ModelViewTransform2D transform;
    protected Branch branch;
    protected CCK2Module module;
    JPopupMenu menu;
    protected ImagePortion imagePortion;
    private DefaultBranchInteractionHandler interactionHandler;
    protected TextDisplay2 textDisplay = new TextDisplay2();
    protected double angle;
    JPopupMenu showMenuMenu;
    public static boolean showJunctionHoles = false;
    private Rectangle bounds;
    private Point viewStart;
    private Point viewEnd;

    public ConnectibleImageGraphic( final Circuit circuit, ModelViewTransform2D transform,
                                    final Branch branch,
                                    final CCK2Module module, BufferedImage image,
                                    final Stroke highlightStroke, final Color highlightColor ) {
        this.transform = transform;
        this.branch = branch;
        this.module = module;
        transform.addTransformListener( this );
        branch.addObserver( this );

        menu = new JPopupMenu();
        JMenuItem delete = new JMenuItem( "Delete" );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                circuit.removeBranch( branch );
            }
        } );
        menu.add( delete );

        final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( "Show Value", false );
        showValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                textDisplay.setVisible( showValue.isSelected() );
            }
        } );
        menu.add( showValue );

        final JMenuItem showEditor = new JMenuItem( "Change Value" );
        showEditor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isBulbOrResistor( branch ) ) {
                    update();
                    if( !showValue.isSelected() ) {
                        showValue.doClick( 50 );
                    }
                    final JDialog jf = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Editing Resistor", false );
                    final HasResistance r = (HasResistance)branch;
                    final JSlider js = new JSlider( 1, 100, (int)r.getResistance() );
                    final JPanel jp = new JPanel();
                    jp.setLayout( new BorderLayout() );
                    js.setPaintTicks( true );
                    js.setMajorTickSpacing( 20 );
                    js.setMinorTickSpacing( 5 );
                    js.addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            double value = js.getValue();
                            r.setResistance( value );
                        }
                    } );

                    jp.add( js, BorderLayout.CENTER );
                    js.setBounds( 0, 0, 300, 100 );

                    JButton hide = new JButton( "Done" );
                    jp.add( hide, BorderLayout.SOUTH );
                    jp.setBorder( BorderFactory.createTitledBorder( "Resistance" ) );
                    jp.setSize( 200, 200 );
                    hide.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            module.getApparatusPanel().remove( jp );
                        }
                    } );
                    module.getApparatusPanel().setLayout( null );
                    jf.setContentPane( jp );

                    jf.pack();
                    Point loc = textDisplay.src;
                    jf.setLocation( loc.x, loc.y + jf.getHeight() );
                    jf.setVisible( true );
                    hide.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            jf.setVisible( false );
                            jf.dispose();
                        }
                    } );
                }
                else if( branch instanceof Battery ) {
                    update();
                    final Battery b = (Battery)branch;
                    if( !showValue.isSelected() ) {
                        showValue.doClick( 50 );
                    }
//                    JFrame jf=new JFrame("Editing Resistor");
//                    JFrame jf=new JFrame("Editing Resistor");
                    final JDialog jf = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Editing Battery", false );
//                    final Resistor r = (Resistor) branch;
//                    SpinnerNumberModel snm=new SpinnerNumberModel(r.getResistance(), 0,1000.0,.2);
                    final JSlider js = new JSlider( 0, 100, (int)b.getVoltageDrop() );
                    final JPanel jp = new JPanel();
                    jp.setLayout( new BorderLayout() );
//                    Rectangle2D.Double modelRectX=new Rectangle2D.Double(0,0,100,);
//                    ModelViewTransform2D transform=new ModelViewTransform2D();
                    js.setPaintTicks( true );
                    js.setMajorTickSpacing( 20 );
                    js.setMinorTickSpacing( 5 );
                    js.addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            double value = js.getValue();
                            b.setVoltageDrop( value );
                        }
                    } );

                    jp.add( js, BorderLayout.CENTER );
                    js.setBounds( 0, 0, 300, 100 );

                    JButton hide = new JButton( "Done" );
                    jp.add( hide, BorderLayout.SOUTH );
                    jp.setBorder( BorderFactory.createTitledBorder( "Voltage" ) );
//                    jp.setBounds(bounds.x,bounds.y,200,200);
                    jp.setLocation( bounds.x, bounds.y );
//                    jp.setPreferredSize(jp.getPreferredSize());
//                    jp.setSize(jp.getPreferredSize());
                    jp.setSize( 200, 200 );
                    hide.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            module.getApparatusPanel().remove( jp );
                        }
                    } );
                    module.getApparatusPanel().setLayout( null );
//                    module.getApparatusPanel().add(jp);

                    jf.setContentPane( jp );

                    jf.pack();
                    jf.setLocation( bounds.x, bounds.y + jf.getHeight() );
                    jf.setVisible( true );
                    hide.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            jf.setVisible( false );
                            jf.dispose();
                        }
                    } );
                }
            }
        } );
        if( branch instanceof Resistor || branch instanceof Battery || branch instanceof Bulb ) {
            menu.add( showEditor );
        }

        showMenuMenu = new JPopupMenu();
        final JMenuItem jmi = new JMenuItem( "Menu" );
        jmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                menu.show( module.getApparatusPanel(), jmi.getX(), jmi.getY() );
            }
        } );
        showMenuMenu.add( jmi );

        interactionHandler = new DefaultBranchInteractionHandler( branch, transform, circuit, module, menu, showMenuMenu );

        imagePortion = new ImagePortion( image, interactionHandler, module.getFlameImage(), branch );
        addGraphic( imagePortion, 3 );
        Graphic highlighter = new Graphic() {
            public void paint( Graphics2D g ) {
                if( branch.isSelected() ) {
                    Shape s = imagePortion.getExpandedShape( 5 );
                    g.setColor( highlightColor );
                    g.setStroke( highlightStroke );
                    g.draw( s );
                }
            }
        };
        addGraphic( highlighter, -1 );
        update();

        addGraphic( textDisplay, 5 );

//        super.addGraphic(new Graphic() {
//            public void paint(Graphics2D g) {
////                PhetVector ctrl = bulbBranch.getControlPoint();
//                Point2D.Double in = branch.getStartJunction().getLocation();
//                Point2D.Double out = branch.getEndJunction().getLocation();
////                paintJunctionHole(ctrl, Color.white, g);
//                if (showJunctionHoles) {
//                    paintJunctionHole(in, Color.green, g);
//                    paintJunctionHole(out, Color.red, g);
//                }
//            }
//        }, 15);
        addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                ParticleSetGraphic particleSetGraphic = module.getParticleSetGraphic();
                particleSetGraphic.paint( g, getBranch() );
            }
        }, 1000 );

//        JunctionGraphic jg=new JunctionGraphic(branch.getStartJunction(), module, DefaultCompositeBranchGraphic.JUNCTION_RADIUS, DefaultCompositeBranchGraphic.JUNCTION_COLOR, DefaultCompositeBranchGraphic.JUNCTION_STROKE,DefaultCompositeBranchGraphic.JUNCTION_COLOR);
//        addGraphic(jg,100);

//        if (wireGraphic instanceof TransformListener)
//            compositeTransformListener.addTransformListener(wireGraphic);
//        compositeTransformListener.addTransformListener(startJunctionGraphic);
//        compositeTransformListener.addTransformListener(endJunctionGraphic);
        JunctionGraphic startJunctionGraphic = new JunctionGraphic( branch.getStartJunction(), module, DefaultCompositeBranchGraphic.JUNCTION_RADIUS, DefaultCompositeBranchGraphic.JUNCTION_COLOR, DefaultCompositeBranchGraphic.JUNCTION_STROKE, DefaultCompositeBranchGraphic.JUNCTION_COLOR );
//            public void mouseDragged(MouseEvent event) {
//                if (branch instanceof Battery) {
//                    Battery b = (Battery) branch;
//                    b.setRotateEnabled(true);
//                }
//                super.mouseDragged(event);
//            }
//
//            public void mouseReleased(MouseEvent event) {
//                if (branch instanceof Battery) {
//                    Battery b = (Battery) branch;
//                    b.setRotateEnabled(false);
//                }
//                super.mouseReleased(event);
//            }
//        };

        JunctionGraphic endJunctionGraphic = new JunctionGraphic( branch.getEndJunction(), module, DefaultCompositeBranchGraphic.JUNCTION_RADIUS, DefaultCompositeBranchGraphic.JUNCTION_COLOR, DefaultCompositeBranchGraphic.JUNCTION_STROKE, DefaultCompositeBranchGraphic.JUNCTION_COLOR );

        addGraphic( startJunctionGraphic, 1 );
        addGraphic( endJunctionGraphic, 1 );
        if( branch instanceof Battery ) {
            BranchGraphic bg = new BranchGraphic( circuit, transform, branch, DefaultCompositeBranchGraphic.JUNCTION_COLOR, LifelikeGraphicFactory.WIRE_STROKE, module, highlightColor, highlightStroke );
            addGraphic( bg, -5 );
        }
    }

    private boolean isBulbOrResistor( Branch branch ) {
        return branch instanceof Resistor || branch instanceof Bulb;
    }


//    public void paintJunctionHole(PhetVector pt, Color c, Graphics2D g) {
//        paintJunctionHole(new Point2D.Double(pt.getX(), pt.getY()), c, g);
//    }

    public void paintJunctionHole( Point2D.Double pt, Color c, Graphics2D g ) {

        Point vpt = transform.modelToView( pt );
        int radius = DefaultCompositeBranchGraphic.JUNCTION_RADIUS - 4;
        Stroke str = DefaultCompositeBranchGraphic.JUNCTION_STROKE;
        Color color = DefaultCompositeBranchGraphic.JUNCTION_COLOR;
        g.setColor( color );
        g.setStroke( str );

        Ellipse2D.Double ella = new Ellipse2D.Double();
        ella.setFrameFromCenter( vpt.x, vpt.y, vpt.x + radius, vpt.y + radius );
//        g.setColor(c);
        g.draw( ella );
    }

    protected Rectangle getImageRectangle() {
        Rectangle imageRect = new Rectangle( imagePortion.getImage().getWidth(), imagePortion.getImage().getHeight() );
        return imageRect;
    }

    /**
     * Returns the AffineTransform that will take the specified image, and place it centered at (x,y) at the specified angle.
     */
    public AffineTransform getImageTransform( BufferedImage bi, double angle, double x, double y ) {
        AffineTransform at = new AffineTransform();
        at.translate( x - bi.getWidth() / 2.0, y - bi.getHeight() / 2.0 );
        at.rotate( angle, bi.getWidth() / 2.0, bi.getHeight() / 2.0 );
        return at;
    }

    public void update() {
        PhetVector start = branch.getStart();
        PhetVector end = branch.getEnd();
        double length = getBranchLength();
        PhetVector dir = branch.getDirection();
        double modelWidthForImage = transform.viewToModelDifferentialX( imagePortion.getImageWidth() );
        double segmentLength = ( length - modelWidthForImage ) / 2;

        PhetVector imageCenterModel = start.getAddedInstance( dir.getScaledInstance( segmentLength + modelWidthForImage / 2 ) );
        Point imCtr = transform.modelToView( imageCenterModel );
//        angle = Math.atan2(imCtr.y - pre.getTarget().getStartPoint().y, imCtr.x - pre.getTarget().getStartPoint().x);
//        angle=-Math.atan2(start.getY()-end.getY(),start.getX()-end.getX());
        viewStart = transform.modelToView( start );
        viewEnd = transform.modelToView( end );
        angle = Math.atan2( viewStart.getY() - viewEnd.getY(), viewStart.getX() - viewEnd.getX() ) + Math.PI;
        AffineTransform imageTransform = getImageTransform( imagePortion.getImage(), angle, imCtr.getX(), imCtr.getY() );
        AffineTransform fireTransform = getImageTransform( imagePortion.getFlameImage(), angle, imCtr.getX(), imCtr.getY() );
        imagePortion.setImageTransform( imageTransform, fireTransform );

//        Rectangle imageRect = new Rectangle(imagePortion.image.getWidth(), imagePortion.image.getHeight());
//        this.imagePortion.setImageShape(imagePortion.imageTransform.createTransformedShape(imageRect));

        currentOrVoltageChanged( branch );
        bounds = imagePortion.getImageShape().getBounds();
//        this.loc=new Point(bounds.x,bounds.y);
        textDisplay.setLocation( bounds.x, bounds.y );
    }

    protected PhetVector getSecondStartPoint( PhetVector start, PhetVector dir, double segmentLength, double modelWidthForImage ) {
        return start.getAddedInstance( dir.getScaledInstance( segmentLength + modelWidthForImage ) );
    }

    protected double getBranchLength() {
        return branch.getLength();
    }

    public void transformChanged( ModelViewTransform2D mvt ) {
        update();
    }

    public void junctionMoved( Branch branch2, Junction junction ) {
        update();
    }

    DecimalFormat df = new DecimalFormat( "0.0#" );

    public void currentOrVoltageChanged( Branch branch2 ) {
//        String currentText = df.format(this.branch.getCurrent());
        String text = "";
        if( branch2 instanceof Battery ) {
            String voltText = df.format( this.branch.getVoltageDrop() );

//        String text = currentText + " Amps" + ", " + voltText + " Volts";
            text += voltText + " Volts";
        }
        else if( branch instanceof HasResistance ) {
            HasResistance hr = (HasResistance)branch;
            String resText = df.format( hr.getResistance() );
            if( resText.equals( "1.0" ) ) {
                text += resText + " Ohm";
            }
            else {
                text += resText + " Ohms";
            }
        }

        textDisplay.setText( text );
    }

    public void setImage( BufferedImage image ) {
        imagePortion.setImage( image );
    }

    public AffineTransform getImageTransform() {
        return imagePortion.getImageTranform();
    }

    public Shape getStartWireShape() {
        return new Rectangle();
    }

    public Shape getEndWireShape() {
        return new Rectangle();
    }

    public Branch getBranch() {
        return branch;
    }

    public void setWireColor( Color color ) {
        //no wire color.
    }

    public InteractiveGraphic getMainBranchGraphic() {
        return imagePortion;
    }


}
