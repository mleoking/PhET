require( [ "model/Contact", "../js/model/Person" ],
         function ( Contact, Person ) {
             var contact = new Contact( "Joe Plumber" );
             if ( contact.person instanceof Person ) {
                 console.log( contact.person.name );
             }
             else {
                 console.log( "ERROR: contact.person has wrong type: " + typeof( contact.person ) );
             }
         } );