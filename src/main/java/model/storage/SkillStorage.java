package model.storage;

import model.config.DatabaseManagerConnector;
import model.dao.SkillDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillStorage implements Storage<SkillDao> {

    public DatabaseManagerConnector manager;

    private final String INSERT = "INSERT INTO skill(language, level) VALUES (?, ?)";
    private final String FIND_BY_NAME = "SELECT * FROM skill WHERE language LIKE ? and level LIKE ? ";
    private final String GET_SKILLS_BY_DEVELOPER_ID =
     "SELECT  language, level FROM developer JOIN developer_skill ON developer_skill.developer_id = developer.developer_id " +
     "JOIN skill ON skill.skill_id = developer_skill.skill_id WHERE developer.developer_id = ?";
    private final String COUNT_BY_LANGUAGE = "SELECT COUNT(skill_id) FROM skill WHERE language LIKE ?";
    private final String COUNT_BY_LEVEL = "SELECT COUNT(skill_id) FROM skill WHERE level LIKE ?";

    public SkillStorage(DatabaseManagerConnector manager) throws SQLException {
        this.manager = manager;
    }

    @Override
    public SkillDao save(SkillDao entity) {
        try (Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getLanguage());
            statement.setString(2, entity.getLevel());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setSkill_id(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Skill saving was interrupted, ID has not been obtained.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("The skill was not created");
        }
        return entity;
    }

    @Override
    public boolean isExist(String name) {
        return false;
    }

    @Override
    public Optional<SkillDao> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Optional<SkillDao> findByName(String name) {
        return Optional.empty();
    }
    public Optional<SkillDao> findByName(String language, String level) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME)) {
            statement.setString(1, language);
            statement.setString(2, level);
            ResultSet resultSet = statement.executeQuery();
            SkillDao skillDao = mapSkillDao(resultSet);
            return Optional.ofNullable(skillDao);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }
    public long countByLanguage(String language) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(COUNT_BY_LANGUAGE)) {
            statement.setString(1, language);
            ResultSet rs = statement.executeQuery();
            int quantity = 0;
            while (rs.next()) {
              quantity = rs.getInt("count");
            }
            return quantity;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }
    public long countByLevel(String level) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(COUNT_BY_LEVEL)) {
            statement.setString(1, level);
            ResultSet rs = statement.executeQuery();
            int quantity = 0;
            while (rs.next()) {
                quantity = rs.getInt("count");
            }
            return quantity;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Optional<SkillDao>> findAll() {
        return null;
    }

    @Override
    public boolean isExist(long id) {
        return false;
    }

    public boolean isLanguageExist(String language) {
        return countByLanguage(language) > 0;
    }

    public boolean isLevelExist(String language) {
        return countByLevel(language) > 0;
    }

    @Override
    public SkillDao update(SkillDao entity) {return new SkillDao();}

    @Override
    public void delete(SkillDao entity) {
    }

    public List<String> getSkillSetByDeveloperId(long id) {
        List<String> skills = new ArrayList<>();
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_SKILLS_BY_DEVELOPER_ID)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                skills.add("( " +rs.getString("language") + " - " + rs.getString("level") + " )");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return skills;
    }

    private SkillDao mapSkillDao(ResultSet resultSet) throws SQLException {
        SkillDao skillDao = null;
        while (resultSet.next()) {
            skillDao = new SkillDao();
            skillDao.setSkill_id(resultSet.getLong("skill_id"));
            skillDao.setLanguage(resultSet.getString("language"));
            skillDao.setLevel(resultSet.getString("level"));
        }
        return skillDao;
    }

}
