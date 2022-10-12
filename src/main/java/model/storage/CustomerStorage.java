package model.storage;

import model.config.DatabaseManagerConnector;
import model.dao.CustomerDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerStorage implements Storage<CustomerDao> {
    public DatabaseManagerConnector manager;

    private final String GET_ALL_INFO = "SELECT * FROM customer";
    private final String FIND_BY_NAME = "SELECT * FROM customer WHERE  customer_name  LIKE  ?";
    private final String FIND_BY_ID = "SELECT * FROM customer WHERE customer_id = ?";
    private final String INSERT = "INSERT INTO customer(customer_name, reputation) VALUES (?, ?)";
    private final String UPDATE =
            "UPDATE customer SET reputation=? WHERE customer_name LIKE ? RETURNING *";
    private  final String DELETE = "DELETE FROM customer WHERE customer_name LIKE  ?";

    public CustomerStorage(DatabaseManagerConnector manager) throws SQLException {
        this.manager = manager;
    }

    @Override
    public CustomerDao save(CustomerDao entity) {
        try (Connection connection = manager.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getCustomer_name());
            statement.setString(2, entity.getReputation().toString());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setCustomer_id(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Customer saving was interrupted, ID has not been obtained.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Customer was not created");
        }
        return entity;
    }

    @Override
    public Optional<CustomerDao> findById(long id) {
        try(Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            CustomerDao customerDao = mapCustomerDao(resultSet);
            return Optional.ofNullable(customerDao);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<CustomerDao> findByName(String name) {
         try(Connection connection = manager.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME)) {
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        CustomerDao customerDao = mapCustomerDao(resultSet);
        return Optional.ofNullable(customerDao);
    }
        catch (SQLException exception) {
        exception.printStackTrace();
    }
        return Optional.empty();
    }

    @Override
    public List<Optional<CustomerDao>> findAll() {
        List<Optional<CustomerDao>> customerDaoList = new ArrayList<>();
        try (Connection connection = manager.getConnection();
            ResultSet rs = connection.prepareStatement(GET_ALL_INFO).executeQuery()) {
                while (rs.next()) {
                    CustomerDao customerDao = new CustomerDao();
                    customerDao.setCustomer_id(rs.getLong("customer_id"));
                    customerDao.setCustomer_name(rs.getString("customer_name"));
                    customerDao.setReputation(CustomerDao.Reputation.valueOf(rs.getString("reputation")));
                    customerDaoList.add(Optional.ofNullable(customerDao));
                }
            }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return customerDaoList;
    }

    @Override
    public boolean isExist(long id) {
        return false;
    }

    @Override
    public boolean isExist(String name) {
        return false;
    }

    @Override
    public CustomerDao update(CustomerDao entity) {
        CustomerDao customerDao = null;
        try (Connection connection = manager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, entity.getReputation().toString());
            statement.setString(2, entity.getCustomer_name());
            ResultSet resultSet = statement.executeQuery();
            customerDao = mapCustomerDao(resultSet);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return customerDao;
    }

    @Override
    public void delete(CustomerDao entity) {
        try (Connection connection = manager.getConnection();
                 PreparedStatement statement = connection.prepareStatement(DELETE)) {
                statement.setString(1, entity.getCustomer_name());
                statement.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private CustomerDao mapCustomerDao(ResultSet resultSet) throws SQLException {
        CustomerDao customerDao = null;
        while (resultSet.next()) {
            customerDao = new CustomerDao();
            customerDao.setCustomer_id(resultSet.getLong("customer_id"));
            customerDao.setCustomer_name(resultSet.getString("customer_name"));
            customerDao.setReputation(CustomerDao.Reputation.valueOf(resultSet.getString("reputation")));
        }
        return customerDao;
    }

}
