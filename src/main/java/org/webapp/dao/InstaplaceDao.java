package org.webapp.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.webapp.model.Instaplace;
import javax.sql.DataSource;
import java.util.*;


@Repository
public class InstaplaceDao extends Dao<Instaplace>{
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;
    private String sql;
    private List<Instaplace> instaplaceList;

    @Autowired
    public InstaplaceDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("instaplace")
                                                    .usingGeneratedKeyColumns("key");
    }

    public InstaplaceDao() {}

    @Override
    public void save(Instaplace instaplace) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("station", instaplace.getStation());
        parameters.put("post", instaplace.getPost());
        parameters.put("likeCNT", instaplace.getLikeCNT());
        parameters.put("date", instaplace.getDate());
        parameters.put("hashtag", instaplace.getHashtag());
        parameters.put("description", instaplace.getDescription());

        jdbcInsert.execute(parameters);
    }

    @Override
    public List<Instaplace> findByParam(Map<String, Object> parameter){
        sql = "select * from Instaplace where ";
        List<Instaplace> result;

        result = jdbcTemplate.query(selectTarget("Instaplace", parameter, sql)
                                                , new InstaplaceMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from Instaplace where ";
        jdbcTemplate.update(selectTarget("Instaplace", parameter, sql));
    }

    @Override
    public List<Instaplace> findAll() {
        sql = "select * from Instaplace";
        instaplaceList = jdbcTemplate.query(sql, new InstaplaceMapper());

        return instaplaceList;
    }
}
