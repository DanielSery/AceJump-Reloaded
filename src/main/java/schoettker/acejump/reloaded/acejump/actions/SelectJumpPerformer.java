package schoettker.acejump.reloaded.acejump.actions;

import com.intellij.openapi.application.ApplicationManager;
import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.offsets.OffsetsFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;
import schoettker.acejump.reloaded.acejump.runnable.JumpRunnable;

import java.util.Stack;

public class SelectJumpPerformer extends ActionPerformer{
    private final OffsetsFinder _nextOffsetFinder;

    public SelectJumpPerformer(OffsetsFinder nextOffsetFinder) {
        _nextOffsetFinder = nextOffsetFinder;
    }

    @Override
    public boolean performAction(JOffset offset, AceJumpAction aceJumpAction, Stack<CommandAroundJump> commandsAround) {
        ApplicationManager.getApplication().runReadAction(new JumpRunnable(offset, aceJumpAction));

        AceJumpAction.getInstance().setOffsetsFinder(_nextOffsetFinder);
        AceJumpAction.getInstance().setActionsPerformer(new SimpleJumpPerformer());
        AceJumpAction.getInstance().continueAction();
        return false;
    }
}
