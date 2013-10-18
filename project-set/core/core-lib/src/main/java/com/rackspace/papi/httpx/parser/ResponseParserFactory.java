package com.rackspace.papi.httpx.parser;

import com.rackspace.httpx.ResponseHeadDetail;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fran
 */
public final class ResponseParserFactory {
    
    private ResponseParserFactory(){
    }

    public static Parser<HttpServletResponse, ResponseHeadDetail> newInstance() {
        return new HttpResponseParser();     
    }
}
