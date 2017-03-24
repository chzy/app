package com.chd.service.RPCchannel.upload;


import com.chd.service.RPCchannel.upload.parser.BaseResponseParser;
import com.chd.service.RPCchannel.upload.preprocessor.BasePreProcessor;

/**
 * Created by hjy on 7/19/15.<br>
 */
public class UploadOptions {
    public boolean overwrite; //覆盖
    public boolean  resume;  // 续传


  //private BasePreProcessor mPreProcessor;
    private BaseResponseParser mResponseParser;

    public UploadOptions( FileUploadConfiguration.Builder builder) {
       // mPreProcessor = builder.preProcessor;
        mResponseParser = builder.responseProcessor;
    }

    public BaseResponseParser getResponseParser() {
        return mResponseParser;
    }

 /*


public BasePreProcessor getPreProcessor() {
        return mPreProcessor;
    }


    public BaseResponseParser getResponseParser() {
        return mResponseParser;
    }

    public static class Builder {

        private BasePreProcessor preProcessor;
        private BaseResponseParser responseParser;

        public Builder() {

        }

        public Builder setPreProcessor(BasePreProcessor preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder setResponseParser(BaseResponseParser parser) {
            this.responseParser = parser;
            return this;
        }

        public UploadOptions build() {
            UploadOptions options = new UploadOptions(this);
            return options;
        }

    }*/

}