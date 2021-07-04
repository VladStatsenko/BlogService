package main.service;

import main.api.response.TagResponse;
import main.api.response.body.TagsBody;
import main.dao.TagDao;
import main.repository.PostRepository;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    private final TagDao tagDao;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagDao tagDao, PostRepository postRepository, TagRepository tagRepository) {
        this.tagDao = tagDao;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public TagResponse getTags() {
        List<TagsBody> TagList = new ArrayList<>();
        tagDao.findAll().forEach(tag -> TagList.add(new TagsBody(tag.getName(), getWeight(tag.getName()))));

        return new TagResponse(TagList);
    }

    private double getWeight(String name) {
        double count = postRepository.count();
        double tagCount = tagRepository.findByName(name).size();
        double dWeight = tagCount/count;

        double k = 1.1;
        double Weight = k * dWeight;

        return Weight;
    }

}
