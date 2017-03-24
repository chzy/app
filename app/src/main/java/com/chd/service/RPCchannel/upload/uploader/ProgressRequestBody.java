package com.chd.service.RPCchannel.upload.uploader;


import com.chd.service.RPCchannel.upload.listener.OnFileTransferredListener;

import java.io.IOException;



/**
 * Created by hjy on 9/22/15.<br>
 */
public class ProgressRequestBody /*extends RequestBody*/ {

    /**
     * 实际的带包装请求体
     */
    /*private final RequestBody mRequestBody;

    *//**
     * 传输进度监听
     *//*
    private final OnFileTransferredListener mOnFileTransferredListener;

    private BufferedSink mBufferedSink;

    public ProgressRequestBody(RequestBody requestBody, OnFileTransferredListener listener) {
        mRequestBody = requestBody;
        mOnFileTransferredListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if(mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink));
        }
        mRequestBody.writeTo(mBufferedSink);
        mBufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {

            long contentLength = 0l;
            long bytesWritten = 0l;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if(contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                mOnFileTransferredListener.transferred(bytesWritten, contentLength);
            }
        };
    }*/

}