package main.service;

import main.api.response.TagResponse;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    public TagResponse tags(){
        TagResponse tagResponse = new TagResponse();
        return tagResponse;
    }
}
