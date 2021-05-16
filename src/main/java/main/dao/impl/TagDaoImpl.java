package main.dao.impl;

import main.dao.TagDao;
import main.model.Tag;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagDaoImpl implements TagDao {

    private final TagRepository tagRepository;

    @Autowired
    public TagDaoImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> findFreq() {
        return tagRepository.findFreq();
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
