// Copyright 2002-2012, University of Colorado

define( [],
        function () {

            function Person( name ) {
                this.name = name;
            }

            Person.prototype.printName = function() {
                console.log( this.name );
            }

            return Person;
        } );