# Contribution guide

Contributions come in all shapes and sizes. Features, documentation improvements, even suggestions all count.

For features and other code contributions we only have a few asks:
- Mirror existing design patterns where possible
- Try and match the surrounding code style

For documentation improvements
- Place documentation files in the `docs/` directory
- Place images and other media files in the `docs/media/` directory
- Try to give explanations where relevant when providing example code/signature snippets when relevant

## Adding signature models

You can find documentation on creating signature models [[here]](docs/ModelFormat.md).

Currently, we have a number of sample signatures as [test resources](concoction-lib/src/test/resources/models).
At a later date we will have a place dedicated to maintaining signatures.

## Adding yourself as a developer

Developer metadata is included in the Maven artifact generated on each release.
To include yourself update the following files:

- `jreleaser.yml` - Under the `authors` block
- `concoction-lib/build.gradle` - Under the `publishing` block
