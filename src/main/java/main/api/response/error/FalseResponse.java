package main.api.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FalseResponse {
    private boolean result;
    private ErrorBody errors;

}
