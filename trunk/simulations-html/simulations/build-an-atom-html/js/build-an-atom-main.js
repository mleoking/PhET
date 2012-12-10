// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'view/BuildAnAtomStage',
             'model/BuildAnAtomModel',
             'view/SymbolView',
             'view/MassNumberView',
             'view/PeriodicTableView'
         ], function ( _, Easel, BuildAnAtomStage, BuildAnAtomModel, SymbolView, MassNumberView, PeriodicTablelView ) {

    var buildAnAtomModel = new BuildAnAtomModel();
    window.buildAnAtomStage = new BuildAnAtomStage( document.getElementById( 'atom-construction-canvas' ), buildAnAtomModel );

    $( document ).ready( function () {

        var atom = buildAnAtomModel.atom;

        var symbolWidget = new SymbolView( atom );
        var massNumberWidget = new MassNumberView( atom );
        var periodicTableWidget = new PeriodicTablelView( atom );
    } );
} );
