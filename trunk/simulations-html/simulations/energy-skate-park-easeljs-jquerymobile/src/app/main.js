require( ['websocket-refresh',
             'tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, Tab, Strings ) {

    $( "#theMainBody" ).append( $( '<div data-role="navbar" id="navBar"><ul>' +
                                   '<li><a href="#tab1" id="introNavBarButton" data-transition="none" class="ui-btn-active ui-state-persist">' + Strings["tab.introduction"] + '</a></li>' +
                                   '<li><a href="#tab2" id="frictionNavBarButton" data-transition="none" >' + Strings["tab.friction"] + '</a></li>' +
                                   '<li><a href="#tab3" id="playgroundNavBarButton" data-transition="none" >' + Strings["tab.trackPlayground"] + '</a></li></ul></div>' ) ).trigger( "create" );

    WebsocketRefresh.listenForRefresh();
    var tab1 = new Tab( "tab1" );
    var tab2 = new Tab( "tab2" );
} );