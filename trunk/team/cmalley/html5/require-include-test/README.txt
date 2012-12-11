Demonstrate an include problem with requirejs that is getting in the way of migrating code to common.

Run index.html and look at the console. You'll see that a Person created in common (in Model.js)
is not the same type as a Person in main.js.  This is presumably because the Person.js file is
loaded via a different path.