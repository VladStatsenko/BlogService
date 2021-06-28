package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.response.error.ErrorsPostBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostResponse {

    private boolean result;
    private ErrorsPostBody errors;
}
