// Copyright 2002-2012, University of Colorado

/*
 * This function is called when scripts/helper/util.js is loaded.
 * If util.js calls define(), then this function is not fired until util's dependencies have loaded,
 * and the util argument will hold the module value for "helper/util".
 */
require( [
             "../../../common/mycommon/js/model/Model",
             "../../../common/mycommon/js/util/Person"
         ],
         function ( Model, Person ) {

             var model = new Model( "joe", "plumber" );
             console.log( "Do we still have a Person in main? : " + ( this.person instanceof Person ) );

             if ( model.person instanceof Person ) {
                 model.person.printName();
             }
             else {
                 console.log( "ERROR: model.person has wrong type: " + typeof( model.person ) );
             }
         } );