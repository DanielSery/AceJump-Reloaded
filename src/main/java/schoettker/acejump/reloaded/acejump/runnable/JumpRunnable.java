package schoettker.acejump.reloaded.acejump.runnable;

import schoettker.acejump.reloaded.acejump.marker.JOffset;

public class JumpRunnable implements Runnable {

    private final JOffset _offsetToJump;


    public JumpRunnable(JOffset _offsetToJump) {
        this._offsetToJump = _offsetToJump;
    }

    @Override
    public void run() {
        _offsetToJump.editor.getContentComponent().requestFocus();
        _offsetToJump.editor.getCaretModel().moveToOffset(_offsetToJump.offset);
        _offsetToJump.editor.getSelectionModel().removeSelection();
    }
}
