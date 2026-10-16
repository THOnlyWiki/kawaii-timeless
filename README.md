# Kawaii-Timeless（皮肤插件，**私有移植，不入 git**）

移植自音MAD中文维基（thonly.cc / OtomadWiki）的 MediaWiki **Timeless** 定制皮肤
（`skin.json` 里的名字：`Kawaii-Timeless-Citizen`），源码来自站点备份
`D:\wwwroot\THONLY_WIKI_BACKUP\skins\Timeless`（MediaWiki 1.39.8）。

> **私有**：本目录与构建产物都**不要提交进公开仓库**。
> `plugins/kawaii-timeless-*.jar` 已被仓库的 `.gitignore`（`/plugins/`）忽略；
> 本目录已加入本地 `.git/info/exclude`（不进仓库的本地忽略）。

## 结构

```text
plugin/kawaii-timeless/
  build-tools/compile-less.php         # 用原站同款 less.php 编译器把 LESS 编译成 CSS（一次性构建）
  src/main/java/.../KawaiiTimelessPlugin.java   # onStart: 注册消息包 / 皮肤 / 安装向导
  src/main/java/.../KawaiiTimelessSkin.java     # 皮肤：layout + 样式表 + 脚本
  src/main/java/.../KawaiiTimelessSetup.java    # /init 里可勾选「设为站点默认皮肤」
  src/main/java/.../KawaiiTimelessMessages.java # 消息包（i18n/messages*.properties）
  src/main/resources/templates/skins/kawaii-timeless/layout.html   # 布局（DOM 对齐 TimelessTemplate.php）
  src/main/resources/static/skins/kawaii-timeless/
    kawaii-timeless.css        # 编译产物（原 skin.json 两个模块的全部样式，含 @media print）
    kawaii-timeless-extra.css  # 手写补充：Material Icons 字体 + 分类栏/打印适配
    kawaii-timeless.js         # 原 resources/main.js（原样复制：下拉菜单、表格滚动、MDUI、滚动状态、控制台彩蛋）
    mobile.js                  # 原 resources/mobile.js（移动端弹出菜单）
    images/**.svg|png          # CSS 引用的全部图片（相对路径 images/… 原样可用）
    fonts/MaterialIcons-Regular.ttf  # 原站由站内资产提供的图标字体（#header-upload 等使用）
```

## 组件对应关系（原皮肤 → 本移植）

| 原皮肤（MediaWiki） | 本移植（WikiKernel） |
| --- | --- |
| `#mw-header-container/#mw-header` 顶栏 | 相同 DOM；`#p-banner` 显示站点名（`siteName`，>13 字自动加 `.long`） |
| `#user-tools` 用户栏（头像下拉、`#header-upload`） | 相同 DOM；头像用 `/img/avatar-default.svg`（用户页可用 `userAvatarUrl`），上传指向 `特殊:上传文件` |
| `#p-search`（`#simpleSearch`、`#searchButton`/`#mw-searchButton`） | 相同 DOM；表单提交到 `特殊:搜索?q=` |
| `#p-logo`（侧栏大 logo） | 用皮肤上下文的 `${skin.logoUrl}`（默认 `/img/wiki-logo.svg`，可换） |
| `#mw-page-header-links`（命名空间 / 更多 / 页面操作页签） | `p-namespaces`、`p-more`（`#ca-more`）、`p-views`：页面·讨论 / 阅读·编辑(源代码)·历史 / 监视（`#watch-toggle` 供 watch.js） |
| `#mw-site-navigation`：`#site-navigation` + `#site-tools` | `WikiKernel:Sidebar` 的分组渲染为 portlet；「工具」分组按文案拆分：上传文件/特殊页面→`#site-tools`，其余→右侧 |
| `#mw-related-navigation`：`page-tools` / 语言 / 分类 | `p-cactions`（移动·保护）、`p-userpagetools`（用户页）、`p-pagemisc`（链入页面…页面信息）、`#other-languages`（`languageLinks`）、`#catlinks-sidebar`（`pageCategories`） |
| `#mw-footer`（`#footer-icons` + `#footer-list/#footer-places`） | 相同 DOM；数据用 `footerLeftHtml/footerRightHtml/footerLastEdited` 与 core 的页脚片段 |
| `#menus-cover`、`.kawaii-header-background`、`.ts-container/.ts-inner` | 相同 DOM/类名，原 JS/CSS 的交互与视觉直接生效 |

