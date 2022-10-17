package controller.customerController;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.CompanyDto;
import model.dto.CustomerDto;
import model.service.*;
import model.service.converter.CompanyConverter;
import model.service.converter.CustomerConverter;
import model.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@WebServlet(urlPatterns = "/customer/list_all_customers")
public class ListOfAllCustomer extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static CustomerStorage customerStorage;
    private static CustomerService customerService;

    @Override
    public void init() throws ServletException {
        String dbPassword = System.getenv("dbPassword");
        String dbUsername = System.getenv("dbusername");
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Properties properties = propertiesConfig.loadProperties("application.properties");
        managerConnector = new DatabaseManagerConnector(properties, dbUsername, dbPassword);
        new Migration(managerConnector).initDb();
        try {
            customerStorage = new CustomerStorage(managerConnector);
            customerService = new CustomerService(customerStorage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CustomerDto> customers = customerService.findAllCustomers();
        req.setAttribute("customers", customers);
        req.getRequestDispatcher("/WEB-INF/view/customer/listAllCustomers.jsp").forward(req, resp);

    }
}
