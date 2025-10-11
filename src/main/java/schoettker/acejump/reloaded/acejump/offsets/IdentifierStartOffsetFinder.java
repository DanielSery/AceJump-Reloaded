package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;

public class IdentifierStartOffsetFinder extends OffsetsFinder{

    @Override
    public List<Integer> getOffsets(Editor editor) {
        Document document = editor.getDocument();
        TextRange textRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> wordStartOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        char previousChar = ' ';
        for (int i = 0; i < visibleText.length(); i++) {
            char currentChar = visibleText.charAt(i);

            // Check if current character is a word character (letter or digit)
            if (Character.isJavaIdentifierStart(currentChar)) {

                // Check if this is the start of a word
                boolean isWordStart = (i == 0) || !Character.isJavaIdentifierStart(previousChar);
                if (isWordStart) {
                    wordStartOffsets.add(startOffset + i);
                }
            }

            previousChar = currentChar;
        }

        return wordStartOffsets;
    }
}