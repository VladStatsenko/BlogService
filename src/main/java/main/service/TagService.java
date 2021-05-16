package main.service;

import main.api.response.TagResponse;
import main.api.response.body.TagsBody;
import main.dao.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    private final TagDao tagDao;

    @Autowired
    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public TagResponse getTags() {
        List<TagsBody> TagList = new ArrayList<>();
        tagDao.findAll().forEach(tag -> TagList.add(new TagsBody(tag.getName(), getWeight())));

        return new TagResponse(TagList);
    }

    private double getWeight() {
        return Math.random();
    }

}
