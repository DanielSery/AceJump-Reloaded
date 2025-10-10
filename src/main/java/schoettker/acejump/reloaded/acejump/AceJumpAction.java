package schoettker.acejump.reloaded.acejump;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import schoettker.acejump.reloaded.acejump.command.CommandAroundJump;
import schoettker.acejump.reloaded.acejump.command.SelectAfterJumpCommand;
import schoettker.acejump.reloaded.acejump.marker.JOffset;
import schoettker.acejump.reloaded.acejump.marker.MarkerCollection;
import schoettker.acejump.reloaded.acejump.marker.MarkersPanel;
import schoettker.acejump.reloaded.acejump.offsets.CharOffsetsFinder;
import schoettker.acejump.reloaded.acejump.offsets.OffsetsFinder;
import schoettker.acejump.reloaded.acejump.offsets.WordOffsetsFinder;
import schoettker.acejump.reloaded.acejump.runnable.JumpRunnable;
import schoettker.acejump.reloaded.acejump.runnable.ShowMarkersRunnable;
import schoettker.acejump.reloaded.common.EmacsIdeasAction;
import schoettker.acejump.reloaded.util.EditorUtils;
import schoettker.acejump.reloaded.util.Str;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AceJumpAction extends EmacsIdeasAction {
    private MarkerCollection _markers;
    private ArrayList<MarkersPanel> _markersPanels;
    private KeyListener _showMarkersKeyListener;
    private KeyListener _jumpToMarkerKeyListener;
    private Stack<CommandAroundJump> _commandsAroundJump = new Stack<>();
    private static volatile AceJumpAction _instance;
    private boolean _isCalledFromOtherAction; //TODO
    private OffsetsFinder _offsetsFinder = new WordOffsetsFinder();

    @SuppressWarnings("WeakerAccess")
    public AceJumpAction() {
        _instance = this;
    }

    void performAction(AnActionEvent e) {
        _offsetsFinder = new CharOffsetsFinder();
        _isCalledFromOtherAction = true;
        postAction(e);

        // show background on plugin activing
        Editor editor = getEditor();// 或者getEditors()？
        MarkersPanel markersPanel = new MarkersPanel(editor, getMarkerCollection());
        _markersPanels = new ArrayList<>();
        _markersPanels.add(markersPanel);
        showNewMarkersPanel(_markersPanels);
    }

    public void postAction(@NotNull AnActionEvent e) {
        if (isCalledFromOtherAction()) {
            _offsetsFinder = new CharOffsetsFinder();
        }

        Project p = getProjectFrom(e);

        if (!ToolWindowManager.getInstance(p).isEditorComponentActive()) {
            ToolWindowManager.getInstance(p).activateEditorComponent();
            return;
        }

        if (super.initAction(e)) {
            _contentComponent.addKeyListener(_showMarkersKeyListener);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (isCalledFromOtherAction()) {
            _offsetsFinder = new CharOffsetsFinder();
        }

        Project p = getProjectFrom(e);

        if (!ToolWindowManager.getInstance(p).isEditorComponentActive()) {
            ToolWindowManager.getInstance(p).activateEditorComponent();
            return;
        }

        if (super.initAction(e)) {
            _contentComponent.addKeyListener(_showMarkersKeyListener);
        }
    }

    private boolean handleShowMarkersKey(KeyEvent keyEvent) {
        char key = keyEvent.getKeyChar();
        if (EditorUtils.isPrintableChar(key)) {
            runReadAction(new ShowMarkersRunnable(getOffsetsOfCharInVisibleArea(key), (AceJumpAction) _action));

            if (_markers.hasNoPlaceToJump()) {
                cleanupSetupsInAndBackToNormalEditingMode();
                return false;
            }

            if (_isCalledFromOtherAction && _markers.hasOnlyOnePlaceToJump()) {
                jumpToOffset(_markers.getFirstOffset());
                return false;
            }

            _contentComponent.addKeyListener(_jumpToMarkerKeyListener);
            return true;
        }

        cleanupSetupsInAndBackToNormalEditingMode();
        return false;
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

            jumpToOffset(offsetsOfKey.getFirst());
            return true;
        }

        cleanupSetupsInAndBackToNormalEditingMode();
        return false;
    }

    private KeyListener createShowMarkersKeyListener() {
        return new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {
                keyEvent.consume();
                boolean showMarkersFinished = handleShowMarkersKey(keyEvent);
                if (showMarkersFinished) {
                    _contentComponent.removeKeyListener(_showMarkersKeyListener);
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

    private KeyListener createJumpToMarkupKeyListener(final AnActionEvent e) {
        return new KeyListener() {
            public void keyTyped(KeyEvent keyEvent) {
                keyEvent.consume();
                if (keyEvent.isShiftDown()) {
                    addCommandAroundJump(new SelectAfterJumpCommand(_editor));
                }

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

    private List<JOffset> getOffsetsOfCharInVisibleArea(char key) {
        if (_markers.get(String.valueOf(key)) != null) {
            return _markers.get(String.valueOf(key)).getOffsets();
        }

        return findOffsetsInEditors(key);
    }

    private List<JOffset> findOffsetsInEditors(char key) {
        List<JOffset> joffsets = new ArrayList<>();

        for (Editor editor : _editors) {
            List<Integer> offsets = _offsetsFinder.getOffsets(key, editor, _editor);
            for (Integer offset : offsets) {
                joffsets.add(new JOffset(editor, offset));
            }
        }

        return joffsets;
    }

    private void jumpToOffset(final JOffset jumpOffset) {
        for (CommandAroundJump cmd : _commandsAroundJump) {
            cmd.beforeJump(jumpOffset);
        }

        ApplicationManager.getApplication().runReadAction(new JumpRunnable(jumpOffset, this));

        for (CommandAroundJump cmd : _commandsAroundJump) {
            cmd.preAfterJump(jumpOffset);
            cmd.afterJump();
        }

        cleanupSetupsInAndBackToNormalEditingMode();
    }

    public void cleanupSetupsInAndBackToNormalEditingMode() {
        if (_showMarkersKeyListener != null) {
            _contentComponent.removeKeyListener(_showMarkersKeyListener);
            _showMarkersKeyListener = null;
        }

        if (_jumpToMarkerKeyListener != null) {
            _contentComponent.removeKeyListener(_jumpToMarkerKeyListener);
            _jumpToMarkerKeyListener = null;
        }

        repaintParent();

        for (Editor editor : _editors) {
            editor.getComponent().repaint();
        }

        _commandsAroundJump = new Stack<>();
        _offsetsFinder = new WordOffsetsFinder();
        _isCalledFromOtherAction = false;
        super.cleanupSetupsInAndBackToNormalEditingMode();
    }

    protected void initMemberVariableForConvenientAccess(AnActionEvent e) {
        super.initMemberVariableForConvenientAccess(e);

        _markers = new MarkerCollection();
        _showMarkersKeyListener = createShowMarkersKeyListener();
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

    static AceJumpAction getInstance() {
        if (_instance == null) {
            _instance = new AceJumpAction();
        }
        return _instance;
    }

    void addCommandAroundJump(CommandAroundJump commandAroundJump) {
        _commandsAroundJump.push(commandAroundJump);
    }

    private boolean isCalledFromOtherAction() {
        return _isCalledFromOtherAction;
    }
}
