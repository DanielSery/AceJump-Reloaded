package me.ihxq.acejump.reloaded.common.predictor;

import me.ihxq.acejump.reloaded.util.Chars;

public class SymbolPredictor extends Predictor<Character> {

    @Override
    public boolean is(Character character) {
        return Chars.isSymbol(character);
    }
}
