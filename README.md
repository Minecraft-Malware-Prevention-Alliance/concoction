# Concoction

A shared Java malware scanner capable of static and dynamic analysis.

## Usage

Concoction can be used either as a Java library or as a command line application.

### Scan models

The format of scan models is described [here](docs/ModelFormat.md).

### As a library

First add Concoction as a library to your project. It is available on maven central:
```xml
<!-- Maven dependency declaration, for the latest version see the project releases page -->
<dependency>
    <groupId>info.mmpa</groupId>
    <artifactId>concoction</artifactId>
    <version>${version}</version>
</dependency>
```
```groovy
// Gradle dependency declaration
implementation "info.mmpa:concoction:${version}"
```

For code examples on using Concoction as a library, see [the test cases](concoction-lib/src/test/java/info/mmpa/concoction).

### As a command line application

> Not yet implemented