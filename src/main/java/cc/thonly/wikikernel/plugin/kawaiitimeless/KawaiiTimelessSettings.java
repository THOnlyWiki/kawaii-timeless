package cc.thonly.wikikernel.plugin.kawaiitimeless;

/**
 * Kawaii-Timeless 皮肤的可配置项（站点设置键，存在 {@code wiki_setting}）：
 * 「特殊:权限配置 → 站点设置」里的分区由 {@link KawaiiTimelessSettingsSection} 渲染；
 * LocalSettings.yaml 里对应 {@code wikikernel.skins.kawaii-timeless.*}（见 {@link KawaiiTimelessYamlSettings}，
 * 每次启动导入一次，与 core 的站点设置是同一批键）。
 */
public final class KawaiiTimelessSettings {

    private KawaiiTimelessSettings() {
    }

    /** 左栏站点 logo 图片 URL；留空 = 不显示 logo（{@code #p-logo} 整块隐藏）。 */
    public static final String LOGO_KEY = "kawaii-timeless.logo";

    /**
     * 顶栏站名（wordmark）：可填**文字**（如「东方Project线下活动维基」）或**图片 URL**
     * （以 {@code /} 或 {@code http(s)://} 开头）；留空 = 站点名称。
     * 图片版与原皮肤一致带 {@code wordmark} 类，文字版超 13 字自动用 {@code long} 小字号。
     */
    public static final String WORDMARK_KEY = "kawaii-timeless.wordmark";

    /** 旧的 wordmark 形态取值：{@code svg} = 内置动画 SVG（等价于 {@link #DEFAULT_WORDMARK_IMAGE}）。 */
    public static final String LEGACY_WORDMARK_SVG = "svg";

    /** 旧的 wordmark 形态取值：{@code text} = 站点名称文字（等价于留空）。 */
    public static final String LEGACY_WORDMARK_TEXT = "text";

    /** 插件内置的动画 wordmark 图片（原站线上样式）。 */
    public static final String DEFAULT_WORDMARK_IMAGE = "/skins/kawaii-timeless/images/Wiki_Logo_Text_Animated.svg";

    /**
     * 顶栏头像图片 URL；留空 = 用当前用户自己的头像（未登录 / 无头像时用内置默认头像）。
     * 适用「站点统一头像」的场景（例如都显示站点吉祥物）。
     */
    public static final String HEADER_AVATAR_KEY = "kawaii-timeless.header-avatar";

    /** 值是否按图片 URL 处理：以 {@code /} 或 {@code http(s)://} 开头。 */
    public static boolean isImageUrl(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.startsWith("/") || trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }

    /** 归一化旧值：{@code svg} → 内置图片地址；{@code text} / 空白 → 空串（站点名称）；其它原样。 */
    public static String normalizeWordmark(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (LEGACY_WORDMARK_SVG.equalsIgnoreCase(trimmed)) {
            return DEFAULT_WORDMARK_IMAGE;
        }
        if (LEGACY_WORDMARK_TEXT.equalsIgnoreCase(trimmed)) {
            return "";
        }
        return trimmed;
    }
}
