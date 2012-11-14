require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.large );
    console.log( Strings["plots.position.meters"] );
    console.log( Strings["plots.energy-vs-position"] );
    console.log( Strings["energy-skate-park.description"] );

    $( "#theMainBody" ).append( $( '<div data-role="navbar" id="navBar"><ul>' +
                                   '<li><a href="" class="ui-btn-active ui-state-persist">' + Strings["tab.introduction"] + '</a></li>' +
                                   '<li><a href="" id="frictionNavBarButton">' + Strings["tab.friction"] + '</a></li>' +
                                   '<li><a href="">' + Strings["tab.trackPlayground"] + '</a></li></ul></div>' ) ).trigger( "create" );
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
} );