/**
 * Copyright 2013 Joan Zapata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joanzapata.pdfview;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.AsyncTask;

import com.joanzapata.pdfview.model.PagePart;

import org.vudroid.core.DecodeService;
import org.vudroid.core.codec.CodecPage;

import java.util.ArrayList;
import java.util.List;

class RenderingAsyncTask extends AsyncTask<Void, PagePart, Void> {

    private DecodeService decodeService;

    private List<RenderingTask> renderingTasks;

    private PDFView pdfView;

    public RenderingAsyncTask(PDFView pdfView) {
        this.pdfView = pdfView;
        this.renderingTasks = new ArrayList<RenderingTask>();
    }

    public void addRenderingTask(int userPage, int page, float width, float height, RectF bounds, boolean thumbnail, int cacheOrder) {
        RenderingTask task = new RenderingTask(width, height, bounds, userPage, page, thumbnail, cacheOrder);
        renderingTasks.add(task);
        wakeUp();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (!isCancelled()) {

            // Proceed all tasks
            while (!renderingTasks.isEmpty()) {
                RenderingTask task = renderingTasks.get(0);
                PagePart part = proceed(task);

                if (renderingTasks.remove(task)) {
                    publishProgress(part);
                } else {
                    part.getRenderedBitmap().recycle();
                }
            }

            // Wait for new task, return if canceled
            if (!waitForRenderingTasks() || isCancelled()) {
                return null;
            }

        }
        return null;

    }

    @Override
    protected void onProgressUpdate(PagePart... part) {
        pdfView.onBitmapRendered(part[0]);
    }

    private boolean waitForRenderingTasks() {
        try {
            synchronized (renderingTasks) {
                renderingTasks.wait();
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private PagePart proceed(RenderingTask renderingTask) {
        this.decodeService = pdfView.getDecodeService();
        CodecPage page = decodeService.getPage(renderingTask.page);
        Bitmap render;

        synchronized (decodeService.getClass()) {
            render = page.renderBitmap(Math.round(renderingTask.width), Math.round(renderingTask.height), renderingTask.bounds);
        }

        PagePart part = new PagePart(renderingTask.userPage, renderingTask.page, render, //
                renderingTask.width, renderingTask.height, //
                renderingTask.bounds, renderingTask.thumbnail, //
                renderingTask.cacheOrder);

        return part;
    }

    public void removeAllTasks() {
        renderingTasks.clear();
    }

    public void wakeUp() {
        synchronized (renderingTasks) {
            renderingTasks.notify();
        }
    }

    private class RenderingTask {
        float width, height;

        RectF bounds;

        int page;

        int userPage;

        boolean thumbnail;

        int cacheOrder;

        public RenderingTask(float width, float height, RectF bounds, int userPage, int page, boolean thumbnail, int cacheOrder) {
            super();
            this.page = page;
            this.width = width;
            this.height = height;
            this.bounds = bounds;
            this.userPage = userPage;
            this.thumbnail = thumbnail;
            this.cacheOrder = cacheOrder;
        }

    }

}