## 重新编译样式（改了 LESS 或要换主题时）

```powershell
php plugin\kawaii-timeless\build-tools\compile-less.php
.\gradlew.bat :plugin:kawaii-timeless:jar installPlugins
# 重启 18080 后生效
```

- 编译器用的是备份站自带的 `vendor/wikimedia/less.php`（与线上 ResourceLoader 同款），
  `mediawiki.mixins` 从备份的 `resources/src/mediawiki.less` 解析；
- 媒体查询与原 `skin.json` 一一对应（desktop 851+/1340+/1100–1339/1100+/851–1099、
  mobile ≤850、misc、print），静态库（normalise/mdui-mini/nprogress/shared）原样拼接；
- `resources/themes/kawaii.less` 是默认主题，已被 `variables.less` 导入（粉色 kawaii 配色就来自它）。

## 与原站的已知差异

- logo/站点名/头像/页脚/分类/语言都接的是 WikiKernel 的数据源（原站是 MediaWiki 的 `$wgLogos`、
  自定义头像目录 `/images/avatars/…`、`MediaWiki:Sidebar` 等）；
- 「工具」分组的拆分按**本地化文案**匹配（上传文件/特殊页面/用户贡献/日志/…），
  侧栏页面若把这些条目改成了别的显示文字，会落回「更多」分组；
- Material Icons 字体随皮肤打包（原站由站内资产提供，备份里没有 @font-face）；
- `nprogress` 只保留样式（原站 `skins.timeless.js` 模块也没有加载它的 JS）；
- 正文底部的 core 分类栏在本皮肤下隐藏（分类改到右侧栏展示，与原皮肤一致）；
- **用户菜单按站点所有者要求与原站保持一致**：登录后只显示
  用户页 / 用户讨论 / 参数设置 / 监视列表 / 贡献 / 退出——不渲染 core 的「外观」入口；
  需要切换皮肤时直接访问 `特殊:外观`（或其它皮肤菜单里的入口）；
- **顶栏右侧按原站排布**：搜索框 → 头像（紧跟搜索框右边，不是最右）→ 个人工具图标组
  （Echo 插件注入的「提醒 / 一般通知」两个铃铛，与原站一致）→ 上传按钮（最右）。
  原皮肤 `#user-tools` 固定 14em 宽 + `row-reverse` + `#personal{margin-right:auto}` 就是这套排布，
  本皮肤保持原样；图标由 Echo 的 `echo.css` 指定（`echo.css` 选中 `#personal-extra .echo-entry`）。
  （social-profile 的「私信 / 好友 / 礼物」图标组已随 Echo 移植移除。）
- **页签与右侧栏按原站对齐**：监视星标放在「命名空间」组里（页面 / 讨论 之后，原皮肤
  `getPageTools()` 的行为）；「页面工具」组为 删除 / 移动 / 保护；「更多」组为
  链入页面 / 相关更改 / 可打印版 / 固定链接 / 页面信息 / **页面日志**
  （`特殊:日志?page=`）；「历史」页签用原皮肤文案「历史」（core 默认是「查看历史」）；
- 「删除」用 core 新增的 `特殊:删除页面`（`Special:DeletePage`）：确认页 + 删除理由，
  提交到 `POST /api/pages/delete`（软删除、写删除日志；受保护页面只有管理员能删）——
  与 action API 的 `action=delete` 行为一致；
