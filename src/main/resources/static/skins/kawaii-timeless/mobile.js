/* Popout menus (header) */

/* eslint-disable no-jquery/no-fade */

$( function () {
	var toggleTime = 200;
	var $cover = $( '#menus-cover' );
	// 移动端所有弹出菜单（头像 / 导航 / wiki工具 / 页面工具 / 其他语言）
	var menuSelector = '#personal-inner, .sidebar-inner';

	function isNarrow() {
		return $( window ).width() < 851;
	}

	function allMenus() {
		return $( menuSelector );
	}

	function isOpen( $menu ) {
		return $menu.hasClass( 'mobile-menu-open' );
	}

	/**
	 * 关闭菜单。
	 *
	 * 用 mobile-menu-open 记录状态，而不是用 :visible 判断：导航面板滑出后
	 * 仍可能是 display:flex（只是被 transform 移出屏幕），:visible 会误判为打开。
	 *
	 * @param {jQuery} $menu
	 */
	function hideMenu( $menu ) {
		$menu.removeClass( 'mobile-menu-open' )
			.stop( true, true )
			.fadeOut( toggleTime );
	}

	/**
	 * 打开菜单。
	 *
	 * 关键：先在同一帧里把 opacity 设为 0 再显示，不能让浏览器先按默认
	 * opacity:1 画出 display:flex 的第一帧——原来的
	 * fadeToggle().css('display', 'flex') 正是这样闪一下再从头淡入的。
	 *
	 * @param {jQuery} $menu
	 */
	function showMenu( $menu ) {
		allMenus().not( $menu ).each( function () {
			if ( isOpen( $( this ) ) ) {
				hideMenu( $( this ) );
			}
		} );
		$menu.stop( true, true )
			.addClass( 'mobile-menu-open' )
			.css( { display: 'flex', opacity: 0 } )
			.animate( { opacity: 1 }, toggleTime, function () {
				$( this ).css( 'opacity', '' );
			} );
	}

	/**
	 * 遮罩跟随“是否有菜单打开”，而不是每次点击都盲目 fadeToggle：
	 * 从一个菜单切到另一个菜单时，遮罩不应先淡出再淡入。
	 */
	function syncCover() {
		var anyOpen = allMenus().filter( '.mobile-menu-open' ).length > 0;

		if ( anyOpen ) {
			if ( !$cover.hasClass( 'mobile-cover-open' ) ) {
				$cover.addClass( 'mobile-cover-open' )
					.stop( true, true )
					.fadeIn( toggleTime );
			}
		} else if ( $cover.hasClass( 'mobile-cover-open' ) ) {
			$cover.removeClass( 'mobile-cover-open' )
				.stop( true, true )
				.fadeOut( toggleTime );
		}
	}

	/**
	 * @param {jQuery} $menu
	 */
	function toggleMenu( $menu ) {
		if ( !isNarrow() || !$menu.length ) {
			return;
		}

		if ( isOpen( $menu ) ) {
			hideMenu( $menu );
		} else {
			showMenu( $menu );
		}
		syncCover();
	}

	// Open the various menus.
	// 使用命名空间事件先解绑再绑定：即使脚本被重复加载，也不会一次点击触发两次。
	$( '#user-tools h2' ).off( 'click.kawaiiMobile' ).on( 'click.kawaiiMobile', function () {
		toggleMenu( $( '#personal-inner' ) );
	} );
	$( '#site-navigation h2' ).off( 'click.kawaiiMobile' ).on( 'click.kawaiiMobile', function () {
		toggleMenu( $( '#site-navigation .sidebar-inner' ) );
	} );
	$( '#site-tools h2' ).off( 'click.kawaiiMobile' ).on( 'click.kawaiiMobile', function () {
		toggleMenu( $( '#site-tools .sidebar-inner' ) );
	} );
	$( '#ca-more' ).off( 'click.kawaiiMobile' ).on( 'click.kawaiiMobile', function () {
		$( '#page-tools .sidebar-inner' ).css( 'top', $( '#ca-more' ).offset().top + 25 );
		toggleMenu( $( '#page-tools .sidebar-inner' ) );
	} );
	$( '#ca-languages' ).off( 'click.kawaiiMobile' ).on( 'click.kawaiiMobile', function () {
		$( '#other-languages .sidebar-inner' ).css( 'top', $( '#ca-languages' ).offset().top + 25 );
		toggleMenu( $( '#other-languages .sidebar-inner' ) );
	} );

	// Close menus on click outside.
	$( document )
		.off( 'click.kawaiiMobile touchstart.kawaiiMobile' )
		.on( 'click.kawaiiMobile touchstart.kawaiiMobile', function ( e ) {
			if ( $( e.target ).closest( '#menus-cover' ).length > 0 ) {
				allMenus().each( function () {
					hideMenu( $( this ) );
				} );
				syncCover();
			}
		} );

	// 从窄屏拉宽到桌面布局时，把移动端留下的 inline display/遮罩收掉，
	// 避免旋转屏幕或拖动窗口后菜单还挂在桌面上。
	$( window )
		.off( 'resize.kawaiiMobile' )
		.on( 'resize.kawaiiMobile', function () {
			if ( isNarrow() ) {
				return;
			}
			var $open = allMenus().filter( '.mobile-menu-open' );
			if ( $open.length ) {
				$open.each( function () {
					$( this ).closest( '.sidebar-chunk, #personal' ).removeClass( 'dropdown-active' );
					hideMenu( $( this ) );
				} );
				syncCover();
			}
		} );
} );
