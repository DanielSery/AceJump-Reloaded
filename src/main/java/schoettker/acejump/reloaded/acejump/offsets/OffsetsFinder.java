package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.options.PluginConfig;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class OffsetsFinder {
    public List<Integer> getOffsets(char key, Editor editor, Editor selectedEditor) {
        Document document = editor.getDocument();
        TextRange visibleRange = EditorUtils.getVisibleTextRange(editor);

        return getWordStartOffsets(visibleRange, document);
//        List<Integer> offsets = getOffsetsOfCharIgnoreCase(String.valueOf(key), visibleRange, document, editor, selectedEditor);
//
//        if (key == KeyEvent.VK_SPACE) {
//            offsets.addAll(getOffsetsOfCharIgnoreCase("\t\r\n", visibleRange, document, editor, selectedEditor));
//            addStartLineOffsetsTo(offsets, editor);
//        } else if (key == ',') {
//            offsets.addAll(getOffsetsOfCharIgnoreCase("|`/\\;.{}()[]<>?_=-+'\"!@#$%^&*", visibleRange, document, editor, selectedEditor));
//        }
//
//        return offsets;
    }

    private void addStartLineOffsetsTo(List<Integer> offsets, Editor editor) {
        ArrayList<Integer> visibleLineStartOffsets = EditorUtils.getVisibleLineStartOffsets(editor);
        for (Integer i : visibleLineStartOffsets) {
            if (!offsets.contains(i)) {
                offsets.add(i);
            }
        }
    }

    private ArrayList<Integer> getOffsetsOfCharIgnoreCase(String charSet, TextRange markerRange, Document document, Editor editor, Editor selectedEditor) {
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        String visibleText = document.getText(markerRange);

        for (char charToFind : charSet.toCharArray()) {
            char lowCase = Character.toLowerCase(charToFind);
            char upperCase = Character.toUpperCase(charToFind);

            offsets.addAll(getOffsetsOfChar(markerRange.getStartOffset(), lowCase, visibleText, editor, selectedEditor));
            if (upperCase != lowCase) {
                offsets.addAll(getOffsetsOfChar(markerRange.getStartOffset(), upperCase, visibleText, editor, selectedEditor));
            }
        }

        return offsets;
    }

    private ArrayList<Integer> getOffsetsOfChar(int startOffset, char c, String visibleText, Editor editor, Editor selectedEditor) {
        int caretOffset = editor.getCaretModel().getOffset();
        ArrayList<Integer> offsets = new ArrayList<>();

        int index = visibleText.indexOf(c);
        while (index >= 0) {
            int offset = startOffset + index;

            if (isValidOffset(c, visibleText, index, offset, caretOffset)) {
                offsets.add(offset);
            }

            index = visibleText.indexOf(c, index + 1);
        }

        return offsets;
    }

    protected boolean isValidOffset(char c, String visibleText, int index, int offset, int caretOffset) {
        return true;
    }

    /**
     * Gets the offsets where words start within the visible text range.
     * A word start is defined as the position of the first character of a word,
     * where words are sequences of alphanumeric characters separated by whitespace or punctuation.
     *
     * @param textRange the text range to search within
     * @param document the document to search in
     * @return List of offsets where words start
     */
    public List<Integer> getWordStartOffsets(TextRange textRange, Document document) {
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
