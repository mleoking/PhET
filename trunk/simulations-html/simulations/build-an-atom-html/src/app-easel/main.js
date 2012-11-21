// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'view/BuildAnAtomStage',
             'model/BuildAnAtomModel',
             'view/symbol-view',
             'view/mass-number-view',
             'tpl!templates/periodic-table.html'
         ], function ( _, Easel, BuildAnAtomStage, BuildAnAtomModel, SymbolView, MassNumberView, periodicTable ) {

    var buildAnAtomModel = new BuildAnAtomModel();
    var buildAnAtomStage = new BuildAnAtomStage( document.getElementById( 'atom-construction-canvas' ), buildAnAtomModel );

    $( document ).ready( function () {

        var atom = buildAnAtomModel.atom;
        var symbolWidget = new SymbolView( atom );
        var massNumberWidget = new MassNumberView( atom );

        $( '#periodic-table' ).html( periodicTable() );
    } );
} );