- **desktop-small（851–1099px）修正**：原皮肤把「导航 / 页面工具」两个下拉块用
  `top:-2.35em` 定位到内容容器上沿，实际会钻进固定头部下面而不可见；
  本皮肤在 `kawaii-timeless-extra.css` 里改回显示在头部下方的导航条（与上游 Timeless 一致）。
- **顶栏高度与按压反馈对齐原站**（2026-10 复核）：
  - 未滚动时顶栏 68px（原皮肤 `#mw-header-container` 的固定高度）、向下滚动后由皮肤 JS 加
    `.scrolled` 收缩到 48px；本移植原先让 `<body>` 常驻 `scrolled`，顶栏永久 48px，
    而页面为固定顶栏预留 68px → 按钮看起来「贴顶、上下不居中」。现在 body 不常驻该类，
    并在 `kawaii-timeless-extra.css` 里显式给回 `#mw-header-container{height:68px}`（图标中心
    与线上一致：36px 用户栏在 68px 顶栏里居中，cy = 34px）；
  - 头像旁「提醒 / 一般通知」图标颜色按上传按钮的规则统一为 `color:#000; opacity:var(--opacity-icon-base)`
    （原皮肤 `#header-upload:after` 就是这么写的；此前铃铛是 `#54595d`，比上传图标更浅）；
  - 按下不放的**由里到外水波纹**（MDUI `mdui-ripple`）：原站 `main.js` 给
    `#mw-site-navigation li, #mw-related-navigation li, #personal li …` 一批元素加这个类；
    本移植在 `kawaii-timeless.js` 的同一行选择器里补了 `#personal-extra li`
    （Echo 的铃铛 / 托盘也要有同样的按压涟漪，上传按钮本来就在模板里带 `mdui-ripple`）；
- **界面外壳的链接悬停不加下划线**：core 的 `base.css` 有一条 vector 风格的通用
  `a:hover, a:focus { text-decoration: underline }`；原皮肤在侧栏 / 页签 / 顶栏 / 页脚都只换颜色
  （侧栏再加 `translateX(5px)` 位移，见原皮肤 `…sidebar-chunk li:hover a` 规则）。
  `kawaii-timeless-extra.css` 里对这些区域取消下划线，**正文（`#mw-content`）保持原样**。

### 原站有、WikiKernel 没有的组件（无法在皮肤层补）

- **引用本页**（Cite 扩展的 `特殊:引用此页面`）：WikiKernel 没有对应的特殊页；
- **评论控制**页签（原站评论扩展的 FlowThread 管理入口）：WikiKernel 的 comments 插件不提供页签；
- **ULS 语言选择器**（UniversalLanguageSelector）：WikiKernel 的语言切换在页脚 / 参数设置；
- 原站右侧边缘那两个浮动小图标疑似来自站内 `Common.js` / 小工具（不在皮肤 CSS 里）。

## 启用方式

- 预览：任意页面加 `?useskin=kawaii-timeless`；
- 用户级：`特殊:参数设置 → 外观`（或 `特殊:外观`）；
- 站点级：`/init` 的「KawaiiTimeless」tab 勾选默认皮肤，或 `特殊:权限配置 → 站点设置`
  （`wikikernel.skins.default-skin=kawaii-timeless`）。

## 与主仓库的关系

本仓库是 [WikiKernel](https://github.com/THOnlyWiki/WikiKernel) 的**子模块**
（主仓库路径 `plugin/kawaii-timeless`）：Kawaii-Timeless 皮肤是 Timeless 皮肤的现代化魔改移植，
随主仓库一起发布，也可单独作为皮肤插件安装（构建出的 jar 放进 WikiKernel 的 `plugins/`）。

适配 WikiKernel `0.0.1-SNAPSHOT`。

## 许可

GPL-3.0（与主仓库一致），见 [LICENSE](LICENSE)。
