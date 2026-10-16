/* 以下内容均从https://en.wikipedia.org/wiki/MediaWiki:Common.js导入 */
/**
 * Keep code in MediaWiki:Common.js to a minimum as it is unconditionally
 * loaded for all users on every wiki page. If possible create a gadget that is
 * enabled by default instead of adding it here (since gadgets are fully
 * optimized ResourceLoader modules with possibility to add dependencies etc.)
 *
 * Since Common.js isn't a gadget, there is no place to declare its
 * dependencies, so we have to lazy load them with mw.loader.using on demand and
 * then execute the rest in the callback. In most cases these dependencies will
 * be loaded (or loading) already and the callback will not be delayed. In case a
 * dependency hasn't arrived yet it'll make sure those are loaded before this.
 */

/* global mw, $ */
/* jshint strict:false, browser:true */

var loaded = false;

if (mw.config.get('wgPageName') === '近期活动（东方市场）') {
    importScript( 'MediaWiki:RecentActivities.js' );
}

importScript( 'MediaWiki:Node.js' );

mw.loader.using( [ 'mediawiki.util' ] ).done( function () {
    $(document).ready(function() {
        const observer = new MutationObserver(function() {
            if (!$('body').hasClass('scrolled')) {
                $('body').addClass('scrolled');
            }
        });

        const config = { attributes: true, childList: true, subtree: true };
    
        observer.observe(document.body, config);
    });




    /**
     * Collapsible tables; reimplemented with mw-collapsible
     * Styling is also in place to avoid FOUC
     *
     * Allows tables to be collapsed, showing only the header. See [[Help:Collapsing]].
     * @version 3.0.0 (2018-05-20)
     * @source https://www.mediawiki.org/wiki/MediaWiki:Gadget-collapsibleTables.js
     * @author [[User:R. Koot]]
     * @author [[User:Krinkle]]
     * @author [[User:TheDJ]]
     * @deprecated Since MediaWiki 1.20: Use class="mw-collapsible" instead which
     * is supported in MediaWiki core. Shimmable since MediaWiki 1.32
     *
     * @param {jQuery} $content
     */
    function makeCollapsibleMwCollapsible( $content ) {
        var $tables = $content
            .find( 'table.collapsible:not(.mw-collapsible)' )
            .addClass( 'mw-collapsible' );

        $.each( $tables, function ( index, table ) {
            // mw.log.warn( 'This page is using the deprecated class collapsible. Please replace it with mw-collapsible.');
            if ( $( table ).hasClass( 'collapsed' ) ) {
                $( table ).addClass( 'mw-collapsed' );
                // mw.log.warn( 'This page is using the deprecated class collapsed. Please replace it with mw-collapsed.');
            }
        } );
        if ( $tables.length > 0 ) {
            mw.loader.using( 'jquery.makeCollapsible' ).then( function () {
                $tables.makeCollapsible();
            } );
        }
    }
    mw.hook( 'wikipage.content' ).add( makeCollapsibleMwCollapsible );

    // 查找所有包含特定结构的 <ul> 元素
    document.querySelectorAll('ul').forEach(function(ulElement) {
        // 检查每个 <ul> 元素下的子元素是否匹配特定结构
        var lis = ulElement.querySelectorAll('li');
        var isMatched = false;

        lis.forEach(function(liElement) {
            // 检查 li 元素的类名和子元素的结构是否符合预期
            var hasClass_nvView = liElement.classList.contains('nv-view');
            var anchor = liElement.querySelector('a');
            var href = anchor ? anchor.getAttribute('href') : '';

            // 根据具体需求匹配 href 或其他条件
            // 这里可以根据 href 的内容进行更详细的匹配
            if (hasClass_nvView && href.includes('/index.php?title=%E6%A8%A1%E6%9D%BF:Navbox/doc')) {
                isMatched = true;
            }
        });

        // 如果匹配到了特定结构，删除整个 <ul> 元素
        if (isMatched) {
            ulElement.remove();
        }
    });


    /**
     * Add support to mw-collapsible for autocollapse, innercollapse and outercollapse
     *
     * Maintainers: TheDJ
     */
    function mwCollapsibleSetup( $collapsibleContent ) {
        var $element,
            $toggle,
            autoCollapseThreshold = 2;
        $.each( $collapsibleContent, function ( index, element ) {
            $element = $( element );
            if ( $element.hasClass( 'collapsible' ) ) {
                $element.find( 'tr:first > th:first' ).prepend( $element.find( 'tr:first > * > .mw-collapsible-toggle' ) );
            }
            if ( $collapsibleContent.length >= autoCollapseThreshold && $element.hasClass( 'autocollapse' ) ) {
                $element.data( 'mw-collapsible' ).collapse();
            } else if ( $element.hasClass( 'innercollapse' ) ) {
                if ( $element.parents( '.outercollapse' ).length > 0 ) {
                    $element.data( 'mw-collapsible' ).collapse();
                }
            }
            // because of colored backgrounds, style the link in the text color
            // to ensure accessible contrast
            $toggle = $element.find( '.mw-collapsible-toggle' );
            if ( $toggle.length ) {
                // Make the toggle inherit text color
                if ( $toggle.parent()[ 0 ].style.color ) {
                    $toggle.find( 'a' ).css( 'color', 'inherit' );
                }
            }
        } );
    }

    mw.hook( 'wikipage.collapsibleContent' ).add( mwCollapsibleSetup );

    /**
     * Dynamic Navigation Bars (experimental)
     *
     * Description: See [[Wikipedia:NavFrame]].
     * Maintainers: UNMAINTAINED
     */

    var collapseCaption = '隐藏';
    var expandCaption = '显示';

    // Set up the words in your language
    var navigationBarHide = '[' + collapseCaption + ']';
    var navigationBarShow = '[' + expandCaption + ']';

    /**
     * Shows and hides content and picture (if available) of navigation bars.
     *
     * @param {number} indexNavigationBar The index of navigation bar to be toggled
     * @param {jQuery.Event} event Event object
     * @return {boolean}
     */
    function toggleNavigationBar( indexNavigationBar, event ) {
        var navToggle = document.getElementById( 'NavToggle' + indexNavigationBar );
        var navFrame = document.getElementById( 'NavFrame' + indexNavigationBar );
        var navChild;

        if ( !navFrame || !navToggle ) {
            return false;
        }

        // If shown now
        if ( navToggle.firstChild.data === navigationBarHide ) {
            for ( navChild = navFrame.firstChild; navChild !== null; navChild = navChild.nextSibling ) {
                if ( $( navChild ).hasClass( 'NavContent' ) ) {
                    navChild.style.display = 'none';
                }
            }
            navToggle.firstChild.data = navigationBarShow;

            // If hidden now
        } else if ( navToggle.firstChild.data === navigationBarShow ) {
            for ( navChild = navFrame.firstChild; navChild !== null; navChild = navChild.nextSibling ) {
                if ( $( navChild ).hasClass( 'NavContent' ) ) {
                    navChild.style.display = 'block';
                }
            }
            navToggle.firstChild.data = navigationBarHide;
        }

        event.preventDefault();
    }
    /* End of mw.loader.using callback */
} );
/* DO NOT ADD CODE BELOW THIS LINE */