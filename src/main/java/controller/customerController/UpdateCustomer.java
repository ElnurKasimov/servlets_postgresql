package controller.customerController;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.CustomerDto;
import model.service.CustomerService;
import model.storage.CustomerStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet(urlPatterns = "/customer/update")
public class UpdateCustomer extends HttpServlet {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerName = req.getParameter("customerName");
        String reputation = req.getParameter("reputation");
        CustomerDto customerDtoToUpdate = new CustomerDto(customerName, CustomerDto.Reputation.valueOf(reputation));
        String result = customerService.updateCustomer(customerDtoToUpdate);
        req.setAttribute("result", result);
        req.getRequestDispatcher("/WEB-INF/view/customer/updateCustomer.jsp").forward(req, resp);

    }
}
