/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.components.AmmeterBranch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.cck.elements.branch.components.Resistor;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.selection.SelectionListener;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 3:15:26 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class ImageBranchGraphic extends CompositeInteractiveGraphic implements TransformListener, BranchObserver, AbstractBranchGraphic {
    protected ModelViewTransform2D transform;
    protected Branch branch;
    protected CCK2Module module;
    JPopupMenu menu;
    private SubSegmentGraphic pre;
    private SubSegmentGraphic post;
    protected ImagePortion imagePortion;
    private DefaultBranchInteractionHandler interactionHandler;
    private Circuit circuit;

    TextDisplay2 textDisplay = new TextDisplay2();
    private double angle;
    JPopupMenu showMenuMenu;
    private Rectangle bounds;
    private int IMAGE_LAYER = 3;
    private boolean showIndex = false;

    public ImageBranchGraphic( final Circuit circuit, ModelViewTransform2D transform,
                               final Branch branch, Color color, Stroke stroke,
                               final CCK2Module module, BufferedImage image,
                               Stroke highlightStroke, Color highlightColor ) {
        this.circuit = circuit;
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
        if( !( branch instanceof AmmeterBranch ) ) {
            menu.add( delete );
        }
        final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( "Show Value", false );
//        JMenuItem showValue=new JMenuItem("Show Value");
        showValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                textDisplay.setVisible( showValue.isSelected() );
            }
        } );
        menu.add( showValue );

        final JMenuItem showEditor = new JMenuItem( "Change Value" );
        showEditor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( branch instanceof Resistor ) {
                    if( !showValue.isSelected() ) {
                        showValue.doClick( 50 );
                    }
//                    JFrame jf=new JFrame("Editing Resistor");
//                    JFrame jf=new JFrame("Editing Resistor");
                    final JDialog jf = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Editing Resistor", false );
                    final Resistor r = (Resistor)branch;
//                    SpinnerNumberModel snm=new SpinnerNumberModel(r.getResistance(), 0,1000.0,.2);
                    final JSlider js = new JSlider( 1, 100, (int)r.getResistance() );
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
                            r.setResistance( value );
                        }
                    } );

                    jp.add( js, BorderLayout.CENTER );
                    js.setBounds( 0, 0, 300, 100 );

                    JButton hide = new JButton( "Done" );
                    jp.add( hide, BorderLayout.SOUTH );
                    jp.setBorder( BorderFactory.createTitledBorder( "Resistance" ) );
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
                else if( branch instanceof Battery ) {
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
        if( branch instanceof Resistor || branch instanceof Battery ) {
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

        pre = new SubSegmentGraphic( color, stroke, highlightStroke, highlightColor, transform, interactionHandler );
        post = new SubSegmentGraphic( color, stroke, highlightStroke, highlightColor, transform, interactionHandler );

        addGraphic( pre, 1 );
        addGraphic( post, 2 );
        imagePortion = new ImagePortion( image, interactionHandler, module.getFlameImage(), branch );
//        fireImage=new ImagePortion(module.getFlameImage(),null);
        addGraphic( imagePortion, IMAGE_LAYER );

        update();

        addGraphic( textDisplay, 5 );
        branch.addSelectionListener( new SelectionListener() {
            public void selectionChanged( boolean sel ) {
                pre.getTarget().setSelected( sel );
                post.getTarget().setSelected( sel );
            }
        } );
        final Font showFont = new Font( "Lucida Sans", Font.BOLD, 16 );
        addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                if( showIndex ) {
                    g.setFont( showFont );
                    g.setColor( Color.blue );
                    String dir = "[" + branch.getId() + "]";//, "+branch.getCurrentGuessString();
                    g.drawString( dir, bounds.x, bounds.y );
                }
            }
        }, 100 );


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

    public double getAngle() {
        return angle;
    }

    public void update() {
        PhetVector start = branch.getStart();
        PhetVector end = branch.getEnd();
        double length = getBranchLength();
        PhetVector dir = branch.getDirection();
        double modelWidthForImage = transform.viewToModelDifferentialX( imagePortion.getImageWidth() );
        double segmentLength = ( length - modelWidthForImage ) / 2;

        PhetVector preEndVector = start.getAddedInstance( dir.getScaledInstance( segmentLength ) );
        pre.getTarget().setState( start.getX(), start.getY(), preEndVector.getX(), preEndVector.getY() );

        PhetVector secondStartPoint = getSecondStartPoint( start, dir, segmentLength, modelWidthForImage );
        post.getTarget().setState( secondStartPoint.getX(), secondStartPoint.getY(), end.getX(), end.getY() );
        PhetVector imageCenterModel = start.getAddedInstance( dir.getScaledInstance( segmentLength + modelWidthForImage / 2 ) );
        Point imCtr = transform.modelToView( imageCenterModel );
        angle = Math.atan2( imCtr.y - pre.getTarget().getStartPoint().y, imCtr.x - pre.getTarget().getStartPoint().x );
        AffineTransform imageTransform = getImageTransform( imagePortion.getImage(), angle, imCtr.getX(), imCtr.getY() );
        AffineTransform fireTransform = getImageTransform( imagePortion.getFlameImage(), angle, imCtr.getX(), imCtr.getY() );
        imagePortion.setImageTransform( imageTransform, fireTransform );

//        Rectangle imageRect = new Rectangle(imagePortion.image.getWidth(), imagePortion.image.getHeight());
//        this.imagePortion.setImageShape(imagePortion.imageTransform.createTransformedShape(imageRect));

        currentOrVoltageChanged( branch );
        bounds = imagePortion.getImageShape().getBounds();
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
////        String currentText = df.format(this.branch.getCurrent());
//        String voltText = df.format(this.branch.getVoltageDrop());
//        //amm
//        String text = voltText + " Volts";
//        if (branch instanceof HasResistance) {
//            HasResistance hr = (HasResistance) branch;
//            String resText = df.format(hr.getResistance());
//            text += ", " + resText + " Ohms";
//        }
//        textDisplay.setText(text);
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

    public Shape getStartWireShape() {
        return pre.getTarget().getShape();
    }

    public Shape getEndWireShape() {
        return post.getTarget().getShape();
    }

    public Branch getBranch() {
        return branch;
    }

    public void setWireColor( Color color ) {
        pre.getTarget().setColor( color );
        post.getTarget().setColor( color );
    }

    public InteractiveGraphic getMainBranchGraphic() {
        return this.imagePortion;
    }

    public void setImage( BufferedImage image ) {
        imagePortion.setImage( image );
    }

    public AffineTransform getImageTransform() {
        return imagePortion.getImageTranform();
    }

    public void addGraphicAfterImage( Graphic g ) {
        addGraphic( g, IMAGE_LAYER + 1 );
    }


}
