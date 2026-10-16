<?php
/**
 * Kawaii-Timeless 样式编译脚本（一次性构建用，不进 git）。
 *
 * 用 MediaWiki 1.39 备份自带的 vendor/wikimedia/less.php（与原站 ResourceLoader
 * 同一个 LESS 编译器）把 skins/Timeless 的样式链编译成一份 CSS：
 *   - 每个 LESS 文件单独编译（与原站每个文件的 media 声明对应）；
 *   - 按 skin.json 的媒体查询把各块包进 @media 里；
 *   - 静态库（normalise / mdui / nprogress / shared）原样拼接；
 *   - 图片引用保持 "images/..." 相对路径（运行时放同目录 images/ 下）。
 *
 * 用法：php compile-less.php
 */

$BACKUP = 'D:/wwwroot/THONLY_WIKI_BACKUP';
$SKIN   = $BACKUP . '/skins/Timeless';
$RES    = $SKIN . '/resources';
$OUT    = __DIR__ . '/../src/main/resources/static/skins/kawaii-timeless/kawaii-timeless.css';

require $BACKUP . '/vendor/wikimedia/less.php/lib/Less/Autoloader.php';
Less_Autoloader::register();

/**
 * 编译单个 LESS 文件；import 解析路径 = 皮肤 resources/ + MediaWiki core 的 mediawiki.less。
 */
function compile_less(string $file, string $res, string $backup): string {
    $parser = new Less_Parser([
        'compress'     => false,
        'relativeUrls' => false,
    ]);
    // import 解析路径（path => uri_root；uri_root 留空，url() 保持原样）：
    // 相对自己的 import + 皮肤根 + MediaWiki core 的 mediawiki.less（@import 'mediawiki.mixins'）
    $parser->SetImportDirs([
        str_replace('\\', '/', dirname($file))                              => '',
        str_replace('\\', '/', $res)                                        => '',
        str_replace('\\', '/', $backup . '/resources/src/mediawiki.less')   => '',
    ]);
    $parser->parseFile(str_replace('\\', '/', $file));
    return trim($parser->getCss());
}

/** 拼一块 CSS；$media 非空时包进 @media。 */
function block(string $css, string $media = ''): string {
    $css = trim($css);
    if ($css === '') {
        return '';
    }
    if ($media !== '') {
        return "@media {$media} {\n{$css}\n}\n";
    }
    return $css . "\n";
}

function raw_css(string $file): string {
    return rtrim(file_get_contents($file)) . "\n";
}

$parts = [];
$parts[] = "/* ==========================================================================\n" .
           "   Kawaii-Timeless（移植自 MediaWiki skins/Timeless，OtomadWiki 定制版）\n" .
           "   由 build-tools/compile-less.php 生成（less.php 编译 LESS + 静态库拼接）：\n" .
           "   顺序与媒体查询对应原站 skin.json 的 skins.timeless / skins.timeless.js 模块。\n" .
           "   ========================================================================== */\n";

// ---- skin.json skins.timeless（styles，按声明顺序） ----
$parts[] = raw_css("$RES/libraries/normalise.css");
$parts[] = raw_css("$RES/libraries/mdui-mini.css");
$parts[] = raw_css("$RES/libraries/nprogress.min.css");
$parts[] = raw_css("$RES/libraries/shared.css");
$parts[] = block(compile_less("$RES/content.media-common.less", $RES, $BACKUP), '');
$parts[] = block(compile_less("$RES/forms.less", $RES, $BACKUP), 'screen');
$parts[] = block(compile_less("$RES/content.media-screen.less", $RES, $BACKUP), 'screen');
$parts[] = block(compile_less("$RES/screen-common.less", $RES, $BACKUP), 'screen');
// ---- skins.timeless.js 的样式 ----
$parts[] = block(compile_less("$RES/main.js.less", $RES, $BACKUP), 'screen');
$parts[] = block(compile_less("$RES/screen-desktop.less", $RES, $BACKUP), 'screen and (min-width: 851px)');
$parts[] = block(compile_less("$RES/screen-desktop-full.less", $RES, $BACKUP), 'screen and (min-width: 1340px)');
$parts[] = block(compile_less("$RES/screen-desktop-mid.less", $RES, $BACKUP), 'screen and (min-width: 1100px) and (max-width: 1339px)');
$parts[] = block(compile_less("$RES/screen-desktop-midup.less", $RES, $BACKUP), 'screen and (min-width: 1100px)');
$parts[] = block(compile_less("$RES/screen-desktop-small.less", $RES, $BACKUP), 'screen and (min-width: 851px) and (max-width: 1099px)');
$parts[] = block(compile_less("$RES/screen-mobile.less", $RES, $BACKUP), 'screen and (max-width: 850px)');
$parts[] = block(compile_less("$RES/screen-misc.less", $RES, $BACKUP), '');

// ---- 打印（原本是独立 media=print 的样式块） ----
$print = '';
$print .= block(compile_less("$RES/libraries/common-print.less", $RES, $BACKUP), '');
$print .= raw_css("$RES/print.css");
$print .= block(compile_less("$RES/content.media-print.less", $RES, $BACKUP), '');
$parts[] = "@media print {\n{$print}}\n";

$css = implode("\n", array_filter($parts, static fn ($p) => $p !== ''));
file_put_contents($OUT, $css);
echo 'written: ' . realpath($OUT) . ' (' . strlen($css) . " bytes)\n";
