require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.large );
    console.log( Strings["plots.position.meters"] );
    console.log( Strings["plots.energy-vs-position"] );
    console.log( Strings["energy-skate-park.description"] );
    $( "#tab1" ).append( $( '<canvas id="c" width=400 height=300></canvas>' ) );
//    $( "#tab2" ).append( $( '<canvas id="c" width=400 height=300></canvas>' ) );
//    $( "#tab3" ).append( $( '<canvas id="c" width=400 height=300></canvas>' ) );

    $( '#tab1' ).css( 'position', 'absolute' ).css( 'width', '100%' ).css( 'height', '100%' );


    $( "#theMainBody" ).append( $( '<div data-role="navbar" id="navBar"><ul>' +
                                   '<li><a href="" id="introNavBarButton" class="ui-btn-active ui-state-persist">' + Strings["tab.introduction"] + '</a></li>' +
                                   '<li><a href="" id="frictionNavBarButton">' + Strings["tab.friction"] + '</a></li>' +
                                   '<li><a href="" id="playgroundNavBarButton">' + Strings["tab.trackPlayground"] + '</a></li></ul></div>' ) ).trigger( "create" );

    $( "#introNavBarButton" ).click( function () {
        $( '#tab1' ).css( "-webkit-transition", "all 1s ease-in-out" );
        $( '#tab1' ).css( 'webkit-transform', 'translate(0px,0px)' );
    } );
    $( "#frictionNavBarButton" ).click( function () {
        $( '#tab1' ).css( "-webkit-transition", "all 1s ease-in-out" );
        $( '#tab1' ).css( 'webkit-transform', 'translate(-1000px,0px)' );
    } );
    $( "#playgroundNavBarButton" ).click( function () { console.log( "playground" ); } );
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
} );