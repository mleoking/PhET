// Copyright 2002-2012, University of Colorado

define( [],
        function () {

            function Person( firstName, lastName ) {

                this.firstName = firstName;
                this.lastName = lastName;
            }

            Person.prototype.printName = function() {
                console.log( this.firstName + " " + this.lastName );
            }

            return Person;
        } );