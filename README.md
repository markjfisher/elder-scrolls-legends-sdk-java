This is the Elder Scrolls: Legends SDK Java implementation.

It is a wrapper around the Elder Scrolls: Legends API of 
https://elderscrollslegends.io.

# Including in your project

TODO: Upload to maven and make available as a dependency

# Example usage

The following illustrates using all(), where() and find().

```java
import io.elderscrollslegends.Card;
import io.elderscrollslegends.Keyword;
import io.elderscrollslegends.Set;

import java.util.Map;

public class Legends {
	public static void main(String args[]) {
		System.out.println(Keyword.all());

		System.out.println(Card.find("3921e71a0b3a5f30032d54d402cefb37b60aa46e"));

		Map<String, String> cardQuery = Map.of(
			"attributes", "strength",
			"keywords", "guard");

		Card.where(cardQuery)
			.forEach(System.out::println);

		Set.all()
			.stream()
			.filter(set -> set.getTotalCards() > 100)
			.forEach(System.out::println);
	}
}
```
# SDK

See [SDK Usage](SdkUsage.md)

# Locally Building

Ensure you have Java 8+ installed (e.g. via [SdkMan](https://sdkman.io/))

    ./gradlew clean build

# Underlying implementation details

This library uses [Unirest](http://unirest.io/) for low level client connection.

This sdk was developed in Kotlin.
