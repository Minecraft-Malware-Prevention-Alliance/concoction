# Contribution guide

We use SSVM as normal bytecode signatures are just not enough, obfuscator detections are allowed, It doesnt matter.
> TODO: Elaborate on process

## Adding yourself as a developer

Developer metadata is included in the Maven artifact generated on each release.
To include yourself update the following files:

- `jreleaser.yml` - Under the `authors` block
- `concoction-lib/build.gradle` - Under the `publishing` block
