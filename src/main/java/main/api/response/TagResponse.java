package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.response.body.TagsBody;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private List<TagsBody> tags;
}
