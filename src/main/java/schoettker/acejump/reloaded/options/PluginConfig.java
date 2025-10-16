package schoettker.acejump.reloaded.options;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@State(
    name = "schoettker.acejump.reloaded.options.PluginConfig",
    storages = {@Storage(value = "AceActionsPlugin.xml")})

public class PluginConfig implements PersistentStateComponent<PluginConfig> {
  int _firstJumpBackground = new JBColor(new Color(0, 153, 255), new Color(0, 137, 230)).getRGB();
  int _firstJumpForeground = new JBColor(new Color(249, 255, 249), new Color(232, 232, 225)).getRGB();
  int _secondJumpBackground = new JBColor(new Color(0, 107, 179), new Color(0, 92, 153)).getRGB();
  int _secondJumpForeground = new JBColor(new Color(255, 248, 248), new Color(254, 253, 254)).getRGB();
  boolean _needSelectTextAfterJump = true;
  int _panelBackground = new JBColor(new Color(111, 110, 110), new Color(71, 71, 73)).getRGB();
  public boolean _toUpperCase = true;
  public boolean _wordStartAddLineEnd = false;
  public boolean _wordEndAddLineStart = false;
  public String _fontType = "Bold";
  public int _bgOpacity = 0;

  @SuppressWarnings("SpellCheckingInspection")
  public String _markersCharsets = "fjdksla;rueiwoqpvmc,x.z/ghtybn";

  public Color getFirstJumpBackground() {
    return new JBColor(new Color(_firstJumpBackground), new Color(_firstJumpBackground));
  }

  public Color getFirstJumpForeground() {
    return new JBColor(new Color(_firstJumpForeground), new Color(_firstJumpForeground));
  }

  public Color getSecondJumpBackground() {
    return new JBColor(new Color(_secondJumpBackground), new Color(_secondJumpBackground));
  }

  public Color getSecondJumpForeground() {
    return new JBColor(new Color(_secondJumpForeground), new Color(_secondJumpForeground));
  }

  public Color getPanelBackground() {
    return new JBColor(new Color(_panelBackground), new Color(_panelBackground));
  }

  public static PluginConfig getInstance() {
    return ApplicationManager.getApplication().getService(PluginConfig.class);
  }

  @Override
  public PluginConfig getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PluginConfig config) {
    XmlSerializerUtil.copyBean(config, this);
  }
}
