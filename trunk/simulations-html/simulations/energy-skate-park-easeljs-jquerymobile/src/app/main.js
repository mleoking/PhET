require( ['websocket-refresh',
             'tab'
         ], function ( WebsocketRefresh, Tab ) {

    WebsocketRefresh.listenForRefresh();
    new Tab( "tab1", true );
    new Tab( "tab2", false );
    new Tab( "tab3", false );
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