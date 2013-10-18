package com.rackspace.papi.httpx.parser;

import com.rackspace.httpx.RequestHeadDetail;
import javax.servlet.http.HttpServletRequest;

/**
 * @author fran
 */
public final class RequestParserFactory {

    private RequestParserFactory() {
    }

    public static Parser<HttpServletRequest, RequestHeadDetail> newInstance() {
        return new HttpRequestParser();
    }
}
