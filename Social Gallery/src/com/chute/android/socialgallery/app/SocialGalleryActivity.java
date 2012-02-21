package com.chute.android.socialgallery.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chute.android.comments.util.intent.CommentsActivityIntentWrapper;
import com.chute.android.comments.util.intent.MainActivityIntentWrapper;
import com.chute.android.gallery.components.GalleryViewFlipper;
import com.chute.android.gcshareview.intent.ShareActivityIntentWrapper;
import com.chute.android.socialgallery.R;
import com.chute.android.socialgallery.util.Constants;
import com.chute.android.socialgallery.util.view.HeartCheckbox;
import com.chute.sdk.api.GCHttpCallback;
import com.chute.sdk.api.chute.GCChutes;
import com.chute.sdk.collections.GCAssetCollection;
import com.chute.sdk.model.GCAssetModel;
import com.chute.sdk.model.GCHttpRequestParameters;

public class SocialGalleryActivity extends Activity {

	private ImageButton comments;
	private ImageButton share;
	private HeartCheckbox heart;
	private GalleryViewFlipper gallery;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gallery = (GalleryViewFlipper) findViewById(R.id.galleryId);
		comments = (ImageButton) findViewById(R.id.btnComment);
		comments.setOnClickListener(new CommentsClickListener());
		share = (ImageButton) findViewById(R.id.btnShare);
		share.setOnClickListener(new ShareClickListener());
		heart = (HeartCheckbox) findViewById(R.id.btnHeart);
		heart.setOnClickListener(new HeartClickListener());

		GCChutes.Resources.assets(getApplicationContext(), Constants.CHUTE_ID,
				new AssetCollectionCallback()).executeAsync();

	}

	private final class AssetCollectionCallback implements
			GCHttpCallback<GCAssetCollection> {

		@Override
		public void onSuccess(GCAssetCollection responseData) {
			gallery.setAssetCollection(responseData);
			for (int i=0; i<responseData.size(); i++) {
				GCAssetModel asset = new GCAssetModel();
				asset.setId(responseData.get(i).getId());
				CommentsActivityIntentWrapper wrapper = new CommentsActivityIntentWrapper(SocialGalleryActivity.this);
				wrapper.setAssetId(asset.getId());
				
			}
		}

		@Override
		public void onHttpException(GCHttpRequestParameters params,
				Throwable exception) {

		}

		@Override
		public void onHttpError(int responseCode, String statusMessage) {

		}

		@Override
		public void onParserException(int responseCode, Throwable exception) {

		}

	}

	private final class CommentsClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			CommentsActivityIntentWrapper wrapper = new CommentsActivityIntentWrapper(
					SocialGalleryActivity.this);
			wrapper.setChuteId(Constants.CHUTE_ID);
			wrapper.setAssetId(Constants.ASSET_ID);
			wrapper.setChuteName(Constants.CHUTE_NAME);
			wrapper.startActivityForResult(SocialGalleryActivity.this,
					Constants.ACTIVITY_FOR_RESULT_KEY);
		}

	}

	private final class ShareClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			ShareActivityIntentWrapper wrapper = new ShareActivityIntentWrapper(
					SocialGalleryActivity.this);
			wrapper.setChuteId(Constants.CHUTE_ID);
			wrapper.setChuteName(Constants.CHUTE_NAME);
			wrapper.setChuteShortcut(Constants.CHUTE_SHORTCUT);
			wrapper.startActivity(SocialGalleryActivity.this);
		}

	}

	private final class HeartClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			heart.markHeartByAssetId(Constants.ASSET_ID);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != Constants.ACTIVITY_FOR_RESULT_KEY
				|| resultCode != RESULT_OK) {
			return;
		}
		MainActivityIntentWrapper wrapper = new MainActivityIntentWrapper(data);
		if (wrapper.getExtraComments() > 0) {
			Toast.makeText(getApplicationContext(),
					wrapper.getExtraComments() + " Comments added!",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "No Comments added!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		gallery.destroyGallery();
	}

}