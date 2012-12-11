// Copyright 2002-2012, University of Colorado

require( [
             "model/Model",
             "../js/model/Person"
         ],
         function ( Model, Person ) {

             var model = new Model( "joe", "plumber" );
             console.log( "Do we still have a Person in main? : " + ( model.person instanceof Person ) );

             if ( model.person instanceof Person ) {
                 model.person.printName();
             }
             else {
                 console.log( "ERROR: model.person has wrong type: " + typeof( model.person ) );
             }
         } );