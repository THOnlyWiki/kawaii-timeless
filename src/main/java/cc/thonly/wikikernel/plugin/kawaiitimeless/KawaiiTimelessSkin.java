package cc.thonly.wikikernel.plugin.kawaiitimeless;

import cc.thonly.wikikernel.common.extension.Skin;
import cc.thonly.wikikernel.common.service.SiteConfig;

import java.util.List;

/**
 * Kawaii-Timeless 皮肤（本插件注册）：移植自 MediaWiki skins/Timeless（音MAD中文维基定制版）。
 * <p>
 * 资源全部在插件 jar 内：布局 {@code templates/skins/kawaii-timeless/layout.html}，
 * 样式 {@code /skins/kawaii-timeless/kawaii-timeless.css}（less.php 编译产物 + 手写补充样式），
 * 脚本 {@code kawaii-timeless.js}（原 main.js）与 {@code mobile.js}（原移动端菜单脚本）。
 * 名称 / 说明由 core 按消息键 {@code skin.kawaii-timeless.name} / {@code skin.kawaii-timeless.description}
 * 取本地化文案（见 {@link KawaiiTimelessMessages}）。
 */
public class KawaiiTimelessSkin implements Skin {

    private final SiteConfig siteConfig;

    public KawaiiTimelessSkin() {
        this(null);
    }

    public KawaiiTimelessSkin(SiteConfig siteConfig) {
        this.siteConfig = siteConfig;
    }

    @Override
    public String id() {
        return "kawaii-timeless";
    }

    @Override
    public String displayName() {
        return "Kawaii-Timeless";
    }

    @Override
    public String description() {
        return "Customized Timeless skin (kawaii theme) ported from the OtomadWiki; "
                + "layout, compiled styles and scripts ship inside the plugin jar";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public List<String> stylesheets() {
        // 主样式 = 原站 skin.json 两个模块的编译产物；extra = Material Icons 字体与 WikiKernel 适配；
        // editor = CodeMirror / 编辑工具栏 / 源码视图的皮肤主题
        return List.of(
                "/skins/kawaii-timeless/kawaii-timeless.css",
                "/skins/kawaii-timeless/kawaii-timeless-extra.css",
                "/skins/kawaii-timeless/kawaii-timeless-editor.css");
    }

    @Override
    public List<String> scripts() {
        return List.of(
                "/skins/kawaii-timeless/kawaii-timeless.js",
                "/skins/kawaii-timeless/mobile.js");
    }

    /** 通过公共 Skin 接口暴露同一份站点 Logo，独立应用也能沿用；空表示隐藏。 */
    @Override
    public String logoUrl() {
        if (siteConfig == null) { return Skin.super.logoUrl(); }
        String logo = siteConfig.get(KawaiiTimelessSettings.LOGO_KEY, "");
        return logo == null ? "" : logo.trim();
    }

    @Override
    public int order() {
        // 排在 plain（50）之前：皮肤列表里更靠前
        return 40;
    }
}
