package schoettker.acejump.reloaded.acejump.actions;

import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.marker.JOffset;

import java.util.Stack;

public abstract class ActionPerformer {
    public abstract boolean performAction(JOffset offset, AceJumpAction aceJumpAction, Stack<CommandAroundJump> commandsAround);
}
