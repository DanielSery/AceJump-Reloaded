package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.command.SelectAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.offsets.WordEndOffsetFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordStartOffsetFinder;

public class AceJumpWordEndAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AceJumpAction.getInstance().switchEditorIfNeed(e);
        AceJumpAction.getInstance().setOffsetsFinder(new WordEndOffsetFinder());
        AceJumpAction.getInstance().performAction(e);

//        Editor editor = e.getData(PlatformDataKeys.EDITOR);
//        AceJumpAction.getInstance().addCommandAroundJump(new SelectAfterJumpCommand(editor));
    }
}
