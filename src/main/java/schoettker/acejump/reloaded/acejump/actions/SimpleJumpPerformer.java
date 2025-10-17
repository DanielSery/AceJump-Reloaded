package schoettker.acejump.reloaded.acejump.actions;

import com.intellij.openapi.application.ApplicationManager;
import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.runnable.JumpRunnable;

import java.util.Stack;

public class SimpleJumpPerformer extends ActionPerformer{

    @Override
    public boolean performAction(JOffset offset, AceJumpAction aceJumpAction, Stack<CommandAroundJump> commandsAround) {
        for (CommandAroundJump cmd : commandsAround) {
            cmd.beforeJump(offset);
        }

        ApplicationManager.getApplication().runReadAction(new JumpRunnable(offset));

        for (CommandAroundJump cmd : commandsAround) {
            cmd.preAfterJump(offset);
            cmd.afterJump();
        }

        AceJumpAction.getInstance().cleanupSetupsInAndBackToNormalEditingMode();
        return true;
    }
}
