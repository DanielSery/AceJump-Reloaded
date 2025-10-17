package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.options.PluginConfig;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordEndOffsetFinder extends OffsetsFinder{
    @Override
    public List<Integer> getOffsets(Editor editor) {
        Document document = editor.getDocument();
        TextRange textRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> wordEndOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        if (Objects.requireNonNull(PluginConfig.getInstance().getState())._wordEndAddLineStart)
        {
            // Handle the first line (starting at position 0)
            int currentPos = 0;

            // Find first non-whitespace character in the first line
            while (currentPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(currentPos))) {
                currentPos++;
            }

            // If we found a non-whitespace character or reached the end, add this position
            if (currentPos < visibleText.length()) {
                wordEndOffsets.add(startOffset + currentPos);
            }

            // Process remaining lines after each '\n'
            for (int i = currentPos; i < visibleText.length(); i++) {
                char currentChar = visibleText.charAt(i);

                if (currentChar == '\n') {
                    wordEndOffsets.add(startOffset + i);

                    if (i + 1 < visibleText.length()) {
                        // Found a newline, check the next character
                        int lineStartPos = i + 1;

                        // Skip whitespace characters to find the first non-whitespace character
                        while (lineStartPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(lineStartPos))) {
                            lineStartPos++;
                        }

                        // If we found a non-whitespace character or reached the end, add this position
                        if (lineStartPos < visibleText.length()) {
                            wordEndOffsets.add(startOffset + lineStartPos);
                        }

                        i = lineStartPos;
                        continue;
                    }
                }

                // Check if current character is a word character (letter, digit, or underscore)
                if (isWord(currentChar)) {

                    // Check if this is the end of a word
                    boolean isWordEnd = (i == visibleText.length() - 1) || !isWord(visibleText.charAt(i + 1));
                    if (isWordEnd) {
                        wordEndOffsets.add(startOffset + i + 1);
                    }
                }
            }

            wordEndOffsets.add(visibleText.length());
        }
        else
        {
            for (int i = 0; i < visibleText.length(); i++) {
                char currentChar = visibleText.charAt(i);

                if (currentChar == '\n') {
                    wordEndOffsets.add(startOffset + i);
                }

                // Check if current character is a word character (letter, digit, or underscore)
                if (isWord(currentChar)) {

                    // Check if this is the end of a word
                    boolean isWordEnd = (i == visibleText.length() - 1) || !isWord(visibleText.charAt(i + 1));
                    if (isWordEnd) {
                        wordEndOffsets.add(startOffset + i + 1);
                    }
                }
            }

            wordEndOffsets.add(visibleText.length());
        }

        return wordEndOffsets;
    }

    private static final String WORD_CHARS = "\"'`";
    public boolean isWord(char c) {
        return Character.isJavaIdentifierPart(c) || WORD_CHARS.indexOf(c) >= 0;
    }
}