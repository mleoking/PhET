-keep public class akka.remote.**{
    <fields>;
    <methods>;
}

-keep public class akka.event.**{
    <fields>;
    <methods>;
}

-keep public class akka.serialization.**{
    <fields>;
    <methods>;
}

-keep class com.google.protobuf.**{
    <fields>;
    <methods>;
}

-keep class akka.actor.**{
    <fields>;
    <methods>;
}

#TODO dynamically loaded PhET code should implement IProguardKeep marker interface
-keep class edu.colorado.phet.simsharing.**{
    <fields>;
    <methods>;
}

-keep class ch.qos.logback.core.rolling.RollingFileAppender{
    <fields>;
    <methods>;
}

-keep class ch.qos.logback.core.rolling.TimeBasedRollingPolicy{
    <fields>;
    <methods>;
}

-keep class org.slf4j.impl.StaticLoggerBinder{
    <fields>;
    <methods>;
}