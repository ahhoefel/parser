2024-07-21
 - LayeredParser is done.
 - Next, I'd like actions to apply to arbitrary objects rather than Locatables. It would be nice for actions to respect whatever type is underneath, and not need to know if they are locatable. This also means tests can work on strings and lists rather than tokens and LocatableLists.
 - Separately, parser2 should be migrated to this dependency.
 - Language idea for interfaces:
   - Instead of vtables, use "interfaces" which are structs of bound methods.
   - Have automatic coersion from objects to interfaces which extract the require methods and binds them.
   - This covers the polymorphic case where you want a collection of interfaces and call the same method on different types of objects.
   - For the case where you want to pass different types of objects to a function, that can be covered by the compiler in a variety of ways:
      - multiple instances of the function for the different types passed, determined by static analysis.
      - a labelled union could be passed by the compiler and different types would be treated by implicit case statements
      - interfaces could be passed
    - Can we make labelled unions easier to use by lifting common methods? 

2024-07-18
 - Following on the notes below, I changed the Tokenizer to be generic and RangeTokenizer to implement Tokenizer<Token<String>>.
 - The LayeredParser needs a recursive generic structure to be able to provide different outputs at different Layers.
    - Note, this is almost done, but needs testing.

2024-05-23
 - Worked on tests for the LayeredParser.
 - Tokens are now generic containers, rather than String containers.
 - The current issue is a mix of types returned by actions, the lexer, and the parser layers.
 - Actions process Locateables. The lexer (Tokenizer) returns a LocatableList of tokens. Parser layers currently require Iterators of Tokens which are not Locatable. These all need to be agree.
    - Ideas.
        - Ideally you could have tuples and combine each with different actions. This would eliminate the need for exposing the locatable on everything.
        - The iterables could be made locatable.
        - We could 
 - In future, it would be nice to generate the next token on demand by the iterator. Though this means consuming the iterator is required to see parse errors.

2024-05-22
 - Worked on adding a LayeredParser.
 - This involve separating the lexing from the LRParser and grammer and replacing it with a terminal symbol table.
 - The work was left where a decision has to be made about what type of iterator of tokens is passed between the layers. Ideally we'd abstract out the location and the symbol and have a general payload rather than the string payload currently on the Token. See the LRParser errors for where to continue.

2024-04-10
 - Worked on connecting LRParser, Grammer and RangeTokenizer.
 - Specifically, I'm adding token parsering to RangeTokenizer so the LRParser can get a LocateableList<Token>.
 - Also, I've added the RangeTokenizer and ShiftReducer to the Grammer since it's needed in parsing.
 - Eventually, we might make a Language that is a list of LanguageLayers and a RangeTokenizer. Each LanguageLayer is a list of Langauge components. This would improve the separation of logic. Eventually we'd have one LRParser per layer and the initial range tokenization would be outside the LR parser.