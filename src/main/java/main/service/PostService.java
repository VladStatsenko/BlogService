package main.service;

import main.api.request.PostRequest;
import main.api.response.AddPostResponse;
import main.api.response.OnePostResponse;
import main.api.response.PostResponse;
import main.api.response.StatisticResponse;
import main.api.response.body.CommentBody;
import main.api.response.body.PostBody;
import main.api.response.error.ErrorsPostBody;
import main.dao.PostDao;
import main.model.Post;
import main.model.Tag;
import main.model.TagPost;
import main.model.User;
import main.repository.PostRepository;
import main.repository.TagPostRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDao postDao;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final TagPostRepository tagPostRepository;

    @Autowired
    public PostService(PostDao postDao, UserRepository userRepository, PostRepository postRepository, TagRepository tagRepository, TagPostRepository tagPostRepository) {
        this.postDao = postDao;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tagPostRepository = tagPostRepository;
    }

    /**
     * Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов
     * "Новые", "Самые обсуждаемые", "Лучшие" и "Старые". Метод выводит посты, отсортированные в
     * соответствии с параметром mode
     * @param offset
     * @param limit
     * @param mode
     * @return
     */
    public PostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable;
        PostResponse postResponse;

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

    /**
     * Метод возвращает посты, соответствующие поисковому запросу - строке query. В случае, если запрос
     * пустой или содержит только пробелы, метод должен выводить все посты.
     * @param offset
     * @param limit
     * @param searchQuery
     * @return
     */
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

    /**
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * @param offset
     * @param limit
     * @param sDate
     * @return
     */
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

    /**
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра
     * tag.
     * @param offset
     * @param limit
     * @param tag
     * @return
     */
    public PostResponse getPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<PostBody> postDTOList = postDao
                .findByTag(pageable, tag)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    /**
     * Метод выводит данные конкретного поста для отображения на странице поста, в том числе, список
     * комментариев и тэгов, привязанных к данному посту.
     * @param id
     * @return
     */
    public OnePostResponse getPostById(int id) {

        PostBody postBody = postDao.findById(id)
                .map(PostBody::new)
                .get();

        incrementCountView(id);

        String text = postDao.findById(id).get().getText();

        List<CommentBody> comments = postDao.findById(id)
                .get()
                .getComments().stream().map(CommentBody::new)
                .collect(Collectors.toList());

        List<String> tags = postDao.findById(id).get()
                .getTags().stream().map(TagPost::getTag).map(Tag::getName).collect(Collectors.toList());

        return new OnePostResponse(postBody.getId(), postBody.getTimestamp(), postBody.getUser()
                , postBody.getTitle(), postBody.getAnnounce(), postBody.getLikeCount(), postBody.getDislikeCount(),
                postBody.getCommentCount(), postBody.getViewCount()+1, comments, tags, text);
    }

    private void incrementCountView(int id) {
        Post post = postRepository.findById(id).orElse(null);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    /**
     * Метод выводит только те посты, которые создал пользователь.
     * @param offset
     * @param limit
     * @param status
     * @param principal
     * @return
     */
    public PostResponse getMyPosts(int offset, int limit, String status, Principal principal) {
        Pageable pageable;
        PostResponse postResponse;

        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

        switch (status) {
            case ("inactive"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").descending());
                postResponse = getInactivePosts(user.getId(), pageable);
                break;
            case ("pending"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").ascending());
                postResponse = getActivePosts(Post.ModerationStatus.NEW.toString(), user.getId(), pageable);
                break;
            case ("declined"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getActivePosts(Post.ModerationStatus.DECLINED.toString(), user.getId(), pageable);
                break;
            case ("published"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getActivePosts(Post.ModerationStatus.ACCEPTED.toString(), user.getId(), pageable);
                break;
            default:
                postResponse = new PostResponse();
        }

        return postResponse;
    }

    private PostResponse getActivePosts(String status, int id, Pageable pageable) {

        List<PostBody> postDTOList = postDao
                .findMyActivePosts(pageable, status, id)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    private PostResponse getInactivePosts(int id, Pageable pageable) {

        List<PostBody> postDTOList = postDao
                .findMyInactivePosts(pageable, id)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    /**
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если
     * заголовок или текст поста не установлены и/или слишком короткие (короче 3 и 50 символов
     * соответственно), метод должен выводить ошибку и не добавлять пост.
     * @param postRequest
     * @param principal
     * @return
     */
    public AddPostResponse addPost(PostRequest postRequest, Principal principal) {
        AddPostResponse addPostResponse = new AddPostResponse();
        addPostResponse.setResult(true);
        ErrorsPostBody errors = new ErrorsPostBody();
        if (postRequest.getTitle().isEmpty() || postRequest.getTitle().length() < 3) {
            addPostResponse.setResult(false);
            errors.setTitle("Заголовок не установлен, либо текст заголовка слишком короткий");
        }
        if (postRequest.getText().isEmpty() || postRequest.getText().length() < 50) {
            addPostResponse.setResult(false);
            errors.setText("Текст не установлен, либо текст слишком короткий");
        }
        if (addPostResponse.isResult()) {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));
            Post post = new Post();
            post.setIsActive(postRequest.getActive());
            post.setTime(new Date());
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());
            post.setUser(user);
            post.setStatus(Post.ModerationStatus.NEW);

            post = postRepository.save(post);

            for (String t : postRequest.getTags()
            ) {
                Tag tag = tagRepository.findFirstByNameLike(t).orElse(new Tag());
                tag.setName(t);
                TagPost tags2Post = new TagPost();
                tags2Post.setPost(post);
                tags2Post.setTag(tagRepository.save(tag));
                tagPostRepository.save(tags2Post);
            }
        } else {
            addPostResponse.setErrors(errors);
        }
        return addPostResponse;
    }

    /**
     * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму
     * публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче
     * 3 и 50 символов соответственно), метод должен выводить ошибку и не изменять пост.
     * @param postRequest
     * @param id
     * @param principal
     * @return
     */
    public AddPostResponse editPost(PostRequest postRequest, int id, Principal principal) {
        AddPostResponse addPostResponse = new AddPostResponse();
        addPostResponse.setResult(true);
        ErrorsPostBody errors = new ErrorsPostBody();
        if (postRequest.getTitle().isEmpty() || postRequest.getTitle().length() < 3) {
            addPostResponse.setResult(false);
            errors.setTitle("Заголовок не установлен, либо текст заголовка слишком короткий");
        }
        if (postRequest.getText().isEmpty() || postRequest.getText().length() < 50) {
            addPostResponse.setResult(false);
            errors.setText("Текст не установлен, либо текст слишком короткий");
        }
        if (addPostResponse.isResult()) {

            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));

            Post post = postDao.findById(id).orElse(null);
            post.setIsActive(postRequest.getActive());
            post.setTime(new Date());
            post.setTitle(postRequest.getTitle());
            post.setText(post.getText());
            post.setUser(user);
            post = postRepository.save(post);

            for (String t : postRequest.getTags()
            ) {

                Tag tag = tagRepository.findFirstByNameLike(t).orElse(new Tag());
                tag.setName(t);
                TagPost tags2Post = new TagPost();
                tags2Post.setPost(post);
                tags2Post.setTag(tagRepository.save(tag));
                tagPostRepository.save(tags2Post);
            }
        } else {
            addPostResponse.setErrors(errors);
        }
        return addPostResponse;
    }

    /**
     * Метод выдаёт статистику по всем постам блога.
     * @return
     */
    public StatisticResponse getAllStatistic() {

        List<Post> allPosts = (List<Post>) postRepository.findAll();
        StatisticResponse statistic = new StatisticResponse();
        statistic.setPostsCount(allPosts.size());
        statistic.setLikesCount(allPosts.stream().mapToInt(post -> post.getLikes().size()).sum());
        statistic.setDislikesCount(allPosts.stream().mapToInt(post -> post.getDislikes().size()).sum());
        statistic.setViewsCount(allPosts.stream().mapToInt(Post::getViewCount).sum());
        statistic.setFirstPublication(allPosts.get(0).getTime().getTime() / 1000);
        return statistic;
    }

}
