
2024-04-10
 - Worked on connecting LRParser, Grammer and RangeTokenizer.
 - Specifically, I'm adding token parsering to RangeTokenizer so the LRParser can get a LocateableList<Token>.
 - Also, I've added the RangeTokenizer and ShiftReducer to the Grammer since it's needed in parsing.
 - Eventually, we might make a Language that is a list of LanguageLayers and a RangeTokenizer. Each LanguageLayer is a list of Langauge components. This would improve the separation of logic. Eventually we'd have one LRParser per layer and the initial range tokenization would be outside the LR parser.