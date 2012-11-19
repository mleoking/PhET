require( ['websocket-refresh',
             'tab',
             "tpl!view/massSlider.html",
             "tpl!view/frictionControls.html"
         ], function ( WebsocketRefresh, Tab, massSlider, frictionControls ) {

    WebsocketRefresh.listenForRefresh();

    //TODO: move page creation to here, so it reads like $("body").append($(createTab("tab1")))
    new Tab( "tab1", true, massSlider( {id: "tab1"} ) );
    new Tab( "tab2", false, frictionControls( {id: "tab2"} ) );
    new Tab( "tab3", false, frictionControls( {id: "tab3"} ) );
//    new Tab( "tab4", false );
//    new Tab( "tab5", false );
//    new Tab( "tab6", false );
//    new Tab( "tab7", false );
//    new Tab( "tab8", false );
//    new Tab( "tab9", false );
//    new Tab( "tab10", false );
//    new Tab( "tab11", false );
//    new Tab( "tab12", false );
//    new Tab( "tab13", false );
//    new Tab( "tab14", false );
//    new Tab( "tab15", false );
} );