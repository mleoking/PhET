package edu.colorado.phet.fractionsintro.matchinggame.tests;

/**
 * @author Sam Reid
 */
public class TestRepaintRegions {

    //Feasibility test for performance improvements in rendering just the changed parts of the screen
//        addRepaintListener( model );


//    private Rectangle getBounds( final Option<MovableFraction> value, final SettableProperty<MatchingGameState> model ) {
//        return new MovableFractionNode( model, value.some(), value.some().toNode(), rootNode, false ).getGlobalFullBounds().getBounds();
//    }

//    private void addRepaintListener( final MatchingGameModel model ) {//Testing getting optimal performance, not necessarily the best way
//        //        PDebug.debugRegionManagement = true;
//        model.state.addObserver( new ChangeObserver<MatchingGameState>() {
//            @Override public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {
//
//                final F<MovableFraction, Integer> getID = new F<MovableFraction, Integer>() {
//                    @Override public Integer f( final MovableFraction m ) {
//                        return m.id;
//                    }
//                };
//                List<Integer> ids = newValue.fractions.map( getID ).append( oldValue.fractions.map( getID ) );
//                final HashSet<Integer> set = new HashSet<Integer>( Equal.<Integer>anyEqual(), Hash.<Integer>anyHash() );
//                ids.foreach( new F<Integer, Unit>() {
//                    @Override public Unit f( final Integer integer ) {
//                        set.set( integer );
//                        return Unit.unit();
//                    }
//                } );
//                System.out.println( "set = " + set.toCollection() );
//                for ( final Integer id : set ) {
//                    Option<MovableFraction> oldOne = oldValue.fractions.find( new F<MovableFraction, Boolean>() {
//                        @Override public Boolean f( final MovableFraction m ) {
//                            return m.id == id;
//                        }
//                    } );
//                    final Option<MovableFraction> newOne = newValue.fractions.find( new F<MovableFraction, Boolean>() {
//                        @Override public Boolean f( final MovableFraction m ) {
//                            return m.id == id;
//                        }
//                    } );
//
//                    if ( oldOne.isSome() && newOne.isSome() && !oldOne.some().position.equals( newOne.some().position ) ) {
//                        final Rectangle newBounds = MatchingGameCanvas.this.getBounds( newOne, model.state );
//                        paintImmediately( newBounds );
//                        final Rectangle oldBounds = MatchingGameCanvas.this.getBounds( oldOne, model.state );
//                        System.out.println( "newBounds = " + newBounds + ", oldBounds = " + oldBounds );
//                        paintImmediately( oldBounds );
//                    }
//                }
//
////                    Option<MovableFraction> newDrag = newValue.fractions.find( new F<MovableFraction, Boolean>() {
////                        @Override public Boolean f( final MovableFraction m ) {
////                            return m.dragging;
////                        }
////                    } );
////                    Option<MovableFraction> oldDrag = oldValue.fractions.find( new F<MovableFraction, Boolean>() {
////                        @Override public Boolean f( final MovableFraction m ) {
////                            return m.dragging;
////                        }
////                    } );
////
////                    count++;
//                if ( ( oldValue.getMode() != newValue.getMode() ) || newValue.info.mode == Mode.CHOOSING_SETTINGS || true ) {
//                    paintImmediately( new Rectangle( 0, 0, MatchingGameCanvas.this.getWidth(), MatchingGameCanvas.this.getHeight() ) );
//                    System.out.println( "immediate, w = " + getWidth() + "" );
//                }
////                    else {
////                        if ( newDrag.isSome() ) { paintImmediately( newDrag.some().toNode().getGlobalFullBounds().getBounds() ); }
////                        if ( oldDrag.isSome() ) { paintImmediately( oldDrag.some().toNode().getGlobalFullBounds().getBounds() ); }
////                    }
//            }
//        } );
//    }

}