/**Subclass of Property that adds methods specific to boolean values*/
define( ['model/Property'], function ( Property ) {
    test( "Count notifications", function () {
        var name = new Property( "Moe" );
        name.notificationCount = 0;
        var observer = function ( newName, oldName ) {
            console.log( "newName=" + newName + ", oldName=" + oldName );
            name.notificationCount++;
        };

        //Before adding the listener, should be no notifications
        ok( name.notificationCount == 0 );

        //After adding a listener, should get one callback due to immediate auto-notification
        name.addObserver( observer );
        ok( name.notificationCount == 1 );

        //After changing the name, should get another callback
        name.set( "Larry" ); // observer should be notified
        ok( name.notificationCount == 2 );

        name.removeObserver( observer );
        name.set( "Curly" ); // this should result in no notification
        ok( name.notificationCount == 2 );
    } );
} );