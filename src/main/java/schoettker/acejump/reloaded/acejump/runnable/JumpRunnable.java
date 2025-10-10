package schoettker.acejump.reloaded.acejump.runnable;

import com.intellij.openapi.editor.Document;
import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.options.PluginConfig;

public class JumpRunnable implements Runnable {

    private JOffset _offsetToJump;
    @SuppressWarnings("FieldCanBeLocal")
    private AceJumpAction _action;
    private final PluginConfig _config = PluginConfig.getInstance().getState();


    public JumpRunnable(JOffset _offsetToJump, AceJumpAction _action) {
        this._offsetToJump = _offsetToJump;
        this._action = _action;
    }

    @Override
    public void run() {
        _offsetToJump.editor.getContentComponent().requestFocus();
        _offsetToJump.editor.getCaretModel().moveToOffset(_offsetToJump.offset);
        _offsetToJump.editor.getSelectionModel().removeSelection();
    }

    private boolean isLineEndOrLineStartOffset(int offset) {
        Document _document = _offsetToJump.editor.getDocument();
        int lineNumber = _document.getLineNumber(offset);
        int lineStartOffset = _document.getLineStartOffset(lineNumber);
        int lineEndOffset = _document.getLineEndOffset(lineNumber);
        return lineEndOffset == _offsetToJump.offset || lineStartOffset == _offsetToJump.offset;
    }
}
