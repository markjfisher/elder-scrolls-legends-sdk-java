# SDK Usage

## Cards

### Find by id

    Card.find(id)
    
### Get all cards

    Card.all()

### Applying where clause

    Card.where(paramMap)

where paramMap is a `Map<String, String>` of queriable fields, as detailed in paramters section of
https://docs.elderscrollslegends.io/#api_v1cards_list

## Decks

Decks are supported to allow importing from a code, or creating from lists of Card objects.

### Importing from code

    Deck deck = Deck.importCode("SPABcCAAAA")

    Deck deck = new Deck(List<Card>)

### Getting cards by count from deck

    List<Card> cards = deck.of(1) // returns all the single cards in the deck.
    List<Card> cards = deck.of(2) // all cards where there are 2 of them in the deck
    List<Card> cards = deck.of(3) // all cards where there are 3 of them in the deck

### Getting list of all cards in deck

This returns the list of all cards in the deck, not de-duped, i.e. contains multiple of same card
if there are any duplicates. Use `deck.of(int)` to get list of cards de-duped.

    List<Card> cards = deck.getCards()

## Collections

Collections (exported from game client) are a larger type of Deck, and created in the same way as Decks.

### Importing from code

    Collection c = Collection.importCode("SP!!!#cCef!!")

See Deck section above for getting cards out of collection, it supports the same methods.

## Sets

### Find by id

    Set.find(id)
    
### Get all sets

    Set.all()

### Applying where clause

    Set.where(paramMap)

where paramMap is a `Map<String, String>` of queriable fields, as detailed in paramters section of
https://docs.elderscrollslegends.io/#api_v1sets_list

## Attributes

### Get all attributes

    Attribute.all()

## Keywords

### Get all keywords

    Keyword.all()

## Types

### Get all types

    Type.all()

## Subtypes

### Get all subtypes

    Subtype.all()

