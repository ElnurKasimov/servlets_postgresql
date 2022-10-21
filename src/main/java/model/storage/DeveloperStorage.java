package model.storage;

import lombok.Data;
import model.config.DatabaseManagerConnector;
import model.dao.DeveloperDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class DeveloperStorage implements Storage<DeveloperDao>{
    private DatabaseManagerConnector manager;
    private CompanyStorage companyStorage;
    private SkillStorage skillStorage;

    private final String GET_ALL_INFO = "SELECT * FROM developer";
    private final String FIND_BY_NAME = "SELECT * FROM developer WHERE lastName LIKE ? and firstName LIKE ? ";
    private final String INSERT = "INSERT INTO developer(lastName, firstName, age, company_id, salary) VALUES (?, ?, ?, ?, ?)";

    private final String GET_LIST_LANGUAGE_DEVELOPERS =
            "SELECT lastName, firstName, level FROM developer JOIN developer_skill " +
             "ON developer.developer_id = developer_skill.developer_id " +
             "JOIN skill ON developer_skill.skill_id = skill.skill_id WHERE language LIKE ?";

    private final String GET_LIST_LEVEL_DEVELOPERS =
            "SELECT lastName, firstName, language FROM developer JOIN developer_skill " +
                    "ON developer.developer_id = developer_skill.developer_id " +
                    "JOIN skill ON developer_skill.skill_id = skill.skill_id WHERE level LIKE ?";
    private final String GET_PROJECT_DEVELOPERS =
            "SELECT lastname, firstname FROM project " +
            "JOIN project_developer ON project_developer.project_id = project.project_id " +
            "JOIN developer ON developer.developer_id = project_developer.developer_id " +
            "WHERE project_name LIKE ?";
    private final String GET_QUANTITY_PROJECT_DEVELOPERS =
            "SELECT COUNT(developer_id) FROM project JOIN project_developer " +
                    "ON project.project_id = project_developer.project_id " +
                    " WHERE project_name  LIKE  ?";
    private final String UPDATE =
            "UPDATE developer SET age=?, salary=?, company_id=? WHERE lastName LIKE ? AND firstName LIKE ? RETURNING *";
    private final String GET_ID_BY_NAME =
            "SELECT developer_id FROM developer WHERE  lastName LIKE ? AND firstName LIKE ?";
    private  final String DELETE = "DELETE FROM developer WHERE lastName LIKE ? AND firstName LIKE ?";

    public DeveloperStorage (DatabaseManagerConnector manager, CompanyStorage companyStorage,
                             SkillStorage skillStorage) {
        this.manager = manager;
        this.companyStorage = companyStorage;
        this.skillStorage = skillStorage;
    }

    @Override
    public DeveloperDao save(DeveloperDao entity) {
        try (Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.setInt(3, entity.getAge());
            statement.setLong(4, entity.getCompanyDao().getCompany_id());
            statement.setInt(5, entity.getSalary());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setDeveloper_id(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Developer saving was interrupted, ID has not been obtained.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("The developer was not created");
        }
        return entity;
    }

    @Override
    public Optional<DeveloperDao> findById(long id) {
        return null;
    }

   @Override
   public Optional<DeveloperDao> findByName(String name) {
       return null;
   }

    public Optional<DeveloperDao> findByName(String lastName, String firstName) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME)) {
            statement.setString(1, lastName);
            statement.setString(2, firstName);
            ResultSet resultSet = statement.executeQuery();
            DeveloperDao developerDao = mapDeveloperDao(resultSet);
            return Optional.ofNullable(developerDao);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    public long getIdByName(String lastName, String firstName) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME)) {
            statement.setString(1, lastName);
            statement.setString(2, firstName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getLong("developer_id");
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }
    @Override
    public List<Optional<DeveloperDao>> findAll() {
        List<Optional<DeveloperDao>> developerDaoList = new ArrayList<>();
        try (Connection connection = manager.getConnection();
            ResultSet rs = connection.prepareStatement(GET_ALL_INFO).executeQuery()) {
                while (rs.next()) {
                    DeveloperDao developerDao = new DeveloperDao();
                    developerDao.setDeveloper_id(rs.getLong("developer_id"));
                    developerDao.setLastName(rs.getString("lastName"));
                    if (rs.getString("firstName") != null) {
                        developerDao.setFirstName(rs.getString("firstName"));
                    }
                    developerDao.setAge(rs.getInt("age"));
                    developerDao.setCompanyDao(companyStorage.findById(rs.getInt("company_id")).get());
                    developerDao.setSalary(rs.getInt("salary"));
                    developerDaoList.add(Optional.ofNullable(developerDao));
                }
            }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    return developerDaoList;
    }

    @Override
    public boolean isExist(long id) {
        return false;
    }

    public boolean isExist(String lastName, String firstName) {
        return findByName(lastName, firstName).isPresent();
    }

    @Override
    public boolean isExist(String name) {
        return false;
    }

    @Override
    public DeveloperDao update(DeveloperDao entity) {
        DeveloperDao developerDao=null;
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setInt(1, entity.getAge());
            statement.setInt(2, entity.getSalary());
            statement.setLong(3, entity.getCompanyDao().getCompany_id());
            statement.setString(4, entity.getLastName());
            statement.setString(5, entity.getFirstName());
            ResultSet resultSet = statement.executeQuery();
            developerDao = mapDeveloperDao(resultSet);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return developerDao;
    }

    @Override
    public void delete(DeveloperDao entity) {
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, entity.getLastName());
            statement.setString(2, entity.getFirstName());
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> getNamesListOfCertainLanguageDevelopers(String language) {
        List<String> developersNames = new ArrayList<>();
        try (Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_LIST_LANGUAGE_DEVELOPERS)) {
            statement.setString(1, language);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String lastName = rs.getString("lastname");
                String firstName = rs.getString("firstname");
                String level = rs.getString("level");
                developersNames.add(String.format("%s %s - %s",
                        lastName, firstName, level));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return developersNames;
    }

    public List<String> getNamesListOfCertainLevelDevelopers(String level) {
        List<String> developersNames = new ArrayList<>();
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_LIST_LEVEL_DEVELOPERS)) {
            statement.setString(1, level);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String lastName = rs.getString("lastname");
                String firstName = rs.getString("firstname");
                String language = rs.getString("language");
                developersNames.add(String.format("%s %s -  %s",
                        lastName, firstName, language));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return developersNames;
    }

    public List<String> getDevelopersNamesByProjectName(String projectName) {
        List<String> developersNames = new ArrayList<>();
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_PROJECT_DEVELOPERS)){
            statement.setString(1, projectName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String lastName = rs.getString("lastname");
                String firstName = rs.getString("firstname");
                developersNames.add(String.format("%s %s,", lastName, firstName));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return developersNames;
    }

    public long getQuantityOfProjectDevelopers(String name) {
        long quantity = 0;
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_QUANTITY_PROJECT_DEVELOPERS)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                quantity = rs.getLong("count");
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return quantity;
    }
    private DeveloperDao mapDeveloperDao(ResultSet resultSet) throws SQLException {
        DeveloperDao developerDao = null;
        while (resultSet.next()) {
            developerDao = new DeveloperDao();
            developerDao.setDeveloper_id(resultSet.getLong("developer_id"));
            developerDao.setLastName(resultSet.getString("lastName"));
            developerDao.setFirstName(resultSet.getString("firstName"));
            developerDao.setAge(resultSet.getInt("age"));
            developerDao.setSalary(resultSet.getInt("salary"));
            developerDao.setCompanyDao(companyStorage.findById(resultSet.getLong("company_id")).get());
        }
        return developerDao;
    }

}
