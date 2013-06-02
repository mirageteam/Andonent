Andonment
=========

Android Useful componment

# PDF library 使用方法：

setContentView(R.layout.layout_of_pdf_viewer);

		mPdfView = (PDFView) findViewById(R.id.pdfView);
		filePath = getIntent().getStringExtra("file");

		page_detail = (TextView) findViewById(R.id.page_detail);

		title = (View) findViewById(R.id.title);

		mPdfView.fromSdcard(filePath).defaultPage(0).enableSwipe(true)
				.onPageChange(new OnPageChangeListener() {

					@Override
					public void onPageChanged(int page, int pageCount) {
						page_detail.setText(page + "/" + pageCount);
					}
				}).onLoad(new OnLoadCompleteListener() {

					@Override
					public void loadComplete(int arg0) {
						// TODO Auto-generated method stub

					}
				}).onSingleTab(new OnSingleListener() {

					@Override
					public void onSingleTab() {
						// TODO Auto-generated method stub
						title.setVisibility(title.getVisibility() == View.VISIBLE ? View.GONE
								: View.VISIBLE);
					}
				}).load();
