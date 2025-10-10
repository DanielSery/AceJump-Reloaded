package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.actions.SimpleJumpPerformer;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;

public class AceJumpWordEndAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AceJumpAction.getInstance().switchEditorIfNeed(e);
        AceJumpAction.getInstance().setOffsetsFinder(new WordEndOffsetFinder());
        AceJumpAction.getInstance().setActionsPerformer(new SimpleJumpPerformer());
        AceJumpAction.getInstance().performAction(e);
    }
}
