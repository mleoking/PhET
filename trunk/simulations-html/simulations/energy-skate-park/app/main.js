require( ['WebsocketRefresh',
             '../../../common/phet/app/analytics/Analytics',
             'i18n!../nls/energy-skate-park-strings',
             'view/Tab',
             'easel'
         ], function ( WebsocketRefresh, Analytics, Strings, Tab, Easel ) {

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    new Tab( $( "#tab1" ), Easel, Strings, analytics ).render();

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
//    window.tab1 = new Tab( "tab1", true, massSlider( {id: "tab1"} ), analytics );
//    new Tab( "tab2", false, frictionControls( {id: "tab2"} ), analytics );
//    new Tab( "tab3", false, frictionControls( {id: "tab3"} ), analytics );
} );
