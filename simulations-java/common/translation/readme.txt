Should the Translation application be a project in common or build-tools?
>>Perhaps we should create a new directory called "tools" or "utilities" at the same level as build-tools?

Should the Translation application be implemented as a Java Web Start application?
    -To access filesystem and launch 'java.exe' process, a web start application should be signed.
>>Double-clickable jar might be easier, but Web Start makes it easier to propagate updates to users.

Should all simulations be downloaded before a translation launch, or lazily download requested simulations?
>>Download as needed.  Unlikely that anyone will translate everything.  Even if they wanted to, a sim might be updated before they finish.

Should we try to fit simulations into the same 'java.exe' process as the translation program, or use java.exe to launch?
>>Whatever makes the translation process the most transparent.