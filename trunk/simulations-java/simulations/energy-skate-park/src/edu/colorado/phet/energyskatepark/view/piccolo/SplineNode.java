// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.PopupMenuHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.common.spline.ParametricFunction2D;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSplineEnvironment;
import edu.colorado.phet.energyskatepark.view.SplineMatch;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.*;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserActions.attached;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.controlPoint;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 1:17:41 PM
 */

public class SplineNode extends PNode {
    private final TrackNode splinePath;
    private final PhetPPath rollerCoasterPath;

    private final PNode controlPointLayer;

    private Point2D.Double[] initDragSpline;
    private Point2D.Double controlPointLoc;

    private final BasicStroke dottedStroke = new BasicStroke( 0.03f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 0.09f, 0.09f }, 0 );
    private final BasicStroke lineStroke = new BasicStroke( 0.03f );
    private EnergySkateParkSpline spline;
    private EnergySkateParkSpline lastState;
    private final PBasicInputEventHandler dragHandler;

    private final JComponent parent;
    private final EnergySkateParkSplineEnvironment splineEnvironment;
    private final TrackNode centerPath;
    private final SplineNode.TrackPopupMenu popupMenu;
    private final EnergySkateParkSpline.Listener splineListener;
    private final boolean isDev = false;

    public SplineNode( JComponent parent, EnergySkateParkSpline energySkateParkSpline, EnergySkateParkSplineEnvironment splineEnvironment, boolean controllable ) {
        this.parent = parent;
        this.splineEnvironment = splineEnvironment;
        this.spline = energySkateParkSpline;
        splinePath = new TrackNode( 0.75f, Color.gray );
        centerPath = new TrackNode( 0.15f, new Color( 235, 193, 56 ) );

        rollerCoasterPath = new PhetPPath( getRailroadStroke( 0.4f ), Color.gray );
        rollerCoasterPath.setPickable( false );
        rollerCoasterPath.setChildrenPickable( false );

        centerPath.setPickable( false );
        centerPath.setChildrenPickable( false );

        controlPointLayer = new PNode();

        addChild( splinePath );
        addChild( centerPath );
        addChild( rollerCoasterPath );
        if ( controllable ) {
            addChild( controlPointLayer );
        }

        dragHandler = new SimSharingDragHandler( chain( EnergySkateParkSimSharing.UserComponents.track, spline.getParametricFunction2D().index ), UserComponentTypes.sprite ) {

            @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( trackIndex, spline.getParametricFunction2D().index );
            }

            @Override protected void drag( PInputEvent event ) {
                super.drag( event );
                dragSpline( event );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                finishDragSpline( event );
            }

            @Override protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                initDragSpline();
            }
        };

        popupMenu = new TrackPopupMenu( splineEnvironment );
        if ( controllable ) {
            splinePath.addInputEventListener( this.dragHandler );
            splinePath.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            splinePath.addInputEventListener( new PopupMenuHandler( chain( EnergySkateParkSimSharing.UserComponents.track, spline.getParametricFunction2D().index ), parent, popupMenu ) );
        }

        splineListener = new EnergySkateParkSpline.Listener() {
            public void rollerCoasterModeChanged() {
                update();
            }

            public void controlPointsChanged() {
                update();
            }
        };
        energySkateParkSpline.addListener( splineListener );
        update();
    }

    private void showColorControls() {
        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;

        panel.add( new TrackEditPanel( splinePath, "Outside Line" ), gridBagConstraints );
        panel.add( new TrackEditPanel( centerPath, "Center Line" ), gridBagConstraints );

        JDialog colorControls = new JDialog( (Frame) SwingUtilities.getWindowAncestor( parent ) );
        colorControls.setContentPane( panel );
        colorControls.pack();
        SwingUtils.centerDialogInParent( colorControls );
        colorControls.setVisible( true );
    }

    public ParametricFunction2D getParametricFunction2D() {
        return spline.getParametricFunction2D();
    }

    private BasicStroke getTrackStroke( float thickness ) {
        return new BasicStroke( (float) ( EnergySkateParkModel.SPLINE_THICKNESS * thickness ) );
    }

    private BasicStroke getRailroadStroke( float thickness ) {
        return new BasicStroke( (float) ( EnergySkateParkModel.SPLINE_THICKNESS * thickness ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { (float) ( EnergySkateParkModel.SPLINE_THICKNESS * 0.4 ), (float) ( EnergySkateParkModel.SPLINE_THICKNESS * 0.6f ) }, 0 );
    }

    private void dragSpline( PInputEvent event ) {
        Point2D.Double tx = new Point2D.Double( event.getDeltaRelativeTo( this ).width, event.getDeltaRelativeTo( this ).height );
        dragSpline( event, tx );
    }

    private void dragSpline( PInputEvent event, Point2D.Double tx ) {
        double dy = tx.getY();
        double dx = tx.getX();

        //Make it so the spline can't be dragged out of the screen, tricky since the visible screen changes depending on the aspect ratio
        if ( dy < 0 && spline.getMinControlPointY() < 0 ||
             dy > 0 && spline.getMaxControlPointY() > splineEnvironment.getMaxDragY() ||
             dx < 0 && spline.getMinControlPointX() < splineEnvironment.getMinDragX() ||
             dx > 0 && spline.getMaxControlPointX() > splineEnvironment.getMaxDragX() ) {
            return;
        }

        translateAll( tx );
        if ( isAttachAllowed( event ) ) {
            proposeMatchesTrunk();
        }
    }

    public EnergySkateParkSpline getSpline() {
        return spline;
    }

    public void detachListeners() {
        this.spline.removeListener( splineListener );
        popupMenu.detachListeners();
    }

    public void processExternalStartDragEvent() {
        initDragSpline();
    }

    public void processExternalDragEvent( PInputEvent event, double dx, double dy ) {
        dragSpline( event, new Point2D.Double( dx, dy ) );
    }

    public void processExternalDropEvent( PInputEvent event ) {
        finishDragSpline( event );
    }

    private void finishDragSpline( PInputEvent event ) {
        if ( isAttachAllowed( event ) ) {
            boolean didAttach = testAttach( 0 );
            if ( !didAttach ) {
                testAttach( numControlPointGraphics() - 1 );//can't do two at once.
            }
        }
        initDragSpline = null;
        spline.setUserControlled( false );
    }

    private boolean testAttach( int index ) {
        SplineMatch startMatch = getTrunkMatch( index );
        if ( startMatch != null ) {
            EnergySkateParkSpline result = attach( index, startMatch );
            SimSharingManager.sendUserMessage( chain( EnergySkateParkSimSharing.UserComponents.track, spline.getParametricFunction2D().index ), UserComponentTypes.sprite, attached
                    , ParameterSet.parameterSet( inputTrack1, spline.getParametricFunction2D().index ).
                    with( inputTrack2, startMatch.getEnergySkateParkSpline().getParametricFunction2D().index ).
                    with( outputTrack, result.getParametricFunction2D().index ) );
            return true;
        }
        return false;
    }

    private void initDragSpline() {
        spline.setUserControlled( true );
        initDragSpline = new Point2D.Double[spline.getControlPoints().length];
        for ( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i] = new Point2D.Double( spline.getControlPoint( i ).getX(), spline.getControlPoint( i ).getY() );
        }
    }

    private void proposeMatchesTrunk() {
        boolean ok = proposeMatchTrunk( 0 );
        if ( !ok ) {
            proposeMatchTrunk( numControlPointGraphics() - 1 );
        }
    }

    private boolean proposeMatchTrunk( int index ) {
        SplineMatch match = getTrunkMatch( index );
        if ( match != null ) {
            spline.setControlPointLocation( index, new SerializablePoint2D( match.getTarget().getFullBounds().getCenter2D() ) );
            return true;
        }
        else {
            spline.setControlPointLocation( index, new SerializablePoint2D( initDragSpline[index] ) );
            return false;
        }
    }

    private SplineMatch getTrunkMatch( int index ) {
        if ( initDragSpline == null ) {
            EnergySkateParkLogging.println( "initdragspline was null" );
            return null;
        }
        if ( index < 0 || index > initDragSpline.length ) {
            EnergySkateParkLogging.println( "index = " + index + ", initDragSpline.length=" + initDragSpline.length );
            return null;
        }
        else {
            Point2D toMatch = new Point2D.Double( initDragSpline[index].getX(), initDragSpline[index].getY() );
            return splineEnvironment.proposeMatch( this, toMatch );
        }
    }

    private void translateAll( Point2D pt ) {
        translateAll( pt.getX(), pt.getY() );
    }

    private void translateAll( double dx, double dy ) {
        spline.translate( dx, dy );
        for ( int i = 0; i < initDragSpline.length; i++ ) {
            initDragSpline[i].x += dx;
            initDragSpline[i].y += dy;
        }
    }

    private void update() {
        setPickable( spline.isInteractive() );
        setChildrenPickable( spline.isInteractive() );
        if ( changed() ) {
            GeneralPath path = spline.getInterpolationPath();

            splinePath.setPathTo( path );
            centerPath.setPathTo( path );
            rollerCoasterPath.setPathTo( path );
            rollerCoasterPath.setVisible( spline.isRollerCoasterMode() );
            rollerCoasterPath.setStrokePaint( spline.isRollerCoasterMode() ? Color.gray : Color.black );

            controlPointLayer.removeAllChildren();

            for ( int i = 0; i < spline.numControlPoints(); i++ ) {
                Point2D point = spline.getControlPoint( i );
                addControlPoint( point, i );
            }
            for ( int i = 0; i < controlPointLayer.getChildrenCount(); i++ ) {
                PPath child = (PPath) controlPointLayer.getChild( i );
                if ( i == 0 || i == controlPointLayer.getChildrenCount() - 1 ) {
                    child.setStroke( dottedStroke );
                    child.setStrokePaint( PhetColorScheme.RED_COLORBLIND );
                }
                else {
                    child.setStroke( lineStroke );
                    child.setStrokePaint( Color.black );
                }
            }
            lastState = spline.copy();
        }
        popupMenu.updateAll();
    }

    private boolean changed() {
        return lastState == null || !same();
    }

    private boolean same() {
        return lastState.equals( spline.copy() );
    }


    private class ControlPointNode extends PPath {

        public ControlPointNode( double x, double y, double diameter, final int index ) {
            super( new Ellipse2D.Double( x - diameter / 2, y - diameter / 2, diameter, diameter ) );

            setStroke( dottedStroke );
            setStrokePaint( Color.black );
            setPaint( new Color( 0, 0, 1f, 0.5f ) );

            final UserComponentChain controlPointUserComponent = chain( EnergySkateParkSimSharing.UserComponents.track, new UserComponent( spline.getParametricFunction2D().index ), controlPoint, new UserComponent( index ) );
            addInputEventListener( new SimSharingDragHandler( controlPointUserComponent, UserComponentTypes.sprite ) {
                @Override protected void startDrag( PInputEvent event ) {
                    super.startDrag( event );
                    initDragControlPoint( index );
                    spline.setUserControlled( true );
                    event.setHandled( true );
                }

                @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( trackIndex, spline.getParametricFunction2D().index );
                }

                @Override protected void drag( PInputEvent event ) {
                    super.drag( event );
                    PDimension rel = event.getDeltaRelativeTo( SplineNode.this );

                    //Not allowed to drag out of the stage
                    if ( spline.getControlPoints()[index].getY() + rel.getHeight() < splineEnvironment.getMinDragY() ) {
                        rel.height = splineEnvironment.getMinDragY() - spline.getControlPoints()[index].getY();
                    }
                    if ( spline.getControlPoints()[index].getY() + rel.getHeight() > splineEnvironment.getMaxDragY() ) {
                        rel.height = splineEnvironment.getMaxDragY() - spline.getControlPoints()[index].getY();
                    }
                    if ( spline.getControlPoints()[index].getX() + rel.getWidth() < splineEnvironment.getMinDragX() ) {
                        rel.width = splineEnvironment.getMinDragX() - spline.getControlPoints()[index].getX();
                    }
                    if ( spline.getControlPoints()[index].getX() + rel.getWidth() > splineEnvironment.getMaxDragX() ) {
                        rel.width = splineEnvironment.getMaxDragX() - spline.getControlPoints()[index].getX();
                    }

                    spline.translateControlPoint( index, rel.getWidth(), rel.getHeight() );
                    if ( index == 0 || index == numControlPointGraphics() - 1 ) {
                        controlPointLoc.x += rel.getWidth();
                        controlPointLoc.y += rel.getHeight();
                    }
                    if ( isAttachAllowed( event ) ) {
                        proposeMatchesEndpoint( index );
                    }
                    event.setHandled( true );
                }

                @Override protected void endDrag( PInputEvent event ) {
                    super.endDrag( event );
                    if ( isAttachAllowed( event ) ) {
                        finishDragControlPoint( index );
                    }
                    spline.setUserControlled( false );
                    event.setHandled( true );
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            addInputEventListener( new PopupMenuHandler( controlPointUserComponent, parent, new ControlCirclePopupMenu( index ) ) );
        }
    }

    public boolean isAttachAllowed( PInputEvent event ) {
        return !event.isControlDown();
    }

    private void addControlPoint( Point2D point, int index ) {
        controlPointLayer.addChild( new ControlPointNode( point.getX(), point.getY(), 0.5, index ) );
    }

    class ControlCirclePopupMenu extends JPopupMenu {
        public ControlCirclePopupMenu( final int index ) {
            JMenuItem delete = new JMenuItem( EnergySkateParkResources.getString( "controls.delete-point" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {

                    //If there are 2 or less control points and the user removes one, it should just remove the entire track
                    //System.out.println( "spline.numControlPoints() = " + spline.numControlPoints() );
                    if ( spline.numControlPoints() <= 2 ) {
                        splineEnvironment.removeSpline( SplineNode.this );
                        splineEnvironment.notifySplineDeletedByUser();
                    }
                    else {
                        spline.removeControlPoint( index );
                    }
                }
            } );
            add( delete );
        }
    }

    private void proposeMatchesEndpoint( int index ) {
        if ( index == 0 || index == numControlPointGraphics() - 1 ) {
            SplineMatch match = getEndpointMatch();
            EnergySkateParkLogging.println( "match=" + match );
            if ( match != null ) {
                spline.setControlPointLocation( index, new SerializablePoint2D( match.getTarget().getFullBounds().getCenter2D() ) );
            }
            else {
                spline.setControlPointLocation( index, new SerializablePoint2D( controlPointLoc ) );
            }
        }
    }

    private SplineMatch getEndpointMatch() {
        Point2D toMatch = new Point2D.Double( controlPointLoc.getX(), controlPointLoc.getY() );
        return splineEnvironment.proposeMatch( this, toMatch );
    }

    private void finishDragControlPoint( int index ) {
        if ( index == 0 || index == numControlPointGraphics() - 1 ) {
            SplineMatch match = getEndpointMatch();
            if ( match != null ) {
                attach( index, match );
            }
            controlPointLoc = null;
        }
    }

    private EnergySkateParkSpline attach( int index, SplineMatch match ) {
        return splineEnvironment.attach( this, index, match );
    }

    private void initDragControlPoint( int index ) {
        if ( index == 0 || index == numControlPointGraphics() - 1 ) {
            controlPointLoc = new Point2D.Double( spline.getControlPoint( index ).getX(), spline.getControlPoint( index ).getY() );
        }
    }

    public PNode getControlPointGraphic( int index ) {
        return controlPointLayer.getChild( index );
    }

    public int numControlPointGraphics() {
        return controlPointLayer.getChildrenCount();
    }

    public void setControlPointsPickable( boolean pick ) {
        controlPointLayer.setPickable( pick );
        controlPointLayer.setChildrenPickable( pick );
        for ( int i = 0; i < numControlPointGraphics(); i++ ) {
            getControlPointGraphic( i ).setPickable( pick );
            getControlPointGraphic( i ).setChildrenPickable( pick );
        }
        update();
    }


    class TrackEditPanel extends JPanel {

        public TrackEditPanel( final TrackNode splinePath, String name ) {
            final ModelSlider modelSlider = new ModelSlider( name, "", 0, 10, splinePath.getThickness() );
            modelSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    splinePath.setThickness( (float) modelSlider.getValue() );
                }
            } );
            setLayout( new GridBagLayout() );
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
            gridBagConstraints.gridy = 0;

            final JColorChooser colorChooser = new JColorChooser( splinePath.getColor() );
            colorChooser.getSelectionModel().addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    splinePath.setColor( colorChooser.getColor() );
                }
            } );

            add( modelSlider, gridBagConstraints );
            add( colorChooser, gridBagConstraints );
        }
    }

    class TrackPopupMenu extends JPopupMenu {
        private final JCheckBoxMenuItem rollerCoasterMode;
        private final EnergySkateParkSpline.Listener splineListener = new EnergySkateParkSpline.Listener() {
            public void rollerCoasterModeChanged() {
                TrackPopupMenu.this.updateAll();
            }

            public void controlPointsChanged() {
            }
        };

        public TrackPopupMenu( final EnergySkateParkSplineEnvironment splineEnvironment ) {
            rollerCoasterMode = new JCheckBoxMenuItem( EnergySkateParkResources.getString( "track.roller-coaster-mode" ), spline.isRollerCoasterMode() );
            attachListeners( spline );
            rollerCoasterMode.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    splineEnvironment.setRollerCoasterMode( rollerCoasterMode.isSelected() );
                    spline.setRollerCoasterMode( rollerCoasterMode.isSelected() );
                    lastState = null;
                }
            } );

            JMenuItem delete = new JMenuItem( EnergySkateParkResources.getString( "controls.delete-track" ) );
            delete.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    splineEnvironment.removeSpline( SplineNode.this );
                    splineEnvironment.notifySplineDeletedByUser();
                }
            } );

            JMenuItem colors = new JMenuItem( "Edit Look" );//dev control
            colors.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    showColorControls();
                }
            } );

            add( rollerCoasterMode );
            addSeparator();
            add( delete );

            if ( isDev ) {
                addSeparator();
                add( colors );
            }
        }

        public void detachListeners() {
            spline.removeListener( splineListener );
        }

        private void updateAll() {
            rollerCoasterMode.setSelected( spline.isRollerCoasterMode() );
        }

        public void attachListeners( EnergySkateParkSpline spline ) {
            spline.addListener( splineListener );
        }
    }

    class TrackNode extends PhetPPath {
        private float thickness;
        private Color color;

        public TrackNode( float thickness, Color strokePaint ) {
            super( getTrackStroke( thickness ), strokePaint );
            this.thickness = thickness;
            this.color = strokePaint;
        }

        public float getThickness() {
            return thickness;
        }

        public void setThickness( float thickness ) {
            this.thickness = thickness;
            setStroke( getTrackStroke( thickness ) );
        }

        public Color getColor() {
            return color;
        }

        public void setColor( Color color ) {
            this.color = color;
            setStrokePaint( color );
        }
    }


}
