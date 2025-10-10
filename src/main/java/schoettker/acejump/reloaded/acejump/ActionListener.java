package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import org.jetbrains.annotations.NotNull;

public class ActionListener implements AnActionListener {

    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        AceJumpAction.getInstance().handleAction(action, event.getInputEvent());
    }
}
