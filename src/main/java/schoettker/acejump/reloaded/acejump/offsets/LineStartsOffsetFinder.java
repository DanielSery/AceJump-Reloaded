package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;

public class LineStartsOffsetFinder extends OffsetsFinder {

    @Override
    public List<Integer> getOffsets(Editor editor) {
        Document document = editor.getDocument();
        TextRange textRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> lineStartOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        // Handle the first line (starting at position 0)
        int currentPos = 0;

        // Find first non-whitespace character in the first line
        while (currentPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(currentPos))) {
            currentPos++;
        }

        // If we found a non-whitespace character or reached the end, add this position
        if (currentPos < visibleText.length()) {
            lineStartOffsets.add(startOffset + currentPos);
        }

        // Process remaining lines after each '\n'
        for (int i = currentPos; i < visibleText.length(); i++) {
            if (visibleText.charAt(i) == '\n' && i + 1 < visibleText.length()) {
                // Found a newline, check the next character
                int lineStartPos = i + 1;

                // Skip whitespace characters to find the first non-whitespace character
                while (lineStartPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(lineStartPos))) {
                    lineStartPos++;
                }

                // If we found a non-whitespace character or reached the end, add this position
                if (lineStartPos < visibleText.length()) {
                    lineStartOffsets.add(startOffset + lineStartPos);
                }
            }
        }

        return lineStartOffsets;
    }
}
