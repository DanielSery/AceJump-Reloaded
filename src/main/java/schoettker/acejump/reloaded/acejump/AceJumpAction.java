package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schoettker.acejump.reloaded.acejump.actions.ActionPerformer;
import schoettker.acejump.reloaded.acejump.actions.SimpleJumpPerformer;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.command.TypeKeyAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.marker.MarkerCollection;
import schoettker.acejump.reloaded.acejump.marker.MarkersPanel;
import schoettker.acejump.reloaded.acejump.offsets.OffsetsFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordStartOffsetFinder;
import schoettker.acejump.reloaded.acejump.runnable.ShowMarkersRunnable;
import schoettker.acejump.reloaded.common.EmacsIdeasAction;
import schoettker.acejump.reloaded.util.EditorUtils;
import schoettker.acejump.reloaded.util.Str;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AceJumpAction extends EmacsIdeasAction {
    private MarkerCollection _markers;
    private ArrayList<MarkersPanel> _markersPanels;
    private KeyListener _jumpToMarkerKeyListener;
    private Stack<CommandAroundJump> _commandsAroundJump = new Stack<>();
    private static volatile AceJumpAction _instance;
    private ActionPerformer _actionsPerformer = new SimpleJumpPerformer();
    private OffsetsFinder _offsetsFinder = new WordStartOffsetFinder();
    private boolean _performingAction = false;

    @SuppressWarnings("WeakerAccess")
    public AceJumpAction() {
        _instance = this;
    }

    public void setOffsetsFinder(OffsetsFinder offsetsFinder) {
        _offsetsFinder = offsetsFinder;
    }

    public void setActionsPerformer(ActionPerformer actionsPerformer) {
        _actionsPerformer = actionsPerformer;
    }

    public void performAction(AnActionEvent e) {
        postAction(e);

        // show background on plugin activing
        Editor editor = getEditor();
        MarkersPanel markersPanel = new MarkersPanel(editor, getMarkerCollection());
        ArrayList<MarkersPanel> markersPanels = new ArrayList<>();
        markersPanels.add(markersPanel);
        showNewMarkersPanel(markersPanels);
    }

    public void continueAction() {
        _markers.clear();
        runReadAction(new ShowMarkersRunnable(findOffsetsInEditors(), (AceJumpAction) _action));
    }

    public void postAction(@NotNull AnActionEvent e) {
        Project p = getProjectFrom(e);

        if (!ToolWindowManager.getInstance(p).isEditorComponentActive()) {
            ToolWindowManager.getInstance(p).activateEditorComponent();
            return;
        }

        if (super.initAction(e)) {
            runReadAction(new ShowMarkersRunnable(findOffsetsInEditors(), (AceJumpAction) _action));
            _contentComponent.addKeyListener(_jumpToMarkerKeyListener);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project p = getProjectFrom(e);

        if (!ToolWindowManager.getInstance(p).isEditorComponentActive()) {
            ToolWindowManager.getInstance(p).activateEditorComponent();
            return;
        }

        if (super.initAction(e)) {
            runReadAction(new ShowMarkersRunnable(findOffsetsInEditors(), (AceJumpAction) _action));
            _contentComponent.addKeyListener(_jumpToMarkerKeyListener);
        }
    }

    private boolean handleJumpToMarkerKey(KeyEvent keyEvent) {
        char key = keyEvent.getKeyChar();
        if (!_markers.containsMarkerWithKey(key)) {
            key = Str.getCounterCase(key);
        }

        if (EditorUtils.isPrintableChar(key) && _markers.containsMarkerWithKey(key)) {
            ArrayList<JOffset> offsetsOfKey = _markers.getOffsetsOfKey(key);

            if (offsetsOfKey.size() > 1) {
                _markers.clear();
                runReadAction(new ShowMarkersRunnable(offsetsOfKey, (AceJumpAction) _action));
                return false;
            }

            _performingAction = true;
            boolean result = _actionsPerformer.performAction(offsetsOfKey.getFirst(), this, _commandsAroundJump);
            _performingAction = false;
            return result;
        }

        cleanupSetupsInAndBackToNormalEditingMode();
        return false;
    }

    public void handleAction(AnAction action, @Nullable InputEvent inputEvent) {
        if (_jumpToMarkerKeyListener != null && !_performingAction) {
            if (action.toString().equals("Escape (null)"))
                return;

            if (action.toString().startsWith("AceJumpWord"))
                return;

            _commandsAroundJump.add(new TypeKeyAfterJumpCommand(getEditor(), action, inputEvent));
        }
    }

    private KeyListener createJumpToMarkupKeyListener(final AnActionEvent e) {
        return new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {
                keyEvent.consume();
                boolean jumpFinished = handleJumpToMarkerKey(keyEvent);
                if (jumpFinished) {
                    _contentComponent.removeKeyListener(_jumpToMarkerKeyListener);
                    handlePendingActionOnSuccess();
                }
            }

            public void keyPressed(KeyEvent keyEvent) {
            }

            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cleanupSetupsInAndBackToNormalEditingMode();
                }
            }
        };
    }

    private List<JOffset> findOffsetsInEditors() {
        List<JOffset> jOffsets = new ArrayList<>();

        for (Editor editor : _editors) {
            List<Integer> offsets = _offsetsFinder.getOffsets(editor);
            for (Integer offset : offsets) {
                jOffsets.add(new JOffset(editor, offset));
            }
        }

        return jOffsets;
    }

    public void cleanupSetupsInAndBackToNormalEditingMode() {
        if (_jumpToMarkerKeyListener != null) {
            _contentComponent.removeKeyListener(_jumpToMarkerKeyListener);
            _jumpToMarkerKeyListener = null;
        }

        repaintParent();

        for (Editor editor : _editors) {
            editor.getComponent().repaint();
        }

        _commandsAroundJump = new Stack<>();
        super.cleanupSetupsInAndBackToNormalEditingMode();
    }

    protected void initMemberVariableForConvenientAccess(AnActionEvent e) {
        super.initMemberVariableForConvenientAccess(e);

        _markers = new MarkerCollection();
        _jumpToMarkerKeyListener = createJumpToMarkupKeyListener(e);
    }

    public void showNewMarkersPanel(ArrayList<MarkersPanel> markersPanels) {
        repaintParent();

        _markersPanels = markersPanels;

        for (MarkersPanel markersPanel : markersPanels) {
            markersPanel._editor.getContentComponent().add(markersPanel);
            markersPanel._editor.getContentComponent().repaint();
        }
    }

    private void repaintParent() {
        if (_markersPanels != null) {
            for (MarkersPanel markersPanel : _markersPanels) {
                Container parent = markersPanel.getParent();
                if (parent != null) {
                    parent.remove(markersPanel);
                    parent.repaint();
                }
            }
        }
    }

    public MarkerCollection getMarkerCollection() {
        return _markers;
    }

    public static AceJumpAction getInstance() {
        if (_instance == null) {
            _instance = new AceJumpAction();
        }
        return _instance;
    }

    public void addCommandAroundJump(CommandAroundJump commandAroundJump) {
        _commandsAroundJump.push(commandAroundJump);
    }
}
