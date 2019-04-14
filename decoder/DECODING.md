# Decoding card ids and deck import strings

## Import/Export String Format

A typical import code is of the form:

    SPABcCAAAA

The string is broken into these parts:

1. "SP"

    This is Sparky Pants fixed string

2. Section count ("AB")

    There are 3 sections designating the start of the count for
1, 2, 3 counts of cards.
    
    The format is base 26 starting at A = 0, B = 1, ...
so CX is (26*2 + 23) = 75, and would represent 75 cards to follow.
    A code of AA means zero cards for that count.

3. Card data ("cC")

    Each card is a 2 letter code, e.g. cC is Bushwhack.
    The cards must match the card count from the previous value.
    In our example, AB = 1, hence just "cC" code in this section.

4. Repeat section counts for other counts of cards.

Thus to represent 1 x Bushwhack ("cC"), 2 x Close Call ("dq") would require
the string:

    SPABcCABdqAA

## Converting SPxx data to Card Name

This was a 2 pass process via legends-decks importer.

First get their internal hash code to represent a combination of cards from the import code:

    $ curl -s 'https://www.legends-decks.com/ajax/deck_import.php' \
           -H 'origin: https://www.legends-decks.com' \
           -H 'accept-encoding: gzip, deflate, br' \
           -H 'accept-language: en-GB,en;q=0.9,en-US;q=0.8,fr;q=0.7' \
           -H 'x-requested-with: XMLHttpRequest' \
           -H 'cookie: PHPSESSID=a35b1c1abb02bebd3bf61c06a8e852d3; _ga=GA1.2.1397344180.1555230769; _gid=GA1.2.45595269.1555230769; cookieconsent_status=dismiss' \
           -H 'pragma: no-cache' \
           -H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36' \
           -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
           -H 'accept: application/json, text/javascript, */*; q=0.01' \
           -H 'cache-control: no-cache' \
           -H 'authority: www.legends-decks.com' \
           -H 'referer: https://www.legends-decks.com/deck-builder' \
           --data 'import_desc=SPABcCAAAA' \ 
           --compressed

    {"url":"a888","title":""}

Then request that data, and pull out the `<span class='name'>Card Name</span>` section

    curl -s 'https://www.legends-decks.com/ajax/deck_builder_first_load.php'
         -H 'origin: https://www.legends-decks.com' -H 'accept-encoding: gzip, deflate, br' \
         -H 'accept-language: en-GB,en;q=0.9,en-US;q=0.8,fr;q=0.7' \
         -H 'x-requested-with: XMLHttpRequest' \
         -H 'cookie: PHPSESSID=a35b1c1abb02bebd3bf61c06a8e852d3; _ga=GA1.2.1397344180.1555230769; _gid=GA1.2.45595269.1555230769; cookieconsent_status=dismiss' \
         -H 'pragma: no-cache' \
         -H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36' \
         -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
         -H 'accept: application/json, text/javascript, */*; q=0.01' \
         -H 'cache-control: no-cache' \
         -H 'authority: www.legends-decks.com' \
         -H 'referer: https://www.legends-decks.com/deck-builder' \
         --data 'hash=#a888' \
         --compressed | \
     jq -r '.deck' | pup 'span.name text{}'

    Bushwhack

Thus we can convert every pair of characters `[a-z][a-zA-Z]` into a card name.

It appears that the first character of the codes 