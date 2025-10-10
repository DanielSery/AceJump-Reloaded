package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import schoettker.acejump.reloaded.util.EditorUtils;

import java.util.ArrayList;
import java.util.List;

public class OffsetsFinder {
    public List<Integer> getOffsets(char key, Editor editor, Editor selectedEditor) {
        Document document = editor.getDocument();
        TextRange visibleRange = EditorUtils.getVisibleTextRange(editor);

        List<Integer> offsets = getWordEndOffsets(visibleRange, document);
        addOffsetsOfChar(visibleRange.getStartOffset(), '\n', document.getText(visibleRange), editor, offsets);
        return offsets;
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

    private void addOffsetsOfChar(int startOffset, char c, String visibleText,
                                                Editor editor, List<Integer> offsets) {
        int caretOffset = editor.getCaretModel().getOffset();

        int index = visibleText.indexOf(c);
        while (index >= 0) {
            int offset = startOffset + index;

            if (isValidOffset(c, visibleText, index, offset, caretOffset)) {
                offsets.add(offset);
            }

            index = visibleText.indexOf(c, index + 1);
        }
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

    /**
     * Gets the offsets where words end within the visible text range.
     * A word end is defined as the position of the last character of a word,
     * where words are sequences of alphanumeric characters and underscores separated by whitespace or punctuation.
     *
     * @param textRange the text range to search within
     * @param document the document to search in
     * @return List of offsets where words end
     */
    public List<Integer> getWordEndOffsets(TextRange textRange, Document document) {
        List<Integer> wordEndOffsets = new ArrayList<>();
        String visibleText = document.getText(textRange);
        int startOffset = textRange.getStartOffset();

        for (int i = 0; i < visibleText.length(); i++) {
            char currentChar = visibleText.charAt(i);

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
