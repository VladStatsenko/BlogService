package main.service;

import main.api.response.CalendarResponse;
import main.dao.PostDao;
import main.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneralService {

    private final PostDao postDao;
    @Autowired
    public GeneralService(PostDao postDao) {
        this.postDao = postDao;
    }

    /**
     * Метод выводит количества публикаций на каждую дату переданного в параметре year года или
     * текущего года, если параметр year не задан. В параметре years всегда возвращается список всех годов,
     * за которые была хотя бы одна публикация, в порядке возрастания.
     * @param year
     * @return
     */
    public CalendarResponse getPostsByYear(Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Calendar.JANUARY, 1, 0, 0, 1);
        Date from = calendar.getTime();
        calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        Date to = calendar.getTime();
        postDao.findPostsByYear(from, to);
        return new CalendarResponse(getAllYearsWhenMadePublication(postDao.findAllPublicationDate()), getDatesAndNumberOfPublication(postDao.findPostsByYear(from, to)));
    }

    private List<Integer> getAllYearsWhenMadePublication(List<Date> dates) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return dates.stream().map(sdf::format).map(Integer::parseInt).distinct().sorted().collect(Collectors.toList());
    }

    private Map<String, Integer> getDatesAndNumberOfPublication(List<Post> posts) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Integer> datesAndNumberOfPublication = new HashMap<>();
        for (Post post : posts) {
            datesAndNumberOfPublication.computeIfPresent(sdf.format(post.getTime()), ((s, integer) -> integer += 1));
            datesAndNumberOfPublication.putIfAbsent(sdf.format(post.getTime()), 1);
        }
        return datesAndNumberOfPublication;
    }
}
