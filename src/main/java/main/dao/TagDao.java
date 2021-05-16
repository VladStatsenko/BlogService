package main.dao;

import main.model.Tag;

import java.util.List;

public interface TagDao {

    List<Tag> findFreq();

    List<Tag> findAll();
}
