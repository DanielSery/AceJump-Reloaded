package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;

public class LineEndsOffsetFinder extends OffsetsFinder{

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
        }

        wordEndOffsets.add(visibleText.length());
        return wordEndOffsets;
    }
}