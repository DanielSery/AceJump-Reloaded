package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.options.PluginConfig;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordStartOffsetFinder extends OffsetsFinder{

    @Override
    public List<Integer> getOffsets(Editor editor) {
        Document document = editor.getDocument();
        TextRange textRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> wordStartOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        char previousChar = ' ';
        if (Objects.requireNonNull(PluginConfig.getInstance().getState())._wordStartAddLineEnd)
        {
            // Handle the first line (starting at position 0)
            int currentPos = 0;

            // Find first non-whitespace character in the first line
            while (currentPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(currentPos))) {
                currentPos++;
            }

            // If we found a non-whitespace character or reached the end, add this position
            if (currentPos < visibleText.length()) {
                wordStartOffsets.add(startOffset + currentPos);
            }

            // Process remaining lines after each '\n'
            for (int i = currentPos; i < visibleText.length(); i++) {
                char currentChar = visibleText.charAt(i);

                if (currentChar == '\n') {
                    wordStartOffsets.add(startOffset + i);

                    if (i + 1 < visibleText.length()) {
                        // Found a newline, check the next character
                        int lineStartPos = i + 1;

                        // Skip whitespace characters to find the first non-whitespace character
                        while (lineStartPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(lineStartPos))) {
                            lineStartPos++;
                        }

                        // If we found a non-whitespace character or reached the end, add this position
                        if (lineStartPos < visibleText.length()) {
                            wordStartOffsets.add(startOffset + lineStartPos);
                        }

                        previousChar = visibleText.charAt(lineStartPos);
                        i = lineStartPos;
                        continue;
                    }
                }

                // Check if current character is a word character (letter or digit)
                if (isWord(currentChar)) {

                    // Check if this is the start of a word
                    boolean isWordStart = (i == 0) || !isWord(previousChar);
                    if (isWordStart) {
                        wordStartOffsets.add(startOffset + i);
                    }
                }

                previousChar = currentChar;
            }

            wordStartOffsets.add(visibleText.length());
        }
        else
        {
            // Handle the first line (starting at position 0)
            int currentPos = 0;

            // Find first non-whitespace character in the first line
            while (currentPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(currentPos))) {
                currentPos++;
            }

            // If we found a non-whitespace character or reached the end, add this position
            if (currentPos < visibleText.length()) {
                wordStartOffsets.add(startOffset + currentPos);
            }

            // Process remaining lines after each '\n'
            for (int i = currentPos; i < visibleText.length(); i++) {
                char currentChar = visibleText.charAt(i);

                if (currentChar == '\n' && i + 1 < visibleText.length()) {
                    // Found a newline, check the next character
                    int lineStartPos = i + 1;

                    // Skip whitespace characters to find the first non-whitespace character
                    while (lineStartPos < visibleText.length() && Character.isWhitespace(visibleText.charAt(lineStartPos))) {
                        lineStartPos++;
                    }

                    // If we found a non-whitespace character or reached the end, add this position
                    if (lineStartPos < visibleText.length()) {
                        wordStartOffsets.add(startOffset + lineStartPos);
                    }

                    previousChar = visibleText.charAt(lineStartPos);
                    i = lineStartPos;
                    continue;
                }

                // Check if current character is a word character (letter or digit)
                if (isWord(currentChar)) {

                    // Check if this is the start of a word
                    boolean isWordStart = (i == 0) || !isWord(previousChar);
                    if (isWordStart) {
                        wordStartOffsets.add(startOffset + i);
                    }
                }

                previousChar = currentChar;
            }
        }

        return wordStartOffsets;
    }

    private static final String WORD_CHARS = "\"@'`";
    public boolean isWord(char c) {
        return Character.isJavaIdentifierPart(c) || WORD_CHARS.indexOf(c) >= 0;
    }
}