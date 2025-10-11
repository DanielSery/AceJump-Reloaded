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

    private final String _actionId;

    public TypeKeyAfterJumpCommand(Editor editor, String actionId) {
        super(editor);
        _actionId = actionId;
    }

    @Override
    public void beforeJump(final JOffset jumpTargetOffset) {
        super.beforeJump(jumpTargetOffset);
    }

    @Override
    public void afterJump() {
        dispatchEvent(_actionId);
    }
}