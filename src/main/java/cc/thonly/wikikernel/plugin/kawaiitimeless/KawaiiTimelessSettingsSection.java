package cc.thonly.wikikernel.plugin.kawaiitimeless;

import cc.thonly.wikikernel.common.extension.SettingField;
import cc.thonly.wikikernel.common.extension.SiteSettingsSection;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;

/**
 * Kawaii-Timeless 皮肤在「站点设置」页里的分区（{@code 特殊:权限配置 → 站点设置}）：
 * 字段键 = {@link KawaiiTimelessSettings} 使用的站点设置键（与 LocalSettings.yaml
 * {@code wikikernel.skins.kawaii-timeless.*} 是同一批键）；core 只负责渲染 / 保存。
 */
public class KawaiiTimelessSettingsSection implements SiteSettingsSection {

    private final MessageSource messages;

    public KawaiiTimelessSettingsSection(MessageSource messages) {
        this.messages = messages;
    }

    @Override
    public String id() {
        return "kawaii-timeless";
    }

    @Override
    public String title() {
        return text("settings.kawaiiTimeless");
    }

    @Override
    public String description() {
        return text("settings.kawaiiTimelessHint");
    }

    @Override
    public int order() {
        return 150;
    }

    @Override
    public List<SettingField> fields() {
        return List.of(
                SettingField.text(KawaiiTimelessSettings.LOGO_KEY, text("settings.kawaiiTimelessLogo"), ""),
                SettingField.text(KawaiiTimelessSettings.WORDMARK_KEY, text("settings.kawaiiTimelessWordmark"), ""),
                SettingField.text(KawaiiTimelessSettings.DEFAULT_AVATAR_KEY,
                        text("settings.kawaiiTimelessHeaderAvatar"), ""));
    }

    /** 取本地化文案；漏翻时由 core 的消息源回退成翻译键本身。 */
    private String text(String key) {
        if (messages == null) {
            return key;
        }
        return messages.getMessage(key, new Object[0], LocaleContextHolder.getLocale());
    }
}
