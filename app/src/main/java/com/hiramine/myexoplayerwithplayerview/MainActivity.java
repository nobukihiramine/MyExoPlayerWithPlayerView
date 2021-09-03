package com.hiramine.myexoplayerwithplayerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity
{
	private final String m_strUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
	// 再生動画として、commondatastorage.googleapis.com の gtv-videos-bucket の sample に置いてある「Big Buck Bunny」動画ファイルを使用。
	// Big Buck Bunny
	// 　Copyright (C) 2008 Blender Foundation | peach.blender.org Some Rights Reserved.
	// 　Creative Commons Attribution 3.0 license. https://peach.blender.org/

	private PlayerView      m_playerview;
	private SimpleExoPlayer m_simpleexoplayer;
	private boolean         m_bIsPlayingWhenActivityIsPaused;
	private ImageView       m_imageviewFullScreen;
	private boolean         m_bIsFullScreen;

	private View.OnClickListener m_onFullScreenButtonClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View view )
		{
			if( m_bIsFullScreen )
			{    // フルスクリーン中。非フルスクリーンへ移行。画面の向きは、縦向き。
				m_imageviewFullScreen.setImageDrawable( ResourcesCompat.getDrawable( getResources(), R.drawable.ic_fullscreen, null ) );
				setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
				m_bIsFullScreen = false;
			}
			else
			{    // 非フルスクリーン中。フルスクリーンへ移行。画面の向きは、横向き。
				m_imageviewFullScreen.setImageDrawable( ResourcesCompat.getDrawable( getResources(), R.drawable.ic_fullscreen_exit, null ) );
				setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
				m_bIsFullScreen = true;
			}
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// 初期状態は、画面は縦向きで、非フルスクリーン。
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		m_bIsFullScreen = false;

		// フルスクリーンボタンのビューの取得
		m_imageviewFullScreen = findViewById( R.id.imageview_fullscreen );

		// フルスクリーンボタンのビューのクリックリスナーの設定
		m_imageviewFullScreen.setOnClickListener( m_onFullScreenButtonClickListener );

		// 動画を再生するビューの取得
		m_playerview = findViewById( R.id.playerview );

		// SimpleExoPlayerの作成と設定
		SimpleExoPlayer.Builder playerbuilder = new SimpleExoPlayer.Builder( this );
		playerbuilder.setSeekBackIncrementMs( 10000 );    // SeekBack 10s
		playerbuilder.setSeekForwardIncrementMs( 10000 ); // SeekForward 10s
		m_simpleexoplayer = playerbuilder.build();

		// 動画を再生するビューとSimpleExoPlayerの紐づけ
		m_playerview.setPlayer( m_simpleexoplayer );

		// MediaItemの作成とSimpleExoPlayerへのセット
		Uri uriVideo = Uri.parse( m_strUri );
		m_simpleexoplayer.setMediaItem( MediaItem.fromUri( uriVideo ) );

		// 再生の準備
		m_simpleexoplayer.prepare();
		// 再生の開始
		m_simpleexoplayer.play();
	}

	// 初回表示時、および、ポーズからの復帰時
	@Override
	protected void onResume()
	{
		super.onResume();

		// タイトルバー、ステータスバー、ナビゲーションバーの非表示
		// （ポーズ、ストップから復帰した際には、ナビゲーションバーのImmersiveモード（ナビゲーションバーの折り畳み表示）が解除されるので、
		// 　onCreateではなく、onResumeで、バー非表示処理を行う。
		// 　onCreateで、ウインドウのフルスクリーン設定を行うと、ポーズからレジュームした際に、ナビゲーションバーが常時表示となってしまう。）
		getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE // タイトルバー非表示
														  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // ステータスバーが無いものとしてビューをレイアウトする
														  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // ナビゲーションバーが無いものとしてビューをレイアウトする
														  | View.SYSTEM_UI_FLAG_FULLSCREEN // ステータスバーの非表示
														  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // ナビゲーションバーの非表示
														  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );// ナビゲーションバーの折り畳み表示ねばねばモード（スワイプで表示。しばらくすると再非表示）

		if( m_bIsPlayingWhenActivityIsPaused )
		{    // Activity pause時に動画再生中なら、動画再生再開
			m_simpleexoplayer.play();
		}
	}

	// アクティビティがフォアグラウンドからバックグラウンドに追いやられた時
	@Override
	protected void onPause()
	{
		// 動画再生中かどうかの取得と保持
		m_bIsPlayingWhenActivityIsPaused = m_simpleexoplayer.isPlaying();
		// 動画再生の一時停止
		m_simpleexoplayer.pause();

		super.onPause();
	}
}