## Anagram Service
The anagram service is a simple program designed to find anagrams of word given a dictionary.

This project uses Java 8 and Maven.
###Getting Started
There are three different modes you can run the program in; local, server and client. When running
this application, pass in; `server`, `client` or [blank]/`local` as a command line argument. It
only reads the first parameter in the command line argument.

###local
Runs the program on your local machine and loads the words from the embedded file `words.txt` from
the `/src/main/resources`. The program then prompts you to input a word and responds with any
anagrams found for the word.

###server
Runs the program on your local machine exposing a socket on port 5555, it then waits for clients
to connect to this socket. Clients are presented with a Menu from which they can choose to;
- add a word
- remove a word
- find the anagram for a word

The server can handle multiple clients connecting at the same time.

###client
Runs the program as a client, it looks for a server running on the localhost and tries to connect
using port 5555. Upon connecting successfully with a server running locally, you are presented with a
menu from which you can select what to do. Type `quit program` to exit the program.

###Dictionary
The dictionary it uses comes embedded in the program, it is loaded in a case-insensitive manner.
The dictionary removes all duplicates when it is loaded.