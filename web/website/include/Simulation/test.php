<?php

class B {
    public function b() {
        print "b() called\n";
    }
  }

class A extends B {
    public function a() {
        print "a() called\n";
    }

    public function __call($method, $args) {
        print "__call() called\n";
        var_dump($method);
        var_dump($args);
    }
  }

    $x = new A();
    $x->a();
    $x->b();
    $x->z();
?>
