package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class AceJumpCharAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AceJumpAction.getInstance().performAction(e);
    }
}

