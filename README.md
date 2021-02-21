# GraphMk

![intro](https://raw.githubusercontent.com/graphmk/graphmk/master/branding/graphmk.PNG)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.graphmk/graphmk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.graphmk/graphmk)
## About

module to create graph schema from relational data

## Building GraphMk

### Prerequisites

- Sources compilation requires Java SDK 11.
- The project is built with Apache Maven 3+.
- Set or export JAVA_HOME to point to JDK. For example in Ubuntu:
  `export JAVA_HOME=/usr/lib/jvm/adoptopenjdk-11`
- Git

### Build the distribution

    git clone https://github.com/graphmk/graphmk.git
    cd graphmk
    ./gradlew clean build --parallel --refresh-dependencies


## Getting Started

The easiest way to get started with running graphmk locally and setting up a
development environment is add the maven library
from [maven repository](https://mvnrepository.com/artifact/io.github.graphmk/graphmk/1.0.0).
 
Then follow [docs](https://graphmk-docs.readthedocs.io/en/latest/) examples to try it out.

## Contact

For additional information about GraphMk, please contact keshann.18@cse.mrt.ac.lk 

## Links

- [Documentation](https://graphmk-docs.readthedocs.io/en/latest/)

## Changelog

Please check the [Releases](./CHANGELOG.md) | [Github](https://github.com/graphmk/graphmk/releases) page of this project.

## Contributing

Want to help contribute to the development of GraphMk? Check out our
[contribution guide](./contributing.md).

## License

[MIT License](./LICENSE)