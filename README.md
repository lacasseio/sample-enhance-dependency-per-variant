# Demonstrate how to inject additional dependencies

In this example, we have a dependency `com.squareup.okhttp3:okhttp` that we wish to run with additional internal dependencies (in this case `org.apache.commons:commons-lang3`).
One important requirement is being able to inject the internal dependencies only for specific variant (read Android variants).

Android variants' dependency resolution are nothing more than repeated sets of `Configuration`.
To achieve the desire result, we use a selecting attribute (in this case `com.example.enhanced`) together with a `ComponentMetadataRule`.
The rule is responsible to create the derived variant containing the additional dependencies while the attribute signal which `Configuration` will request the derived variant.

In this particular example, we assume the internal dependency is a runtime only dependency thus we only derive from the `runtimeElements` variant.
We also only want the application's runtime dependency to request the derived variant (i.e. `runtimeClasspath`).
Under a more complex scenario, say Android, we could request the derived variant on `freeDebugRuntimeClasspath` which would means the `free` flavor of the `debug` build type for the runtime classpath.

It's important to understand this approach is prone to potential forward compatibility given it expect a variant named `runtimeElements`.
There is unfortunately no way to work around this limitation with this approach.
A different approach could help but would make the code considerably more complex.
Given variant names rarely change, the recommendation is to ensure sufficient testing coverage are in place to detect a variant name change and build a look aside table for the appropriate variant name to use.

Looking at the dependencies for `compileClasspath` vs `runtimeClasspath`, we can see the additional `org.apache.commons:commons-lang3` only on the `runtimeClasspath`:
```
$ ./gradlew :app:dependencies
> Task :app:dependencies

------------------------------------------------------------
Project ':app'
------------------------------------------------------------

[...]

compileClasspath - Compile classpath for source set 'main'.
+--- com.google.guava:guava:31.0.1-jre
[...]
\--- com.squareup.okhttp3:okhttp:4.12.0
     +--- com.squareup.okio:okio:3.6.0
     |    \--- com.squareup.okio:okio-jvm:3.6.0
     |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10
     |         |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.10
     |         |    |    +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10
     |         |    |    \--- org.jetbrains:annotations:13.0
     |         |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10
     |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.10 (*)
     |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10
     \--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21 -> 1.9.10 (*)

[...]

runtimeClasspath - Runtime classpath of source set 'main'.
+--- com.google.guava:guava:31.0.1-jre
[...]
\--- com.squareup.okhttp3:okhttp:4.12.0
     +--- com.squareup.okio:okio:3.6.0
     |    \--- com.squareup.okio:okio-jvm:3.6.0
     |         +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10
     |         |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.9.10
     |         |    |    +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10
     |         |    |    \--- org.jetbrains:annotations:13.0
     |         |    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10
     |         |         \--- org.jetbrains.kotlin:kotlin-stdlib:1.9.10 (*)
     |         \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10
     +--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21 -> 1.9.10 (*)
     \--- org.apache.commons:commons-lang3:3.17.0
     
[...]
```