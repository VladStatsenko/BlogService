package main.service;

import main.api.response.OnePostResponse;
import main.api.response.PostResponse;
import main.api.response.body.CommentBody;
import main.api.response.body.PostBody;
import main.dao.PostDao;
import main.model.Tag;
import main.model.TagPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDao postDao;

    @Autowired
    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public PostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable;
        PostResponse postResponse = null;

        switch (mode) {
            case ("recent"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").descending());
                postResponse = getPostsSortedByTime(pageable);
                break;
            case ("early"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").ascending());
                postResponse = getPostsSortedByTime(pageable);
                break;
            case ("best"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getPostsSortedByLikes(pageable);
                break;
            case ("popular"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getPostsSortedByComments(pageable);
                break;
            default:
                postResponse = new PostResponse();
        }
        return postResponse;
    }

    private PostResponse getPostsSortedByTime(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findAllPosts(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    private PostResponse getPostsSortedByLikes(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findByLikes(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    private PostResponse getPostsSortedByComments(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findByComments(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    public PostResponse getPostsBySearch(int offset, int limit, String searchQuery) {
        List<PostBody> postDTOList;
        Pageable pageable = PageRequest.of(offset, limit);
        if (searchQuery.equals("")) {
            postDTOList = postDao
                    .findAllPosts(pageable)
                    .get()
                    .map(PostBody::new)
                    .collect(Collectors.toList());
        } else {
            postDTOList = postDao.postsSearch(pageable, searchQuery)
                    .get()
                    .map(PostBody::new)
                    .collect(Collectors.toList());
        }
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    public PostResponse getPostsByDate(int offset, int limit, String sDate) {
        Pageable pageable = PageRequest.of(offset, limit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        Date from = calendar.getTime();
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date to = calendar.getTime();
        List<PostBody> postDTOList = postDao.findByDate(pageable, from, to)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());

        return new PostResponse(postDTOList.size(), postDTOList);
    }

    public PostResponse getPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<PostBody> postDTOList = postDao
                .findByTag(pageable, tag)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    public OnePostResponse getPostById(int id) {

        PostBody postBody = postDao.findById(id)
                .map(PostBody::new)
                .get();

        List<CommentBody> comments = postDao.findById(id)
                .get()
                .getComments().stream().map(CommentBody::new)
                .collect(Collectors.toList());
        List<String> tags = postDao.findById(id).get()
                .getTags().stream().map(TagPost::getTag).map(Tag::getName).collect(Collectors.toList());

        return new OnePostResponse(postBody.getId(), postBody.getTimestamp(), postBody.getUser()
                , postBody.getTitle(), postBody.getAnnounce(), postBody.getLikeCount(), postBody.getDislikeCount(),
                postBody.getCommentCount(), postBody.getViewCount(), comments, tags);
    }

}
