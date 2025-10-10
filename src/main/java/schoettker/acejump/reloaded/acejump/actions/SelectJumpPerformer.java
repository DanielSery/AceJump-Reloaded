package schoettker.acejump.reloaded.acejump.actions;

import com.intellij.openapi.application.ApplicationManager;
import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;
import schoettker.acejump.reloaded.acejump.runnable.JumpRunnable;

import java.util.Stack;

public class SelectJumpPerformer extends ActionPerformer{
    @Override
    public boolean performAction(JOffset offset, AceJumpAction aceJumpAction, Stack<CommandAroundJump> commandsAround) {
        ApplicationManager.getApplication().runReadAction(new JumpRunnable(offset, aceJumpAction));

        AceJumpAction.getInstance().setOffsetsFinder(new WordEndOffsetFinder());
        AceJumpAction.getInstance().setActionsPerformer(new SimpleJumpPerformer());
        AceJumpAction.getInstance().continueAction();
        return false;
    }
}
