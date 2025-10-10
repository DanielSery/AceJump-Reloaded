package schoettker.acejump.reloaded.acejump.command;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import schoettker.acejump.reloaded.acejump.marker.JOffset;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Command that types a specific key character into the editor after performing a jump operation.
 * This command extends the base CommandAroundJump functionality to provide key typing behavior
 * that occurs after the caret has been moved to the target location.
 */
public class TypeKeyAfterJumpCommand extends CommandAroundJump {

    private final AnAction _action;
    private final InputEvent _inputEvent;

    public TypeKeyAfterJumpCommand(Editor editor, AnAction action, InputEvent inputEvent) {
        super(editor);
        _inputEvent = inputEvent;
        _action = action;
    }

    @Override
    public void beforeJump(final JOffset jumpTargetOffset) {
        super.beforeJump(jumpTargetOffset);
    }

    @Override
    public void afterJump() {
        dispatchEvent(_action, _inputEvent);
    }
}