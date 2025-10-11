package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.actions.SelectJumpPerformer;
import schoettker.acejump.reloaded.acejump.command.SelectAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.command.TypeKeyAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordStartOffsetFinder;

public class AceSelectDeleteAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        AceJumpAction.getInstance().switchEditorIfNeed(e);
        AceJumpAction.getInstance().setOffsetsFinder(new WordStartOffsetFinder());
        AceJumpAction.getInstance().setActionsPerformer(new SelectJumpPerformer(new WordEndOffsetFinder()));
        AceJumpAction.getInstance().performAction(e);

        AceJumpAction.getInstance().addCommandAroundJump(new SelectAfterJumpCommand(
                AceJumpAction.getInstance().getEditor()
        ));

        AceJumpAction.getInstance().addCommandAroundJump(new TypeKeyAfterJumpCommand(
                AceJumpAction.getInstance().getEditor(),
                IdeActions.ACTION_EDITOR_DELETE
        ));
    }
}