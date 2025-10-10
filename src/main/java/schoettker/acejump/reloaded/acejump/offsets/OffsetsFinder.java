package schoettker.acejump.reloaded.acejump.offsets;

import com.intellij.openapi.editor.Editor;

import java.util.List;

public abstract class OffsetsFinder {

    public abstract List<Integer> getOffsets(Editor editor);
}
