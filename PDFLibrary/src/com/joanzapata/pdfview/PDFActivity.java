package com.joanzapata.pdfview;

import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class PDFActivity extends Activity {
	PDFView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_of_pdf);

		 view = (PDFView) findViewById(R.id.pdfView);

		view.fromAsset("sample.pdf").defaultPage(0).enableSwipe(true)
				.onPageChange(new OnPageChangeListener() {

					@Override
					public void onPageChanged(int page, int pageCount) {
						// TODO Auto-generated method stub
						// Toast.makeText(MainActivity.this,
						// "当前第" + page + "页,总共" + pageCount + "页", 1000)
						// .show();

					}

				}).onLoad(new OnLoadCompleteListener() {

					@Override
					public void loadComplete(int arg0) {
						// TODO Auto-generated method stub

					}
				}).load();
	}
	
	
}
