Should the Translation application be a project in common or build-tools?
>>Perhaps we should create a new directory called "tools" or "utilities" at the same level as build-tools?

Should the Translation application be implemented as a Java Web Start application?
>>Double-clickable jar might be easier, but Web Start makes it easier to propagate updates to users.
>>Yes.

To access filesystem and launch 'java.exe' process, a web start application should be signed.
>>Sounds like a hassle, but could be useful for other sims, as well.

Should all simulations be downloaded before a translation launch, or lazily download requested simulations?
>>Download as needed.  Unlikely that anyone will translate everything.  Even if they wanted to, a sim might be updated before they finish.
>>This sounds much too complicated and difficult to maintain.
    I imagine something simple, like a separate jnlp that specifies a main located in the common area.
    This main looks for simulations in the resource jar. Thus, we deploy the same jar for each simulation that we are deploying now --
    we just add another jnlp that points to a different main which enables the user to translate that simulation
    (and any others that happen to be located in the jar).

Should we try to fit simulations into the same 'java.exe' process as the translation program, or use java.exe to launch?
>>Whatever makes the translation process the most transparent.