package cc.thonly.wikikernel.plugin.kawaiitimeless;

import cc.thonly.wikikernel.common.extension.SetupContext;
import cc.thonly.wikikernel.common.extension.SetupField;
import cc.thonly.wikikernel.common.extension.SetupResult;
import cc.thonly.wikikernel.common.extension.SetupStep;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Map;

/**
 * Kawaii-Timeless 皮肤插件的安装向导入口（{@code /init} 里的「KawaiiTimeless」tab）：
 * 勾选后把站点默认皮肤设为 {@code kawaii-timeless}（写入 {@code wikikernel.skins.default-skin}）。
 * <p>
 * tab 文案由 core 直接渲染（没有模板消息键），所以这里用插件自己的消息包取本地化文案
 * （键前缀 {@code setup.kawaii-timeless.}，见 {@code i18n/messages*.properties}）。
 */
public class KawaiiTimelessSetup implements SetupStep {

    private final MessageSource messageSource;

    public KawaiiTimelessSetup(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String id() {
        return "kawaii-timeless";
    }

    @Override
    public String displayName() {
        return "KawaiiTimeless";
    }

    @Override
    public String description() {
        return message("setup.kawaii-timeless.description");
    }

    @Override
    public int order() {
        return 90;
    }

    @Override
    public List<SetupField> fields() {
        return List.of(SetupField.checkbox("default", message("setup.kawaii-timeless.default"), false));
    }

    @Override
    public SetupResult apply(Map<String, String> values, SetupContext context) {
        if ("true".equalsIgnoreCase(values.getOrDefault("default", "false"))) {
            context.putGlobal("wikikernel.skins.default-skin", "kawaii-timeless");
            return SetupResult.ok(message("setup.kawaii-timeless.savedDefault"));
        }
        return SetupResult.ok(message("setup.kawaii-timeless.savedKept"));
    }

    /** 取本地化文案；没有消息源（或漏翻）时显示翻译键本身（与 core 的消息源一致，不回落中文）。 */
    private String message(String key) {
        if (messageSource == null) {
            return key;
        }
        return messageSource.getMessage(key, new Object[0], LocaleContextHolder.getLocale());
    }
}
