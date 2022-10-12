package controller;

import model.config.DatabaseManagerConnector;
import model.config.Migration;
import model.config.PropertiesConfig;
import model.dto.DeveloperDto;
import model.service.*;
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

@WebServlet(urlPatterns = "/developer/list_all_developers")
public class ListOfAllDevelopers extends HttpServlet {
    private static DatabaseManagerConnector managerConnector;
    private static DeveloperStorage developerStorage;
    private static CompanyStorage companyStorage;
    private  static SkillStorage skillStorage;

    @Override
    public void init() throws ServletException {
        String dbPassword = System.getenv("dbPassword");
        String dbUsername = System.getenv("dbusername");
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        Properties properties = propertiesConfig.loadProperties("application.properties");
        managerConnector = new DatabaseManagerConnector(properties, dbUsername, dbPassword);
        new Migration(managerConnector).initDb();
        try {
            skillStorage = new SkillStorage(managerConnector);
            companyStorage = new CompanyStorage(managerConnector);
            developerStorage = new DeveloperStorage(managerConnector, companyStorage, skillStorage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<DeveloperDto> developers = developerStorage.findAll()
                .stream().map(Optional::get)
                .map(DeveloperConverter::from)
                .toList();
        req.setAttribute("developers", developers);
        req.getRequestDispatcher("/WEB-INF/view/listAllDevelopers.jsp").forward(req, resp);

    }
}
