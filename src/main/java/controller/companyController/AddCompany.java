package controller.companyController;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.CompanyDto;
import model.service.*;
import model.storage.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@WebServlet(urlPatterns = "/company/add")
public class AddCompany extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static CompanyStorage companyStorage;
    private static CompanyService companyService;

    @Override
    public void init() throws ServletException {
        String dbPassword = System.getenv("dbPassword");
        String dbUsername = System.getenv("dbusername");
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Properties properties = propertiesConfig.loadProperties("application.properties");
        managerConnector = new DatabaseManagerConnector(properties, dbUsername, dbPassword);
        new Migration(managerConnector).initDb();
        try {
            companyStorage = new CompanyStorage(managerConnector);
            companyService = new CompanyService(companyStorage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companyName = req.getParameter("companyName");
        String rating = req.getParameter("rating");
        CompanyDto newCompanyDto = new CompanyDto(companyName, CompanyDto.Rating.valueOf(rating));
        String result = companyService.save(newCompanyDto);
        req.setAttribute("result", result);
        req.getRequestDispatcher("/WEB-INF/view/company/addCompany.jsp").forward(req, resp);

    }




}
