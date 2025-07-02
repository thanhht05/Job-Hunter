package vn.JobHunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.JobHunter.domain.respone.RestResponse;
import vn.JobHunter.util.annotation.ApiMessage;

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

        if (body instanceof String || body instanceof Resource) {
            return body;
        }
        if (statusCode >= 400) {
            return body;

        } else {
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            String message = apiMessage != null ? apiMessage.value() : "Call api success";
            fromatRestResponse.setMessage(message);
            fromatRestResponse.setData(body);
        }
        return fromatRestResponse;
    }

}
