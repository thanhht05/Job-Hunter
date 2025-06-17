package vn.JobHunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.JobHunter.domain.RestResponse;

@RestControllerAdvice
public class FromatRestResponse implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // format any respone
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();

        RestResponse<Object> fromatRestResponse = new RestResponse<>();
        fromatRestResponse.setStatusCode(statusCode);
        if (statusCode >= 400) {
            return body;

        } else {
            fromatRestResponse.setData(body);
            fromatRestResponse.setMessage("Call api success");
        }
        return fromatRestResponse;
    }

}
