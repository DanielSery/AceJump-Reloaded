package schoettker.acejump.reloaded.acejump.runnable;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import schoettker.acejump.reloaded.acejump.AceJumpAction;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.marker.MarkerCollection;
import schoettker.acejump.reloaded.acejump.marker.MarkersPanel;
import schoettker.acejump.reloaded.options.PluginConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowMarkersRunnable implements Runnable {
    private static final char INFINITE_JUMP_CHAR = '/';
    private final PluginConfig _config = PluginConfig.getInstance().getState();
    private final List<JOffset> _offsets;
    private final AceJumpAction _action;
    private final Editor _editor;
    private final ArrayList<Editor> _editors;
    private final MarkerCollection _markerCollection;

    private String getMarkerCharsets() {
        return  _config._markersCharsets;
    }

    public ShowMarkersRunnable(List<JOffset> offsets, AceJumpAction currentExecutingAction) {
        _offsets = offsets;
        _editor = currentExecutingAction.getEditor();
        _editors = currentExecutingAction.getEditors();
        this._action = currentExecutingAction;
        _markerCollection = _action.getMarkerCollection();
    }

    @Override
    public void run() {
        if (_offsets.isEmpty()) {
            return;
        }

        int caretOffset = _editor.getCaretModel().getOffset();
        sortOffsetsByDistanceToCaret(caretOffset);

        int twiceJumpGroupCount = calcTwiceJumpGroupCount();
        int singleJumpCount = Math.min(getMarkerCharsets().length() - twiceJumpGroupCount, _offsets.size());

        createSingleJumpMarkers(singleJumpCount);
        if (twiceJumpGroupCount > 0) {
            createMultipleJumpMarkers(singleJumpCount);
        }

        ArrayList<MarkersPanel> panels = new ArrayList<>();
        for (Editor editor : _editors) {
            MarkersPanel markersPanel = new MarkersPanel(editor, _markerCollection);
            panels.add(markersPanel);
        }

        _action.showNewMarkersPanel(panels);
    }

    private void createSingleJumpMarkers(int singleJumpCount) {
        for (int i = 0; i < singleJumpCount; i++) {
            String marker = String.valueOf(getMarkerCharsets().charAt(i));
            _markerCollection.addMarker(marker, _offsets.get(i));
        }
    }

    private void createMultipleJumpMarkers(int singleJumpCount) {
        int i = singleJumpCount;

        for (; i < _offsets.size(); i++) {
            int group = (i - singleJumpCount) / getMarkerCharsets().length();
            int markerCharIndex = singleJumpCount + group;

            if (markerCharIndex > getMarkerCharsets().length() - 1) {
                break;
            }

            char markerChar = getMarkerCharsets().charAt(markerCharIndex);
            char secondJumpMarkerChar = getMarkerCharsets().charAt((i - singleJumpCount) % getMarkerCharsets().length());

            String marker = "" + markerChar + secondJumpMarkerChar;
            _markerCollection.addMarker(marker, _offsets.get(i));
        }


        boolean hasMarkersNeedMoreJumps = i < _offsets.size();
        if (hasMarkersNeedMoreJumps) {
            for (; i < _offsets.size(); i++) {
                _markerCollection.addMarker(String.valueOf(INFINITE_JUMP_CHAR), _offsets.get(i));
            }
        }
    }

    private int calcTwiceJumpGroupCount() {
        int makerCharSetSize = getMarkerCharsets().length();

        for (int groupsNeedMultipleJump = 0; groupsNeedMultipleJump <= makerCharSetSize; groupsNeedMultipleJump++) {
            int oneJumpMarkerCount = makerCharSetSize - groupsNeedMultipleJump;
            if (groupsNeedMultipleJump * makerCharSetSize + oneJumpMarkerCount >= _offsets.size()) {
                return groupsNeedMultipleJump;
            }
        }

        return makerCharSetSize;
    }

    private void sortOffsetsByDistanceToCaret(final int caretOffset) {
        _offsets.sort((joA, joB) -> {
            if (joA.editor != joB.editor) {
                return joA.editor.hashCode() - joB.editor.hashCode();
            }

            int oA = joA.offset;
            int oB = joB.offset;

            int distA = Math.abs(oA - caretOffset);
            int distB = Math.abs(oB - caretOffset);

            if (distA == distB) {
                return oA - oB;
            }

            return distA - distB;
        });
    }
}
