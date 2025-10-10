package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;

public class WordEndOffsetFinder extends OffsetsFinder{
    @Override
    public List<Integer> getOffsets(Editor editor) {
        Document document = editor.getDocument();
        TextRange textRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> wordEndOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        for (int i = 0; i < visibleText.length(); i++) {
            char currentChar = visibleText.charAt(i);

            if (currentChar == '\n') {
                wordEndOffsets.add(startOffset + i);
            }

            // Check if current character is a word character (letter, digit, or underscore)
            if (Character.isJavaIdentifierStart(currentChar) || Character.isJavaIdentifierPart(currentChar)) {

                // Check if this is the end of a word
                boolean isWordEnd = (i == visibleText.length() - 1) ||
                        !(Character.isJavaIdentifierStart(visibleText.charAt(i + 1)) ||
                                Character.isJavaIdentifierPart(visibleText.charAt(i + 1)));

                if (isWordEnd) {
                    wordEndOffsets.add(startOffset + i + 1);
                }
            }
        }

        return wordEndOffsets;
    }
}
