package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.actions.SelectJumpPerformer;
import schoettker.acejump.reloaded.acejump.command.SelectAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.offsets.LineMarksOffsetFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordStartOffsetFinder;

public class AceLineSelectAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        AceJumpAction.getInstance().switchEditorIfNeed(e);
        AceJumpAction.getInstance().setOffsetsFinder(new LineMarksOffsetFinder());
        AceJumpAction.getInstance().setActionsPerformer(new SelectJumpPerformer(new LineMarksOffsetFinder()));
        AceJumpAction.getInstance().performAction(e);

        AceJumpAction.getInstance().addCommandAroundJump(new SelectAfterJumpCommand(
                AceJumpAction.getInstance().getEditor()
        ));
    }
}
