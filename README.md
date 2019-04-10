This is the Elder Scrolls: Legends SDK Java implementation.

It is a wrapper around the Elder Scrolls: Legends API of 
https://elderscrollslegends.io.

# Including in your project

TODO: Upload to maven and make available as a dependency

# Locally Building

Ensure you have Java 8+ installed (e.g. via [SdkMan](https://sdkman.io/))

    ./gradlew clean build

# Functions available

## Cards

### Find by id

    Card.find(id)
    
### Get all cards

    Card.all()

### Applying where clause

    Card.where(mapOfPredicates)

## Sets

### Find by id

    Set.find(id)
    
### Get all sets

    Set.all()

### Applying where clause

    Set.where(mapOfPredicates)

# Underlying implementation details

This library uses [Unirest](http://unirest.io/) for low level client connection.

This sdk was developed in Kotlin.
