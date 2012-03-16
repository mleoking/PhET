The full akka-1.1-core distribution is upwards of 50MB, so I've stripped out documentation to try to go easy on our SVN repo size
(since it is usually downloaded as a monolithic repo).

I also removed the scala-library.jar (8MB), since we already have a copy of scala 2.9.0 in contrib/scala

I've maintained the directory structure from the original distribution (just pruned it).

Sam Reid
PhET
5/13/2011