package sample.cafekiosk.spring.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private int code;
    private HttpStatus status;
    private String description;
    private T data;

    public ApiResponse(HttpStatus status, String description, T data) {
        this.code = status.value();
        this.status = status;
        this.description = description;
        this.data = data;
    }


    public static <T> ApiResponse<T> of(HttpStatus status, String description, T data) {
        return new ApiResponse<>(status, description, data);
    }


    public static <T> ApiResponse<T> of(HttpStatus status, T data) {
        return of(status, status.name(), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

}
