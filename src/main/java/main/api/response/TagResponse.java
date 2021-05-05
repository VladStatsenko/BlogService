package main.api.response;

import lombok.Data;
import main.api.response.body.TagsBody;

import java.util.List;

@Data
public class TagResponse {
    private List<TagsBody> tag;
}
