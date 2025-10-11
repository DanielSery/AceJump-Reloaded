package schoettker.acejump.reloaded.acejump.command;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.editor.Editor;
import schoettker.acejump.reloaded.acejump.marker.JOffset;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CommandAroundJump {
    private final Editor _se; /*source editor*/
    private int _soff;

    private Editor _te; /*target editor*/
    private int _toff;

    CommandAroundJump(Editor editor) {
        _se = editor;
    }

    public void beforeJump(final JOffset jumpTargetOffset) {
        _soff = _se.getCaretModel().getOffset();
    }

    public void preAfterJump(final JOffset jumpTargetOffset) {
        _te = jumpTargetOffset.editor;
        _toff = jumpTargetOffset.offset;
    }

    public void afterJump() {
    }

    void selectJumpArea() {
        if (inSameEditor()) {
            if (_soff < _toff)
                _se.getSelectionModel().setSelection(_soff, _toff);
            else
                _se.getSelectionModel().setSelection(_toff, _soff);
        }
    }

    void dispatchEvent(String actionId) {
        ActionManager.getInstance().tryToExecute(
                ActionManager.getInstance().getAction(actionId),
                new KeyEvent(_te.getContentComponent(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '\n'),
                _te.getContentComponent(),
                null,
                true
        );
    }

    private boolean inSameEditor() {
        return _se == _te;
    }
}

