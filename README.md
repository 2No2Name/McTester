# MC Tester Mod

This mod allows using the automated structure based [test system](https://www.youtube.com/watch?v=vXaWOJTCYNg) Mojang
created for minecraft. The test system is only partially included in minecraft and this mod adds some missing parts to
make it work. Furthermore, more features, for example autorun and non-0 exit-code on failure, are included to make the
usage more convenient. The tests can be run with gradle or even with github actions see the
repositories' [build workflow file](https://github.com/2No2Name/McTester/blob/master/.github/workflows/build.yml).

## Usage as a dependency

Add the following to build.gradle (use existing dependencies/maven blocks) or put a build from the
[releases page](https://github.com/2No2Name/McTester/releases) into the `mods` folder. Currently the mctester mod will
be loaded at every startup e.g. `gradlew runClient` or `gradlew runServer`.

```
dependencies {
    modImplementation 'com.github.2No2Name:McTester:VERSION_TAG_HERE'
}

repositories {
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
}
```

Edit the config/mctester.properties file to adjust available options. Put test structures into the gameteststructures
directory. Prefixing the structure names with a template name, e.g.
`test_redstone.` will automatically use the test function from the template. Other test functions can be defined by
using the [@Test](https://github.com/2No2Name/McTester/blob/master/src/main/java/mctester/annotation/Test.java)
annotation,
cf. [Example test functions](https://github.com/2No2Name/McTester/blob/master/src/main/java/mctester/ExampleTests.java).

## Available test templates

See the test [templates file](https://github.com/2No2Name/McTester/blob/master/src/main/java/mctester/Templates.java)

- `test_redstone.`: Replaces all red stained terracotta with redstone blocks in the structure's area (test area)
  when the test is activated. Automatically fails after 20 seconds. Test succeeds if there is a powered noteblock on top
  of an emerald block or green/lime wool in the test area. The success block positions are cached at the start of the
  test, so placing or removing more success condition blocks may break the test success detection. The test can also be
  failed by powering a noteblock on top of a red wool. The success/failure conditions are checked once per tick, with
  the test failing when both success and failure are detected in the same tick.

## Available options

The following options can changed in the mctester.properties config text file. You may need to create the file manually.

Format: `option=VALUE`, possible values: `true`, `serveronly`, `clientonly`, `false`  
Defaults:

- `autostart=false` Automatically runs all tests when a world is loaded.
- `autostart.shuffle=true` Shuffles the tests before automatically running them.
- `autostart.shuffle.seed` Set the shuffle seed for debug purposes. When this option is not defined, a random seed is
  used.
- `crashOnFail=false` Automatically crashes the server when any test fails.
- `shutdownAfterTest=false` Automatically shut down the server after the tests finished.
- `stayUpAfterFail=false` Do not shutdown the server if a test failed.
- `isDevelopment=false` Sets Minecraft's SharedConstants.isDevelopment field if true.
- `includeExampleTests=false` Loads example test functions defined by this mod. Templates are always loaded.

## Setup for development

For setup instructions please see the [fabric wiki page](https://fabricmc.net/wiki/tutorial:setup) that relates to the
IDE that you are using.
