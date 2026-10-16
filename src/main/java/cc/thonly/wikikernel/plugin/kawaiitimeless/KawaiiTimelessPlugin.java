package cc.thonly.wikikernel.plugin.kawaiitimeless;

import cc.thonly.wikikernel.common.extension.MessageBundle;
import cc.thonly.wikikernel.common.extension.SetupStep;
import cc.thonly.wikikernel.common.extension.SiteSettingsSection;
import cc.thonly.wikikernel.common.extension.Skin;
import cc.thonly.wikikernel.common.plugin.PluginContext;
import cc.thonly.wikikernel.common.plugin.PluginDescriptor;
import cc.thonly.wikikernel.common.plugin.WikiPlugin;
import cc.thonly.wikikernel.common.service.SiteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Kawaii-Timeless 皮肤插件：向 core 注册 {@code kawaii-timeless} 皮肤，
 * 皮肤资源全部随插件 jar 分发（布局、编译好的样式、脚本与图片）：
 * <ul>
 *     <li>{@code templates/skins/kawaii-timeless/layout.html}：皮肤布局
 *     （DOM 结构与 MediaWiki skins/Timeless 的 TimelessTemplate 对齐，由 PluginTemplateResolver 解析）；</li>
 *     <li>{@code static/skins/kawaii-timeless/…}：由 core 的 {@code /skins/kawaii-timeless/…} 端点读取
 *     （kawaii-timeless.css 用原站同款 less.php 编译 LESS 生成，见 {@code build-tools/compile-less.php}）；</li>
 *     <li>{@code context.register(Skin.class, ...)}：注册皮肤；</li>
 *     <li>{@code context.register(SiteSettingsSection.class, ...)}：顶栏站名（wordmark）形态等设置
 *     （{@link KawaiiTimelessSettings}；LocalSettings.yaml 里是 {@code wikikernel.skins.kawaii-timeless.*}）。</li>
 * </ul>
 * 本地化文案在插件自己的消息包里（{@link KawaiiTimelessMessages}）。
 */
public class KawaiiTimelessPlugin implements WikiPlugin {

    @Autowired
    private MessageSource messageSource;

    /** 站点设置读取桥（core 提供）：把 yaml 里写了的 wordmark 设置导入站点设置。 */
    @Autowired
    private SiteConfig siteConfig;

    /** LocalSettings.yaml 的 {@code wikikernel.skins.kawaii-timeless.*}。 */
    @Autowired
    private KawaiiTimelessYamlSettings yamlSettings;

    @Override
    public PluginDescriptor descriptor() {
        return new PluginDescriptor("kawaii-timeless", "KawaiiTimeless", "1.0.0",
                "Kawaii-Timeless skin plugin: port of the customized Timeless skin "
                        + "(layout, compiled styles and scripts ship inside the plugin jar)");
    }

    @Override
    public void onStart(PluginContext context) {
        context.register(MessageBundle.class, new KawaiiTimelessMessages());
        context.register(Skin.class, new KawaiiTimelessSkin());
        context.register(SetupStep.class, new KawaiiTimelessSetup(messageSource));
        context.register(SiteSettingsSection.class, new KawaiiTimelessSettingsSection(messageSource));
        importYamlSettings();
        context.logger().log(System.Logger.Level.INFO, "已注册插件皮肤 kawaii-timeless（Kawaii-Timeless）");
    }

    /** yaml 里写了的 logo / wordmark 设置每次启动导入一次（与 core 的站点设置是同一批键）。 */
    private void importYamlSettings() {
        if (siteConfig == null || yamlSettings == null) {
            return;
        }
        if (yamlSettings.getWordmark() != null) {
            siteConfig.set(KawaiiTimelessSettings.WORDMARK_KEY,
                    KawaiiTimelessSettings.normalizeWordmark(yamlSettings.getWordmark()));
        }
        if (yamlSettings.getLogo() != null) {
            siteConfig.set(KawaiiTimelessSettings.LOGO_KEY, yamlSettings.getLogo().trim());
        }
        if (yamlSettings.getHeaderAvatar() != null) {
            siteConfig.set(KawaiiTimelessSettings.HEADER_AVATAR_KEY, yamlSettings.getHeaderAvatar().trim());
        }
    }
}
