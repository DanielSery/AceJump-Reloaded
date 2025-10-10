package schoettker.acejump.reloaded.acejump.command;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import schoettker.acejump.reloaded.acejump.marker.JOffset;

import java.awt.event.InputEvent;

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

    void dispatchEvent(AnAction action, InputEvent inputEvent) {
        ActionManager.getInstance().tryToExecute(
                action,
                inputEvent,
                _se.getContentComponent(),
                null,
                true
        );
    }

    private boolean inSameEditor() {
        return _se == _te;
    }
}

