package cc.thonly.wikikernel.plugin.kawaiitimeless;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LocalSettings.yaml 的 Kawaii-Timeless 配置（{@code wikikernel.skins.kawaii-timeless.*}）：
 *
 * <pre>
 * wikikernel:
 *   skins:
 *     default-skin: kawaii-timeless
 *     kawaii-timeless:
 *       logo: /skins/kawaii-timeless/images/Touhou_Activities_Wiki_Logo.svg   # 左栏 logo 图片 URL；留空 = 不显示
 *       wordmark: 东方Project线下活动维基                                        # 顶栏站名：文字或图片 URL；留空 = 站点名称
 *       header-avatar: /skins/kawaii-timeless/images/avatar.svg                # 默认头像 URL；留空 = 内置默认头像
 * </pre>
 *
 * 启动时（{@code onStart}）把非 null 的值导入站点设置（与 core 的 yaml 导入同一约定：
 * 文件里写了的每次启动覆盖一次，没写的键不动）；之后在「特殊:权限配置 → 站点设置」改的是同一批键。
 * {@code header-avatar} 是旧字段名，现在写入 core 的共享键 {@code site.default-avatar}（用户页 / 评论等共用）。
 */
@Component
@ConfigurationProperties(prefix = "wikikernel.skins.kawaii-timeless")
public class KawaiiTimelessYamlSettings {

    /** 顶栏站名（wordmark）：文字或图片 URL；null = 不改站点设置。 */
    private String wordmark;

    /** 左栏站点 logo 图片 URL；null = 不改站点设置（空串 = 隐藏 logo）。 */
    private String logo;

    /** 默认头像图片 URL（旧字段名，兼容已有配置）；null = 不改站点设置（空串 = 内置默认头像）。 */
    private String headerAvatar;

    public String getWordmark() {
        return wordmark;
    }

    public void setWordmark(String wordmark) {
        this.wordmark = wordmark;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getHeaderAvatar() {
        return headerAvatar;
    }

    public void setHeaderAvatar(String headerAvatar) {
        this.headerAvatar = headerAvatar;
    }
}
