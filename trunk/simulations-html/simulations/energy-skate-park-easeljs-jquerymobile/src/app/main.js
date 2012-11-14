require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.large );
    console.log( Strings["plots.position.meters"] );
    console.log( Strings["plots.energy-vs-position"] );
    console.log( Strings["energy-skate-park.description"] );

    $( "#theMainBody" ).append( $( '<div data-role="navbar" id="navBar"><ul>' +
                                   '<li><a href="" id="introNavBarButton" class="ui-btn-active ui-state-persist">' + Strings["tab.introduction"] + '</a></li>' +
                                   '<li><a href="" id="frictionNavBarButton">' + Strings["tab.friction"] + '</a></li>' +
                                   '<li><a href="" id="playgroundNavBarButton">' + Strings["tab.trackPlayground"] + '</a></li></ul></div>' ) ).trigger( "create" );

    $( "#introNavBarButton" ).click( function () { console.log( "intro" ); } );
    $( "#frictionNavBarButton" ).click( function () { console.log( "friction" ); } );
    $( "#playgroundNavBarButton" ).click( function () { console.log( "playground" ); } );
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
} );