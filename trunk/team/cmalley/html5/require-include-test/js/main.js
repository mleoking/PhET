// Copyright 2002-2012, University of Colorado

require( [
             "model/Contact",
             "../js/model/Person"
         ],
         function ( Contact, Person ) {

             var contact = new Contact( "joe", "plumber" );
             console.log( "Do we still have a Person in main? : " + ( contact.person instanceof Person ) );

             if ( contact.person instanceof Person ) {
                 contact.person.printName();
             }
             else {
                 console.log( "ERROR: contact.person has wrong type: " + typeof( contact.person ) );
             }
         } );