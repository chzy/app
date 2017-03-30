package com.chd.service.RPCchannel.upload.parser;

/**
 * Created by lxp on 2017/3/24.
 */

public class TrpcResponseParse extends BaseResponseParser {
    @Override
    public ParserResult process(final String responseStr) throws Exception {
        ParserResult<String> result = new ParserResult<String>(responseStr) {
            @Override
            public boolean isSuccessful() {
                return responseStr == null;
            }

            @Override
            public String getMsg() {
                return responseStr;
            }
        };

        return  result;

    }
}