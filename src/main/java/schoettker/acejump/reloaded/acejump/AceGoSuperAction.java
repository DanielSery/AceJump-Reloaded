package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.actions.SimpleJumpPerformer;
import schoettker.acejump.reloaded.acejump.command.TypeKeyAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.offsets.IdentifierStartOffsetFinder;

public class AceGoSuperAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AceJumpAction.getInstance().switchEditorIfNeed(e);
        AceJumpAction.getInstance().setOffsetsFinder(new IdentifierStartOffsetFinder());
        AceJumpAction.getInstance().setActionsPerformer(new SimpleJumpPerformer());
        AceJumpAction.getInstance().performAction(e);

        AceJumpAction.getInstance().addCommandAroundJump(new TypeKeyAfterJumpCommand(
                AceJumpAction.getInstance().getEditor(),
                IdeActions.ACTION_GOTO_SUPER
        ));
    }
}
