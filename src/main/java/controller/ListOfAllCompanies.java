package controller;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.CompanyDto;
import model.dto.DeveloperDto;
import model.service.*;
import model.service.converter.CompanyConverter;
import model.service.converter.DeveloperConverter;
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

@WebServlet(urlPatterns = "/company/list_all_companies")
public class ListOfAllCompanies extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CompanyDto> companies = companyService.findAllCompanies();
        req.setAttribute("companies", companies);
        req.getRequestDispatcher("/WEB-INF/view/listAllCompanies.jsp").forward(req, resp);

    }
}
