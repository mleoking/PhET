// Copyright 2002-2012, University of Colorado

define( [
            "../util/Person"
        ],
        function ( Person ) {

            function Model( firstName, lastName ) {
                this.person = new Person( firstName, lastName );
                console.log( "Do we have a Person in Model? : " + ( this.person instanceof Person ) );
            }

            return Model;
        } );