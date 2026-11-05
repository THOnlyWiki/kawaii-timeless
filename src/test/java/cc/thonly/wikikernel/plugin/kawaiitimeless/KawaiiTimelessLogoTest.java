package cc.thonly.wikikernel.plugin.kawaiitimeless;

import cc.thonly.wikikernel.common.service.SiteConfig;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class KawaiiTimelessLogoTest {
    @Test void commonSkinLogoFollowsLiveSiteSettingIncludingHiddenLogo() {
        Map<String,String> settings = new HashMap<>();
        SiteConfig config = new SiteConfig() {
            @Override public String get(String key,String fallback) { return settings.getOrDefault(key,fallback); }
            @Override public boolean getBoolean(String key,boolean fallback) { return fallback; }
        };
        var skin = new KawaiiTimelessSkin(config);
        settings.put(KawaiiTimelessSettings.LOGO_KEY,"/files/actual-wiki-logo.svg");
        assertEquals("/files/actual-wiki-logo.svg",skin.logoUrl());
        settings.put(KawaiiTimelessSettings.LOGO_KEY,""); assertEquals("",skin.logoUrl());
    }
}
